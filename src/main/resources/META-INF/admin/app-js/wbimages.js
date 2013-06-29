var errorsGeneral = {
	'ERROR_IMAGE_NAME_LENGTH': 'Image name length must be between 1 and 250 characters',
	'ERROR_IMAGE_NAME_BAD_FORMAT': 'Invalid format for image name: allowed characters are 0-9, a-z, A-Z, -, _,. (, is not an allowed character)',
	'ERROR_IMAGE_FILENAME_LENGTH': 'Image file path cannot be empty',
	'ERROR_IMAGE_ENABLED_BAD_FORMAT': 'Invalid image live value'
};

$().ready( function () {
	var wbImageValidations = { 
			name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_IMAGE_NAME_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_IMAGE_NAME_BAD_FORMAT"}],
			filename: [{rule: { rangeLength: { 'min': 1, 'max': 1024 } }, error: "ERROR_IMAGE_FILENAME_LENGTH" }],
			enabled: [{rule: { includedInto: ['0', '1']}, error: "ERROR_IMAGE_ENABLED_BAD_FORMAT" }]
	};
	$('#wbAddImageForm').wbObjectManager( { fieldsPrefix:'wb',
									  errorLabelsPrefix: 'err',
									  errorGeneral:"errgeneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { enabled: 0 },
									  validationRules: wbImageValidations
									});
	$('#wbDeleteImageForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );							

	var displayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="./webimage.html?key=' + encodeURIComponent(record['key']) + '"><i class="icon-eye-open"></i> View </a> | <a href="#" class="wbDeleteImageClass" id="wbDeleteImage_' +encodeURIComponent(record['key']) + '"><i class="icon-trash"></i> Delete </a>'; 
		} else
		if (fieldId=="lastModified") {
			var date = new Date();
			return date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm");
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
					return '<img src="./wbserveimage?size=50&blobKey=' + encodeURIComponent(record['blobKey']) + '">';
				default:
					return '<a href="./wbdownload/{0}?blobKey={1}">{2}</a>'.format(encodeURIComponent(record['fileName']),encodeURIComponent(record["blobKey"]),escapehtml(record['fileName']));				
			}
		}
		
	}
				
	$('#wbImagesTable').wbTable( { columns: [ {display: "Id", fieldId:"key"}, {display: "External key", fieldId: "externalKey"}, {display: "Name", fieldId: "name"},
	                                {display:"Content type", fieldId:"contentType"},
									{display:"Size", fieldId:"size", customHandling: true, customHandler: displayHandler},
									{display:"Last Modified", fieldId:"lastModified", customHandling: true, customHandler: displayHandler},
									{display:"File", fieldId:"blobKey", customHandling: true, customHandler: displayHandler},
									{display: "Operations", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination"
						});

	$('#wbAddImageForm').wbCommunicationManager();
	$('#wbDeleteImageForm').wbCommunicationManager();

	var fSuccessGetUpload = function ( data ) {
		$('#wbAddImageForm')[0].setAttribute('action', data.url);
		$('#wbAddImageForm')[0].setAttribute('method', "post");
		$('#wbAddImageModal').modal('show');			
	}
	var fErrorGetUpload = function (errors, data) {
		alert(data);
	}

	$('#wbAddImageBtn').click( function (e) {
		e.preventDefault();
		$('#wbAddImageForm').wbObjectManager().resetFields();
		$('#wbAddImageForm').wbCommunicationManager().ajax ( { url: "./wbuploaddata",
														 httpOperation:"GET", 
														 payloadData:"",
														 functionSuccess: fSuccessGetUpload,
														 functionError: fErrorGetUpload
														 } );	
	});

	var fSuccessAdd = function ( data ) {
		$('#wbAddImageModal').modal('hide');
		$('#wbImagesTable').wbTable().insertRow(data);			
	}
	var fErrorAdd = function (errors, data) {
		$('#wbImageForm').wbObjectManager().setErrors(errors);
	}

	$('.wbSaveAddImageBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbAddImageForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			$('#wbAddImageForm').submit();
		}
	});

	$(document).on ("click", '.wbDeleteImageClass', function (e) {
		e.preventDefault();
		$('#wbDeleteImageForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDeleteImage_".length);
		var object = $('#wbImagesTable').wbTable().getRowDataWithKey(key);
		$('#wbDeleteImageForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeleteImageModal').modal('show');		
	});

	var fSuccessDelete = function ( data ) {
		$('#wbDeleteImageModal').modal('hide');	
		$('#wbImagesTable').wbTable().deleteRowWithKey(data["key"]);
	}
	var fErrorDelete = function (errors, data) {
		$('#wbDeleteImageForm').wbObjectManager().setErrors(errors);
	}

	$('.webSaveDeleteBtnClass').click( function (e) {
		e.preventDefault();
		var object = $('#wbDeleteImageForm').wbObjectManager().getObjectFromFields();			
		$('#wbDeleteImageForm').wbCommunicationManager().ajax ( { url: "./wbimage/" + encodeURIComponent(object['key']),
														 httpOperation:"DELETE", 
														 payloadData:"",
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete
													} );
		
	});

	var fSuccessGetAll = function (data) {
		$.each(data, function(index, item) {
			$('#wbImagesTable').wbTable().insertRow(item);
		});				

	}
	var fErrorGetAll = function (errors, data) {
	
	}
	var shortType = getURLParameter('type');
	var urlValue = "./wbimage";
	if (shortType && shortType.length) {
		urlValue += "?type={0}".format(encodeURIComponent(shortType));
	}
	$('#wbAddImageForm').wbCommunicationManager().ajax ( { url: urlValue,
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessGetAll,
													 functionError: fErrorGetAll
													} );

});