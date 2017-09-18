export interface ReconciliationResponsePart {
  fileName: string;
  allTransactionRowsCount: number;
  unmatchedTransactions: any[];
}

export interface ReconciliationResponse {
  fileReports: ReconciliationResponsePart[];
}
