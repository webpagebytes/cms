module('wbtable');
test ("test wbtable - creation", function () {
	var $fixture = $( "#qunit-fixture" );
	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name </td>", fieldId: "name"}, {display:"Age </tr> &amp; \' \"", fieldId:"age"} ],
							 tableBaseClass: "testA-class",
							 paginationBaseClass: "testB-class"
						   });
	
	ok ( $('#idtable').children('table').hasClass('testA-class'));
	ok ( $('#idtable').children('table').hasClass('__wbTblclass'));
	ok ( $('#idtable').children('div').hasClass('__wbPagclass'));
	ok ( $('#idtable').children('div').hasClass('testB-class'));
	
	equal ( $('#idtable').find('table thead tr').children('tr :nth-child(1)').text(), "Id");
	equal ( $('#idtable').find('table thead tr').children('tr :nth-child(2)').text(), "Name </td>");
	equal ( $('#idtable').find('table thead tr').children('tr :nth-child(3)').text(), "Age </tr> &amp; \' \"");
	equal ( $('#idtable').find('table thead tr').children().length, 3);
});

test ("test wbtable - appendRows", function () {
	var $fixture = $( "#qunit-fixture" );
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 itemsPerPage: 10 
						   });
	$('#idtable').wbTable().appendRows ([{id:"1", name: "one \" </td></tr> \'", age: 10}, {id:"2", name: "two", age: 20}]);
	
	equal ( $('#idtable').find('table tbody').children().length, 2);
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "1");
	equal ( $(tr1[1]).text(), "one \" </td></tr> \'");
	equal ( $(tr1[2]).html(), "10");
	
	var tr2 = $($('#idtable table tbody tr')[1]).children();
	equal ( $(tr2[0]).html(), "2");
	equal ( $(tr2[1]).html(), "two");
	equal ( $(tr2[2]).html(), "20");	
	
	// we have a single table page
	equal ( $('#idtable').find('div ul').children().length, 1);
	
});

test ("test wbtable - appendRows-text cut", function () {
	var $fixture = $( "#qunit-fixture" );
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 itemsPerPage: 10,
							 textLengthToCut: 5
						   });
	$('#idtable').wbTable().appendRows ([{id:"1", name: "123456", age: 10}, {id:"2", name: "12345", age: 20}, {id:"3", name: "1234", age: 30}]);
	
	equal ( $('#idtable').find('table tbody').children().length, 3);
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "1");
	equal ( $(tr1[1]).text(), "12345...");
	equal ( $(tr1[2]).html(), "10");
	
	var tr2 = $($('#idtable table tbody tr')[1]).children();
	equal ( $(tr2[0]).html(), "2");
	equal ( $(tr2[1]).html(), "12345");
	equal ( $(tr2[2]).html(), "20");	

	var tr2 = $($('#idtable table tbody tr')[2]).children();
	equal ( $(tr2[0]).html(), "3");
	equal ( $(tr2[1]).html(), "1234");
	equal ( $(tr2[2]).html(), "30");	
	
	// we have a single table page
	equal ( $('#idtable').find('div ul').children().length, 1);
	
});

test ("test wbtable - columns custom handling", function () {
	var $fixture = $( "#qunit-fixture" );
	var handler = function(fieldId, record) {
		if (fieldId=='tel') {
			return "+40" + record[fieldId];
		} 
	};
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);	
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id", customHandling: true}, {display: "Name", fieldId: "name"}, 
							            {display:"Age", fieldId:"age"}, { display:"Tel", fieldId:"tel", customHandling: true, customHandler: handler}],
							 itemsPerPage: 10 
						   });
	$('#idtable').wbTable().appendRows ([{id:"1", name: "one \" </td></tr> \'", age: 10, tel:"1234"}, {id:"2", name: "two", age: 20, tel: "8899"}]);
	
	equal ( $('#idtable').find('table tbody').children().length, 2);
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "1");
	equal ( $(tr1[1]).text(), "one \" </td></tr> \'");
	equal ( $(tr1[2]).html(), "10");
	equal ( $(tr1[3]).html(), "+401234");
	
	var tr2 = $($('#idtable table tbody tr')[1]).children();
	equal ( $(tr2[0]).html(), "2");
	equal ( $(tr2[1]).html(), "two");
	equal ( $(tr2[2]).html(), "20");	
	equal ( $(tr2[3]).html(), "+408899");
	// we have a single table page
	equal ( $('#idtable').find('div ul').children().length, 1);
	
});

