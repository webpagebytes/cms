/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
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

	$('.btn-clipboard').WBCopyClipboardButoon({buttonHtml:"<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copy to clipboard</div>", basePath: getAdminPath(), selector: '.btn-clipboard'});
	$('.btn-clipboard').WBCopyClipboardButoon().on("aftercopy", function (e) {
		$('.btn-clipboard').WBCopyClipboardButoon().reset();
		$(e.target).html("<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copied!</div>");
	});
	
	var displayHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			return escapehtml( "Last modified: " + Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} 
		if (fieldId == 'title') {
			var innerHtml = '<a href="./webarticle.html?extKey=' + encodeURIComponent(record['externalKey']) + '">' + escapehtml(record['title']) + '</a>';
			return innerHtml;
		}

		return record[fieldId];
	}
	$('#wbArticleSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	
	var pageKey = getURLParameter('privkey');
	var fSuccessGetArticle = function (data) {
		pageKey = data.data["privkey"];
		$('#wbArticleSummary').wbDisplayObject().display(data.data);
		$('#wbArticleEditForm').wbObjectManager().populateFieldsFromObject(data.data);
		
		$("textarea").sceditor("instance").val(data.data['htmlSource']);
		$('#spinnerTable').WBSpinner().hide();
	}
	
	var fErrorGetArticle = function (errors, data) {
		alert(errors);
		$('#spinnerTable').WBSpinner().hide();
	}

	var externalKey = getURLParameter('extKey'); 
	$('#wbArticleEditForm').wbCommunicationManager().ajax ( { url:"./wbarticle/ext/" + encodeURIComponent(externalKey),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetArticle,
												 functionError: fErrorGetArticle
												} );
	
	var fSuccessEdit = function ( data ) {
		window.location.href = "./webarticles.html";
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
		window.location.href = "./webarticles.html";
	});


													
});