var errorsGeneral = {
	'ERROR_ARTICLETITLE_LENGTH': 'Article title length must be between 1 and 250 characters '
};

$().ready( function () {
	var wbArticlesValidations = { 
			title: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_ARTICLETITLE_LENGTH" }]
	};
	
	$('#wbArticleEditForm').wbObjectManager( { fieldsPrefix:'wbe',
									  errorLabelsPrefix: 'erre',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  validationRules: wbArticlesValidations
									 });

	$('.btn-clipboard').WBCopyClipboardButoon({basePath: getAdminPath(), selector: '.btn-clipboard'});

	var displayHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			return escapehtml( "Last modified: " + Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} 
		if (fieldId == 'title') {
			var innerHtml = '<a href="./webarticle.html?key=' + encodeURIComponent(record['key']) + '">' + escapehtml(record['title']) + '</a>';
			return innerHtml;
		}

		return record[fieldId];
	}
	$('#wbArticleSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	
	var htmlSource = "";
	var prevTimeout = undefined;
	var delayDisplay = function()
	{
		if (tinyMCE && tinyMCE.activeEditor && tinyMCE.activeEditor.initialized) {
			tinyMCE.activeEditor.setContent(htmlSource);
			clearTimeout(prevTimeout);
			$('#spinnerTable').WBSpinner().hide();
		}
	}

	var fSuccessGetArticle = function (data) {
		$('#wbArticleSummary').wbDisplayObject().display(data.data);
		$('#wbArticleEditForm').wbObjectManager().populateFieldsFromObject(data.data);
		
		$("textarea").sceditor("instance").val(data.data['htmlSource']);
		$('#spinnerTable').WBSpinner().hide();
		/*
		if (tinyMCE && tinyMCE.activeEditor && tinyMCE.activeEditor.initialized) {
			tinyMCE.activeEditor.setContent(data.data['htmlSource']);
			$('#spinnerTable').WBSpinner().hide();
		} else {
			htmlSource = data.data['htmlSource'];
			prevTimeout = setTimeout(delayDisplay, 500);
		}*/		

	}
	
	var fErrorGetArticle = function (errors, data) {
		alert(errors);
		$('#spinnerTable').WBSpinner().hide();
	}

	var pageKey = getURLParameter('key'); 
	$('#wbArticleEditForm').wbCommunicationManager().ajax ( { url:"./wbarticle/" + encodeURIComponent(pageKey),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetArticle,
												 functionError: fErrorGetArticle
												} );
	
	var fSuccessEdit = function ( data ) {
		window.location.href = "./webarticle.html?key=" + encodeURIComponent(pageKey);
	}
	var fErrorEdit = function (errors, data) {
		$('#wbEditPageModuleForm').wbObjectManager().setErrors(errors);
	}

	$('.wbArticleEditSaveBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbArticleEditForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var article = $('#wbArticleEditForm').wbObjectManager().getObjectFromFields();
			article['htmlSource'] = $("textarea").sceditor("instance").val();
			var jsonText = JSON.stringify(article);
			$('#wbArticleEditForm').wbCommunicationManager().ajax ( { url: "./wbarticle/" + encodeURIComponent(pageKey),
															 httpOperation:"PUT", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbArticleEditForm').wbObjectManager(),
															 functionSuccess: fSuccessEdit,
															 functionError: fErrorEdit
															 } );
		}
	});
	
	$('.wbArticleEditCancelBtnClass').click ( function (e) {
		e.preventDefault();
		window.location.href = "./webarticle.html?key=" + encodeURIComponent(pageKey);
	});


													
});