test ("test wbtable - columns custom handling - text cut", function () {
	var $fixture = $( "#qunit-fixture" );
	var handler = function(fieldId, record) {
		if (fieldId=='name') {
			return record[fieldId].toUpperCase();
		} 
	};
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);	
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name", customHandling: true, customHandler: handler}, 
							            {display:"Age", fieldId:"age"}, { display:"Tel", fieldId:"tel"}],
							 itemsPerPage: 10,
							 textLengthToCut:5
						   });
	$('#idtable').wbTable().appendRows ([{id:"1", name: "a123456", age: 10, tel:"12345"}, {id:"2", name: "a1234", age: 20, tel: "88998899"}, {id:"3", name: "1234", age: 20, tel: "1111"}]);
	
	equal ( $('#idtable').find('table tbody').children().length, 3);
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "1");
	equal ( $(tr1[1]).text(), "A123456");
	equal ( $(tr1[2]).html(), "10");
	equal ( $(tr1[3]).html(), "12345");
	
	var tr2 = $($('#idtable table tbody tr')[1]).children();
	equal ( $(tr2[0]).html(), "2");
	equal ( $(tr2[1]).html(), "A1234");
	equal ( $(tr2[2]).html(), "20");	
	equal ( $(tr2[3]).html(), "88998...");
	
	var tr2 = $($('#idtable table tbody tr')[2]).children();
	equal ( $(tr2[0]).html(), "3");
	equal ( $(tr2[1]).html(), "1234");
	equal ( $(tr2[2]).html(), "20");	
	equal ( $(tr2[3]).html(), "1111");
	
	// we have a single table page
	equal ( $('#idtable').find('div ul').children().length, 1);
	
});

test ("test wbtable - multiple pages", function () {
	var $fixture = $( "#qunit-fixture" );
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 itemsPerPage: 10 
						   });
	for( var i = 0; i< 30; i++) {
		$('#idtable').wbTable().appendRows ([{id:"1", name: "one \" </td></tr> \'", age: 10}]);
	}
		
	// we have 3 table pages
	equal ( $('#idtable').find('div ul').children().length, 3);
	
	ok ($('#idtable').find('div ul li').hasClass('active'));
	
});


test ("test wbtable - setRows", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class"});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);
	
	equal ( $('#idtable').find('table tbody').children().length, 2);
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "1");
	equal ( $(tr1[1]).html(), "one");
	equal ( $(tr1[2]).html(), "10");
	
	var tr2 = $($('#idtable table tbody tr')[1]).children();
	equal ( $(tr2[0]).html(), "2");
	equal ( $(tr2[1]).html(), "two");
	equal ( $(tr2[2]).html(), "20");		
});

test ("test wbtable - setRows + appendRows", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class"});
	$('#idtable').wbTable().appendRows ([{id:"0", name: "zero", age: 0}]);

	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);
	$('#idtable').wbTable().appendRows ([{id:"3", name: "three", age: 30}]);
	equal ( $('#idtable').find('table tbody').children().length, 3);
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "1");
	equal ( $(tr1[1]).html(), "one");
	equal ( $(tr1[2]).html(), "10");
	
	var tr2 = $($('#idtable table tbody tr')[1]).children();
	equal ( $(tr2[0]).html(), "2");
	equal ( $(tr2[1]).html(), "two");
	equal ( $(tr2[2]).html(), "20");		

	var tr3 = $($('#idtable table tbody tr')[2]).children();
	equal ( $(tr3[0]).html(), "3");
	equal ( $(tr3[1]).html(), "three");
	equal ( $(tr3[2]).html(), "30");
});

