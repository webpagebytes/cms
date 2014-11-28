/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
module('wbsearchbox');
test ("test wbsearchbox - creation", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<div id="idsearchbox"> </div>';
	$fixture.append(htmlString);
	$('#idsearchbox').wbSearchBox( {} );
	
	ok ( $('#idsearchbox').find('input').hasClass('wbsearchbox'));
	ok ( $('#idsearchbox').find('ul').hasClass('wbsearchlist'));
	
});
