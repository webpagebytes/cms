var errorsGeneral = {
};

$().ready( function () {
	
	$('#wbuImageDataUpdateForm').wbObjectManager( { fieldsPrefix:'wbud',
								  errorLabelsPrefix: 'errud',
								  errorGeneral:"errudgeneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  validationRules: {
									'name': { rangeLength: { 'min': 1, 'max': 100 } }
								  }
								});
	$('#wbuImageUploadUpdateForm').wbObjectManager( { fieldsPrefix:'wbuu',
								  errorLabelsPrefix: 'erruu',
								  errorGeneral:"erruugeneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  validationRules: {
									'filename': { rangeLength: { 'min': 1, 'max': 100 } }
								  }
								});

	var displayHandler = function (fieldId, record) {
		if (fieldId == "blobKey") {
			return "<img src='./wbserveimage?blobKey={0}'>".format( escapehtml(record['blobKey']) );
		} else
		if (fieldId == 'lastModified') {
			var date = new Date();
			return date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		} 

		return escapehtml(record[fieldId]);
	}
	
	$('#wbImageView').wbDisplayObject( { fieldsPrefix: 'wbimage', customHandler: displayHandler} );

	var imageKey = getURLParameter('key'); 
	var imageBlobKey = "";
	
	var fSuccessGetImage = function (data) {
		$('#wbImageView').wbDisplayObject().display(data);
		imageBlobKey = data['blobKey'];
	}
	var fErrorGetImage = function (data) {
		alert(data);
	}
	
	$('#wbImageView').wbCommunicationManager().ajax ( { url:"./wbimage/" + imageKey,
											 httpOperation:"GET", 
											 payloadData:"",
											 functionSuccess: fSuccessGetImage,
											 functionError: fErrorGetImage
											} );	
											
	var fSuccessGetServeingUrl = function (data) {
		$('#wbimageblobKey').html('<img src="' + data['url'] + '">');
		$('#servingurl').html('<a target="_new" href="' + data['url'] + '">' + escapehtml(data['url']) + '</a>');
	}
	var fErrorGetServingUrl = function (data) {
		alert(data);
	}
											
	$('.wbGetServingUrlBtnClass').click ( function (e) {
		e.preventDefault();
		var imageSize = parseInt ($('.wbImageSizeInputClass').val());
		var ajaxUrl = "./wbserveimageurl?blobKey=" + imageBlobKey;
		if (imageSize != Number.NaN && imageSize > 0)
		{
			ajaxUrl += ('&size=' + imageSize);
		}
		
		$('#wbImageView').wbCommunicationManager().ajax ( { url:ajaxUrl,
										 httpOperation:"GET", 
										 payloadData:"",
										 functionSuccess: fSuccessGetServeingUrl,
										 functionError: fErrorGetServingUrl
										} );	
	});

	var fSuccessImageUpdate = function (data) {
		$('#wbModalImageDataUpdate').modal('hide');
		window.location.reload();
	};
	
	var fErrorImageUpdate = function (data) {
		alert(data);
	};
	
	$('.wbImageDataSaveBtnClass').click ( function (e) {
		e.preventDefault();
		var errors = $('#wbuImageDataUpdateForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var image = $('#wbuImageDataUpdateForm').wbObjectManager().getObjectFromFields();
			var jsonText = JSON.stringify(image);
			$('#wbImageView').wbCommunicationManager().ajax ( { url: "./wbimage/" + imageKey,
															 httpOperation:"PUT", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbuImageDataUpdateForm').wbObjectManager(),
															 functionSuccess: fSuccessImageUpdate,
															 functionError: fErrorImageUpdate
															 } );
		}
			
	});	
	
	var fSuccessGetImageForUpdate = function (data) {	
		$('#wbuImageDataUpdateForm').wbObjectManager().resetFields();
		$('#wbuImageDataUpdateForm').wbObjectManager().populateFieldsFromObject(data);
		$('#wbModalImageDataUpdate').modal('show');
	}
	
	
	$('.wbUpdateImageDataBtnClass').click ( function (e) {
		e.preventDefault();
		$('#wbImageView').wbCommunicationManager().ajax ( { url:"./wbimage/" + imageKey,
										 httpOperation:"GET", 
										 payloadData:"",
										 functionSuccess: fSuccessGetImageForUpdate,
										 functionError: fErrorGetImage
										} );	

			});

	$('.wbUpdateUploadImageBtnClass').click ( function (e) {
		e.preventDefault();
		$('#wbuImageUploadUpdateForm').wbObjectManager().resetFields();
		$('#wbuImageUploadUpdateForm').wbCommunicationManager().ajax ( { url: "./wbuploaddata",
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetUpload,
												 functionError: fErrorGetUpload
												 } );	
	});
		
	var fSuccessGetUpload = function ( data ) {
		$('#wbuImageUploadUpdateForm')[0].setAttribute('action', data.url);
		$('#wbuImageUploadUpdateForm')[0].setAttribute('method', "post");
		$('#wbuukey').val(imageKey);
		$('#wbModalImageUploadUpdate').modal('show');			
	}
	var fErrorGetUpload = function (errors, data) {
		alert(data);
	}

	
});