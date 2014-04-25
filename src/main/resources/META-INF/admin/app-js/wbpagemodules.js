var errorsGeneral = {
	'ERROR_PAGE_MODULENAME_LENGTH': 'Web page module name length must be between 1 and 250 characters ',
	'ERROR_PAGE_MODULENAME_BAD_FORMAT': 'Invalid format for web page module name: allowed characters are 0-9, a-z, A-Z, -, _,. (, is not an allowed character)'
};

$().ready( function () {
	var wbPageModuleValidations = { 
		name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_PAGE_MODULENAME_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9 a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_PAGE_MODULENAME_BAD_FORMAT"}]
	};

	$('#wbAddPageModuleForm').wbObjectManager( { fieldsPrefix:'wba',
									  errorLabelsPrefix: 'erra',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { isTemplateSource: 0 },
									  validationRules: wbPageModuleValidations
									});
	$('#wbDuplicatePageModuleForm').wbObjectManager( { fieldsPrefix:'wbc',
									  errorLabelsPrefix: 'errc',
									  errorGeneral:"errcgeneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { isTemplateSource: 0 },
									  validationRules: wbPageModuleValidations
									});

	$('#wbDeletePageModuleForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );							

	var itemsOnPage = 20;	
	
	var columnClick = function (table, fieldId, dir) {	
		var newUrl = window.document.location.href;
		newUrl = replaceURLParameter(newUrl, "sort_field", fieldId);
		newUrl = replaceURLParameter(newUrl, "sort_dir", dir);				
		window.document.location.href = newUrl;		
	}

	var displayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="./webpagemodule.html?key=' + encodeURIComponent(record['key']) + '&externalKey=' + encodeURIComponent(record['externalKey']) + '"><i class="icon-eye-open"></i> View </a> '
				   + '| <a href="#" class="wbDeletePageModuleClass" id="wbDeletePageModule_' + encodeURIComponent(record['key']) + '"><i class="icon-trash"></i> Delete </a>'
				   + '| <a href="#" class="wbDuplicatePageModuleClass" id="wbDuplicatePageModule_' + encodeURIComponent(record['key']) + '"><i class="aicon-duplicate"></i> Duplicate </a> ';
		} else
		if (fieldId=="lastModified") {
			return escapehtml(Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		}
	}
				
	$('#wbPageModulesTable').wbSimpleTable( { columns: [ {display: "External Id", fieldId:"externalKey", isHtmlDisplay:true}, 
	                                                     {display: "Name", fieldId: "name", isHtmlDisplay:true}, 
	                                                     {display:"Last Modified", fieldId:"lastModified", customHandler: displayHandler, isHtmlDisplay:true}, 
	                                                     {display: "Operations", fieldId:"_operations", customHandler: displayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination",
						 headerColumnBaseClass: "header-uri-table",
						 headerColumnIdClassPrefix: "uri-table-",							 
						 handlerColumnClick: columnClick,
						 noLinesContent: "<tr> <td colspan='4'>There are no page modules created. </td></tr>"
						});

	$('#wbAddPageModuleForm').wbCommunicationManager();
	$('#wbDuplicatePageModuleForm').wbCommunicationManager();
	$('#wbDeletePageModuleForm').wbCommunicationManager();

	$('#wbAddPageModuleBtn').click( function (e) {
		e.preventDefault();
		$('#wbAddPageModuleForm').wbObjectManager().resetFields();
		$('#wbAddPageModuleModal').modal('show');
	});

	var fSuccessAdd = function ( data ) {
		$('#wbAddPageModuleModal').modal('hide');
		populatePage();			
	}
	var fErrorAdd = function (errors, data) {
		$('#wbAddPageModuleForm').wbObjectManager().setErrors(errors);
	}

	var fSuccessDuplicate = function ( data ) {
		$('#wbDuplicatePageModuleModal').modal('hide');
		populatePage();			
	}
	var fErrorDuplicate = function (errors, data) {
		$('#wbDuplicatePageModuleForm').wbObjectManager().setErrors(errors);
	}

	$('.wbSaveAddPageModuleBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbAddPageModuleForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var jsonText = JSON.stringify($('#wbAddPageModuleForm').wbObjectManager().getObjectFromFields());
			$('#wbAddPageModuleForm').wbCommunicationManager().ajax ( { url: "./wbpagemodule",
															 httpOperation:"POST", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbAddPageModuleForm').wbObjectManager(),
															 functionSuccess: fSuccessAdd,
															 functionError: fErrorAdd
															 } );
		}
	});

	$('.wbSaveDuplicatePageModuleBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbDuplicatePageModuleForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var object = $('#wbDuplicatePageModuleForm').wbObjectManager().getObjectFromFields();
			delete object['externalKey'];
			delete object['key'];
			$('#wbDuplicatePageModuleForm').wbCommunicationManager().ajax ( { url: "./wbpagemodule",
															 httpOperation:"POST", 
															 payloadData:JSON.stringify(object),
															 wbObjectManager : $('#wbDuplicatePageModuleForm').wbObjectManager(),
															 functionSuccess: fSuccessDuplicate,
															 functionError: fErrorDuplicate
															 } );
		}
	});

	$(document).on ("click", '.wbDeletePageModuleClass', function (e) {
		e.preventDefault();
		$('#wbDeletePageModuleForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDeletePageModule_".length);
		var object = $('#wbPageModulesTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDeletePageModuleForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeletePageModuleModal').modal('show');		
	});

	$(document).on ("click", '.wbDuplicatePageModuleClass', function (e) {
		e.preventDefault();
		$('#wbDuplicatePageModuleForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDuplicatePageModule_".length);
		var object = $('#wbPageModulesTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDuplicatePageModuleForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDuplicatePageModuleModal').modal('show');		
	});

	var fSuccessDelete = function ( data ) {
		$('#wbDeletePageModuleModal').modal('hide');	
		populatePage();			
	}
	var fErrorDelete = function (errors, data) {
		$('#wbDeletePageModuleForm').wbObjectManager().setErrors(errors);
	}

	$('.webSaveDeleteBtnClass').click( function (e) {
		e.preventDefault();
		var object = $('#wbDeletePageModuleForm').wbObjectManager().getObjectFromFields();			
		$('#wbDeletePageModuleForm').wbCommunicationManager().ajax ( { url: "./wbpagemodule/" + encodeURIComponent(object['key']),
														 httpOperation:"DELETE", 
														 payloadData:"",
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete
													} );
		
	});

	var populatePage = function() {
		var fSuccessGetModules = function (data) {
			$('#wbPageModulesTable').wbSimpleTable().setRows(data.data);
			$('#wbPageModulesTable').wbSimpleTable().setPagination( document.location.href, data['additional_data']['total_count'], itemsOnPage, "page");
			textItems = { "0":"", "empty":"", "1":"(1 item)", "greater_than_1": "({0} items)"};		
			$(".tablestats").html(escapehtml(getTextForItems(data['additional_data']['total_count'], textItems)));
			$('#spinnerTable').WBSpinner().hide();
		}
		var fErrorGetModules = function (errors, data) {
			alert(data);
			$('#spinnerTable').WBSpinner().hide();
		}
		
		var page = getURLParameter('page') || 1;
		if (page <= 0) page = 1;
		var index_start = (page-1)*itemsOnPage;
		var sort_dir = encodeURIComponent(getURLParameter('sort_dir') || "dsc");
		var sort_field = encodeURIComponent(getURLParameter('sort_field') || "lastModified");	
		$('#wbPageModulesTable').wbSimpleTable().addSortIconToColumnHeader(sort_field, sort_dir);
		
		var page_modules_url = "./wbpagemodule?sort_dir={0}&sort_field={1}&index_start={2}&count={3}".format(sort_dir, sort_field, index_start, itemsOnPage); 

		$('#wbAddPageModuleForm').wbCommunicationManager().ajax ( { url:page_modules_url ,
														 httpOperation:"GET", 
														 payloadData:"",
														 functionSuccess: fSuccessGetModules,
														 functionError: fErrorGetModules
														} );
	}
	
	populatePage();
});