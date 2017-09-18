package com.tutuka.reconciliation.controller;

import com.tutuka.reconciliation.dto.ReconciliationResponse;
import com.tutuka.reconciliation.dto.ReconciliationResponsePart;
import com.tutuka.reconciliation.model.CsvReconciliationResult;
import com.tutuka.reconciliation.model.CsvReconciliationResultPart;
import com.tutuka.reconciliation.service.CsvReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@RestController
public class ReconciliationController {

    @Autowired
    private CsvReconciliationService csvReconciliationService;

    @RequestMapping("/reconcile")
    public ReconciliationResponse reconcile(@RequestParam("firstFile") MultipartFile firstFile,
                                            @RequestParam("secondFile") MultipartFile secondFile) throws IOException {
        InputStream firstCsv = firstFile.getInputStream();
        InputStream secondCsv = secondFile.getInputStream();

        CsvReconciliationResult reconciliationResult = csvReconciliationService.reconcile(firstCsv, secondCsv);

        return new ReconciliationResponse(
                Arrays.asList(
                        createResponsePart(firstFile, reconciliationResult.getFirstReconciliationResult()),
                        createResponsePart(secondFile, reconciliationResult.getSecondReconciliationResult())
                )
        );
    }

    private ReconciliationResponsePart createResponsePart(MultipartFile firstFile, CsvReconciliationResultPart reconciliationResultPart) {
        return new ReconciliationResponsePart(
                firstFile.getOriginalFilename(),
                reconciliationResultPart.getAllTransactionRowsCount(),
                reconciliationResultPart.getUnmatchedTransactions()
        );
    }
}
