var errorsGeneral = {
};

$().ready( function () {

	var date = new Date();
	var link = './wbproject{0}.zip'.format(date.toFormatString(date,'ddmmyyyy_hhmm'));
	$('#backuplink').attr('href', link);
		
	var fSuccessGetUploadData = function (data) {
		$('#wbModalRestoreUploadForm')[0].setAttribute('action', data.url);
		$('#wbModalRestoreUploadForm')[0].setAttribute('method', "post");
		$('#wbModalRestoreUpload').modal('show');
	};

	var fErrorGetUploadData = function (data) {
		alert(data);
	};
												
											
	$('.restoreBtnClass').click ( function (e) {
		e.preventDefault();
		$('#wbModalRestoreUpload').wbCommunicationManager().ajax ( { url:"./wbuploadimportdata",
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetUploadData,
												 functionError: fErrorGetUploadData
												} );	

		
	});
	
});