package com.tutuka.reconciliation.service;

import com.tutuka.reconciliation.model.ReconciliationResult;
import com.tutuka.reconciliation.model.Transaction;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class ReconciliationService {
    private static final int MAX_ALLOWED_LEVENSHTEIN_DISTANCE = 4;

    public ReconciliationResult reconcile(List<Transaction> firstTransactions, List<Transaction> secondTransactions) {
        Map<TransactionKey, List<TransactionCandidate>> matchCandidates = createMatchCandidates(secondTransactions);

        List<Transaction> firstUnmatchedTransactions = getFirstUnmatchedTransactions(firstTransactions, matchCandidates);
        List<Transaction> secondUnmatchedTransactions = getSecondUnmatchedTransactions(matchCandidates);

        return new ReconciliationResult(firstUnmatchedTransactions, secondUnmatchedTransactions);
    }

    private List<Transaction> getSecondUnmatchedTransactions(Map<TransactionKey, List<TransactionCandidate>> matchCandidates) {
        return matchCandidates.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(candidate -> !candidate.matched)
                .map(TransactionCandidate::getTransaction)
                .collect(toList());
    }

    private List<Transaction> getFirstUnmatchedTransactions(List<Transaction> firstTransactions,
                                                            Map<TransactionKey, List<TransactionCandidate>> matchCandidates) {
        return firstTransactions.stream()
                .filter(transaction -> hasNoMatches(transaction, matchCandidates))
                .collect(toList());
    }

    private boolean hasNoMatches(Transaction transaction, Map<TransactionKey, List<TransactionCandidate>> matchCandidates) {
        TransactionKey transactionKey = new TransactionKey(transaction);
        List<TransactionCandidate> candidates = matchCandidates.get(transactionKey);
        return !hasMatches(transaction, candidates);
    }

    private Map<TransactionKey, List<TransactionCandidate>> createMatchCandidates(List<Transaction> secondTransactions) {
        Map<TransactionKey, List<TransactionCandidate>> matchCandidates = new HashMap<>();
        secondTransactions.forEach(transaction -> {
            TransactionKey key = new TransactionKey(transaction);
            TransactionCandidate value = new TransactionCandidate(transaction);
            matchCandidates.putIfAbsent(key, new ArrayList<>());
            matchCandidates.get(key).add(value);
        });
        return matchCandidates;
    }

    private boolean hasMatches(Transaction transaction, List<TransactionCandidate> candidates) {
        if (CollectionUtils.isEmpty(candidates)) {
            return false;
        }

        boolean hasMatches = false;
        for (TransactionCandidate candidate : candidates) {
            if (isMatch(transaction, candidate.getTransaction())) {
                candidate.setMatched(true);
                hasMatches = true;
            }
        }

        return hasMatches;
    }

    private boolean isMatch(Transaction transaction, Transaction candidate) {
        return transaction.getTransactionAmount() == candidate.getTransactionAmount() &&
                transaction.getTransactionDescription().equalsIgnoreCase(candidate.getTransactionDescription()) &&
                isNarrativesMatch(transaction, candidate);
    }

    private boolean isNarrativesMatch(Transaction transaction, Transaction candidate) {
        return getNarrativesLevenshteinDistance(transaction, candidate) <= MAX_ALLOWED_LEVENSHTEIN_DISTANCE;
    }

    private int getNarrativesLevenshteinDistance(Transaction transaction, Transaction candidate) {
        String transactionNarrative = transaction.getTransactionNarrative();
        String candidateNarrative = candidate.getTransactionNarrative();
        return LevenshteinDistance.getDefaultInstance().apply(transactionNarrative, candidateNarrative);
    }

    @Data
    private static class TransactionCandidate {
        private boolean matched;
        private Transaction transaction;

        public TransactionCandidate(Transaction transaction) {
            this.transaction = transaction;
        }
    }

    @Data
    private static class TransactionKey {
        private long transactionId;
        private String walletReference;

        public TransactionKey(Transaction transaction) {
            transactionId = transaction.getTransactionID();
            walletReference = transaction.getWalletReference();
        }
    }
}
