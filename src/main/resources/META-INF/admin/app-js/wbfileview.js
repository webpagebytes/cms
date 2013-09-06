var errorsGeneral = {
	'ERROR_FILE_NAME_LENGTH': 'File name length must be between 1 and 250 characters',
	'ERROR_FILE_NAME_BAD_FORMAT': 'Invalid format for file name: allowed characters are 0-9, a-z, A-Z, -, _,. (, is not an allowed character)',
	'ERROR_FILE_FILENAME_LENGTH': 'File file path cannot be empty',
	'ERROR_ADJUSTED_CONTENT_TYPE_LENGTH': 'Content type length must be between 1 and 30 characters',
	'ERROR_ADJUSTED_CONTENT_TYPE_BAD_FORMAT': 'Invalid format for content type'
};

$().ready( function () {
	var wbFileValidations = { 
		name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_FILE_NAME_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_FILE_NAME_BAD_FORMAT"}],
		filename: [{rule: { rangeLength: { 'min': 1, 'max': 1024 } }, error: "ERROR_FILE_FILENAME_LENGTH" }],
		adjustedContentType: [{rule: { rangeLength: { 'min': 1, 'max': 30 } }, error: "ERROR_ADJUSTED_CONTENT_TYPE_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.-//]*$", modifiers:"gi"}}, error:"ERROR_ADJUSTED_CONTENT_TYPE_BAD_FORMAT"}],
	
	};

	$('#wbuFileDataUpdateForm').wbObjectManager( { fieldsPrefix:'wbud',
								  errorLabelsPrefix: 'errud',
								  errorGeneral:"errudgeneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  validationRules: wbFileValidations
								});
	$('#wbuFileUploadUpdateForm').wbObjectManager( { fieldsPrefix:'wbuu',
								  errorLabelsPrefix: 'erruu',
								  errorGeneral:"erruugeneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  validationRules: wbFileValidations
								});
	var swfzc = getAdminPath() + '/zeroclipboard/ZeroClipboard.swf';
	ZeroClipboard.setDefaults( { moviePath: swfzc } );
	var zcButtons = $.find('.btn-clipboard');
	$.each (zcButtons, function (index, elem) {
		var zc = new ZeroClipboard(elem);
	});

	var displayHandler = function (fieldId, record) {
		if (fieldId == 'shortType') {
			var shortType = record['shortType'];
			if (shortType == 'video') {
				$('#wbfileshortType').attr('href','./webfiles.html?type=video');
				return "Files (Video)";
			} else
			if (shortType == 'image') {
				$('#wbfileshortType').attr('href','./webfiles.html?type=image');
				return "Files (Images)";
			} else
			if (shortType == 'audio') {
				$('#wbfileshortType').attr('href','./webfiles.html?type=audio');
				return "Files (Audio)";
			} else
			if (shortType == 'application') {
				$('#wbfileshortType').attr('href','./webfiles.html?type=application');
				return "Files (Applications)";
			} else
			$('#wbfileshortType').attr('href','./webfiles.html');
			return "Files";
		} else
		if (fieldId == 'lastModified') {
			return escapehtml( "Last modified: " + Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		}
		return escapehtml(record[fieldId]);
	}
	
	$('#wbFileView').wbDisplayObject( { fieldsPrefix: 'wbfile', customHandler: displayHandler} );

	var fileKey = getURLParameter('key'); 
	var fileBlobKey = "";
	
	var fSuccessGetFile = function (payload) {
		var data = payload.data;
		$('#wbFileView').wbDisplayObject().display(data);
		$('.wbDownloadFileDataBtnClass').attr('href', './wbdownload/{0}'.format(encodeURIComponent(data['key'])));
		
		switch (data['shortType']) {
			case "image":
				$('.wbImageContentType').removeClass('wbhidden');
				fileBlobKey = data['blobKey'];
				getServingUrl(0);
				break;
			case 'video':
				var videoHtml = "<video id='idvideocontent'><source type='{0}' src='./wbresource/{1}' /></video>".format(escapehtml(data['contentType']), encodeURI(data['key']));
				$('.wbVideoContentType').removeClass('wbhidden');				
				$("#wbvideocontent").html(videoHtml);
				
				var player = new MediaElementPlayer('#idvideocontent');
				player.load();
				break;
			case 'audio':
				var audioHtml = "<audio id='idaudiocontent' controls> <source type='{0}' src='./wbresource/{1}'> Your browser does not support the audio element. </audio> ".format(escapehtml(data['contentType']), encodeURI(data['key']));
				$('.wbAudioContentType').removeClass('wbhidden');
				$('.wbaudiocontent').html(audioHtml);
				var player = new MediaElementPlayer('#idaudiocontent');
				player.load();
				
				break;
			case 'application':
				$('.wbApplicationContentType').removeClass('wbhidden');
				$('.wbapplicationcontent').html("");
				break;
		}
		
	}
	var fErrorGetFile = function (errors, data) {
		alert(data);
	}
	
	$('#wbFileView').wbCommunicationManager().ajax ( { url:"./wbfile/" + encodeURIComponent(fileKey),
											 httpOperation:"GET", 
											 payloadData:"",
											 functionSuccess: fSuccessGetFile,
											 functionError: fErrorGetFile
											} );	
											
	var fSuccessGetServeingUrl = function (data, clientDataValue) {
		$('#wbfileblobKey').html('<img src="' + encodeURI(data['url']) + '">');
		if (clientDataValue == 0) {
			$('.servingurl').html('<a target="_new" href="' + encodeURI(data['url']) + '">' + escapehtml(data['url']) + '</a>');
			$('.servingresizeurl').html('');
		} else {
			$('.servingresizeurl').html('<a target="_new" href="' + encodeURI(data['url']) + '">' + escapehtml(data['url']) + '</a>');
		}
	}
	var fErrorGetServingUrl = function (errors, data) {
		alert(data);
	};
											
	var getServingUrl =  function (imageSize) {
		var ajaxUrl = "./wbservefileurl?blobKey=" + encodeURIComponent(fileBlobKey);
		var clientDataValue = 0;
		if (imageSize != Number.NaN && imageSize > 0) {
			ajaxUrl += ('&size=' + encodeURIComponent(imageSize));
			clientDataValue = imageSize;
		}
		$('#wbFileView').wbCommunicationManager().ajax ( { url:ajaxUrl,
										 httpOperation:"GET", 
										 payloadData:"",
										 functionSuccess: fSuccessGetServeingUrl,
										 functionError: fErrorGetServingUrl,
										 clientData: clientDataValue
										} );	
	};
	
	var fSuccessFileUpdate = function (data) {
		$('#wbModalFileDataUpdate').modal('hide');
		window.location.reload();
	};
	
	var fErrorFileUpdate = function (errors, data) {
		alert(data);
	};
	
	$('.wbGetServingUrlBtnClass').click ( function (e) {
		e.preventDefault();
		var size = $('.wbImageSizeInputClass').val();
		size = parseInt(size);
		if (size != Number.NaN && size>0){
			getServingUrl(size);
		} else {
			$('.wbImageSizeInputClass').val("");
		}
		
	});
	
	$('.wbFileDataSaveBtnClass').click ( function (e) {
		e.preventDefault();
		var errors = $('#wbuFileDataUpdateForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var file = $('#wbuFileDataUpdateForm').wbObjectManager().getObjectFromFields();
			var jsonText = JSON.stringify(file);
			$('#wbFileView').wbCommunicationManager().ajax ( { url: "./wbfile/" + encodeURIComponent(fileKey),
															 httpOperation:"PUT", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbuFileDataUpdateForm').wbObjectManager(),
															 functionSuccess: fSuccessFileUpdate,
															 functionError: fErrorFileUpdate
															 } );
		}
			
	});	
	
	var fSuccessGetFileForUpdate = function (data) {	
		$('#wbuFileDataUpdateForm').wbObjectManager().resetFields();
		$('#wbuFileDataUpdateForm').wbObjectManager().populateFieldsFromObject(data.data);
		$('#wbModalFileDataUpdate').modal('show');
	}
	
	
	$('.wbUpdateFileDataBtnClass').click ( function (e) {
		e.preventDefault();
		$('#wbFileView').wbCommunicationManager().ajax ( { url:"./wbfile/" + encodeURIComponent(fileKey),
										 httpOperation:"GET", 
										 payloadData:"",
										 functionSuccess: fSuccessGetFileForUpdate,
										 functionError: fErrorGetFile
										} );	

			});

	$('.wbUpdateUploadFileBtnClass').click ( function (e) {
		e.preventDefault();
		$('#wbuFileUploadUpdateForm').wbObjectManager().resetFields();
		$('#wbuFileUploadUpdateForm').wbCommunicationManager().ajax ( { url: "./wbuploaddata",
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetUpload,
												 functionError: fErrorGetUpload
												 } );	
	});
		
	var fSuccessGetUpload = function ( data ) {
		$('#wbuFileUploadUpdateForm')[0].setAttribute('action', data.url);
		$('#wbuFileUploadUpdateForm')[0].setAttribute('method', "post");
		$('#wbuukey').val(fileKey);
		$('#wbModalFileUploadUpdate').modal('show');			
	}
	var fErrorGetUpload = function (errors, data) {
		alert(data);
	}

	$('.fileUploadUpdateSave').click( function (e) {
		e.preventDefault();
		var errors = $('#wbuFileUploadUpdateForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			$('#wbuFileUploadUpdateForm').submit();
		}
	});

	
});