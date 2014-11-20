var errorsGeneral = {
	ERROR_PAGENAME_LENGTH:'Site page description length must be between 1 and 250 characters',
	ERROR_PAGE_INVALID_TYPE: 'Invalid operation type',
	ERROR_CONTENTTYPE_LENGTH: 'Content type length must be between 1 and 250 characters',
	ERROR_CONTENTTYPE_BAD_FORMAT: 'Invalid format for content type',
	ERROR_CONTROLLER_LENGTH: 'Controller length must be between 1 and 250 characters',
	ERROR_CONTROLLER_BAD_FORMAT: 'Invalid format for controller'
};

$().ready( function () {
	var wbPageValidations = { 
			name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_PAGENAME_LENGTH" }],
			isTemplateSource: [{rule: { includedInto: ['0','1'] }, error: "ERROR_PAGE_INVALID_TYPE" }],
			contentType: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_CONTENTTYPE_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_//.-]*$", modifiers:"gi"}}, error:"ERROR_CONTENTTYPE_BAD_FORMAT"}],
			pageModelProvider: [{rule: { rangeLength: { 'min': 0, 'max': 250 } }, error: "ERROR_CONTROLLER_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.]*$", modifiers:"gi"}}, error:"ERROR_CONTROLLER_BAD_FORMAT"}]
			
	};

	$('#wbPageEditForm').wbObjectManager( { fieldsPrefix:'wbe',
									  errorLabelsPrefix: 'erre',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { isTemplateSource: 0 },
									  validationRules: wbPageValidations
									 });

	$('.btn-clipboard').WBCopyClipboardButoon({buttonHtml:"<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copy to clipboard</div>", basePath: getAdminPath(), selector: '.btn-clipboard'});
	$('.btn-clipboard').WBCopyClipboardButoon().on("aftercopy", function (e) {
		$('.btn-clipboard').WBCopyClipboardButoon().reset();
		$(e.target).html("<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copied!</div>");
	});

	$('#wbecontentType').typeahead({ source: ["text/html", "text/plain", "text/javascript", "text/css", "application/json", "application/x-javascript"]});
	
	var displayHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			return escapehtml( "Last modified: " + Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} 
		if (fieldId == 'name') {
			var innerHtml = '<a href="./webpage.html?extKey=' + encodeURIComponent(record['externalKey']) + '">' + escapehtml(record['name']) + '</a>';
			return innerHtml;
		}

		return record[fieldId];
	}
	$('#wbPageSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	
	var pageKey = getURLParameter('privkey'); 
	var fSuccessGetPage = function (data) {
		pageKey =  data.data["privkey"];
		$('#wbPageSummary').wbDisplayObject().display(data.data);
		$('#wbPageEditForm').wbObjectManager().populateFieldsFromObject(data.data);
		if (data.data["isTemplateSource"] != '1') {
			$('.wbModelProviderContainer').hide();
		} else {
			$('.wbModelProviderContainer').show();
		}
		$('#spinnerTable').WBSpinner().hide();
	}
	
	$('input[name="isTemplateSource"]').on("change", function() {
		$('.wbModelProviderContainer').toggle();
	});
	
	var fErrorGetPage = function (errors, data) {
		alert(errors);
		$('#spinnerTable').WBSpinner().hide();
	}

	var externalKey = getURLParameter('extKey');
	$('#wbEditPageForm').wbCommunicationManager().ajax ( { url:"./wbpage/ext/{0}".format(encodeURIComponent(externalKey)),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetPage,
												 functionError: fErrorGetPage
												} );
	
	var fSuccessEdit = function ( data ) {
		window.location.href = "./webpage.html?extKey=" + encodeURIComponent(externalKey);
	}
	var fErrorEdit = function (errors, data) {
		$('#wbEditPageForm').wbObjectManager().setErrors(errors);
	}

	$('.wbPageEditSaveBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbPageEditForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var page = $('#wbPageEditForm').wbObjectManager().getObjectFromFields();
			var jsonText = JSON.stringify(page);
			$('#wbPageEditForm').wbCommunicationManager().ajax ( { url: "./wbpage/" + encodeURIComponent(pageKey),
															 httpOperation:"PUT", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbEditPageForm').wbObjectManager(),
															 functionSuccess: fSuccessEdit,
															 functionError: fErrorEdit
															 } );
		}
	});
	
	$('.wbPageEditCancelBtnClass').click ( function (e) {
		e.preventDefault();
		window.location.href = "./webpage.html?extKey={0}".format(encodeURIComponent(externalKey));
	});


													
});