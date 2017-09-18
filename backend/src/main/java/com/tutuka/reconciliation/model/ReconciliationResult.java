package com.tutuka.reconciliation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReconciliationResult {
    private List<Transaction> firstUnmatchedTransactions;
    private List<Transaction> secondUnmatchedTransactions;
}
