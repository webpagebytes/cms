
var errorsGeneral = {
	'ERROR_URI_START_CHAR': "Site url must to start with /",
	'ERROR_URI_LENGTH': 'Site url length must be between 1 and 250 characters',
	'ERROR_CONTROLLER_LENGTH': 'Controller class length must be maximum 250 characters',
	'ERROR_INVALID_HTTP_OPERATION': 'Operation not valid, allowed values: GET, PUT, DELETE, POST',
	'ERROR_URI_BAD_FORMAT':'Invalid url format: allowed characters are 0-9, a-z, A-Z,-,_,~,. (, is not an allowed character)',
	'ERROR_CONTROLLER_BAD_FORMAT': 'Invalid format for controller class: allowed characters are 0-9, a-z, A-Z, -, _, (, is not an allowed character)',
	'ERROR_BAD_RESOURCE_TYPE': 'Invaid resource type',
	'ERROR_NO_RESOURCE_EXTERNAL_KEY': 'No resource External Key',
	'ERROR_BAD_RESOURCE_EXTERNAL_KEY': 'Invalid resource External Key',
	'ERROR_BAD_RESOURCE_CONTENT_TYPE': 'Resource content type not supported',
	'ERROR_RESOURCE_CONTENT_TYPE_LENGTH': 'Resource content type length must be between 1 and 50 characters',
	'ERROR_CONTROLLER_LENGTH': 'Controller length must be between 1 and 250 characters',
	'ERROR_CONTROLLER_BAD_FORMAT': 'Invalid format for controller',

	'ERROR_PARAM_NAME_LENGTH': 'Parameter name length must be between 1 and 250 characters',
	'ERROR_PARAM_NAME_BAD_FORMAT': 'Invalid name format: allowed characters are 0-9, a-z, A-Z,-,_,~,. (, is not an allowed character)',
	'ERROR_PARAM_INVALID_OVERWRITE': 'Operation on overwrite not supported',
	'ERROR_PARAM_INVALID_LOCALETYPE': 'Operation on locale not supported'

	
};

