var errorsGeneral = {
};

$().ready( function () {
	
	$('#wbPageModuleEditForm').wbObjectManager( { fieldsPrefix:'wbe',
									  errorLabelsPrefix: 'erre',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { isTemplateSource: 0 }
									 });

	var displayHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			var date = new Date();
			return date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		} 
		if (fieldId == 'name') {
			var innerHtml = '<a href="./webpagemodule.html?key=' + escapehtml(record['key']) + '">' + escapehtml(record['name']) + '</a>';
			return innerHtml;
		}

		return record[fieldId];
	}
	$('#wbPageModuleSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	
	var fSuccessGetPage = function (data) {
		$('#wbPageModuleSummary').wbDisplayObject().display(data);
		$('#wbPageModuleEditForm').wbObjectManager().populateFieldsFromObject(data);
	}
	
	var fErrorGetPage = function (errors, data) {
		alert(errors);
	}

	var pageKey = getURLParameter('key'); 
	$('#wbPageModuleEditForm').wbCommunicationManager().ajax ( { url:"./wbpagemodule/" + escapehtml(pageKey),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetPage,
												 functionError: fErrorGetPage
												} );
	
	var fSuccessEdit = function ( data ) {
		window.location.href = "./webpagemodule.html?key=" + escapehtml(pageKey);
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
			$('#wbPageModuleEditForm').wbCommunicationManager().ajax ( { url: "./wbpagemodule/" + escapehtml(pageKey),
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
		window.location.href = "./webpagemodule.html?key=" + escapehtml(pageKey);
	});


													
});