function getdat(pathname) {
  var firebaseUrl = "https://eepdb-5d383.firebaseio.com/";
  var base = FirebaseApp.getDatabaseByUrl(firebaseUrl);
  var energy = base.getData(pathname);
  //Logger.log(energy);
  return energy;
  
}

function timeStamp()
{
  var d = new Date();
  var tStamp = d.getTime(); 
  return tStamp;
}

function cpvalue(sheetname)
{
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getSheetByName(sheetname);  // or whatever name
  
  var range = sheet.getRange(2, 1);    // assuming your data appears in A1-Z1 
  var values = range.getValue();         // get the data
  return values;
}

function clearRange() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getSheetByName("แผ่น1");  // or whatever name

 var range = sheet.getRange("A14:E").clearContent();
}

function refresh(){
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheet = ss.getSheetByName("แผ่น1");  // or whatever name
  var range = sheet.getRange("F5:F7").clearContent();
  
  sheet.getRange("F5").setValue("=getdata(\"1/energy/tp0\")");
  sheet.getRange("F6").setValue("=getdata(\"1/energy/tp1\")");
  sheet.getRange("F7").setValue("=getdata(\"1/energy/tp2\")");
}
