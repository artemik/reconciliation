import {Component, Input} from '@angular/core';
import {ITdDataTableColumn} from "@covalent/core";
import {ReconciliationResponsePart} from "../model/model";

const SHORT_COLUMN = {min: 90};
const GENERAL_COLUMN = {min: 190};
const LONG_COLUMN = {min: 400};

@Component({
  selector: 'app-file-reports',
  templateUrl: './file-reports.component.html',
  styleUrls: ['./file-reports.component.css']
})
export class FileReportsComponent {
  @Input() fileReports: ReconciliationResponsePart[];

  columnsConfig: ITdDataTableColumn[] = [
    {name: 'profileName', label: 'ProfileName', width: GENERAL_COLUMN},
    {name: 'transactionDate', label: 'TransactionDate', width: GENERAL_COLUMN},
    {name: 'transactionAmount', label: 'TransactionAmount', width: SHORT_COLUMN},
    {name: 'transactionNarrative', label: 'TransactionNarrative', width: LONG_COLUMN},
    {name: 'transactionDescription', label: 'TransactionDescription', width: GENERAL_COLUMN},
    {name: 'transactionID', label: 'TransactionID', width: GENERAL_COLUMN},
    {name: 'transactionType', label: 'TransactionType', width: SHORT_COLUMN},
    {name: 'walletReference', label: 'WalletReference', width: LONG_COLUMN},
  ];

  constructor() {
  }
}