$().ready( function () {
	var wbUriValidationRules = {
								'uri': [ {rule:{startsWith: '/'}, error: 'ERROR_URI_START_CHAR'}, {rule:{customRegexp:{pattern:"^/([0-9a-zA-Z_~.-]*(\{[0-9a-zA-Z_.*-]+\})*[0-9a-zA-Z_~.-]*/?)*$", modifiers:"gi"}}, error:"ERROR_URI_BAD_FORMAT"}, { rule:{rangeLength: { 'min': 1, 'max': 250 } }, error:"ERROR_URI_LENGTH"} ],
								'controllerClass': [{ rule:{ maxLength: 250 }, error: "ERROR_CONTROLLER_LENGTH"}, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_CONTROLLER_BAD_FORMAT"}],
								'httpOperation': [{ rule: { includedInto: ['GET', 'POST', 'PUT', 'DELETE']}, error: "ERROR_INVALID_HTTP_OPERATION" }],
								'resourceType': [ { rule: { includedInto: [ '1', '2', '3' ] }, error:"ERROR_BAD_RESOURCE_TYPE" } ],
								'resourceExternalKey': [ {rule:{customRegexp:{pattern:"^[\\s0-9a-zA-z-]*$", modifiers:"gi"}}, error:"ERROR_BAD_RESOURCE_EXTERNAL_KEY"}],
								'controllerClass': [{rule: { rangeLength: { 'min': 0, 'max': 250 } }, error: "ERROR_CONTROLLER_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.]*$", modifiers:"gi"}}, error:"ERROR_CONTROLLER_BAD_FORMAT"}]
							  };
	var wbParameterValidations = { 
			name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_PARAM_NAME_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_PARAM_NAME_BAD_FORMAT"}],
			overwriteFromUrl: [{rule: { includedInto: ['0','1'] }, error: "ERROR_PARAM_INVALID_OVERWRITE" }],
			localeType: [{rule: { includedInto: ['0','1','2'] }, error: "ERROR_PARAM_INVALID_LOCALETYPE" }]
	};


	$('#wburiedit').wbObjectManager( { fieldsPrefix:'wbe',
									  errorLabelsPrefix: 'erre',
									  errorGeneral:"erregeneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { 'uri': '/', 'httpOperation': 'GET', enabled: 0, 'resourceType': 1 },
									  validationRules: wbUriValidationRules
									});

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

	var tableDisplayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="#" class="wbEditParameterClass" id="wbEditParam_' + encodeURIComponent(record['key']) + '"><i class="icon-pencil"></i> Edit </a> | <a href="#" class="wbDeleteParameterClass" id="wbDelParam_' + encodeURIComponent(record['key'])+ '"><i class="icon-trash"></i> Delete </a>'; 
		} else
		if (fieldId=="lastModified") {
			var date = new Date();
			return date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		}
	}

	$('#wbUriParametersTable').wbSimpleTable( { columns: [ {display: "Id", fieldId:"key"}, {display: "Name", fieldId: "name"}, {display: "Value", fieldId: "value"},
	               								         {display: "Operations", fieldId:"_operations", customHandler: tableDisplayHandler}],
	               						 keyName: "key",
	               						 tableBaseClass: "table table-stripped table-bordered table-color-header",
	               						 paginationBaseClass: "pagination"
	               						});

	$('.btn-clipboard').WBCopyClipboardButoon({basePath: getAdminPath(), selector: '.btn-clipboard'});
	
	var wbhelpcontent = function(){
		return "TBD"
	}
	$(document).on('click', '.wbhelpclose', function (evente) {
		    $(".wbhelp-urls").popover('hide');
		  });
	$('.wbhelp-urls').popover({animation: false, html:true, placement: 'right', content: wbhelpcontent , title: "About site urls <button class='close wbhelpclose' type='button'>&times;</button>"});
	
	var ResourceExternalBlur = function (e) {
		var value = $.trim($(e.target).val());	
		var urlValue = "./search";
		if ($('input[name="resourceType"]:checked').val() == "1") {
			urlValue = "./search?externalKey={0}&class=wbpage".format(encodeURIComponent(value));
		} else if ($('input[name="resourceType"]:checked').val() == "2") {
			urlValue = "./search?externalKey={0}&class=wbfile".format(encodeURIComponent(value));			
		} 
		$('#wburiedit').wbCommunicationManager().ajax ( { url: urlValue,
			 httpOperation:"GET", 
			 payloadData:"",
			 functionSuccess: fSuccessSearch,
			 functionError: fErrorSearch
			} );
			
	};

	$('input[name="resourceType"]').on("change", function() {
		var val = $('input[name="resourceType"]:checked').val();
		if (val == 1 || val == 2) {
			$(".wbResourceExternalKey").show();
			$(".wbUrlController").hide();
			$("#wberesourceExternalKey").trigger("change");
		} else if (val == 3) {
			$(".wbResourceExternalKey").hide();
			$(".wbUrlController").show();			
		}
		
	});

	var displayHandler = function (fieldId, record) {
		if (fieldId == "lastModified") {
			return escapehtml("Last modified: " + Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} else if (fieldId == 'uri') {
			return escapehtml(record[fieldId]);
		} 		
	};
	
	$('#wbUriSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	var oResourceExternalKey = "";
	var uriExternalKey = "";
	var fSuccessGetUri = function (data) {
		$('#wbUriSummary').wbDisplayObject().display(data.data);
		$('#wburiedit').wbObjectManager().populateFieldsFromObject(data.data);
		$("#wberesourceType").trigger("change");
		oResourceExternalKey = data.data["resourceExternalKey"];
		
		var html = "NOT FOUND";
		if ('pages_links' in data.additional_data) {
			if (data.additional_data.pages_links.length >= 1) {
				var page = data.additional_data.pages_links[0];
				html = '<a href="./webpage.html?key={0}&externalKey={1}"> {2} </a>'.format(encodeURIComponent(page['key']), encodeURIComponent(page['externalKey']), encodeURIComponent(page['name']));
			}
			$('#wbresourcelink').html(html);
		} else if ('files_links' in data.additional_data) {
			if (data.additional_data.files_links.length >= 1) {
				var file = data.additional_data.files_links[0];
				html = '<a href="./webfile.html?key={0}"> {1} </a>'.format(encodeURIComponent(file['key']), escapehtml(file['name']));
			}
			$('#wbresourcelink').html(html);
		} else {
			$('#wbresourcelink').html(html);			
		}	
		
		// now get the parameters
		uriExternalKey = data.data['externalKey'];
		$('#wbAddParameterForm').wbCommunicationManager().ajax ( { url:"./wbparameter?ownerExternalKey=" + encodeURIComponent(uriExternalKey),
			 httpOperation:"GET", 
			 payloadData:"",
			 functionSuccess: fSuccessGetParameters,
			 functionError: fErrorGetParameters
			} );

	};
	
	var fSuccessSearch = function (data) {
		var result = data.data;
		var html = "NOT FOUND"
		if (result.length == 1) {
			if ($('input[name="resourceType"]:checked').val() == "1") {
				var page = result[0];
				html = '<a href="./webpage.html?key={0}&externalKey={1}"> {2} </a>'.format(encodeURIComponent(page['key']), encodeURIComponent(page['externalKey']), encodeURIComponent(page['name']));
			} else if ($('input[name="resourceType"]:checked').val() == "2") {
				var file = result[0];
				html = '<a href="./webfile.html?key={0}"> {1} </a>'.format(encodeURIComponent(file['key']), escapehtml(file['name']));			
			} 			
		}
		$('#wbresourcelink').html(html);			
	}

	var fErrorSearch = function (data) {
		alert(data);
	}

	$("#wberesourceExternalKey").on("change", ResourceExternalBlur);
	var fErrorGetUri = function (errors, data) {
		alert(errors);
	};

	// now handle the parameters
	$('#wbAddParameterBtn').click ( function (e) {
		e.preventDefault();
		$('#wbAddParameterForm').wbObjectManager().resetFields();
		$('#wbAddParameterModal').modal('show');
	});
	
	var fSuccessAdd = function ( data ) {
		$('#wbAddParameterModal').modal('hide');
		$('#wbUriParametersTable').wbSimpleTable().insertRow(data.data);			
	}
	var fErrorAdd = function (errors, data) {
		$('#wbAddParameterForm').wbObjectManager().setErrors(errors);
	}

	$('.wbAddParameterBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbAddParameterForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var parameter = $('#wbAddParameterForm').wbObjectManager().getObjectFromFields();
			parameter['ownerExternalKey'] = uriExternalKey;
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
		$('#wbUriParametersTable').wbSimpleTable().updateRowWithKey(data.data,data.data["key"]);
	}
	var fErrorUpdate = function (errors, data) {
		$('#wbUpdateParameterForm').wbObjectManager().setErrors(errors);
	}

	$('.wbUpdateParameterBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wbUpdateParameterForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var object = $('#wbUpdateParameterForm').wbObjectManager().getObjectFromFields();
			object['ownerExternalKey'] = uriExternalKey;
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
		$('#wbUriParametersTable').wbSimpleTable().deleteRowWithKey(data.data["key"]);
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
		var object = $('#wbUriParametersTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbUpdateParameterForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbUpdateParameterModal').modal('show');		
	});

	$(document).on ("click", ".wbDeleteParameterClass", function (e) {
		e.preventDefault();
		$('#wbDeleteParameterForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDelParam_".length);
		var object = $('#wbUriParametersTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDeleteParameterForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeleteParameterModal').modal('show');		
	});
	
	var fSuccessGetParameters = function (data) {
		$('#wbUriParametersTable').wbSimpleTable().setRows(data.data);
	}
	var fErrorGetParameters = function (errors, data) {
		alert(errors);
	}

	// end handle parameters
	
	var uriKey = getURLParameter('key'); 
	
	$('#wburiedit').wbCommunicationManager().ajax ( { url:"./wburi/{0}?include_links=1".format(encodeURIComponent(uriKey)),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetUri,
												 functionError: fErrorGetUri
												} );
	
	var externalKeysArrays = { 'files':[], 'pages': [] }
	
	var fS_GetFilesPageSummary = function (data) {		
		var array = { 'files':[], 'pages': [] };
		for (var i in data['data_files']) {
			var item = data['data_files'][i];
			var val = "{0} {{1}}".format(item['name'], item['externalKey']);
			array['files'].push(val);
		};
		for (var i in data['data_pages']) {
			var item = data['data_pages'][i];
			var val = "{0} {{1}}".format(item['name'], item['externalKey']);
			array['pages'].push(val);
		}		
		externalKeysArrays = array;
	};
	
	var fE_GetFilesPageSummary = function (data) {
		alert(errors);
	};
	
	var updaterFunction = function(item) {
		//return item;
		x = item.lastIndexOf('{');
		if (x>=0) {
			y = item.lastIndexOf('}');
			if (y>=0) {
				return item.substring(x+1,y);
			}
		}
		return item;
	};
	
	var sourceFunction = function(query, process) {
		//what is selected files or pages ?
		if ($('input[name="resourceType"]:checked').val() == "1") {
			return externalKeysArrays['pages'];
		} else if ($('input[name="resourceType"]:checked').val() == "2") {
			return externalKeysArrays['files'];			
		} 
		return [];		
	}
	
	$('#wberesourceExternalKey').typeahead( {
		source: sourceFunction,
		items: 5,
		updater: updaterFunction
	});

	
	$('#wburiedit').wbCommunicationManager().ajax ( { url:"./wbsummary_pages_files",
		 httpOperation:"GET", 
		 payloadData:"",
		 functionSuccess: fS_GetFilesPageSummary,
		 functionError: fE_GetFilesPageSummary
		} );
	
	var fSuccessEdit = function ( data ) {
		window.location.href = "./weburis.html";
	}
	var fErrorEdit = function (errors, data) {
		$('#wburiedit').wbObjectManager().setErrors(errors);
	}

	$('.wbUriEditSaveBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wburiedit').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var uri = $('#wburiedit').wbObjectManager().getObjectFromFields();
			var jsonText = JSON.stringify(uri);
			$('#wburiedit').wbCommunicationManager().ajax ( { url: "./wburi/" + encodeURIComponent(uriKey),
															 httpOperation:"PUT", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wburiedit').wbObjectManager(),
															 functionSuccess: fSuccessEdit,
															 functionError: fErrorEdit
															 } );
		}
	});
	
	$('.wbUriEditCancelBtnClass').click ( function (e) {
		e.preventDefault();
		window.location.href = "./weburis.html";
	});

		
});