var errorsGeneral = {
};

$().ready( function () {
	
	$('#wbPageEditForm').wbObjectManager( { fieldsPrefix:'wbe',
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
			var innerHtml = '<a href="./webpage.html?key=' + escapehtml(record['key']) + '">' + escapehtml(record['name']) + '</a>';
			return innerHtml;
		}

		return record[fieldId];
	}
	$('#wbPageSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	
	var fSuccessGetPage = function (data) {
		$('#wbPageSummary').wbDisplayObject().display(data);
		$('#wbPageEditForm').wbObjectManager().populateFieldsFromObject(data);
	}
	
	var fErrorGetPage = function (errors, data) {
		alert(errors);
	}

	var pageKey = getURLParameter('key'); 
	var externalKey = getURLParameter('externalKey');
	$('#wbEditPageForm').wbCommunicationManager().ajax ( { url:"./wbpage/" + escapehtml(pageKey),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetPage,
												 functionError: fErrorGetPage
												} );
	
	var fSuccessEdit = function ( data ) {
		window.location.href = "./webpage.html?key=" + encodeURIComponent(pageKey) + "?externalKey=" + encodeURIComponent(externalKey);
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
		window.location.href = "./webpage.html?key=" + encodeURIComponent(pageKey) + "?externalKey=" + encodeURIComponent(externalKey);
	});


													
});