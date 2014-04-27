module('wbobjectmanager');
test ("test wbobjectmanager - getObjectFromFields", 1, function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<input type="text" id="wbxuri" value="testtext"> '+
		'<textarea id="wbxsummary">test summary</textarea> '+		
		'<input type="hidden" id="wbxkey" value="1234"> '+ 
		'<input type="radio" value="1" name="enabled" id="wbxenabled" checked> '+
		'<input type="radio" value="0" name="enabled"> '+
		'<input type="radio" value="3" name="live" id="wbxlive"> '+
		'<input type="radio" value="4" name="live" checked> '+
		'<select id="wbxhttpOperation"> <option>GET </option> <option selected="selected">POST</option></select> '+
		'</form>';
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager({ fieldsPrefix:'wbx'});
	var obj = $('#wbadduri').wbObjectManager().getObjectFromFields();
	
	deepEqual ( obj, { key: "1234", uri: "testtext", enabled: "1", live:"4", httpOperation: "POST", summary:"test summary" });
});

test ("test wbobjectmanager - populateFieldsFromObject", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<input type="text" id="wbxuri"> '+ 
		'<textarea id="wbxsummary"> </textarea> '+
		'<input type="hidden" id="wbxkey"> '+ 
		'<input type="radio" value="1" name="enabled" id="wbxenabled"> '+
		'<input type="radio" value="0" name="enabled"> '+
		'<input type="radio" value="3" name="live" id="wbxlive"> '+
		'<input type="radio" value="4" name="live"> '+
		'<select id="wbxhttpOperation"> <option value="GET">GET </option> <option value="POST" selected="selected">POST</option></select> '+
		'</form>';
	$fixture.append(htmlString);
	var objectToSet = { key: "1234", uri: "testtext", enabled: "1", live:"4", httpOperation: "GET", summary:"test summary" };
	$('#wbadduri').wbObjectManager({ fieldsPrefix:'wbx'});
	$('#wbadduri').wbObjectManager().populateFieldsFromObject(objectToSet);
	
	equal ( $("#wbxuri").val(), objectToSet['uri']);
	equal ( $("#wbxkey").val(), objectToSet['key']);
	equal ( $("#wbxsummary").val(), objectToSet['summary']);
	equal ( $("#wbxhttpOperation").val(), objectToSet['httpOperation']);
	equal ( $("[name='enabled']").filter(":checked").val(), objectToSet['enabled']);
	equal ( $("[name='live']").filter(":checked").val(), objectToSet['live']);
});

test ("test wbobjectmanager - resetFields", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<input type="text" id="wbxuri""> '+ 
		'<input type="hidden" id="wbxkey""> '+ 
		'<input type="checkbox" value="1" id="wbxenabled"> '+
		'<input id="wbxvisible" type="radio" name="visible" value="1"> One <input type="radio" name="visible" value="2"> TWO <input type="radio" name="visible" value="3"> THREE' +
		'<select id="wbxhttpOperation"> <option>GET </option> <option>POST</option></select> '+
		'</form>';
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager({ fieldsPrefix:'wbx',fieldsDefaults: { key: 0 ,uri: "/", enabled: 1, visible: 3, httpOperation: "POST"} });
	
	equal ( $("#wbxuri").val(), "/");
	equal ( $("#wbxkey").val(), "0");
	equal ( $("#wbxhttpOperation").val(), "POST");
	equal ( $("#wbxenabled").filter(":checked").val(), 1);
	equal ( $("[name^='visible']").filter(":checked").val(), 3);
	
});

test ("test wbobjectmanager - setErrors", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<span id="generalerror"></span>'+
		'<span id="erroruri""></span>  '+ 
		'<span id="errorenabled"></span> '+
		'<span id="errorhttpOperation"></span> '+
		'</form>';
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager({ fieldsPrefix:'wbx',
									 generalError:'generalerror',
									 errorLabelsPrefix: 'error'
									 });
	$('#wbadduri').wbObjectManager().setErrors( { 'uri':'error 1', 'httpOperation':'error 2','':'general error' });
	equal ( $("#erroruri").html(), "error 1");
	equal ( $("#errorhttpOperation").html(), "error 2");
	equal ( $("#errorenabled").html(), '');
	equal ( $("#generalerror").html(), 'general error');
	
	
});

test ("test wbobjectmanager - validateFields", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<input type="text" id="wbxuri" value="/testtext"> '+ 
		'<input type="hidden" id="wbxkey" value="4"> '+ 
		'<input type="checkbox" value="1" id="wbxenabled" checked> '+
		'<select id="wbxhttpOperation"> <option>GET</option> <option selected="selected">POST</option></select> '+
		'</form>';
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager({ fieldsPrefix:'wbx',
									 validationRules: {
										'uri': { startsWith: '/' },
										'key': { min: 1 },
										'httpOperation': { includedInto: ['POST', 'GET', 'PUT', 'DELETE'] } 
									 }
									});
	var result = $('#wbadduri').wbObjectManager().validateFields( );
	deepEqual ( result, {} );	
});

