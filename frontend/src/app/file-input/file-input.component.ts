import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';

@Component({
  selector: 'app-file-input',
  templateUrl: './file-input.component.html',
  styleUrls: ['./file-input.component.css']
})
export class FileInputComponent {
  @Input() placeholder: String;
  @Input() disabled: boolean;
  @Input() file: File;
  @Output() fileChange = new EventEmitter();
  @ViewChild('fileInput') fileInput;

  constructor() { }

  onFileChange(file) {
    console.log('FileInputComponent.onFileChange: ' + file);
    this.file = file;
    this.fileChange.emit(file);
  }

  clearFile() {
    this.file = null;

    // Wrapped in setTimeout as a workaround for TdFileInputComponent - placeholder doesn't grow back to normal.
    setTimeout(() => {
      this.fileInput.clear();
    });
  }
}
