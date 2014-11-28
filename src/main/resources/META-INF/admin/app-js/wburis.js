/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
var errorsGeneral = {
	'ERROR_URI_START_CHAR': "Site url must to start with /",
	'ERROR_URI_LENGTH': 'Site url length must be between 1 and 255 characters',
	'ERROR_INVALID_VALUE':'Invalid value'
	
};

$().ready( function () {
	var wbUriValidationRules = {
								'uri': [ {rule:{startsWith: '/'}, error: 'ERROR_URI_START_CHAR'}, {rule:{customRegexp:{pattern:"^/([0-9a-zA-Z_~.-]*(\{[0-9a-zA-Z_.*-]+\})*[0-9a-zA-Z_~.-]*/?)*$", modifiers:"gi"}}, error:"ERROR_INVALID_VALUE"}, { rule:{rangeLength: { 'min': 1, 'max': 255 } }, error:"ERROR_URI_LENGTH"} ],
								'controllerClass': [{ rule:{ maxLength: 255 }, error: "ERROR_INVALID_VALUE"}, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_INVALID_VALUE"}],
								'httpOperation': [{ rule: { includedInto: ['GET', 'POST', 'PUT', 'DELETE']}, error: "ERROR_INVALID_VALUE" }],
								'resourceType': [ { rule: { includedInto: [ '1', '2', '3' ] }, error:"ERROR_INVALID_VALUE" } ],
								'resourceExternalKey': [ {rule:{customRegexp:{pattern:"^[\\s0-9a-zA-z-]*$", modifiers:"gi"}}, error:"ERROR_INVALID_VALUE"}]
							  };
	$('#wburiadd').wbObjectManager( { fieldsPrefix:'wba',
									  errorLabelsPrefix: 'erra',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { 'uri': '/', 'httpOperation': 'GET', 'enabled': 0, 'resourceType': 1 },
									  validationRules: wbUriValidationRules

									});

	$('#wburiduplicate').wbObjectManager( { fieldsPrefix:'wbc',
								  errorLabelsPrefix: 'errc',
								  errorGeneral:"errcgeneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  fieldsDefaults: { 'uri': '/', 'httpOperation': 'GET', enabled: 0, 'resourceType': 1 },
								  validationRules: wbUriValidationRules
								});

	$('#wburiupdate').wbObjectManager( { fieldsPrefix:'wbu',
									  errorLabelsPrefix: 'erru',
									  errorGeneral:"errugeneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { 'uri': '/', 'httpOperation': 'GET', enabled: 0, 'resourceType': 1 },
									  validationRules: wbUriValidationRules
									});
	$('#wburidelete').wbObjectManager( { fieldsPrefix: 'wbd',
										 errorGeneral:"errdgeneral",
										 errorLabelsPrefix: 'errd',
										 errorLabelClassName: 'errorvalidationlabel',
									    } );							
	var itemsOnPage = 20;	
	
	$('.btn-clipboard').WBCopyClipboardButoon({basePath: getAdminPath(), selector: '.btn-clipboard'});
	
	var wbhelpcontent = function(){
		return "TBD"
	}
	
	var displayHandler = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a href="./weburiedit.html?extKey=' + encodeURIComponent(record['externalKey'])+ '"><i class="icon-pencil"></i> Edit </a> | <a href="#" class="wbdeleteuri" id="wburidel_' + encodeURIComponent(record['privkey'])+ '"><i class="icon-trash"></i> Delete </a>' 
					+ '| <a href="#" class="wbduplicateuri" id="wburidup_' + encodeURIComponent(record['privkey'])+ '"><i class="aicon-duplicate"></i> Duplicate </a>'; 
		} else
		if (fieldId == "lastModified") {
			return escapehtml(Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} else if (fieldId == "enabled") {
			return record[fieldId] == '1' ? "<div class='circle circle-green'></div>" : "<div class='circle circle-red'></div>";
		}
	};
	
	var columnClick = function (table, fieldId, dir) {	
		var url = "./weburis.html?sort_dir={0}&sort_field={1}".format(encodeURIComponent(dir), encodeURIComponent(fieldId));
		var newUrl = window.document.location.href;
		newUrl = replaceURLParameter(newUrl, "sort_field", fieldId);
		newUrl = replaceURLParameter(newUrl, "sort_dir", dir);				
		window.document.location.href = newUrl;		
	};

	$('#wbtable').wbSimpleTable( { columns: [ {display: "External Id", fieldId:"externalKey", isHtmlDisplay: true}, 
	                                          {display: "Site url", fieldId: "uri", isHtmlDisplay:true},
	                                          {display: "Live", fieldId: "enabled", customHandler: displayHandler, isHtmlDisplay:true}, 
	                                          {display: "Method", fieldId:"httpOperation", isHtmlDisplay:true}, 
	                                          {display: "Last Modified", fieldId:"lastModified", customHandler: displayHandler, isHtmlDisplay:true}, 
	                                          {display: "Operations", fieldId:"_operations", customHandler: displayHandler}],
							 keyName: "privkey",
							 tableBaseClass: "table table-condensed table-color-header",
							 paginationBaseClass: "pagination",
							 headerColumnBaseClass: "header-uri-table",
							 headerColumnIdClassPrefix: "uri-table-",							 
							 handlerColumnClick: columnClick,
							 noLinesContent: "<tr> <td colspan='6'>There are no site urls defined. </td></tr>"
							});
	
	
	var fSuccessDuplicateParams = function ( data ) {
		$('#wbModalUriDuplicate').modal('hide');
		populateUris();				
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
															 wbObjectManager : $('wburiduplicate').wbObjectManager(),
															 functionSuccess: fSuccessDuplicateParams,
															 functionError: fErrorDuplicateParams
															 } );		
	};

	
	var fErrorDuplicate = function (errors, data) {
		$('#wburiduplicate').wbObjectManager().setErrors(errors);
	};

	var fSuccessDelete = function ( data ) {
		$('#wbModalUriDelete').modal('hide');
		populateUris();			
	};
	
	var fErrorDelete = function (errors, data) {
		$('#wburidelete').wbObjectManager().setErrors(errors);
	};

	$('#wburiadd').wbCommunicationManager();
	$('#wburidelete').wbCommunicationManager(); 
	
	$('#wbAddUrlBtn').click( function (e) {
		e.preventDefault();
		window.location.href = "./weburiadd.html";
	});
	
	$(document).on ("click", ".wbduplicateuri", function (e) {
		e.preventDefault();
		$('#wburiduplicate').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wburidup_".length);
		var object = $('#wbtable').wbSimpleTable().getRowDataWithKey(key);
		$('#wburiduplicate').wbObjectManager().populateFieldsFromObject(object);
		$('#wbModalUriDuplicate').modal('show');		
	});

	$(document).on ("click", '.wbdeleteuri', function (e) {
		e.preventDefault();
		$('#wburidelete').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wburidel_".length);
		var object = $('#wbtable').wbSimpleTable().getRowDataWithKey(key);
		$('#wburidelete').wbObjectManager().populateFieldsFromObject(object);
		$('#wbModalUriDelete').modal('show');		
	});
	
	$('.uriDuplicateSave').click( function (e) {
		e.preventDefault();
		var errors = $('#wburiduplicate').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var obj = $('#wburiduplicate').wbObjectManager().getObjectFromFields();
			delete obj["externalKey"];
			var jsonText = JSON.stringify(obj);
			$('#wburiduplicate').wbCommunicationManager().ajax ( { url: "./wburi",
															 httpOperation:"POST", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wburiduplicate').wbObjectManager(),
															 functionSuccess: fSuccessDuplicate,
															 functionError: fErrorDuplicate
															 } );
		}
	});


	$('.uriDeleteSave').click( function (e) {
		e.preventDefault();
		var object = $('#wburidelete').wbObjectManager().getObjectFromFields();			
		$('#wburidelete').wbCommunicationManager().ajax ( { url: "./wburi/" + escapehtml(object['privkey']),
														 httpOperation:"DELETE", 
														 payloadData:"",
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete
													} );
		
	});

	var populateUris = function() {
		
		var fSuccessGetUris = function (data) {		
			$('#wbtable').wbSimpleTable().setRows(data.data);
			
			$('#wbtable').wbSimpleTable().setPagination( document.location.href, data['additional_data']['total_count'], itemsOnPage, "page");
			
			textItems = { "0":"", "empty":"", "1":"(1 item)", "greater_than_1": "({0} items)"};
			
			$(".wburi-table-stats").html(escapehtml(getTextForItems(data['additional_data']['total_count'], textItems)));
			
			$('#spinnerTable').WBSpinner().hide();			
		}
		var fErrorGetUris = function (errors, data) {
			alert(data);
			$('#spinnerTable').WBSpinner().hide();
		}
			
		var page = getURLParameter('page') || 1;
		if (page <= 0) page = 1;
		var index_start = (page-1)*itemsOnPage;
		var sort_dir = encodeURIComponent(getURLParameter('sort_dir') || "dsc");
		var sort_field = encodeURIComponent(getURLParameter('sort_field') || "lastModified");
		
		$('#wbtable').wbSimpleTable().addSortIconToColumnHeader(sort_field, sort_dir);
		
		var uris_url = "./wburi?sort_dir={0}&sort_field={1}&index_start={2}&count={3}".format(sort_dir, sort_field, index_start, itemsOnPage); 
		$('#wburiadd').wbCommunicationManager().ajax ( { url: uris_url,
														 httpOperation:"GET", 
														 payloadData:"",
														 functionSuccess: fSuccessGetUris,
														 functionError: fErrorGetUris
														} );
	}
	populateUris();
});