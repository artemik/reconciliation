import {browser, by, element, protractor} from 'protractor';

var path = require('path');

const RECONCILIATION_MAX_DELAY_MS = 5000;

export class AppPage {
  navigateTo() {
    return browser.get('/');
  }

  getTitleText() {
    return element(by.css('h1')).getText();
  }

  setFile(index, filePath) {
    let fullFilePath = path.resolve(__dirname, filePath);

    element.all(by.css('.td-file-input-hidden')).get(index).sendKeys(fullFilePath);
  }

  clickReconcileButton() {
    element.all(by.css('.reconcile-button')).click();
  }

  waitForReconciliationResult() {
    browser.wait(
      protractor.ExpectedConditions.presenceOf(this._getSummary(0)),
      RECONCILIATION_MAX_DELAY_MS,
      'Reconciliation result doesn\'t appear for too long'
    );
  }

  getSummaryTitle(index) {
    return this._getSummary(index).element(by.css('md-card-title')).getText();
  }

  _getSummary(index) {
    return this._getSummaries().get(index);
  }

  _getSummaries() {
    return element.all(by.css('app-file-summary'));
  }
}
