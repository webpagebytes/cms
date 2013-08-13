var errorsGeneral = {
};

$().ready( function () {

	var swfzc = getAdminPath() + '/zeroclipboard/ZeroClipboard.swf';
	ZeroClipboard.setDefaults( { moviePath: swfzc } );
	var zcButtons = $.find('.btn-clipboard');
	$.each (zcButtons, function (index, elem) {
		var zc = new ZeroClipboard(elem);
	});

	var displayHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			var date = new Date();
			return date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		} 
		if (fieldId == 'title') {
			var innerHtml = '<a href="./webarticle.html?key=' + escapehtml(record['key']) + '">' + escapehtml(record['title']) + '</a>';
			return innerHtml;
		}
		return record[fieldId];
	}
	
	$('#wbArticleSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	$('#wbArticleView').wbDisplayObject( { fieldsPrefix: 'wbArticleView' } );
	
	var fSuccessGetArticle = function (data) {
		$('#wbArticleSummary').wbDisplayObject().display(data);
		$('#wbArticleView').wbDisplayObject().display(data);
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