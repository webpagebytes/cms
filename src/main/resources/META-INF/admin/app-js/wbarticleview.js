var errorsGeneral = {
};

$().ready( function () {

	
	$('.btn-clipboard').WBCopyClipboardButoon({basePath: getAdminPath(), selector: '.btn-clipboard'});

	var displayHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			return escapehtml( "Last modified: " + Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} 
		if (fieldId == 'title') {
			var innerHtml = '<a href="./webarticle.html?key=' + escapehtml(record['key']) + '">' + escapehtml(record['title']) + '</a>';
			return innerHtml;
		}
		return escapehtml(record[fieldId]);
	}
	
	var viewHandler = function (fieldId, record) {
		if (fieldId == 'htmlSource') {
			return record[fieldId];
		}
		
		return record[fieldId];		
	}	
	
	$('#wbArticleSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	$('#wbArticleView').wbDisplayObject( { fieldsPrefix: 'wbArticleView', customHandler: viewHandler } );
	
	var htmlSource = "";
	var prevTimeout = undefined;
	var delayDisplay = function()
	{
		if (tinyMCE && tinyMCE.activeEditor && tinyMCE.activeEditor.initialized) {
			tinyMCE.activeEditor.setContent(htmlSource);
			clearTimeout(prevTimeout);
		}
	}

	var fSuccessGetArticle = function (data) {
		$('#wbArticleSummary').wbDisplayObject().display(data.data);
		$('#wbArticleView').wbDisplayObject().display(data.data);
		if (tinyMCE && tinyMCE.activeEditor && tinyMCE.activeEditor.initialized) {
			tinyMCE.activeEditor.setContent(data.data['htmlSource']);
		} else {
			htmlSource = data.data['htmlSource'];
			prevTimeout = setTimeout(delayDisplay, 500);
		}		
	}
	var fErrorGetArticle = function (errors, data) {
		alert(errors);
	}

	var pageKey = getURLParameter('key'); 
	var pageModuleExternalKey = getURLParameter('externalKey');;
	
	$('.wbArticleViewEditLink').click ( function (e) {
		e.preventDefault();
		window.location.href = "./webarticleedit.html?key=" + pageKey;
	} );
	
	$('#wbArticleSummary').wbCommunicationManager().ajax ( { url:"./wbarticle/" + pageKey,
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetArticle,
												 functionError: fErrorGetArticle
												} );												
});