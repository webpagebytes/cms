var errorsGeneral = {
	'ERROR_IMAGE_NAME_LENGTH': 'Image name length must be between 1 and 250 characters',
	'ERROR_IMAGE_FILENAME_LENGTH': 'Image file path cannot be empty'
};

$().ready( function () {

	var wbProjectValidations = { 
		filename: [{rule: { rangeLength: { 'min': 1, 'max': 1024 } }, error: "ERROR_IMAGE_FILENAME_LENGTH" }]
	};
	$('#wbModalRestoreUploadForm').wbObjectManager( { fieldsPrefix:'wb',
								  errorLabelsPrefix: 'err',
								  errorGeneral:"errgeneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  validationRules: wbProjectValidations
								});

	$('.restoreUploadSubmit').click( function (e) {
		e.preventDefault();
		var errors = $('#wbModalRestoreUploadForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			$('#wbModalRestoreUploadForm').submit();
		}
	});

	var date = new Date();
	var link = './wbproject{0}.zip'.format(date.toFormatString(date,'ddmmyyyy_hhmm'));
	$('#backuplink').attr('href', link);
		
	var fSuccessGetUploadData = function (data) {
		$('#wbModalRestoreUploadForm')[0].setAttribute('action', data.url);
		$('#wbModalRestoreUploadForm')[0].setAttribute('method', "post");
		$('#wbModalRestoreUploadForm').wbObjectManager().resetFields();
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