test ("test wbtable - appendRows with index", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class"});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);
	$('#idtable').wbTable().appendRows ([{id:"3", name: "three", age: 30}], 1);
	equal ( $('#idtable').find('table tbody').children().length, 3);
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "1");
	equal ( $(tr1[1]).html(), "one");
	equal ( $(tr1[2]).html(), "10");
	
	var tr2 = $($('#idtable table tbody tr')[1]).children();
	equal ( $(tr2[0]).html(), "3");
	equal ( $(tr2[1]).html(), "three");
	equal ( $(tr2[2]).html(), "30");		

	var tr3 = $($('#idtable table tbody tr')[2]).children();
	equal ( $(tr3[0]).html(), "2");
	equal ( $(tr3[1]).html(), "two");
	equal ( $(tr3[2]).html(), "20");
});

test ("test wbtable - clearRows", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class"});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);
	$('#idtable').wbTable().clearRows ();
	equal ( $('#idtable').find('table tbody').children().length, 0);
});

test ("test wbtable - updateRow", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class"});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);
	$('#idtable').wbTable().updateRow ({id:"1_", name: "one_", age: 100}, 0);
	$('#idtable').wbTable().updateRow ({id:"2_", name: "two_", age: 200}, 1);	
	equal ( $('#idtable').find('table tbody').children().length, 2);
	
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "1_");
	equal ( $(tr1[1]).html(), "one_");
	equal ( $(tr1[2]).html(), "100");
	
	var tr2 = $($('#idtable table tbody tr')[1]).children();
	equal ( $(tr2[0]).html(), "2_");
	equal ( $(tr2[1]).html(), "two_");
	equal ( $(tr2[2]).html(), "200");			
});

test ("test wbtable - insertRow", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class"});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"3", name: "three", age:30}]);
	$('#idtable').wbTable().insertRow ({id:"2", name: "two", age: 20}, 1);
	equal ( $('#idtable').find('table tbody').children().length, 3);
	
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "1");
	equal ( $(tr1[1]).html(), "one");
	equal ( $(tr1[2]).html(), "10");
	
	var tr2 = $($('#idtable table tbody tr')[1]).children();
	equal ( $(tr2[0]).html(), "2");
	equal ( $(tr2[1]).html(), "two");
	equal ( $(tr2[2]).html(), "20");			

	var tr3 = $($('#idtable table tbody tr')[2]).children();
	equal ( $(tr3[0]).html(), "3");
	equal ( $(tr3[1]).html(), "three");
	equal ( $(tr3[2]).html(), "30");			
});

test ("test wbtable - deleteRow", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class"});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"3", name: "three", age:30}]);
	$('#idtable').wbTable().deleteRow (0);
	equal ( $('#idtable').find('table tbody').children().length, 1);
	
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "3");
	equal ( $(tr1[1]).html(), "three");
	equal ( $(tr1[2]).html(), "30");			
});

test ("test wbtable - getRowData", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class"});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);	
	var data1 = $('#idtable').wbTable().getRowData(0);
	var data2 = $('#idtable').wbTable().getRowData(1);	
	deepEqual ( data1, {id:"1", name: "one", age:10});
	deepEqual ( data2, {id:"2", name: "two", age:20});				
});

test ("test wbtable - getRowData + set, append, delete, insert, update", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class"});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);	
	$('#idtable').wbTable().appendRows ([{id:"0", name: "zero", age: 0}], 0);
	$('#idtable').wbTable().insertRow ({id:"3", name: "three", age: 30});
	$('#idtable').wbTable().deleteRow (1);
	$('#idtable').wbTable().updateRow ({id:"2_", name: "two_", age: 200},1);
	
	equal ( $('#idtable').find('table tbody').children().length, 3);
	
	var data1 = $('#idtable').wbTable().getRowData(0);
	var data2 = $('#idtable').wbTable().getRowData(1);	
	var data3 = $('#idtable').wbTable().getRowData(2);	
	
	deepEqual ( data1, {id:"0", name: "zero", age: 0});
	deepEqual ( data2, {id:"2_", name: "two_", age:200});
	deepEqual ( data3, {id:"3", name: "three", age: 30});
	
});

