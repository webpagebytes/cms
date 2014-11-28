/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
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
	equal(string1, "13/12/2012 14:15");
	
	var string2 = date1.toFormatString(date1 , "mm/dd/yyyy hh:mm:ss");
	equal(string2, "12/13/2012 14:15");
	
	var date2 = new Date(2012,0,1,0,0,0,0);
	var string3 = date2.toFormatString(date2);
	equal(string3, "01/01/2012 00:00");
	
	var date3 = new Date(2012,1,1,1,1,1,1);
	var string4 = date3.toFormatString(date3);
	equal(string4, "01/02/2012 01:01");
	
	var dateNow = new Date();
	dateNow.setMinutes(2);
	dateNow.setHours(1);
	var string5 = dateNow.toFormatString(dateNow, "today|dd/mm/yyyy hh:mm");
	equal(string5, "Today 01:02");

	var dateNow2 = new Date();
	dateNow2.setMinutes(21);
	dateNow2.setHours(11);
	var string6 = dateNow2.toFormatString(dateNow2, "today|dd/mm/yyyy hh:mm");
	equal(string6, "Today 11:21");
	
}); 

test ("test functions - replace query parameter", function () {
	var x = replaceURLParameter("foo?x=abc&y=xyz", "x", "123");
	equal(x, "foo?x=123&y=xyz&");
	
	x = replaceURLParameter("foo?x=abc&", "x", "123");
	equal(x, "foo?x=123&");
	
	x = replaceURLParameter("foo?x=abc", "y", "123");
	equal(x, "foo?x=abc&y=123&");
	
	x = replaceURLParameter("http://www.foo.com", "x", "123");
	equal(x, "http://www.foo.com?x=123&");
	
	x = replaceURLParameter("http://www.foo.com", "x?", "?=#");
	equal(x, "http://www.foo.com?x%3F=%3F%3D%23&");
	
});

test ("test functions - remove query parameter", function () {
	var x = removeURLParameter("foo?x=abc&y=xyz", "x");
	equal(x, "foo?y=xyz&");
	
	x = removeURLParameter("foo?x=abc", "x");
	equal(x, "foo?");
	
	x = removeURLParameter("foo?x=abc", "y");
	equal(x, "foo?x=abc&");
	
	x = removeURLParameter("http://www.foo.com", "x", "123");
	equal(x, "http://www.foo.com?");
	
	x = removeURLParameter("http://www.foo.com?x%3F=%3F%3D%23&", "x?");
	equal(x, "http://www.foo.com?");
	
});