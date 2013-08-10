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

	var swfzc = getAdminPath() + '/zeroclipboard/ZeroClipboard.swf';
	ZeroClipboard.setDefaults( { moviePath: swfzc } );
	var zcButtons = $.find('.btn-clipboard');
	$.each (zcButtons, function (index, elem) {
		var zc = new ZeroClipboard(elem);
	});

	var tableDisplayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="#" class="wbEditParameterClass" id="wbEditParam_' + encodeURIComponent(record['key']) + '"><i class="icon-pencil"></i> Edit </a> | <a href="#" class="wbDeleteParameterClass" id="wbDelParam_' + encodeURIComponent(record['key'])+ '"><i class="icon-trash"></i> Delete </a>'; 
		} else
		if (fieldId=="lastModified") {
			var date = new Date();
			return date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		}
	}
				
	$('#wbPageParametersTable').wbTable( { columns: [ {display: "Id", fieldId:"key"}, {display: "Name", fieldId: "name"}, {display: "Value", fieldId: "value"},
									{display:"Last Modified", fieldId:"lastModified", customHandling:true, customHandler: tableDisplayHandler}, {display: "Operations", fieldId:"_operations", customHandling:true, customHandler: tableDisplayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-stripped table-bordered table-color-header",
						 paginationBaseClass: "pagination"
						});
	var displayHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			var date = new Date();
			return date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		} 
		if (fieldId == 'name') {
			var innerHtml = '<a href="./webpage.html?key=' + encodeURIComponent(record['key']) + '">' + escapehtml(record['name']) + '</a>';
			return innerHtml;
		}
		return record[fieldId];
	}
	var pageSourceHandler = function (fieldId, record) {
		if (fieldId == 'isTemplateSource') {
			var plainValue = "", templateValue = "";
			if ('isTemplateSource' in record)
			{
				if (record['isTemplateSource'] == '0') {
					plainValue='checked';
				} else if (record['isTemplateSource'] == '1') {
					templateValue = 'checked';
				}
			}
			var innerHtml = '<input class="input-xlarge" type="radio" {0} disabled="disabled"> Plain html source <input class="input-xlarge" type="radio" {1} disabled="disabled"> Template html source'.format(plainValue, templateValue); 			
			return innerHtml;
		}
		return escapehtml(record[fieldId]);
	}
	
	$('#wbPageSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	$('#wbPageView').wbDisplayObject( { fieldsPrefix: 'wbPageView', customHandler: pageSourceHandler} );
	
	var fSuccessGetPage = function (data) {
		$('#wbPageSummary').wbDisplayObject().display(data);
		$('#wbPageView').wbDisplayObject().display(data);
	}
	var fErrorGetPage = function (errors, data) {
		alert(errors);
	}

	var pageKey = getURLParameter('key'); 
	var pageExternalKey = getURLParameter('externalKey');;
		
	$('.wbPageViewEditLink').click ( function (e) {
		e.preventDefault();
		window.location.href = "./webpageedit.html?key={0}&externalKey={1}".format(encodeURIComponent(pageKey),encodeURIComponent(pageExternalKey));
	} );
	
	$('#wbPageSummary').wbCommunicationManager().ajax ( { url:"./wbpage/" + encodeURIComponent(pageKey),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetPage,
												 functionError: fErrorGetPage
												} );
	$('#wbAddParameterBtn').click ( function (e) {
		e.preventDefault();
		$('#wbAddParameterForm').wbObjectManager().resetFields();
		$('#wbAddParameterModal').modal('show');
	});
	
	var fSuccessAdd = function ( data ) {
		$('#wbAddParameterModal').modal('hide');
		$('#wbPageParametersTable').wbTable().insertRow(data);			
	}
	var fErrorAdd = function (errors, data) {
		$('#wbAddParameterForm').wbObjectManager().setErrors(errors);
	}

	$('.wbAddParameterBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbAddParameterForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var parameter = $('#wbAddParameterForm').wbObjectManager().getObjectFromFields();
			parameter['ownerExternalKey'] = pageExternalKey;
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
		$('#wbPageParametersTable').wbTable().updateRowWithKey(data,data["key"]);
	}
	var fErrorUpdate = function (errors, data) {
		$('#wbUpdateParameterForm').wbObjectManager().setErrors(errors);
	}

	$('.wbUpdateParameterBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbUpdateParameterForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var object = $('#wbUpdateParameterForm').wbObjectManager().getObjectFromFields();
			object['ownerExternalKey'] = pageExternalKey;
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
		$('#wbPageParametersTable').wbTable().deleteRowWithKey(data["key"]);
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
		var object = $('#wbPageParametersTable').wbTable().getRowDataWithKey(key);
		$('#wbUpdateParameterForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbUpdateParameterModal').modal('show');		
	});

	$(document).on ("click", ".wbDeleteParameterClass", function (e) {
		e.preventDefault();
		$('#wbDeleteParameterForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDelParam_".length);
		var object = $('#wbPageParametersTable').wbTable().getRowDataWithKey(key);
		$('#wbDeleteParameterForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeleteParameterModal').modal('show');		
	});

	var fSuccessGetParameters = function (data) {
		$.each(data, function(index, item) {
			$('#wbPageParametersTable').wbTable().insertRow(item);
		});				

	}
	var fErrorGetParameters = function (errors, data) {
	
	}
	
	$('#wbAddParameterForm').wbCommunicationManager().ajax ( { url:"./wbparameter?ownerExternalKey=" + encodeURIComponent(pageExternalKey),
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessGetParameters,
													 functionError: fErrorGetParameters
													} );

												
});