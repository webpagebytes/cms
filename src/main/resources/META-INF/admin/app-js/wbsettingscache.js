/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
$().ready( function () {
	
	var fSuccessRefresh = function(data) {
		alert('Refresh ok!');
	};
	var fErrorRefresh = function(errors, data) {
		alert(data);
	};
	$('.wbResetCacheBtn').click( function (e) {
		e.preventDefault();
		$('.wbResetCacheBtn').wbCommunicationManager().ajax ( { url: "./wbrefreshResources",
														 httpOperation:"POST", 
														 payloadData:"",
														 functionSuccess: fSuccessRefresh,
														 functionError: fErrorRefresh,
														 functionAuth: authHandler
													} );		
	});
		
	$('.wbClearProjectBtn').wbCommunicationManager().ajax ( { url: "./ping",
														 httpOperation:"GET", 
														 payloadData:"",													
														 functionAuth: authHandler
													} );
	$('#spinnerTable').WBSpinner().hide();			
});