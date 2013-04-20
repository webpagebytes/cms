module('wbdisplayobject');
test ("test wbDisplayObject - display", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		"<div id='panel'>" +
		"<span id='wburi'></span>" +
		"<span id='wbtimestamp'></span>" + 
		"<span id='wbname'></span>" + 
		"</div>";
	$fixture.append(htmlString);
	$('#panel').wbDisplayObject({ fieldsPrefix:'wb'});
	var object = { uri: "/test", timestamp: "time", name: "2"};
	$('#panel').wbDisplayObject().display(object);
	
	deepEqual ($("#wburi").html(), "/test");
	deepEqual ($("#wbtimestamp").html(), "time");
	deepEqual ($("#wbname").html(), "2");
});

test ("test wbDisplayObject - display custom handler", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		"<div id='panel'>" +
		"<span id='wbxuri'></span>" +
		"<span id='wbxtimestamp'></span>" + 
		"<span id='wbxname'></span>" + 
		"</div>";
	$fixture.append(htmlString);
	
	var displayHandler = function( fieldId, record) {
		return "x<b>aaa</b>" + record[fieldId];
	}
	$('#panel').wbDisplayObject({ fieldsPrefix:'wbx', customHandler: displayHandler});
	var object = { uri: "/test", timestamp: "time", name: "2"};
	$('#panel').wbDisplayObject().display(object);
	
	deepEqual ($("#wbxuri").html(), "x<b>aaa</b>/test");
	deepEqual ($("#wbxtimestamp").html(), "x<b>aaa</b>time");
	deepEqual ($("#wbxname").html(), "x<b>aaa</b>2");
});

test ("test wbDisplayObject - display not in div", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		"<ul><li id='panel'>" +
		"<span id='wburi'></span>" +
		"<span id='wbtimestamp'></span>" + 
		"<span id='wbname'></span>" + 
		"</li></ul>";
	$fixture.append(htmlString);
	$('#panel').wbDisplayObject({ fieldsPrefix:'wb'});
	var object = { uri: "/test", timestamp: "time", name: "2"};
	$('#panel').wbDisplayObject().display(object);
	
	deepEqual ($("#wburi").html(), "/test");
	deepEqual ($("#wbtimestamp").html(), "time");
	deepEqual ($("#wbname").html(), "2");
});