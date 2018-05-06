//Remove All Empty Columns in the Entire Workbook
function removeEmptyColumns() {
var ss = SpreadsheetApp.getActive();
var allsheets = ss.getSheets();
for (var s in allsheets){
var sheet=allsheets[s]
var maxColumns = sheet.getMaxColumns(); 
var lastColumn = sheet.getLastColumn();
if (maxColumns-lastColumn != 0){
      sheet.deleteColumns(lastColumn+1, maxColumns-lastColumn);
      }
  }
}

//Remove All Empty Rows in the Entire Workbook
function removeEmptyRows() {
var ss = SpreadsheetApp.getActive();
var allsheets = ss.getSheets();
for (var s in allsheets){
var sheet=allsheets[s]
var maxRows = sheet.getMaxRows(); 
var lastRow = sheet.getLastRow();
if (maxRows-lastRow != 0){
      sheet.deleteRows(lastRow+1, maxRows-lastRow);
      }
  }
}

function saveAsSpreadsheet(){ 
  var sheet = SpreadsheetApp.getActiveSpreadsheet();
  var range = sheet.getRange('แผ่น1!A1:B3');
  var now = new Date();
  sheet.setNamedRange('buildingNameAddress', range);
  var TestRange = sheet.getRangeByName('buildingNameAddress').getValues(); 
  Logger.log(TestRange); 
  var destFolder = DriveApp.getFolderById("1fLsOOEH4jDXqwVj6Y_bte2VcwlvNrn5q"); 
  DriveApp.getFileById(sheet.getId()).makeCopy(now, destFolder); 
  deleteCell();
  

}

function deleteCell(){
var ss = SpreadsheetApp.getActive();
var allsheets = ss.getSheets();
for (var s in allsheets){
var sheet=allsheets[s]
var maxRows = sheet.getMaxRows(); 
  if(maxRows > 1)
  {
    sheet.deleteRows(1, maxRows - 1);
  }   
}
}