test ("test wbobjectmanager - validateFields - single element test pass", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
	'<form id="wbadduri"> ' + 	
	'<input type="text" id="wbxuri" value="/testtext"> ' +
	'</form>'; 
		
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager({ fieldsPrefix:'wbx',
									 validationRules: {
										'uri': [ { rule:{ startsWith: '/'}, error: "ERROR1"}, { rule: {contains:'test'}, error: "ERROR2"}, { rule: {rangeLength: { 'min': 2, 'max': 10 }}, error:"ERROR3" }] 
									 }
								});
	var result = $('#wbadduri').wbObjectManager().validateFields( );
	deepEqual ( result, {} );	
});

test ("test wbobjectmanager - validateFields - single element test fail", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
	'<form id="wbadduri"> ' + 
	'<input type="text" id="wbxuri" value="/testtext"> ' + 
	'</form>';	
	
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager({ fieldsPrefix:'wbx',
									 validationRules: { 'uri': [ {rule: { startsWith: 'a'}, error: "ERROR_1"}, { rule: { contains:'password'}, error: "ERROR_2"}, { rule: {rangeLength: { 'min': 20, 'max': 30 } }, error: "ERROR_3"} ] }
									 }
								);
	var result = $('#wbadduri').wbObjectManager().validateFields( );
	var expected = { 'uri': [ 'ERROR_1', 'ERROR_2', 'ERROR_3'] };
	deepEqual ( result, expected );	
});



test ("test wbobjectmanager - validateElement - text length, min, max, range", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<input type="text" id="wbxuri" value="test"> '+ 
		'<input type="checkbox" value="1" id="wbxenabled" checked> '+
		'<select id="wbxhttpOperation"> <option>GET </option> <option selected="selected">POST</option></select> '+
		'</form>';
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager( {} );
	var errors = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{ rule:{'length': 4}, error:"ERROR1" } , {rule:{'minLength': 2}, error:"ERROR2"}, { rule:{'maxLength': 5}, error:"ERROR3" },  { rule:{'rangeLength': {'min': 2, 'max': 5}}, error:"ERROR4"}] );
	deepEqual (errors, []);	
	var errors1 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{ rule:{'length': 3}, error:"ERROR1"}, {rule:{'minLength': 5}, error:"ERROR2"}, {rule:{'maxLength': 3}, error:"ERROR3"}, {rule:{'rangeLength': {'min': 5, 'max': 6}}, error:"ERROR4"}] );
	var expected1 = ["ERROR1", "ERROR2", "ERROR3", "ERROR4"];
	deepEqual  (errors1, expected1);	
	
});

test ("test wbobjectmanager - validateElement - number min, max, range, equal, not equal ", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<input type="text" id="wbxuri" value="10"> '+ 
		'</form>';
	$fixture.append(htmlString);

	$('#wbadduri').wbObjectManager( {} );

	// happy flows
	var errors = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'min': 4}, error:"ERROR"}]);
	deepEqual (errors, []);	

	var errors_ = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'min': 10}, error:"ERROR"}]);
	deepEqual (errors_, []);	
	
	var errors1 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'max': 12}, error:"ERROR"}]);
	deepEqual (errors1, []);	

	var errors1_ = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'max': 10}, error:"ERROR"}]);
	deepEqual (errors1_, []);	

	var errors2 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{ rule:{'range': { 'min': 5, 'max': 15}}, error:"ERROR"}]);
	deepEqual (errors2, []);	

	var errors2_ = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule: {'range': { 'min': 10, 'max': 10} }, error:"ERROR"}]);
	deepEqual (errors2_, []);	
	
	var errors3 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'equal': 10}, error:"ERROR"}]);
	deepEqual (errors3, []);	

	var errors4 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'notEqual': 11}, error:"ERROR"}]);
	deepEqual (errors4, []);	

	// test errors 
	var errorC = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'min': 20}, error:"ERROR"}]);
	deepEqual ( errorC, ["ERROR"]);	
	
	var errorC1 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'max': 7}, error:"ERROR"}]);
	deepEqual ( errorC1, ["ERROR"]);	
	
	var errorC2 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'range': { 'min': 12, 'max':15}}, error:"ERROR"}]);
	deepEqual ( errorC2, ["ERROR"]);	

	var errorC3 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'range': { 'min': 7, 'max':8} }, error:"ERROR"}]);
	deepEqual ( errorC3, ["ERROR"]);	

	var errorC4 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'equal': 9}, error:"ERROR"}]);
	deepEqual ( errorC4, ["ERROR"]);	
	
	var errorC5 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'notEqual': 10}, error:"ERROR"}]);
	deepEqual ( errorC5, ["ERROR"]);	
	
});

