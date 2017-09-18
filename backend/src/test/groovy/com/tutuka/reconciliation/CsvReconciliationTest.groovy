package com.tutuka.reconciliation

import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.tutuka.reconciliation.model.*
import com.tutuka.reconciliation.service.CsvReconciliationService
import com.tutuka.reconciliation.service.ReconciliationService
import spock.lang.Specification

class CsvReconciliationTest extends Specification {

    def "CSV reconciliation result should include rows of wrong format and those returned by ReconciliationService"() {
        setup:
        def csvReconciliationService = new CsvReconciliationService()

        def firstTestTransactions = createTestTransactionsBag()
        def secondTestTransactions = createTestTransactionsBag()

        def reconciliationService = Stub(ReconciliationService)
        reconciliationService.reconcile(_, _) >> { List<Transaction> firstTransactions, List<Transaction> secondTransactions ->
            return new ReconciliationResult(
                    filterUnmatchedTransactions(firstTransactions, firstTestTransactions),
                    filterUnmatchedTransactions(secondTransactions, secondTestTransactions)
            )
        }
        csvReconciliationService.reconciliationService = reconciliationService

        when:
        CsvReconciliationResult reconciliationResult = csvReconciliationService.reconcile(
                toInputStream(firstTestTransactions.allTransactions),
                toInputStream(secondTestTransactions.allTransactions)
        )

        then:
        resultPartsMatch(reconciliationResult.firstReconciliationResult, firstTestTransactions)
        resultPartsMatch(reconciliationResult.secondReconciliationResult, secondTestTransactions)
    }

    def resultPartsMatch(CsvReconciliationResultPart resultPart, TestTransactionsBag testTransactions) {
        assert resultPart.allTransactionRowsCount == testTransactions.allTransactions.size()
        transactionsMatch(resultPart.unmatchedTransactions, testTransactions.unmatchingTransactions)
    }

    def transactionsMatch(List<TransactionRow> actualTransactions, List<TransactionRow> expectedTransactions) {
        assert expectedTransactions.size() == actualTransactions.size()
        assert expectedTransactions.containsAll(actualTransactions)
        assert actualTransactions.containsAll(expectedTransactions)
        return true
    }

    private List<Transaction> filterUnmatchedTransactions(List<Transaction> transactions, TestTransactionsBag testTransactions) {
        transactions.findAll {
            it.transactionID.toString() in testTransactions.unmatchingTransactions*.transactionID
        }
    }

    private TestTransactionsBag createTestTransactionsBag() {
        return new TestTransactionsBag(
                wrongFormatTransactions: [wrongFormatTransaction()],
                unmatchingTransactions: [validTransaction()],
                validTransactions: [validTransaction()]
        )
    }

    private TransactionRow validTransaction() {
        createSampleTransactionRow().build()
    }

    private TransactionRow wrongFormatTransaction() {
        createSampleTransactionRow().transactionAmount(null).build()
    }

    private TransactionRow.TransactionRowBuilder createSampleTransactionRow() {
        def randomTransactionId = new Random().nextLong().toString()

        return new TransactionRow.TransactionRowBuilder()
                .profileName("Card Campaign")
                .transactionDate("2014-01-11 22:27:44")
                .transactionAmount("-20000")
                .transactionNarrative("*MOLEPS ATM25             MOLEPOLOLE    BW")
                .transactionDescription("DEDUCT")
                .transactionID(randomTransactionId)
                .transactionType("1")
                .walletReference("P_NzI2ODY2ODlfMTM4MjcwMTU2NS45MzA5")
    }

    private InputStream toInputStream(List<TransactionRow> transactionRows) {
        def byteArray = new ByteArrayOutputStream()

        def writer = new OutputStreamWriter(byteArray)
        new StatefulBeanToCsvBuilder<TransactionRow>(writer)
                .build()
                .write(transactionRows)
        writer.flush()

        return new ByteArrayInputStream(byteArray.toByteArray())
    }

    private static class TestTransactionsBag {
        List<TransactionRow> wrongFormatTransactions
        List<TransactionRow> unmatchingTransactions
        List<TransactionRow> validTransactions

        def getAllTransactions() {
            return wrongFormatTransactions + unmatchingTransactions + validTransactions
        }

        def getUnmatchingTransactions() {
            return wrongFormatTransactions + unmatchingTransactions
        }
    }
}
