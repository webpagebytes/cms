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
				
	$('#wbArticlesTable').wbTable( { columns: [ {display: "External Id", fieldId:"externalKey"}, {display: "Title", fieldId: "title"}, 
									{display:"Last Modified", fieldId:"lastModified", customHandling: true, customHandler: displayHandler}, {display: "Operations", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination"
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
		$('#wbArticlesTable').wbTable().insertRow(data.data);			
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
		var object = $('#wbArticlesTable').wbTable().getRowDataWithKey(key);
		$('#wbDeleteArticleForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeleteArticleModal').modal('show');		
	});

	$(document).on ("click", '.wbDuplicateArticleClass', function (e) {
		e.preventDefault();
		$('#wbDuplicateArticleForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDupArticle_".length);
		var object = $('#wbArticlesTable').wbTable().getRowDataWithKey(key);
		$('#wbDuplicateArticleForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDuplicateArticleModal').modal('show');		
	});

	var fSuccessDelete = function ( data ) {
		$('#wbDeleteArticleModal').modal('hide');	
		$('#wbArticlesTable').wbTable().deleteRowWithKey(data.data["key"]);
	}
	var fErrorDelete = function (errors, data) {
		$('#wbDeleteArticleForm').wbObjectManager().setErrors(errors);
	}

	var fSuccessDuplicate = function ( data ) {
		$('#wbDuplicateArticleModal').modal('hide');
		$('#wbArticlesTable').wbTable().insertRow(data.data);			
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
		$.each(data.data, function(index, item) {
			$('#wbArticlesTable').wbTable().insertRow(item);
		});				

	}
	var fErrorGetAll = function (errors, data) {
	
	}
	
	$('#wbAddArticleForm').wbCommunicationManager().ajax ( { url:"./wbarticle",
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessGetAll,
													 functionError: fErrorGetAll
													} );

});