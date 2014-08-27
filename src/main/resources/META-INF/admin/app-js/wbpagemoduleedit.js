var errorsGeneral = {
	'ERROR_PAGE_MODULENAME_LENGTH': 'Web page module name length must be between 1 and 250 characters '
};

$().ready( function () {
	var wbPageModuleValidations = { 
		name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_PAGE_MODULENAME_LENGTH" }]
	};

	$('#wbPageModuleEditForm').wbObjectManager( { fieldsPrefix:'wbe',
									  errorLabelsPrefix: 'erre',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { isTemplateSource: 0 },
									  validationRules: wbPageModuleValidations
									 });
	
	$('.btn-clipboard').WBCopyClipboardButoon({buttonHtml:"<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copy to clipboard</div>", basePath: getAdminPath(), selector: '.btn-clipboard'});
	$('.btn-clipboard').WBCopyClipboardButoon().on("aftercopy", function (e) {
		$('.btn-clipboard').WBCopyClipboardButoon().reset();
		$(e.target).html("<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copied!</div>");
	});

	var displayHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			return escapehtml( "Last modified: " + Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} 
		if (fieldId == 'name') {
			var innerHtml = '<a href="./webpagemodule.html?key=' + encodeURIComponent(record['key']) + '">' + escapehtml(record['name']) + '</a>';
			return innerHtml;
		}

		return record[fieldId];
	}
	$('#wbPageModuleSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	
	var pageModuleKey = getURLParameter('key'); 
	
	var fSuccessGetPage = function (data) {
		pageModuleKey = data.data["key"];
		$('#wbPageModuleSummary').wbDisplayObject().display(data.data);
		$('#wbPageModuleEditForm').wbObjectManager().populateFieldsFromObject(data.data);
		$('#spinnerTable').WBSpinner().hide();
	}
	
	var fErrorGetPage = function (errors, data) {
		alert(errors);
		$('#spinnerTable').WBSpinner().hide();
	}

	var externalKey = getURLParameter('extKey'); 
	
	$('#wbPageModuleEditForm').wbCommunicationManager().ajax ( { url:"./wbpagemodule/ext/" + encodeURIComponent(externalKey),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetPage,
												 functionError: fErrorGetPage
												} );
	
	var fSuccessEdit = function ( data ) {
		window.location.href = "./webpagemodule.html?extKey=" + encodeURIComponent(externalKey);
	}
	var fErrorEdit = function (errors, data) {
		$('#wbEditPageModuleForm').wbObjectManager().setErrors(errors);
	}

	$('.wbPageModuleEditSaveBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbPageModuleEditForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var page = $('#wbPageModuleEditForm').wbObjectManager().getObjectFromFields();
			var jsonText = JSON.stringify(page);
			$('#wbPageModuleEditForm').wbCommunicationManager().ajax ( { url: "./wbpagemodule/" + encodeURIComponent(pageModuleKey),
															 httpOperation:"PUT", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbPageModuleEditForm').wbObjectManager(),
															 functionSuccess: fSuccessEdit,
															 functionError: fErrorEdit
															 } );
		}
	});
	
	$('.wbPageEditCancelBtnClass').click ( function (e) {
		e.preventDefault();
		window.location.href = "./webpagemodule.html?extKey=" + encodeURIComponent(externalKey);
	});


													
});