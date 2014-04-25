var errorsGeneral = {
	'ERROR_PAGENAME_LENGTH': 'Site page description length must be between 1 and 250 characters ',
	'ERROR_PAGE_BAD_FORMAT': 'Invalid format for site page description: allowed characters are numbers, letters(a-z) and , + - .'
};

$().ready( function () {
	var wbPageValidations = { 
			name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_PAGENAME_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9 a-zA-Z_,+.-]*$", modifiers:"gi"}}, error:"ERROR_PAGE_BAD_FORMAT"}]
	};
	$('#wbAddPageForm').wbObjectManager( { fieldsPrefix:'wba',
									  errorLabelsPrefix: 'erra',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { isTemplateSource: 0, contentType: "text/html" },
									  validationRules: wbPageValidations
									});
	$('#wbDuplicatePageForm').wbObjectManager( { fieldsPrefix:'wbc',
								  errorLabelsPrefix: 'errc',
								  errorGeneral:"errcgeneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  fieldsDefaults: { isTemplateSource: 0 },
								  validationRules: wbPageValidations
								});

	$('#wbDeletePageForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );	
	
	var itemsOnPage = 20;	
	
	var displayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="./webpage.html?key=' + encodeURIComponent(record['key']) + '&externalKey=' + encodeURIComponent(record['externalKey']) + '"><i class="icon-eye-open"></i> View </a>' + 
				 '| <a href="#" class="wbDeletePageClass" id="wbDeletePage_' + encodeURIComponent(record['key']) + '"><i class="icon-trash"></i> Delete </a>' +
				 '| <a href="#" class="wbDuplicatePageClass" id="wbDuplicatePage_' + encodeURIComponent(record['key']) + '"><i class="aicon-duplicate"></i> Duplicate </a>'; 
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

	$('#wbPagesTable').wbSimpleTable( { columns: [ {display: "External Id", fieldId:"externalKey", isHtmlDisplay:true}, 
	                                         {display: "Description", fieldId: "name", isHtmlDisplay:true}, 
	                                         {display:"Last modified", fieldId:"lastModified", customHandler: displayHandler, isHtmlDisplay:true}, 
	                                         {display: "Operations", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination",
						 headerColumnBaseClass: "header-uri-table",
						 headerColumnIdClassPrefix: "uri-table-",							 
						 handlerColumnClick: columnClick,
						 noLinesContent: "<tr> <td colspan='4'>There are no site pages defined. </td></tr>"
						});


	$('#wbAddPageForm').wbCommunicationManager();
	$('#wbDeletePageForm').wbCommunicationManager();

	$('#wbAddPageBtn').click( function (e) {
		e.preventDefault();
		$('#wbAddPageForm').wbObjectManager().resetFields();
		$('#wbAddPageModal').modal('show');
	});

	var fromOwnerExternalKey = 0;
	
	var fSuccessAdd = function ( data ) {
		$('#wbAddPageModal').modal('hide');
		populatePages();			
	}
	var fErrorAdd = function (errors, data) {
		$('#wbAddPageForm').wbObjectManager().setErrors(errors);
	}

	$('.wbSaveAddPageBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbAddPageForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var jsonText = JSON.stringify($('#wbAddPageForm').wbObjectManager().getObjectFromFields());
			$('#wbAddPageForm').wbCommunicationManager().ajax ( { url: "./wbpage",
															 httpOperation:"POST", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbAddPageForm').wbObjectManager(),
															 functionSuccess: fSuccessAdd,
															 functionError: fErrorAdd
															 } );
		}
	});

	var fSuccessDuplicateParams = function ( data ) {
		$('#wbDuplicatePageModal').modal('hide');
		populatePages();			
	};
	var fErrorDuplicateParams = function (errors, data) {
		alert(errors);
	};

	var fSuccessDuplicate = function ( data ) {
		var fromOwnerExternalKey = $('#wbcexternalKey').val();
		var ownerExternalKey = data.data['externalKey'];
		$('#wbDuplicatePageForm').wbCommunicationManager().ajax ( { url: "./wbparameter?fromOwnerExternalKey={0}&ownerExternalKey={1}".format(encodeURIComponent(fromOwnerExternalKey), encodeURIComponent(ownerExternalKey)),
															 httpOperation:"POST", 
															 payloadData:"",
															 wbObjectManager : $('#wbDuplicatePageForm').wbObjectManager(),
															 functionSuccess: fSuccessDuplicateParams,
															 functionError: fErrorDuplicateParams
															 } );		
	};
	var fErrorDuplicate = function (errors, data) {
		$('#wbDuplicatePageForm').wbObjectManager().setErrors(errors);
	};

	$('.wbSaveDuplicatePageBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbDuplicatePageForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var object = $('#wbDuplicatePageForm').wbObjectManager().getObjectFromFields();
			delete object['externalKey'];
			delete object['key'];
			var jsonText = JSON.stringify(object);
			$('#wbDuplicatePageForm').wbCommunicationManager().ajax ( { url: "./wbpage",
															 httpOperation:"POST", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbDuplicatePageForm').wbObjectManager(),
															 functionSuccess: fSuccessDuplicate,
															 functionError: fErrorDuplicate
															 } );
		}
	});

	$(document).on ("click", '.wbDuplicatePageClass', function (e) {
		e.preventDefault();
		$('#wbDuplicatePageForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDuplicatePage_".length);
		var object = $('#wbPagesTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDuplicatePageForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDuplicatePageModal').modal('show');		
	});

	$(document).on ("click", '.wbDeletePageClass', function (e) {
		e.preventDefault();
		$('#wbDeletePageForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDeletePage_".length);
		var object = $('#wbPagesTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDeletePageForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeletePageModal').modal('show');		
	});

	var fSuccessDelete = function ( data ) {
		$('#wbDeletePageModal').modal('hide');	
		populatePages();			
	}
	var fErrorDelete = function (errors, data) {
		$('#wbDeletePageForm').wbObjectManager().setErrors(errors);
	}

	$('.webSaveDeleteBtnClass').click( function (e) {
		e.preventDefault();
		var object = $('#wbDeletePageForm').wbObjectManager().getObjectFromFields();			
		$('#wbDeletePageForm').wbCommunicationManager().ajax ( { url: "./wbpage/" + encodeURIComponent(object['key']),
														 httpOperation:"DELETE", 
														 payloadData:"",
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete
													} );
		
	});

	var populatePages = function() {
		
		var fSuccessGetPages = function (data) {
			$('#wbPagesTable').wbSimpleTable().setRows(data.data);
			$('#wbPagesTable').wbSimpleTable().setPagination( document.location.href, data['additional_data']['total_count'], itemsOnPage, "page");
			textItems = { "0":"", "empty":"", "1":"(1 item)", "greater_than_1": "({0} items)"};		
			$(".tablestats").html(escapehtml(getTextForItems(data['additional_data']['total_count'], textItems)));
			$('#spinnerTable').WBSpinner().hide();
		}
		var fErrorGetPages = function (errors, data) {
			alert(data);
			$('#spinnerTable').WBSpinner().hide();
		}
		
		var page = getURLParameter('page') || 1;
		if (page <= 0) page = 1;
		var index_start = (page-1)*itemsOnPage;
		var sort_dir = encodeURIComponent(getURLParameter('sort_dir') || "dsc");
		var sort_field = encodeURIComponent(getURLParameter('sort_field') || "lastModified");	
		$('#wbPagesTable').wbSimpleTable().addSortIconToColumnHeader(sort_field, sort_dir);
		
		var pages_url = "./wbpage?sort_dir={0}&sort_field={1}&index_start={2}&count={3}".format(sort_dir, sort_field, index_start, itemsOnPage); 
	
		
		$('#wbAddPagesForm').wbCommunicationManager().ajax ( { url: pages_url,
														 httpOperation:"GET", 
														 payloadData:"",
														 functionSuccess: fSuccessGetPages,
														 functionError: fErrorGetPages
														} );
	}
	populatePages();
});