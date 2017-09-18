package com.tutuka.reconciliation

import com.opencsv.bean.CsvToBeanBuilder
import com.tutuka.reconciliation.model.CsvReconciliationResult
import com.tutuka.reconciliation.model.CsvReconciliationResultPart
import com.tutuka.reconciliation.model.TransactionRow
import com.tutuka.reconciliation.service.CsvReconciliationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.nio.charset.StandardCharsets

@SpringBootTest
class CsvReconciliationIT extends Specification {

    @Autowired
    CsvReconciliationService csvReconciliationService

    def "Valid CSV files should work"() {
        setup:
        def firstCsvFilePath = "$csvFilesPath/first.csv"
        def firstCsv = getFileStream(firstCsvFilePath)

        def secondCsvFilePath = "$csvFilesPath/second.csv"
        def secondCsv = getFileStream(secondCsvFilePath)

        when:
        CsvReconciliationResult reconciliationResult = csvReconciliationService.reconcile(firstCsv, secondCsv)

        then:
        totalCountAndTransactionsMatchExpected(
                reconciliationResult.firstReconciliationResult,
                firstCsvFilePath,
                "$csvFilesPath/first_unmatched.csv"
        )
        totalCountAndTransactionsMatchExpected(
                reconciliationResult.secondReconciliationResult,
                secondCsvFilePath,
                "$csvFilesPath/second_unmatched.csv"
        )

        where:
        csvFilesPath                  | _
        "/example"                    | _
        "/unmatched_by_empty_columns" | _
        "/unmatched_by_values"        | _
    }

    def "Empty CSV streams should work"() {
        setup:
        def firstCsv = new ByteArrayInputStream(new byte[0])
        def secondCsv = new ByteArrayInputStream(new byte[0])

        when:
        CsvReconciliationResult reconciliationResult = csvReconciliationService.reconcile(firstCsv, secondCsv)

        then:
        resultPartIsEmpty(reconciliationResult.firstReconciliationResult)
        resultPartIsEmpty(reconciliationResult.secondReconciliationResult)
    }

    def totalCountAndTransactionsMatchExpected(CsvReconciliationResultPart resultPart, String csvFilePath, String expectedCsvFilePath) {
        assert totalCountEqual(resultPart, csvFilePath)
        assert transactionsMatch(resultPart.unmatchedTransactions, expectedCsvFilePath)
        return true
    }

    def resultPartIsEmpty(CsvReconciliationResultPart reconciliationResultPart) {
        assert reconciliationResultPart.allTransactionRowsCount == 0
        assert (reconciliationResultPart.unmatchedTransactions == null || reconciliationResultPart.unmatchedTransactions.isEmpty())
        return true
    }

    def totalCountEqual(CsvReconciliationResultPart reconciliationResultPart, String transactionsFilePath) {
        def expectedTransactions = getTransactionRows(transactionsFilePath)
        assert expectedTransactions.size() == reconciliationResultPart.allTransactionRowsCount
        return true
    }

    def transactionsMatch(List<TransactionRow> actualTransactions, String expectedTransactionsFilePath) {
        def expectedTransactions = getTransactionRows(expectedTransactionsFilePath)
        assert expectedTransactions.size() == actualTransactions.size()
        assert expectedTransactions.containsAll(actualTransactions)
        assert actualTransactions.containsAll(expectedTransactions)
        return true
    }

    private List<TransactionRow> getTransactionRows(String filePath) {
        return getTransactionRows(getFileStream(filePath))
    }

    private InputStream getFileStream(String filePath) {
        return getClass().getResourceAsStream(filePath);
    }

    private List<TransactionRow> getTransactionRows(InputStream csvStream) {
        csvStream.withCloseable {
            return new CsvToBeanBuilder<TransactionRow>(new InputStreamReader(csvStream, StandardCharsets.UTF_8))
                    .withType(TransactionRow.class)
                    .build()
                    .parse();
        }
    }
}
