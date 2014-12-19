/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
var errorsGeneral = {
	'ERROR_FILE_NAME_LENGTH': 'File name length must be between 1 and 250 characters',
	'ERROR_FILE_FILENAME_LENGTH': 'File file path cannot be empty',
	'ERROR_ADJUSTED_CONTENT_TYPE_LENGTH': 'Content type length must be between 1 and 30 characters',
	'ERROR_ADJUSTED_CONTENT_TYPE_BAD_FORMAT': 'Invalid format for content type'
};

$().ready( function () {
	var wbFileValidations = { 
		name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_FILE_NAME_LENGTH" }],
		filename: [{rule: { rangeLength: { 'min': 1, 'max': 1024 } }, error: "ERROR_FILE_FILENAME_LENGTH" }],
		adjustedContentType: [{rule: { rangeLength: { 'min': 1, 'max': 30 } }, error: "ERROR_ADJUSTED_CONTENT_TYPE_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_//.-]*$", modifiers:"gi"}}, error:"ERROR_ADJUSTED_CONTENT_TYPE_BAD_FORMAT"}],
	
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
	$('.btn-clipboard').WBCopyClipboardButoon({buttonHtml:"<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copy to clipboard</div>", basePath: getAdminPath(), selector: '.btn-clipboard'});
	$('.btn-clipboard').WBCopyClipboardButoon().on("aftercopy", function (e) {
		$('.btn-clipboard').WBCopyClipboardButoon().reset();
		$(e.target).html("<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copied!</div>");
	});

	$('#wbudadjustedContentType').typeahead({ items: 4,
											  source:[ "image/jpeg", "image/png", "image/bmp", "image/gif", "image/jpg", "image/tiff", 
	                                                   "video/mpeg", "video/quicktime",
	                                                   "audio/basic", "audio/mpeg",
	                                                   "application/pdf", "application/octet-stream", "application/msword", "application/vnd.ms-excel", "application/vnd.ms-powerpoint",
	                                                   "application/x-gzip", "application/x-gtar", "application/zip",
	                                                   "text/css", "text/plain", "text/html", "text/javascript", "application/x-javascript", "application/json"
	                                                   ]}); 
	
	var fileDetailsHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			return escapehtml( Date.toFormatString(record['lastModified'], "today|dd/mm/yyyy hh:mm"));
		};
		if (fieldId == 'name') {
			return record[fieldId];
		}
		return escapehtml(record[fieldId]);	
	}
	
	var displayHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			return escapehtml( "Last modified: " + Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} else
		if (fieldId == 'lastModified_') {
			return escapehtml( Date.toFormatString(record['lastModified'], "today|dd/mm/yyyy hh:mm"));
		}

		return escapehtml(record[fieldId]);
	}
	
	$('#wbFileView').wbDisplayObject( { fieldsPrefix: 'wbfile', customHandler: displayHandler} );
	$('#collapseFileDetails').wbDisplayObject( { fieldsPrefix: 'wbfile', customHandler: fileDetailsHandler} );
	
	var filesDisplayHandler = function (fieldId, record) {
		if (fieldId=="uri") {
			var link = "./weburiedit.html?extKey={0}".format(encodeURIComponent(record['externalKey']));
			return '<a href="{0}"> {1} </a>'.format(link, escapehtml(record['uri'])); 
		} 
	}

	$('#wbUrlsTable').wbSimpleTable( { columns: [{display: "Site urls linked to this file", fieldId:"uri", customHandler: filesDisplayHandler}],
		 keyName: "privkey",
		 tableBaseClass: "table table-stripped table-bordered table-color-header",
		 paginationBaseClass: "pagination",
		 noLinesContent: "<tr> <td colspan='1'>There are no site urls serving this file. </td></tr>"
		});

	var fSuccessUploadFile = function(data) {
		$('#wbModalFileUploadUpdate').modal('hide');
		window.location.reload();
	}
	
	var fErrorUploadFile = function(data) {
		alert(data);
	}
	
	$('#wbuFileUploadUpdateForm').ajaxForm({ success: fSuccessUploadFile, error: fErrorUploadFile });

	var fileKey = getURLParameter('privkey'); 
	var fileExternalKey = getURLParameter('extKey');
	
	var fSuccessGetFile = function (payload) {
		var data = payload.data;
		fileKey = data["privkey"];
		$("#wbuFileUploadUpdateForm").attr("action", "./wbfileupload/{0}".format(encodeURIComponent(fileKey)));		
		$('#wbFileView').wbDisplayObject().display(data);
		$('#collapseFileDetails').wbDisplayObject().display(data);
		$('.wbDownloadFileDataBtnClass').attr('href', './wbdownload/{0}'.format(encodeURIComponent(data['privkey'])));
		$('#wbUrlsTable').wbSimpleTable().setRows(payload.additional_data.uri_links);
		$('#spinnerTable').WBSpinner().hide();
		var contentType = data["adjustedContentType"] || "";
		if (contentType.toLowerCase().startsWith("image")) {
			var imgHtml = "<img src='{0}'>".format(data['publicUrl']);
			$('.wbimagecontent').html(imgHtml);			
		}
		if (contentType.toLowerCase().startsWith("video")) {
			var videoHtml = "<video id='idvideocontent'><source type='{0}' src='./wbresource/{1}' /></video>".format(escapehtml(data['contentType']), encodeURI(data['privkey']));
			$(".wbvideocontent").html(videoHtml);
			var player = new MediaElementPlayer('#idvideocontent');
			player.load();			
		}
				
	}
	var fErrorGetFile = function (errors, data) {
		alert(data);
		$('#spinnerTable').WBSpinner().hide();
	}
	
	$('#wbFileView').wbCommunicationManager().ajax ( { url:"./wbfile/ext/{0}?include_links=1".format(encodeURIComponent(fileExternalKey)),
											 httpOperation:"GET", 
											 payloadData:"",
											 functionSuccess: fSuccessGetFile,
											 functionError: fErrorGetFile
											} );	
											
	
											

	var fSuccessFileUpdate = function (data) {
		$('#wbModalFileDataUpdate').modal('hide');
		window.location.reload();
	};
	
	var fErrorFileUpdate = function (errors, data) {
		alert(data);
	};
	
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
		$('#wbFileView').wbCommunicationManager().ajax ( { url:"./wbfile/ext/" + encodeURIComponent(fileExternalKey),
										 httpOperation:"GET", 
										 payloadData:"",
										 functionSuccess: fSuccessGetFileForUpdate,
										 functionError: fErrorGetFile
										} );	

			});

	$('.wbUpdateUploadFileBtnClass').click ( function (e) {
		e.preventDefault();
		$('#wbuFileUploadUpdateForm').wbObjectManager().resetFields();
		$('#wbModalFileUploadUpdate').modal('show');
	});
		
	$('.fileUploadUpdateSave').click( function (e) {
		var errors = $('#wbuFileUploadUpdateForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if (! $.isEmptyObject(errors)) {
			e.preventDefault();
		}
	});

	$("#wbAddUrlBtn").click ( function (e) {
		e.preventDefault();
		window.location.href = "./weburiadd.html?qtype=file&qprivkey={0}".format(encodeURIComponent(fileExternalKey));
	});
	
	
});