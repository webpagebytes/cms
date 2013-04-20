var errorsGeneral = {
};

$().ready( function () {
	$('#wbAddImageForm').wbObjectManager( { fieldsPrefix:'wb',
									  errorLabelsPrefix: 'err',
									  errorGeneral:"errgeneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { enabled: 0 },
									  validationRules: {
										'name': { rangeLength: { 'min': 1, 'max': 100 } }
									  }
									});
	$('#wbDeleteImageForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );							

	var displayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="./webimage.html?key=' + escapehtml(record['key']) + '"><i class="icon-pencil"></i> View </a> | <a href="#" class="wbDeleteImageClass" id="wbDeleteImage_' +record['key']+ '"><i class="icon-trash"></i> Delete </a>'; 
		} else
		if (fieldId=="lastModified") {
			var date = new Date();
			return date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		} else
		if (fieldId=="blobKey"){
			return '<img src="./wbserveimage?size=50&blobKey=' + escapehtml(record['blobKey']) + '">';
		}
		
	}
				
	$('#wbImagesTable').wbTable( { columns: [ {display: "Id", fieldId:"key"}, {display: "External key", fieldId: "externalKey"}, {display: "Name", fieldId: "name"},
									{display:"Last Modified", fieldId:"lastModified", customHandling: true, customHandler: displayHandler},
									{display:"Image", fieldId:"blobKey", customHandling: true, customHandler: displayHandler},
									{display: "View/delete", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
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
			var jsonText = JSON.stringify($('#wbAddImageForm').wbObjectManager().getObjectFromFields());
			$('#wbAddImageForm').wbCommunicationManager().ajax ( { url: "./wbimage",
															 httpOperation:"POST", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbAddPageModuleForm').wbObjectManager(),
															 functionSuccess: fSuccessAdd,
															 functionError: fErrorAdd
															 } );
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
		$('#wbDeleteImageForm').wbCommunicationManager().ajax ( { url: "./wbimage/" + escapehtml(object['key']),
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
	
	$('#wbAddImageForm').wbCommunicationManager().ajax ( { url:"./wbimage",
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessGetAll,
													 functionError: fErrorGetAll
													} );

});