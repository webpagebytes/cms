var errorsGeneral = {
	'ERROR_ARTICLETITLE_LENGTH': 'Article title length must be between 1 and 250 characters '
};

$().ready( function () {
	var wbArticlesValidations = { 
			title: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_ARTICLETITLE_LENGTH" }]
	};

	$('#wbAddArticleForm').wbObjectManager( { fieldsPrefix:'wba',
									  errorLabelsPrefix: 'erra',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  validationRules: wbArticlesValidations
									});
	$('#wbDuplicateArticleForm').wbObjectManager( { fieldsPrefix:'wbc',
									  errorLabelsPrefix: 'errc',
									  errorGeneral:"errcgeneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  validationRules: wbArticlesValidations
									});

	$('#wbDeleteArticleForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );							

	var itemsOnPage = 20;	

	var displayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="./webarticle.html?key=' + encodeURIComponent(record['key']) + '&externalKey=' + encodeURIComponent(record['externalKey']) + '"><i class="icon-eye-open"></i> View </a> '
			      +'| <a href="#" class="wbDeleteArticleClass" id="wbDeleteArticle_' + encodeURIComponent(record['key']) + '"><i class="icon-trash"></i> Delete </a>'
				  +'| <a href="#" class="wbDuplicateArticleClass" id="wbDupArticle_' + encodeURIComponent(record['key']) + '"><i class="aicon-duplicate"></i> Duplicate </a>';				  
		} else
		if (fieldId=="lastModified") {
			return escapehtml(Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		}
	}
				
	var columnClick = function (table, fieldId, dir) {	
		var newUrl = window.document.location.href;
		newUrl = replaceURLParameter(newUrl, "sort_field", fieldId);
		newUrl = replaceURLParameter(newUrl, "sort_dir", dir);				
		window.document.location.href = newUrl;		
	}

	$('#wbArticlesTable').wbSimpleTable( { columns: [ {display: "External Id", fieldId:"externalKey", isHtmlDisplay:true}, 
	                                                  {display: "Title", fieldId: "title", isHtmlDisplay:true}, 
	                                                  {display:"Last Modified", fieldId:"lastModified", customHandler: displayHandler, isHtmlDisplay:true}, 
	                                                  {display: "Operations", fieldId:"_operations", customHandler: displayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination",
						 headerColumnBaseClass: "header-uri-table",
						 headerColumnIdClassPrefix: "uri-table-",							 
						 handlerColumnClick: columnClick,
						 noLinesContent: "<tr> <td colspan='4'>There are no articles defined. </td></tr>"
						});

	$('#wbAddArticleForm').wbCommunicationManager();
	$('#wbDuplicateArticleForm').wbCommunicationManager();
	$('#wbDeleteArticleForm').wbCommunicationManager();

	$('#wbAddArticleBtn').click( function (e) {
		e.preventDefault();
		$('#wbAddArticleForm').wbObjectManager().resetFields();
		$('#wbAddArticleModal').modal('show');
	});
	
	var fSuccessAdd = function ( data ) {
		$('#wbAddArticleModal').modal('hide');
		window.location.reload();			
	}
	var fErrorAdd = function (errors, data) {
		$('#wbAddArticleForm').wbObjectManager().setErrors(errors);
	}

	$('.wbSaveAddArticleBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbAddArticleForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var jsonText = JSON.stringify($('#wbAddArticleForm').wbObjectManager().getObjectFromFields());
			$('#wbAddArticleForm').wbCommunicationManager().ajax ( { url: "./wbarticle",
															 httpOperation:"POST", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbAddArticleForm').wbObjectManager(),
															 functionSuccess: fSuccessAdd,
															 functionError: fErrorAdd
															 } );
		}
	});

	$(document).on ("click", '.wbDeleteArticleClass', function (e) {
		e.preventDefault();
		$('#wbDeleteArticleForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDeleteArticle_".length);
		var object = $('#wbArticlesTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDeleteArticleForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeleteArticleModal').modal('show');		
	});

	$(document).on ("click", '.wbDuplicateArticleClass', function (e) {
		e.preventDefault();
		$('#wbDuplicateArticleForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDupArticle_".length);
		var object = $('#wbArticlesTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDuplicateArticleForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDuplicateArticleModal').modal('show');		
	});

	var fSuccessDelete = function ( data ) {
		$('#wbDeleteArticleModal').modal('hide');	
		window.location.reload();			
	}
	var fErrorDelete = function (errors, data) {
		$('#wbDeleteArticleForm').wbObjectManager().setErrors(errors);
	}

	var fSuccessDuplicate = function ( data ) {
		$('#wbDuplicateArticleModal').modal('hide');
		window.location.reload();			
	}
	var fErrorDuplicate = function (errors, data) {
		$('#wbDuplicateArticleForm').wbObjectManager().setErrors(errors);
	}

	$('.wbSaveDuplicateArticleBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbDuplicateArticleForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {	
			var object = $('#wbDuplicateArticleForm').wbObjectManager().getObjectFromFields();
			delete object['key'];
			delete object['externalKey'];
			var jsonText = JSON.stringify(object);
			$('#wbDeleteArticleForm').wbCommunicationManager().ajax ( { url: "./wbarticle",
														 httpOperation:"POST", 
														 payloadData: jsonText,
														 wbObjectManager : $('#wbDuplicateArticleForm').wbObjectManager(),
														 functionSuccess: fSuccessDuplicate,
														 functionError: fErrorDuplicate
													} );
		}
	});

	$('.wbSaveDeleteBtnClass').click( function (e) {
		e.preventDefault();
		var object = $('#wbDeleteArticleForm').wbObjectManager().getObjectFromFields();			
		$('#wbDeleteArticleForm').wbCommunicationManager().ajax ( { url: "./wbarticle/" + encodeURIComponent(object['key']),
														 httpOperation:"DELETE", 
														 payloadData:"",
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete
													} );
		
	});

	var fSuccessGetAll = function (data) {
		$('#wbArticlesTable').wbSimpleTable().setRows(data.data);
		$('#wbArticlesTable').wbSimpleTable().setPagination( document.location.href, data['additional_data']['total_count'], itemsOnPage, "page");
		textItems = { "0":"", "empty":"", "1":"(1 item)", "greater_than_1": "({0} items)"};		
		$(".tablestats").html(escapehtml(getTextForItems(data['additional_data']['total_count'], textItems)));
	
	}
	var fErrorGetAll = function (errors, data) {
	
	}
	
	var page = getURLParameter('page') || 1;
	if (page <= 0) page = 1;
	var index_start = (page-1)*itemsOnPage;
	var sort_dir = encodeURIComponent(getURLParameter('sort_dir') || "asc");
	var sort_field = encodeURIComponent(getURLParameter('sort_field') || "title");	
	$('#wbArticlesTable').wbSimpleTable().addSortIconToColumnHeader(sort_field, sort_dir);
	
	var page_articles_url = "./wbarticle?sort_dir={0}&sort_field={1}&index_start={2}&count={3}".format(sort_dir, sort_field, index_start, itemsOnPage); 

	$('#wbAddArticleForm').wbCommunicationManager().ajax ( { url:page_articles_url,
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessGetAll,
													 functionError: fErrorGetAll
													} );

});