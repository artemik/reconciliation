import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-file-summary',
  templateUrl: './file-summary.component.html',
  styleUrls: ['./file-summary.component.css']
})
export class FileSummaryComponent {
  @Input() fileReport: any;

  constructor() {
  }
}
