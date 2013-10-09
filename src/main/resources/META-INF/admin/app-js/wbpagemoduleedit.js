var errorsGeneral = {
	'ERROR_PAGE_MODULENAME_LENGTH': 'Web page module name length must be between 1 and 250 characters ',
	'ERROR_PAGE_MODULENAME_BAD_FORMAT': 'Invalid format for web page module name: allowed characters are 0-9, a-z, A-Z, -, _,.(, is not an allowed character)'
};

$().ready( function () {
	var wbPageModuleValidations = { 
		name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_PAGE_MODULENAME_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9 a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_PAGE_MODULENAME_BAD_FORMAT"}]
	};

	$('#wbPageModuleEditForm').wbObjectManager( { fieldsPrefix:'wbe',
									  errorLabelsPrefix: 'erre',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { isTemplateSource: 0 },
									  validationRules: wbPageModuleValidations
									 });
	
	$('.btn-clipboard').WBCopyClipboardButoon({basePath: getAdminPath(), selector: '.btn-clipboard'});

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
	
	var fSuccessGetPage = function (data) {
		$('#wbPageModuleSummary').wbDisplayObject().display(data.data);
		$('#wbPageModuleEditForm').wbObjectManager().populateFieldsFromObject(data.data);
	}
	
	var fErrorGetPage = function (errors, data) {
		alert(errors);
	}

	var pageKey = getURLParameter('key'); 
	$('#wbPageModuleEditForm').wbCommunicationManager().ajax ( { url:"./wbpagemodule/" + encodeURIComponent(pageKey),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetPage,
												 functionError: fErrorGetPage
												} );
	
	var fSuccessEdit = function ( data ) {
		window.location.href = "./webpagemodule.html?key=" + encodeURIComponent(pageKey);
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
			$('#wbPageModuleEditForm').wbCommunicationManager().ajax ( { url: "./wbpagemodule/" + encodeURIComponent(pageKey),
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
		window.location.href = "./webpagemodule.html?key=" + encodeURIComponent(pageKey);
	});


													
});