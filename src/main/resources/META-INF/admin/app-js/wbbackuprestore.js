/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
var errorsGeneral = {
	'ERROR_PROJECT_FILENAME_LENGTH': 'Upload file path cannot be empty'
};

$().ready( function () {

	var wbProjectValidations = { 
		filename: [{rule: { rangeLength: { 'min': 1, 'max': 1024 } }, error: "ERROR_PROJECT_FILENAME_LENGTH" }]
	};
	$('#wbModalRestoreUploadForm').wbObjectManager( { fieldsPrefix:'wb',
								  errorLabelsPrefix: 'err',
								  errorGeneral:"errgeneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  validationRules: wbProjectValidations
								});

	var fSuccessRestore = function(data) {
		$('#wbModalRestoreUpload').modal('hide');
	}
	
	var fErrorRestore = function(data) {
		alert(data);
	}
	
	$('#wbModalRestoreUploadForm').ajaxForm({ success: fSuccessRestore, error: fErrorRestore });
	$('.restoreUploadSubmit').click( function (e) {
		var errors = $('#wbModalRestoreUploadForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if (! $.isEmptyObject(errors)) {
			e.preventDefault();
		}
	});

	var date = new Date();
	var link = './wbproject{0}.zip'.format(date.toFormatString(date,'ddmmyyyy_hhmm'));
	$('#backuplink').attr('href', link);
					
	$('.restoreBtnClass').click ( function (e) {
		e.preventDefault();
		$('#wbModalRestoreUploadForm').wbObjectManager().resetFields();
		$('#wbModalRestoreUpload').modal('show');
	});	
								
	$('.wbModalRestoreUploadForm').wbCommunicationManager().ajax ( { url: "./ping",
														 httpOperation:"GET", 
														 payloadData:"",													
														 functionAuth: authHandler
													} );
	
	
});