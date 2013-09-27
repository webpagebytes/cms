var errorsGeneral = {
	ERROR_PARAM_NAME_LENGTH: 'Parameter name length must be between 1 and 250 characters',
	ERROR_PARAM_NAME_BAD_FORMAT: 'Invalid name format: allowed characters are 0-9, a-z, A-Z,-,_,~,. (, is not an allowed character)',
	ERROR_PARAM_INVALID_OVERWRITE: 'Operation on overwrite not supported',
	ERROR_PARAM_INVALID_LOCALETYPE: 'Operation on locale not supported'
};

$().ready( function () {
	var wbParameterValidations = { 
			name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_PARAM_NAME_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_PARAM_NAME_BAD_FORMAT"}],
			overwriteFromUrl: [{rule: { includedInto: ['0','1'] }, error: "ERROR_PARAM_INVALID_OVERWRITE" }],
			localeType: [{rule: { includedInto: ['0','1','2'] }, error: "ERROR_PARAM_INVALID_LOCALETYPE" }]
	};
	
	$('#wbAddParameterForm').wbObjectManager( { fieldsPrefix:'wba',
									  errorLabelsPrefix: 'erra',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { overwriteFromUrl: 0, localeType: 0 },
									  validationRules: wbParameterValidations
									});
	$('#wbUpdateParameterForm').wbObjectManager( { fieldsPrefix:'wbu',
									  errorLabelsPrefix: 'erru',
									  errorGeneral:"errageneral",
									  fieldsDefaults: { overwriteFromUrl: 0, localeType: 0 },
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  validationRules: wbParameterValidations
									});

	$('#wbDeleteParameterForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );							
	var itemsOnPage = 50;	

	var tableDisplayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="#" class="wbEditParameterClass" id="wbEditParam_' + encodeURIComponent(record['key']) + '"><i class="icon-pencil"></i> Edit </a> | <a href="#" class="wbDeleteParameterClass" id="wbDelParam_' + encodeURIComponent(record['key'])+ '"><i class="icon-trash"></i> Delete </a>'; 
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

	$('#wbGlobalParamsTable').wbSimpleTable( { columns: [ {display: "External Id", fieldId:"externalKey", isHtmlDisplay:true}, 
	                                                {display: "Name", fieldId: "name", isHtmlDisplay:true}, 
	                                                {display: "Value", fieldId: "value"},
	                                                {display:"Last Modified", fieldId:"lastModified", customHandler: tableDisplayHandler, isHtmlDisplay:true}, 
	                                                {display: "Operations", fieldId:"_operations", customHandler: tableDisplayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination",
	                     headerColumnBaseClass: "header-uri-table",
	                     headerColumnIdClassPrefix: "uri-table-",							 
	                     handlerColumnClick: columnClick					 
						});
		
			
	$('#wbAddParameterBtn').click ( function (e) {
		e.preventDefault();
		$('#wbAddParameterForm').wbObjectManager().resetFields();
		$('#wbAddParameterModal').modal('show');
	});
	
	var fSuccessAdd = function ( data ) {
		$('#wbAddParameterModal').modal('hide');
		window.location.reload();			
	}
	var fErrorAdd = function (errors, data) {
		$('#wbAddParameterForm').wbObjectManager().setErrors(errors);
	}

	$('.wbAddParameterBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbAddParameterForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var parameter = $('#wbAddParameterForm').wbObjectManager().getObjectFromFields();
			parameter['ownerExternalKey'] = "";
			var jsonText = JSON.stringify(parameter);
			$('#wbAddParameterForm').wbCommunicationManager().ajax ( { url: "./wbparameter",
															 httpOperation:"POST", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbAddParamaterForm').wbObjectManager(),
															 functionSuccess: fSuccessAdd,
															 functionError: fErrorAdd
															 } );
		}
	});

	var fSuccessUpdate = function ( data ) {
		$('#wbUpdateParameterModal').modal('hide');		
		window.location.reload();			
	}
	
	var fErrorUpdate = function (errors, data) {
		$('#wbUpdateParameterForm').wbObjectManager().setErrors(errors);
	}

	$('.wbUpdateParameterBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbUpdateParameterForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var object = $('#wbUpdateParameterForm').wbObjectManager().getObjectFromFields();
			object['ownerExternalKey'] = "";
			var jsonText = JSON.stringify(object);
			$('#wbUpdateParameterForm').wbCommunicationManager().ajax ( { url: "./wbparameter/" + encodeURIComponent(object['key']),
															 httpOperation:"PUT", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbUpdateParameterForm').wbObjectManager(),
															 functionSuccess: fSuccessUpdate,
															 functionError: fErrorUpdate
															 } );
		}
	});

	var fSuccessDelete = function ( data ) {
		$('#wbDeleteParameterModal').modal('hide');		
		window.location.reload();			
	}
	var fErrorDelete = function (errors, data) {
		$('#wbDeleteParameterForm').wbObjectManager().setErrors(errors);
	}

	$('.wbDeleteParameterBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbDeleteParameterForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var object = $('#wbDeleteParameterForm').wbObjectManager().getObjectFromFields();
			$('#wbDeleteParameterForm').wbCommunicationManager().ajax ( { url: "./wbparameter/" + encodeURIComponent(object['key']),
															 httpOperation:"DELETE", 
															 payloadData:"",
															 wbObjectManager : $('#wbDeleteParameterForm').wbObjectManager(),
															 functionSuccess: fSuccessDelete,
															 functionError: fErrorDelete
															 } );
		}
	});

	
	$(document).on ("click", ".wbEditParameterClass", function (e) {
		e.preventDefault();
		$('#wbUpdateParameterForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbEditParam_".length);
		var object = $('#wbGlobalParamsTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbUpdateParameterForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbUpdateParameterModal').modal('show');		
	});

	$(document).on ("click", ".wbDeleteParameterClass", function (e) {
		e.preventDefault();
		$('#wbDeleteParameterForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDelParam_".length);
		var object = $('#wbGlobalParamsTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDeleteParameterForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeleteParameterModal').modal('show');		
	});

	var fSuccessGetParameters = function (data) {
		$('#wbGlobalParamsTable').wbSimpleTable().setRows(data.data);
		$('#wbGlobalParamsTable').wbSimpleTable().setPagination( document.location.href, data['additional_data']['total_count'], itemsOnPage, "page");

	}
	var fErrorGetParameters = function (errors, data) {
		alert(errors);
	}
	
	var page = getURLParameter('page') || 1;
	if (page <= 0) page = 1;
	var index_start = (page-1)*itemsOnPage;
	var sort_dir = encodeURIComponent(getURLParameter('sort_dir') || "asc");
	var sort_field = encodeURIComponent(getURLParameter('sort_field') || "name");	
	$('#wbGlobalParamsTable').wbSimpleTable().addSortIconToColumnHeader(sort_field, sort_dir);
	
	var parameters_url = "./wbparameter?sort_dir={0}&sort_field={1}&index_start={2}&count={3}&ownerExternalKey=&".format(sort_dir, sort_field, index_start, itemsOnPage); 

	$('#wbAddParameterForm').wbCommunicationManager().ajax ( { url: parameters_url,
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessGetParameters,
													 functionError: fErrorGetParameters
													} );

												
});