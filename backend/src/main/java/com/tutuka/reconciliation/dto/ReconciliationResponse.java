package com.tutuka.reconciliation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReconciliationResponse {
    private List<ReconciliationResponsePart> fileReports;
}
