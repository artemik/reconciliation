import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TdFadeInOutAnimation} from "@covalent/core";
import {ReconciliationResponse, ReconciliationResponsePart} from "./model/model";
import {MdSnackBar} from "@angular/material";

const RECONCILE_URL = '/api/reconcile';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [
    TdFadeInOutAnimation(),
  ],
})
export class AppComponent {
  firstFile: File;
  secondFile: File;
  inProgress: boolean = false;
  result: ReconciliationResponsePart[];

  constructor(private http: HttpClient, private snackBar: MdSnackBar) {
  }

  runReconciliation() {
    this.inProgress = true;

    let formData = new FormData();
    formData.append('firstFile', this.firstFile);
    formData.append('secondFile', this.secondFile);
    this.http.post<ReconciliationResponse>(RECONCILE_URL, formData)
      .subscribe(
        (success) => this._handleSuccess(success),
        (error) => this._handleError(error)
      )
  }

  _handleSuccess(success) {
    console.log(success);
    this.result = success.fileReports;
    this.inProgress = false;
  }

  _handleError(error) {
    console.log(error);

    this.inProgress = false;

    let status = error.status;
    if (status >= 400 && status < 500) {
      this._showSnackBar('Unsupported files provided');
    } else {
      this._showSnackBar('Communication or Server Error');
    }
  }

  _showSnackBar(message) {
    this.snackBar.open(message, null, {
      duration: 3000,
    });
  }
}
