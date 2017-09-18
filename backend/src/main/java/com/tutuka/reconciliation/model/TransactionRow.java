package com.tutuka.reconciliation.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRow {
    @CsvBindByName(column = "ProfileName")
    private String profileName;

    @CsvBindByName(column = "TransactionDate")
    private String transactionDate;

    @CsvBindByName(column = "TransactionAmount")
    private String transactionAmount;

    @CsvBindByName(column = "TransactionNarrative")
    private String transactionNarrative;

    @CsvBindByName(column = "TransactionDescription")
    private String transactionDescription;

    @CsvBindByName(column = "TransactionID")
    private String transactionID;

    @CsvBindByName(column = "TransactionType")
    private String transactionType;

    @CsvBindByName(column = "WalletReference")
    private String walletReference;
}
