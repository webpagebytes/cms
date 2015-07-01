/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
$().ready( function () {
	
	var fSuccessClear = function(data) {
		alert('Clear project performed successful!');
	};
	var fErrorClear = function(errors, data) {
		alert(data);
	};
	$('.wbClearProjectBtn').click( function (e) {
		e.preventDefault();
		$('.wbClearProjectBtn').wbCommunicationManager().ajax ( { url: "./wball",
														 httpOperation:"DELETE", 
														 payloadData:"",
														 functionSuccess: fSuccessClear,
														 functionError: fErrorClear,
														 functionAuth: authHandler
													} );		
	});

	$('#spinnerTable').WBSpinner().hide();			
});