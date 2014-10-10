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
														 functionError: fErrorRefresh
													} );		
	});

	$('#spinnerTable').WBSpinner().hide();			
});