package com.tutuka.reconciliation.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class Transaction {
    private String profileName;
    private LocalDateTime transactionDate;
    private long transactionAmount;
    private String transactionNarrative;
    private String transactionDescription;
    private long transactionID;
    private int transactionType;
    private String walletReference;

    public Transaction(String profileName, String transactionNarrative, String transactionDescription, String walletReference,
                       LocalDateTime transactionDate, long transactionAmount, long transactionID, int transactionType) {
        this.profileName = profileName;
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
        this.transactionNarrative = transactionNarrative;
        this.transactionDescription = transactionDescription;
        this.transactionID = transactionID;
        this.transactionType = transactionType;
        this.walletReference = walletReference;
    }
}
