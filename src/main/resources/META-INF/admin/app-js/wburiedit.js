
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
	'ERROR_RESOURCE_CONTENT_TYPE_LENGTH': 'Resource content type length must be between 1 and 50 characters'
	
};

$().ready( function () {
	var wbUriValidationRules = {
								'uri': [ {rule:{startsWith: '/'}, error: 'ERROR_URI_START_CHAR'}, {rule:{customRegexp:{pattern:"^/([0-9a-zA-Z_~.-]*(\{[0-9a-zA-Z_.*-]+\})*[0-9a-zA-Z_~.-]*/?)*$", modifiers:"gi"}}, error:"ERROR_URI_BAD_FORMAT"}, { rule:{rangeLength: { 'min': 1, 'max': 250 } }, error:"ERROR_URI_LENGTH"} ],
								'controllerClass': [{ rule:{ maxLength: 250 }, error: "ERROR_CONTROLLER_LENGTH"}, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_CONTROLLER_BAD_FORMAT"}],
								'httpOperation': [{ rule: { includedInto: ['GET', 'POST', 'PUT', 'DELETE']}, error: "ERROR_INVALID_HTTP_OPERATION" }],
								'resourceType': [ { rule: { includedInto: [ '1', '2' ] }, error:"ERROR_BAD_RESOURCE_TYPE" } ],
								'resourceExternalKey': [ {rule:{customRegexp:{pattern:"^[\\s0-9a-zA-z-]*$", modifiers:"gi"}}, error:"ERROR_BAD_RESOURCE_EXTERNAL_KEY"}]
							  };


	$('#wburiedit').wbObjectManager( { fieldsPrefix:'wbe',
									  errorLabelsPrefix: 'erre',
									  errorGeneral:"erregeneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { 'uri': '/', 'httpOperation': 'GET', enabled: 0, 'resourceType': 1 },
									  validationRules: wbUriValidationRules
									});
	
	$('.btn-clipboard').WBCopyClipboardButoon({basePath: getAdminPath(), selector: '.btn-clipboard'});
	
	var wbhelpcontent = function(){
		return "TBD"
	}
	$(document).on('click', '.wbhelpclose', function (evente) {
		    $(".wbhelp-urls").popover('hide');
		  });
	$('.wbhelp-urls').popover({animation: false, html:true, placement: 'right', content: wbhelpcontent , title: "About site urls <button class='close wbhelpclose' type='button'>&times;</button>"});
	
	var displayHandler = function (fieldId, record) {
		if (fieldId == "lastModified") {
			return escapehtml("Last modified: " + Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} else if (fieldId == 'uri') {
			return escapehtml(record[fieldId]);
		} 		
	};
	
	$('#wbUriSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	
	var fSuccessGetUri = function (data) {
		$('#wbUriSummary').wbDisplayObject().display(data.data);
		$('#wburiedit').wbObjectManager().populateFieldsFromObject(data.data);
	};
	
	var fErrorGetUri = function (errors, data) {
		alert(errors);
	};

	var uriKey = getURLParameter('key'); 
	$('#wburiedit').wbCommunicationManager().ajax ( { url:"./wburi/" + encodeURIComponent(uriKey),
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
		items: 3,
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