test ("test wbobjectmanager - validateElement - text startsWith, endsWith, contains,", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<input type="text" id="wbxuri" value="test"> '+ 
		'</form>';
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager( {} );
	var errors = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'startsWith': 'tEs'}, error:"ERROR"}]);
	deepEqual (errors, []);	

	var errors1 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'startsWith': 'xyz'}, error:"ERROR"}]);
	deepEqual  (errors1, ["ERROR"]);	

	var errors2 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'endsWith': 'xyz'}, error:"ERROR"}]);
	deepEqual  (errors2, ["ERROR"]);	
	
	var errors3 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'endsWith': 'ST'}, error:"ERROR"}]);
	deepEqual  (errors3, [] );	

	var errors4 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'contains': 'xyz'}, error:"ERROR"}]);
	deepEqual  (errors4, ["ERROR"]);	
	
	var errors5 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'contains': 'eS'}, error:"ERROR"}]);
	deepEqual  (errors5, []);	

	var errors6 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'notContains': 'eS'}, error:"ERROR"}]);
	deepEqual  (errors6, ["ERROR"]);	
	
	var errors7 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule:{'notContains': 'xyz'}, error:"ERROR"}]);
	deepEqual  (errors7, [] );	
	
});

test ("test wbobjectmanager - validateElement - includedInto, notIncludedInto", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<input type="text" id="wbxuri" value="POST"> '+ 
		'</form>';
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager( {} );
	var errors = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{ rule: {'includedInto': ['POST','GET','PUT','DELETE']}, error: "ERROR_OK"}] );
	deepEqual ( errors, []);	

	var errors = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{ rule:{'includedInto': ['XYZ','ABC','123']}, error: "ERROR_1"}] );
	deepEqual ( errors, ["ERROR_1"] );	

	var errors2 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{ rule:{'notIncludedInto': ['XYZ','ABC','123']}, error: "ERROR_OK" }] );
	deepEqual ( errors2, [] );

	var errors3 = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{ rule:{'notIncludedInto': ['XYZ','POST','123']}, error: "ERROR_2" }] );
	deepEqual ( errors3, ["ERROR_2"] );
	
});

test ("test wbobjectmanager - validateElement - regexp", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
'<input type="text" id="wbxuri" value="abcd/-._">'+
		'</form>';
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager( {} );
	var errors = $('#wbadduri').wbObjectManager().validateElement( $('#wbxuri'), [{rule: {'customRegexp': {'pattern':"^[0-9a-zA-Z/_.-]*$", 'modifiers': "gi"} }, error: "ERROR_REGEXP"}]);
	deepEqual ( errors, []);	

	
});

test ("test wbobjectmanager - validateFieldsAndSetLabels", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<input id="wbxuri" value="/test">' +
		'<span id="erroruri""></span>' + 
		'</form>';
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager({ fieldsPrefix:'wbx',
									 errorLabelsPrefix: 'error',
									 errorLabelClassName: 'errValidationLabel',
									 errorInputClassName: 'errValidationInput',
									 validationRules: {
										'uri': [ {rule:{startsWith: 'a'}, error:"E1"}, { rule: {contains:'url'}, error: "E2" } , { rule: { rangeLength: { 'min': 20, 'max': 30 } }, error: "E3"} ]
									 }
									});
	var textErrors = { 'E1': 'Uri must start with a.',
					   'E2' : 'Uri must contain "url" word.',
					   'E3' : 'Uri length must be between 20 and 30 characters' };
	$('#wbadduri').wbObjectManager().validateFieldsAndSetLabels( textErrors );
	equal ( $('#erroruri').html(), 'Uri must start with a.');
	equal ( $('#wbxuri').hasClass('errValidationInput'), true);
	equal ( $('#erroruri').hasClass('errValidationLabel'), true);
});
test ("test wbobjectmanager - convertErrors", function () {
	var $fixture = $( "#qunit-fixture" );
	var htmlString =  
		'<form id="wbadduri"> ' + 
		'<input id="wbxuri" value="/test">' +
		'<span id="erroruri""></span>' + 
		'</form>';
	$fixture.append(htmlString);
	$('#wbadduri').wbObjectManager({ fieldsPrefix:'wbx',
									 errorLabelsPrefix: 'error',
									 errorLabelClassName: 'errValidationLabel',
									 errorInputClassName: 'errValidationInput'
									});
	var errors = {'A':'E1', 'B':'E2'};
	var errorsGeneral = {'E1':'Text1', 'E2': 'Text2'};
	var result = $('#wbadduri').wbObjectManager().convertErrors(errors, errorsGeneral);
	deepEqual( result, {'A':'Text1','B':'Text2'});
});