test ("test wbtable - getAllRowsData", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class"});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);		
	var data = $('#idtable').wbTable().getAllRowsData();	
	deepEqual ( data, [{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);	
});

test ("test wbtable - findIndexWithKey", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class",
							 keyName: "id"
						});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}, {id:"3", name: "three", age: 30}, {id:"four", name: "four", age: 40}]);		
	var index1 = $('#idtable').wbTable().findIndexWithKey("1");	
	equal ( index1, 0);
	var index2 = $('#idtable').wbTable().findIndexWithKey("2");	
	equal ( index2, 1);
	var index3 = $('#idtable').wbTable().findIndexWithKey(3);	
	equal ( index3, 2);
	var index4 = $('#idtable').wbTable().findIndexWithKey("four");	
	equal ( index4, 3);	
});

test ("test wbtable - updateRowWithKey", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class",
							 keyName: "id"
						});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);
	$('#idtable').wbTable().updateRowWithKey ({id:"1_", name: "one_", age: 100}, "1");
	$('#idtable').wbTable().updateRowWithKey ({id:"2_", name: "two_", age: 200}, 2);	
	equal ( $('#idtable').find('table tbody').children().length, 2);
	
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "1_");
	equal ( $(tr1[1]).html(), "one_");
	equal ( $(tr1[2]).html(), "100");
	
	var tr2 = $($('#idtable table tbody tr')[1]).children();
	equal ( $(tr2[0]).html(), "2_");
	equal ( $(tr2[1]).html(), "two_");
	equal ( $(tr2[2]).html(), "200");			
});

test ("test wbtable - deleteRowWithKey", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class",
							 keyName: "id"
						});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"3", name: "three", age:30}]);
	$('#idtable').wbTable().deleteRowWithKey ("1");
	equal ( $('#idtable').find('table tbody').children().length, 1);
	
	var tr1 = $($('#idtable table tbody tr')[0]).children();
	equal ( $(tr1[0]).html(), "3");
	equal ( $(tr1[1]).html(), "three");
	equal ( $(tr1[2]).html(), "30");			
});

test ("test wbtable - getRowDataWithKey", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class",
							 keyName: "id"
						});
	$('#idtable').wbTable().setRows ([{id:"1", name: "one", age: 10}, {id:"2", name: "two", age:20}]);	
	var data1 = $('#idtable').wbTable().getRowDataWithKey(1);
	var data2 = $('#idtable').wbTable().getRowDataWithKey("2");	
	deepEqual ( data1, {id:"1", name: "one", age:10});
	deepEqual ( data2, {id:"2", name: "two", age:20});				
});

test ("test wbtable - notifier set, append, delete, insert, update", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	var operations = [];
	var listenerData = 23;
	var listener = function (operation, item, keyName, data) {
		var obj = [operation, item, keyName, data];
		operations.push(obj);
	};	
	
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class",
							 keyName: "id"});
	$('#idtable').wbTable().addNotifier( listener, listenerData);
	var o1 = {id:"1", name: "one", age: 10};
	var o2 = {id:"2", name: "two", age:20};
	var o2_ = {id:"2_", name: "two_", age:200};
	var o0 = {id:"0", name: "zero", age: 0};
	var o3 = {id:"3", name: "three", age: 30};
	$('#idtable').wbTable().setRows ([o1, o2]);	
	$('#idtable').wbTable().appendRows ([o0], 0);
	$('#idtable').wbTable().insertRow (o3);
	$('#idtable').wbTable().deleteRow (1);
	$('#idtable').wbTable().updateRow (o2,1);
	
	equal ( operations.length,  7);
	
	var expected = [ ['deleteAll', undefined, undefined, listenerData], ['insert', o1, 'id', listenerData], ['insert', o2, 'id', listenerData], ['insert', o0, 'id', listenerData], ['insert', o3, 'id', listenerData], ['delete', o1, 'id', listenerData], ['update', o2, 'id', listenerData]	]
	
	deepEqual(operations, expected);
});

