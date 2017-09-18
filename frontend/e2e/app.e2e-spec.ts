import {AppPage} from './app.po';

describe('frontend App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should successfully upload two files and receive a reconciliation result', () => {
    page.navigateTo();
    expect(page.getTitleText()).toEqual('Reconciliation');

    const filesPath = 'resources/';
    const firstCsvName = 'first.csv';
    const secondCsvName = 'second.csv';

    page.setFile(0, filesPath + firstCsvName);
    page.setFile(1, filesPath + secondCsvName);

    page.clickReconcileButton();

    page.waitForReconciliationResult();

    expect(page.getSummaryTitle(0)).toEqual(firstCsvName);
    expect(page.getSummaryTitle(1)).toEqual(secondCsvName);
  });
});
