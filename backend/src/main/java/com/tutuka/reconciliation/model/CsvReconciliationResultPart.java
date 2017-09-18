package com.tutuka.reconciliation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
public class CsvReconciliationResultPart {
    private long allTransactionRowsCount;
    private List<TransactionRow> unmatchedTransactions;
}