test ("test wbtable - two notifiers set, append, delete, insert, update", function () {
	var $fixture = $( "#qunit-fixture" );	
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	var operations1 = [];
	var listenerData1 = 23;
	var listener1 = function (operation, item, keyName, data) {
		var obj = [operation, item, keyName, data];
		operations1.push(obj);
	};	

	var operations2 = [];
	var listenerData2 = 24;
	var listener2 = function (operation, item, keyName, data) {
		var obj = [operation, item, keyName, data];
		operations2.push(obj);
	};	
	
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 baseClass: "test-class",
							 keyName: "id"});
	$('#idtable').wbTable().addNotifier( listener1, listenerData1);
	$('#idtable').wbTable().addNotifier( listener2, listenerData2);
	
	var o1 = {id:"1", name: "one", age: 10};
	var o2 = {id:"2", name: "two", age:20};
	var o2_ = {id:"2_", name: "two_", age:200};
	var o0 = {id:"0", name: "zero", age: 0};
	var o3 = {id:"3", name: "three", age: 30};
	$('#idtable').wbTable().setRows ([o1, o2]);	
	$('#idtable').wbTable().appendRows ([o0], 0);
	$('#idtable').wbTable().insertRow (o3);
	$('#idtable').wbTable().deleteRow (1);
	$('#idtable').wbTable().updateRow (o2,1);
	
	equal ( operations1.length,  7);
	equal ( operations2.length,  7);
	
	var expected1 = [ ['deleteAll', undefined, undefined, listenerData1], ['insert', o1, 'id', listenerData1], ['insert', o2, 'id', listenerData1], ['insert', o0, 'id', listenerData1], ['insert', o3, 'id', listenerData1], ['delete', o1, 'id', listenerData1], ['update', o2, 'id', listenerData1]	]	
	deepEqual(operations1, expected1);
	
	var expected2 = [ ['deleteAll', undefined, undefined, listenerData2], ['insert', o1, 'id', listenerData2], ['insert', o2, 'id', listenerData2], ['insert', o0, 'id', listenerData2], ['insert', o3, 'id', listenerData2], ['delete', o1, 'id', listenerData2], ['update', o2, 'id', listenerData2]	]	
	deepEqual(operations2, expected2);

});


test ("test wbtable - multiple pages test rows", function () {
	var $fixture = $( "#qunit-fixture" );
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 itemsPerPage: 8 
						   });
	for( var i = 0; i< 30; i++) {
		$('#idtable').wbTable().appendRows ([{id:i, name: "item" + i, age: i}]);
	}
		
	// we have 4 table pages
	equal ( $('#idtable').find('div ul').children().length, 4);
	// the first page is active
	ok ($('#idtable').find('div ul :nth-child(1)').hasClass('active'));
	ok (! $('#idtable').find('div ul :nth-child(2)').hasClass('active'));
	ok (! $('#idtable').find('div ul :nth-child(3)').hasClass('active'));
	ok (! $('#idtable').find('div ul :nth-child(4)').hasClass('active'));
	
	
	// we have only 8 rows on the table 
	equal ( $('#idtable').find('table tbody').children().length, 8);
	for(var i = 0; i< 8; i++) {
		var tr = $($('#idtable table tbody tr')[i]).children();
		equal ( $(tr[0]).html(), "" + i);
		equal ( $(tr[1]).text(), "item" + i);
		equal ( $(tr[2]).html(), "" + i);		
	}

});

