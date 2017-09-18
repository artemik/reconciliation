package com.tutuka.reconciliation.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.tutuka.reconciliation.model.CsvReconciliationResult;
import com.tutuka.reconciliation.model.CsvReconciliationResultPart;
import com.tutuka.reconciliation.model.ReconciliationResult;
import com.tutuka.reconciliation.model.Transaction;
import com.tutuka.reconciliation.model.TransactionRow;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
public class CsvReconciliationService {

    private static final DateTimeFormatter TRANSACTION_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");

    @Autowired
    private ReconciliationService reconciliationService;

    public CsvReconciliationResult reconcile(InputStream firstCsvStream, InputStream secondCsvStream) {
        List<TransactionRow> firstTransactions = getTransactionRows(firstCsvStream);
        List<TransactionRow> secondTransactions = getTransactionRows(secondCsvStream);

        Pair<List<TransactionRow>, List<TransactionRow>> unmatchedTransactions = reconcile(firstTransactions, secondTransactions);

        return new CsvReconciliationResult(
                new CsvReconciliationResultPart(firstTransactions.size(), unmatchedTransactions.getLeft()),
                new CsvReconciliationResultPart(secondTransactions.size(), unmatchedTransactions.getRight())
        );
    }

    private Pair<List<TransactionRow>, List<TransactionRow>> reconcile(List<TransactionRow> firstTransactions,
                                                                       List<TransactionRow> secondTransactions) {
        TransactionsParseResult firstParseResult = parseTransactions(firstTransactions);
        TransactionsParseResult secondParseResult = parseTransactions(secondTransactions);

        ReconciliationResult reconciliationResult = reconciliationService.reconcile(
                firstParseResult.getTransactionsToReconcile(),
                secondParseResult.getTransactionsToReconcile()
        );

        List<TransactionRow> firstUnmatchedTransactions = mergeResults(firstParseResult, reconciliationResult.getFirstUnmatchedTransactions());
        List<TransactionRow> secondUnmatchedTransactions = mergeResults(secondParseResult, reconciliationResult.getSecondUnmatchedTransactions());

        return Pair.of(firstUnmatchedTransactions, secondUnmatchedTransactions);
    }

    private List<TransactionRow> mergeResults(TransactionsParseResult parseResult, List<Transaction> reconciledTransactions) {
        return ListUtils.union(
                parseResult.getUnmatchedTransactions(),
                getRawRows(parseResult, reconciledTransactions)
        );
    }

    private List<TransactionRow> getRawRows(TransactionsParseResult parseResult, List<Transaction> reconciledTransactions) {
        return reconciledTransactions.stream()
                .map(transaction -> parseResult.transactionToRow.get(transaction))
                .collect(toList());
    }

    private TransactionsParseResult parseTransactions(List<TransactionRow> transactions) {
        List<TransactionRow> unmatchedTransactions = new ArrayList<>();
        List<Transaction> transactionsToReconcile = new ArrayList<>();
        Map<Transaction, TransactionRow> transactionToRow = new IdentityHashMap<>();

        transactions.forEach(transactionRow -> {
            try {
                Transaction transaction = parseTransaction(transactionRow);
                transactionsToReconcile.add(transaction);
                transactionToRow.put(transaction, transactionRow);
            } catch (IllegalArgumentException e) {
                unmatchedTransactions.add(transactionRow);
            }
        });

        return new TransactionsParseResult(unmatchedTransactions, transactionsToReconcile, transactionToRow);
    }

    private Transaction parseTransaction(TransactionRow transactionRow) {
        return new Transaction(
                parseText(transactionRow.getProfileName()),
                parseText(transactionRow.getTransactionNarrative()),
                parseText(transactionRow.getTransactionDescription()),
                parseText(transactionRow.getWalletReference()),
                parseDateTime(transactionRow.getTransactionDate()),
                parseLong(transactionRow.getTransactionAmount()),
                parseLong(transactionRow.getTransactionID()),
                parseInt(transactionRow.getTransactionType())
        );
    }

    private long parseLong(String value) {
        value = parseText(value);
        return Long.parseLong(value);
    }

    private int parseInt(String value) {
        value = parseText(value);
        return Integer.parseInt(value);
    }

    private LocalDateTime parseDateTime(String value) {
        value = parseText(value);
        try {
            return LocalDateTime.parse(value, TRANSACTION_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Column doesn't contain a valid date time", e);
        }
    }

    private String parseText(String text) {
        checkNotBlank(text);
        return text.trim();
    }

    private void checkNotBlank(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Blank columns are not allowed");
        }
    }

    private List<TransactionRow> getTransactionRows(InputStream csvStream) {
        try {
            return new CsvToBeanBuilder<TransactionRow>(new InputStreamReader(csvStream, StandardCharsets.UTF_8))
                    .withType(TransactionRow.class)
                    .build()
                    .parse();
        } finally {
            IOUtils.closeQuietly(csvStream);
        }
    }

    @Data
    @AllArgsConstructor
    private static class TransactionsParseResult {
        private List<TransactionRow> unmatchedTransactions;
        private List<Transaction> transactionsToReconcile;

        /**
         * For simplicity, a map is used to remember original rows.
         * <p>
         * Another option - don't use map, but wrap transactions in a RowAwareTransaction
         * that remembers which TransactionRow it was converted from.
         */
        private Map<Transaction, TransactionRow> transactionToRow;
    }
}
