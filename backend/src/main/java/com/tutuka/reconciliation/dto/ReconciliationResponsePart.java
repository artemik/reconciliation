package com.tutuka.reconciliation.dto;

import com.tutuka.reconciliation.model.TransactionRow;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReconciliationResponsePart {
    private String fileName;
    private long allTransactionRowsCount;
    private List<TransactionRow> unmatchedTransactions;
}
