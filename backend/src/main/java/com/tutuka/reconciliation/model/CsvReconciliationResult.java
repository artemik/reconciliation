package com.tutuka.reconciliation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CsvReconciliationResult {
    private CsvReconciliationResultPart firstReconciliationResult;
    private CsvReconciliationResultPart secondReconciliationResult;
}
