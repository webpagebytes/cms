module('wbsearchbox');
test ("test functions - format", function () {
	var x1 = "this is a text";
	var y1 = x1.format ("1","2");
	equal (x1, y1);
	
	var x2 = "number {0} string {1} number {2}".format (2, "test 1", 3.14);  
	equal (x2, "number 2 string test 1 number 3.14");
	
	var x3 = "value 1 {0}, value 2 {1}".format ("", "");
	equal(x3, "value 1 , value 2 ");
	
	var x4 = "value 1 {0}, value 2 {1}".format ("");
	equal(x4, "value 1 , value 2 {1}");
	
});

test ("test functions - escapehtml", function () {
	var x1 = "this is a text";
	var y1 = x1.escapehtml();
	equal (x1, y1);
	
	var x2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^*(){}[];:/.,~\\|?";  
	var y2 = x2.escapehtml();
	equal (x2, y2);
	
	var x3 = "<>\'\"&";
	var y3 = x3.escapehtml();
	equal(y3, "&lt;&gt;&apos;&quot;&amp;");
	
});

test ("test functions - Date.toFormatString", function () {
	// month in Date is 0 based index
	var date1 = new Date(2012,11,13,14,15,16,0);
	var string1 = date1.toFormatString(date1);
	equal(string1, "13/12/2012 14:15:16");
	
	var string2 = date1.toFormatString(date1 , "mm/dd/yyyy hh:mm:ss");
	equal(string2, "12/13/2012 14:15:16");
	
	var date2 = new Date(2012,0,1,0,0,0,0);
	var string3 = date2.toFormatString(date2);
	equal(string3, "01/01/2012 00:00:00");
	
	var date3 = new Date(2012,1,1,1,1,1,1);
	var string4 = date3.toFormatString(date3);
	equal(string4, "01/02/2012 01:01:01");
	
}); 