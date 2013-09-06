var errorsGeneral = {
	'ERROR_FILE_NAME_LENGTH': 'File name length must be between 1 and 250 characters',
	'ERROR_FILE_NAME_BAD_FORMAT': 'Invalid format for file name: allowed characters are 0-9, a-z, A-Z, -, _,. (, is not an allowed character)',
	'ERROR_FILE_FILENAME_LENGTH': 'File file path cannot be empty',
	'ERROR_FILE_ENABLED_BAD_FORMAT': 'Invalid file live value'
};

$().ready( function () {
	var wbFileValidations = { 
			name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_FILE_NAME_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_FILE_NAME_BAD_FORMAT"}],
			filename: [{rule: { rangeLength: { 'min': 1, 'max': 1024 } }, error: "ERROR_FILE_FILENAME_LENGTH" }],
			enabled: [{rule: { includedInto: ['0', '1']}, error: "ERROR_FILE_ENABLED_BAD_FORMAT" }]
	};
	$('#wbAddFileForm').wbObjectManager( { fieldsPrefix:'wb',
									  errorLabelsPrefix: 'err',
									  errorGeneral:"errgeneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { enabled: 0 },
									  validationRules: wbFileValidations
									});
	$('#wbDeleteFileForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );							

	var displayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="./webfile.html?key=' + encodeURIComponent(record['key']) + '"><i class="icon-eye-open"></i> View </a> | <a href="#" class="wbDeleteFileClass" id="wbDeleteFile_' +encodeURIComponent(record['key']) + '"><i class="icon-trash"></i> Delete </a>'; 
		} else
		if (fieldId=="lastModified") {
			return escapehtml(Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} else
		if (fieldId=="size") {
			var size = parseInt(record['size']);
			if (size < 1024) {
				return size + ' bytes';
			} else 
			if (size < 1048576) {
				var f = size/1024;
				return (f.toFixed(2)) + ' KB';
			} else {
				var f = size/1048576;
				return (f.toFixed(2)) + ' MB';
			};
			
			return size;
		} else
		if (fieldId=="blobKey"){
			switch (record["shortType"]) {
				case "image":
					return '<img src="./wbservefile?size=50&blobKey=' + encodeURIComponent(record['blobKey']) + '">';
				default:
					return '<a href="./wbdownload/{0}">{1}</a>'.format(encodeURIComponent(record['key']),escapehtml(record['fileName']));				
			}
		}
		
	}
				
	$('#wbFilesTable').wbTable( { columns: [ {display: "External key", fieldId: "externalKey"}, {display: "Name", fieldId: "name"},
	                                {display:"Content type", fieldId:"contentType"},
									{display:"Size", fieldId:"size", customHandling: true, customHandler: displayHandler},
									{display:"Last Modified", fieldId:"lastModified", customHandling: true, customHandler: displayHandler},
									{display:"File", fieldId:"blobKey", customHandling: true, customHandler: displayHandler},
									{display: "Operations", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination"
						});

	$('#wbAddFileForm').wbCommunicationManager();
	$('#wbDeleteFileForm').wbCommunicationManager();

	var fSuccessGetUpload = function ( data ) {
		$('#wbAddFileForm')[0].setAttribute('action', data.url);
		$('#wbAddFileForm')[0].setAttribute('method', "post");
		$('#wbAddFileModal').modal('show');			
	}
	var fErrorGetUpload = function (errors, data) {
		alert(errors);
	}

	$('#wbAddFileBtn').click( function (e) {
		e.preventDefault();
		$('#wbAddFileForm').wbObjectManager().resetFields();
		$('#wbAddFileForm').wbCommunicationManager().ajax ( { url: "./wbuploaddata",
														 httpOperation:"GET", 
														 payloadData:"",
														 functionSuccess: fSuccessGetUpload,
														 functionError: fErrorGetUpload
														 } );	
	});

	var fSuccessAdd = function ( data ) {
		$('#wbAddFileModal').modal('hide');
		$('#wbFilesTable').wbTable().insertRow(data.data);			
	}
	var fErrorAdd = function (errors, data) {
		$('#wbFileForm').wbObjectManager().setErrors(errors);
	}

	$('.wbSaveAddFileBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbAddFileForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			$('#wbAddFileForm').submit();
		}
	});

	$(document).on ("click", '.wbDeleteFileClass', function (e) {
		e.preventDefault();
		$('#wbDeleteFileForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDeleteFile_".length);
		var object = $('#wbFilesTable').wbTable().getRowDataWithKey(key);
		$('#wbDeleteFileForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeleteFileModal').modal('show');		
	});

	var fSuccessDelete = function ( data ) {
		$('#wbDeleteFileModal').modal('hide');	
		$('#wbFilesTable').wbTable().deleteRowWithKey(data.data["key"]);
	}
	var fErrorDelete = function (errors, data) {
		$('#wbDeleteFileForm').wbObjectManager().setErrors(errors);
	}

	$('.webSaveDeleteBtnClass').click( function (e) {
		e.preventDefault();
		var object = $('#wbDeleteFileForm').wbObjectManager().getObjectFromFields();			
		$('#wbDeleteFileForm').wbCommunicationManager().ajax ( { url: "./wbfile/" + encodeURIComponent(object['key']),
														 httpOperation:"DELETE", 
														 payloadData:"",
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete
													} );
		
	});

	var fSuccessGetAll = function (data) {
		$.each(data.data, function(index, item) {
			$('#wbFilesTable').wbTable().insertRow(item);
		});				

	}
	var fErrorGetAll = function (errors, data) {
	
	}
	var shortType = getURLParameter('type');
	var urlValue = "./wbfile";
	if (shortType && shortType.length) {
		urlValue += "?type={0}".format(encodeURIComponent(shortType));
	}
	$('#wbAddFileForm').wbCommunicationManager().ajax ( { url: urlValue,
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessGetAll,
													 functionError: fErrorGetAll
													} );

});