test ("test wbtable - change page", function () {
	var $fixture = $( "#qunit-fixture" );
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 itemsPerPage: 8 
						   });
	for( var i = 0; i< 30; i++) {
		$('#idtable').wbTable().appendRows ([{id:i, name: "item" + i, age: i}]);
	}
		
	$('#idtable').wbTable().changePage(3);
	
	// we have only 6 rows on the table 30 = 3*8 + 6 
	equal ( $('#idtable').find('table tbody').children().length, 6);
	var startI = 24;
	for(var i = 0; i< 6; i++) {
		var tr = $($('#idtable table tbody tr')[i]).children();
		equal ( $(tr[0]).html(), "" + (i + startI));
		equal ( $(tr[1]).text(), "item" + (i + startI));
		equal ( $(tr[2]).html(), "" + (i + startI));		
	}
	// page 4 is the active one
	ok ($('#idtable').find('div ul :nth-child(4)').hasClass('active'));	
});

test ("test wbtable - deleteRecords:before with multiple pages", function () {
	var $fixture = $( "#qunit-fixture" );
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 itemsPerPage: 8 
						   });
	for( var i = 0; i< 30; i++) {
		$('#idtable').wbTable().appendRows ([{id:i, name: "item" + i, age: i}]);
	}		
	$('#idtable').wbTable().changePage(1);
	$('#idtable').wbTable().deleteRow(0);
	// since we delete the first record the records on page 2 will be shifted + 1 
	// we have only 8 rows on the table  
	equal ( $('#idtable').find('table tbody').children().length, 8);
	var startI = 9;
	for(var i = 0; i< 8; i++) {
		var tr = $($('#idtable table tbody tr')[i]).children();
		equal ( $(tr[0]).html(), "" + (i + startI));
		equal ( $(tr[1]).text(), "item" + (i + startI));
		equal ( $(tr[2]).html(), "" + (i + startI));		
	}
	// page 2 is the active one
	ok ($('#idtable').find('div ul :nth-child(2)').hasClass('active'));	
});

test ("test wbtable - deleteRecords:after with multiple pages", function () {
	var $fixture = $( "#qunit-fixture" );
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 itemsPerPage: 8 
						   });
	for( var i = 0; i< 30; i++) {
		$('#idtable').wbTable().appendRows ([{id:i, name: "item" + i, age: i}]);
	}		
	$('#idtable').wbTable().changePage(1);
	$('#idtable').wbTable().deleteRow(29);
	// since we delete the last record the records on page 2 will not be shifted 
	// we have only 8 rows on the table  
	equal ( $('#idtable').find('table tbody').children().length, 8);
	var startI = 8;
	for(var i = 0; i< 8; i++) {
		var tr = $($('#idtable table tbody tr')[i]).children();
		equal ( $(tr[0]).html(), "" + (i + startI));
		equal ( $(tr[1]).text(), "item" + (i + startI));
		equal ( $(tr[2]).html(), "" + (i + startI));		
	}
	// page 2 is the active one
	ok ($('#idtable').find('div ul :nth-child(2)').hasClass('active'));	
});

test ("test wbtable - insertRecords:after with multiple pages", function () {
	var $fixture = $( "#qunit-fixture" );
	var htmlString =  
		'<div id="idtable"> </div>';
	$fixture.append(htmlString);
	$('#idtable').wbTable( { columns: [ {display: "Id", fieldId:"id"}, {display: "Name", fieldId: "name"}, {display:"Age", fieldId:"age"} ],
							 itemsPerPage: 8 
						   });
	for( var i = 0; i< 30; i++) {
		$('#idtable').wbTable().appendRows ([{id:i, name: "item" + i, age: i}]);
	}		
	$('#idtable').wbTable().changePage(1);
	$('#idtable').wbTable().insertRow({id:100, name: "item100", age: 100});
	// since we inserted after last record the records on page 2 will not be shifted 
	// we have only 8 rows on the table  
	equal ( $('#idtable').find('table tbody').children().length, 8);
	var startI = 8;
	for(var i = 0; i< 8; i++) {
		var tr = $($('#idtable table tbody tr')[i]).children();
		equal ( $(tr[0]).html(), "" + (i + startI));
		equal ( $(tr[1]).text(), "item" + (i + startI));
		equal ( $(tr[2]).html(), "" + (i + startI));		
	}
	// page 2 is the active one
	ok ($('#idtable').find('div ul :nth-child(2)').hasClass('active'));	
});
