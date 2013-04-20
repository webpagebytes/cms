var errorsGeneral = {
};

$().ready( function () {
	$('#wbAddArticleForm').wbObjectManager( { fieldsPrefix:'wba',
									  errorLabelsPrefix: 'erra',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { },
									  validationRules: {
										'title': { rangeLength: { 'min': 1, 'max': 300 } }
									  }
									});
	$('#wbDeleteArticleForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );							

	var displayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="./webarticle.html?key=' + escapehtml(record['key']) + '&externalKey=' + escapehtml(record['externalKey']) + '"><i class="icon-pencil"></i> Edit </a> | <a href="#" class="wbDeleteArticleClass" id="wbDeleteArticle_' +record['key']+ '"><i class="icon-trash"></i> Delete </a>'; 
		} else
		if (fieldId=="lastModified") {
			var date = new Date();
			return date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		}
	}
				
	$('#wbArticlesTable').wbTable( { columns: [ {display: "Id", fieldId:"key"}, {display: "External Id", fieldId:"externalKey"}, {display: "Title", fieldId: "title"}, 
									{display:"Last Modified", fieldId:"lastModified", customHandling: true, customHandler: displayHandler}, {display: "Edit/delete", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination"
						});

	$('#wbAddArticleForm').wbCommunicationManager();
	$('#wbDeleteArticleForm').wbCommunicationManager();

	$('#wbAddArticleBtn').click( function (e) {
		e.preventDefault();
		$('#wbAddArticleForm').wbObjectManager().resetFields();
		$('#wbAddArticleModal').modal('show');
	});

	var fSuccessAdd = function ( data ) {
		$('#wbAddArticleModal').modal('hide');
		$('#wbArticlesTable').wbTable().insertRow(data);			
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

	var fSuccessDelete = function ( data ) {
		$('#wbDeleteArticleModal').modal('hide');	
		$('#wbArticlesTable').wbTable().deleteRowWithKey(data["key"]);
	}
	var fErrorDelete = function (errors, data) {
		$('#wbDeleteArticleForm').wbObjectManager().setErrors(errors);
	}

	$('.webSaveDeleteBtnClass').click( function (e) {
		e.preventDefault();
		var object = $('#wbDeleteArticleForm').wbObjectManager().getObjectFromFields();			
		$('#wbDeleteArticleForm').wbCommunicationManager().ajax ( { url: "./wbarticle/" + escapehtml(object['key']),
														 httpOperation:"DELETE", 
														 payloadData:"",
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete
													} );
		
	});

	var fSuccessGetAll = function (data) {
		$.each(data, function(index, item) {
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