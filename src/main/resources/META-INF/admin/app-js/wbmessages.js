/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
var errorsGeneral = {
	'ERROR_MESSAGE_NAME_LENGTH': 'Message name length must be between 1 and 250 characters',
	'WBMESSAGE_DUPLICATE_NAME': 'Key already exists for this locale',
	'WBMESSAGE_EMPTY_LCID': 'Locale identifier cannot be empty',
	'ERROR_MESSAGE_NAME_BAD_FORMAT':'Invalid format for message name: allowed characters are 0-9, a-z, A-Z, -, _,. (, is not an allowed character)'
};

$().ready( function () {
	var wbMessagesValidations = { 
		name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_MESSAGE_NAME_LENGTH" }, {rule: { customRegexp: {pattern:"^[0-9a-zA-Z_.-]*$", modifiers:"gi"} }, error: "ERROR_MESSAGE_NAME_BAD_FORMAT" }],
		value: [{rule: { rangeLength: { 'min': 1, 'max': 2000 }}}],
		isTranslated: [{rule: { includedInto: ['0', '1']}}]
	};

	$('#wbAddMessageForm').wbObjectManager( { fieldsPrefix:'wba',
								  errorLabelsPrefix: 'erra',
								  errorGeneral:"errageneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  fieldsDefaults: { isTranslated: 0 },
								  validationRules: wbMessagesValidations
								});
	$('#wbDuplicateMessageForm').wbObjectManager( { fieldsPrefix:'wbc',
		  errorLabelsPrefix: 'errc',
		  errorGeneral:"errcgeneral",
		  errorLabelClassName: 'errorvalidationlabel',
		  errorInputClassName: 'errorvalidationinput',
		  fieldsDefaults: { isTranslated: 0 },
		  validationRules: wbMessagesValidations
		});
	
	$('#wbUpdateMessageForm').wbObjectManager( { fieldsPrefix:'wbu',
								  errorLabelsPrefix: 'erru',
								  errorGeneral:"errageneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  fieldsDefaults: { isTranslated: 0 },
								  validationRules: wbMessagesValidations
								});

	$('#wbDeleteMessageForm').wbObjectManager( { fieldsPrefix:'wbd',
							  errorLabelsPrefix: 'errd',
							  errorGeneral:"errageneral",
							  errorLabelClassName: 'errorvalidationlabel',
							  errorInputClassName: 'errorvalidationinput'
							 });

	var itemsOnPage = 50;	

	var displayHandler = function (fieldId, record) {
		if ((fieldId == '_operations') && (record['diff'] != 'default')) {
			var innerHtml = "<a href='#' class='wbEditMessageLinkClass' id='m_edit_id_{0}'> <i class='icon-pencil'></i> Edit </a> ".format( encodeURIComponent(record['externalKey'])) +
						  "| <a href='#' class='wbDeleteMessageLinkClass' id='m_del_id_{0}'> <i class='icon-trash'></i> Delete </a>".format(encodeURIComponent(record['externalKey'])) +
						  "| <a href='#' class='wbDuplicateMessageLinkClass' id='m_dup_id_{0}'><i class='aicon-duplicate'></i> Duplicate </a>".format(encodeURIComponent(record['externalKey']));
			return innerHtml;
		}
		if ((fieldId == '_operations') && (record['diff'] == 'default')) {
			var innerHtml = "<a href='#' class='wbAddMessageLinkClass' id='m_add_id_{0}'> <i class='icon-plus-sign'></i> Add </a>".format(encodeURIComponent(record['externalKey']));			
			return innerHtml;
		}
		
		var value = "" + record[fieldId];
		if (fieldId == 'lastModified') {
			var date = new Date();
			value = date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		} 
		if (value.length > 30) {
			value = value.substring(0,30);
			value += "..."
		}
		value = escapehtml(value);
		if (record['diff'] == 'default')
		{
			value = "<span class='diffdefault'> {0} </span>".format(value);
		}
		if (record['diff'] == 'current')
		{
			value = "<span class='diffcurrent'> {0} </span>".format(value);
		}
		
		return value;
	}
	
	var columnClick = function (table, fieldId, dir) {	
		var newUrl = window.document.location.href;
		newUrl = replaceURLParameter(newUrl, "sort_field", fieldId);
		newUrl = replaceURLParameter(newUrl, "sort_dir", dir);				
		window.document.location.href = newUrl;		
	}

	var pageLcid = getURLParameter('lcid') || ""; 
	var selectedLcid = "";
	var defaultLcid = "";
	$('#wbmessagestable').wbSimpleTable( { columns: [ {display: "KEY", fieldId:"name", customHandler: displayHandler, isHtmlDisplay:true}, 
	                                                  {display: "Value", fieldId: "value", customHandler: displayHandler}, 
	                                                  {display: "Operations", fieldId:"_operations", customHandler: displayHandler}],
					 keyName: "externalKey",
					 tableBaseClass: "table table-condensed",
					 paginationBaseClass: "pagination",
                     headerColumnBaseClass: "header-uri-table",
                     headerColumnIdClassPrefix: "uri-table-",							 
                     handlerColumnClick: columnClick,
                     noLinesContent: "<tr> <td colspan='3'>There are no messages defined. </td></tr>"
	});
	
	var fFixHeightMessages = function()
	{
		$('#wbsupportedlanguages').css('min-height', $('.tab-content').css('height'));
	}
	
	var populateMessages = function() {
		var fSuccessGetMessages = function (data) {
			$('#wbmessagestable').wbSimpleTable().setRows(data.data);
			$('#wbmessagestable').wbSimpleTable().setPagination( document.location.href, data['additional_data']['total_count'], itemsOnPage, "page");
	
			textItems = { "0":"", "empty":"", "1":"(1 item)", "greater_than_1": "({0} items)"};		
			$(".tablestats").html(escapehtml(getTextForItems(data['additional_data']['total_count'], textItems)));
			fFixHeightMessages();		
			$('#spinnerTable').WBSpinner().hide();
		}
		
		var fErrorGetMessages = function (errors, data) {
			alert(errors);
			$('#spinnerTable').WBSpinner().hide();
		}
		
		var page = getURLParameter('page') || 1;
		if (page <= 0) page = 1;
		var index_start = (page-1)*itemsOnPage;
		var sort_dir = encodeURIComponent(getURLParameter('sort_dir') || "asc");
		var sort_field = encodeURIComponent(getURLParameter('sort_field') || "name");	
		$('#wbmessagestable').wbSimpleTable().addSortIconToColumnHeader(sort_field, sort_dir);
		
		var messages_url = "./wbmessagecompare?sort_dir={0}&sort_field={1}&index_start={2}&count={3}&lcid={4}&dlcid={5}".format(sort_dir, sort_field, index_start, itemsOnPage, encodeURIComponent(selectedLcid), encodeURIComponent(defaultLcid)); 

		$("#wbmessagestable").wbCommunicationManager().ajax( { url: messages_url,
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetMessages,
												 functionError: fErrorGetMessages,
												 functionAuth: authHandler
												} );			

	}
	
	var fSuccessGetSupportedLanguages = function (payload) {
		var data = payload.data;
		var html = "";
		var selectedLanguage = "";
		for(var i=0; i< data.length; i++) {
			var isSelectedLanguage = false;
			var isDefaultLanguage = (data[i].default == "true");	
			if (isDefaultLanguage) {
				defaultLcid = data[i].lcid;
			}
			if (pageLcid.length>0) {
				if (data[i].lcid == pageLcid) {
					selectedLanguage = pageLcid;
					selectedLcid = pageLcid;
					isSelectedLanguage = true;
				}
			} else if (data[i].default == "true") {
			 isSelectedLanguage = true;
			 selectedLcid = data[i].lcid;
			}
			html += "<li class='{0}'> <a href='./webmessages.html?lcid={1}'> {2} ({3}) {4} </a> </li>".format(isSelectedLanguage?"active":"", encodeURIComponent(data[i].lcid), escapehtml(data[i].name), escapehtml(data[i].lcid), isDefaultLanguage?"*":"");
		}
		$("#wbsupportedlanguages").html(html);
		
		populateMessages();
		
	}
	var fErrorGetSupportedLanguages = function (errors, data) {
		alert(errors);
	}
	
	
	$("#wbsupportedlanguages").wbCommunicationManager().ajax ( { url:"./wbsupportedlanguages",
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetSupportedLanguages,
												 functionError: fErrorGetSupportedLanguages,
												 functionAuth: authHandler
												} );	
	
	$(".wbCreateMessageBtnClass").click (function (e) {
		e.preventDefault();
		$('#wbAddMessageForm').wbObjectManager().resetFields();
		
		$('#wbAddMessageModal').modal('show');
	});
	
	var fSuccessAdd = function (data) {	
		$('#wbAddMessageModal').modal('hide');
		populateMessages();	
	};
	
	var fErrorAdd = function (errors, data) {
		$('#wbAddMessageForm').wbObjectManager().setErrors(transferProperties(errors, errorsGeneral));
	};
	
	$(".wbSaveAddMessageBtnClass").click(function (e) {
		e.preventDefault();
		var errors = $('#wbAddMessageForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var obj = $('#wbAddMessageForm').wbObjectManager().getObjectFromFields();
			obj['lcid'] = selectedLcid;
			var jsonText = JSON.stringify(obj);
			$('#wbmessagesid').wbCommunicationManager().ajax ( { url: "./wbmessage",
															 httpOperation:"POST", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbAddMessageForm').wbObjectManager(),
															 functionSuccess: fSuccessAdd,
															 functionError: fErrorAdd,
															 functionAuth: authHandler
															 } );
		}
	});

	var fSuccessDuplicate = function (data) {	
		$('#wbDuplicateMessageModal').modal('hide');
		populateMessages();	
	};
	
	var fErrorDuplicate = function (errors, data) {
		$('#wbDuplicateMessageForm').wbObjectManager().setErrors(transferProperties(errors, errorsGeneral));
	};
	
	$(".wbSaveDuplicateMessageBtnClass").click(function (e) {
		e.preventDefault();
		var errors = $('#wbDuplicateMessageForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var obj = $('#wbDuplicateMessageForm').wbObjectManager().getObjectFromFields();
			obj['lcid'] = selectedLcid;
			var jsonText = JSON.stringify(obj);
			$('#wbmessagesid').wbCommunicationManager().ajax ( { url: "./wbmessage",
															 httpOperation:"POST", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbDuplicateMessageForm').wbObjectManager(),
															 functionSuccess: fSuccessDuplicate,
															 functionError: fErrorDuplicate,
															 functionAuth: authHandler
															 } );
		}
	});

	var fSuccessUpdate = function (data) {	
		$('#wbUpdateMessageModal').modal('hide');
		populateMessages();
	};
	var fErrorUpdate = function (errors, data) {
		$('#wbUpdateMessageForm').wbObjectManager().setErrors(errors);
	};

	$(".wbSaveUpdateMessageBtnClass").click(function (e) {
		e.preventDefault();
		var errors = $('#wbUpdateMessageForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var obj = $('#wbUpdateMessageForm').wbObjectManager().getObjectFromFields();
			var jsonText = JSON.stringify(obj);
			$('#wbmessagesid').wbCommunicationManager().ajax ( { url: "./wbmessage/" + encodeURIComponent(obj['externalKey']),
															 httpOperation:"PUT", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbUpdateMessageForm').wbObjectManager(),
															 functionSuccess: fSuccessUpdate,
															 functionError: fErrorUpdate,
															 functionAuth: authHandler
															 } );
		}
	});

	var fSuccessDelete = function (data) {	
		$('#wbDeleteMessageModal').modal('hide');		
		populateMessages();
	};
	
	var fErrorDelete = function (errors, data) {
		$('#wbDeleteMessageForm').wbObjectManager().setErrors(errors);
	};

	$(".wbSaveDeleteMessageBtnClass").click(function (e) {
		e.preventDefault();
		var obj = $('#wbDeleteMessageForm').wbObjectManager().getObjectFromFields();
		$('#wbmessagesid').wbCommunicationManager().ajax ( { url: "./wbmessage/" + encodeURIComponent(obj['externalKey']),
														 httpOperation:"DELETE", 
														 payloadData:"",
														 wbObjectManager : $('#wbUpdateMessageForm').wbObjectManager(),
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete,
														 functionAuth: authHandler
														 } );
	});

	$(document).on ("click", ".wbAddMessageLinkClass", function (e) {
		e.preventDefault();
		$('#wbAddMessageForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("m_add_id_".length);
		var object = $('#wbmessagestable').wbSimpleTable().getRowDataWithKey(key);
		var newObject = $.extend(true, {}, object);
		newObject['externalKey']="";
		newObject['isTranslated']="0";
		$('#wbAddMessageForm').wbObjectManager().populateFieldsFromObject(newObject);
		$('#wbAddMessageModal').modal('show');		
	});

	$(document).on ("click", ".wbDuplicateMessageLinkClass", function (e) {
		e.preventDefault();
		$('#wbDuplicateMessageForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("m_dup_id_".length);
		var object = $('#wbmessagestable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDuplicateMessageForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDuplicateMessageModal').modal('show');		
	});

	$(document).on ("click", ".wbEditMessageLinkClass", function (e) {
		e.preventDefault();
		$('#wbUpdateMessageForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("m_edit_id_".length);
		var object = $('#wbmessagestable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbUpdateMessageForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbUpdateMessageModal').modal('show');		
	});
	
	$(document).on ("click", ".wbDeleteMessageLinkClass", function (e) {
		e.preventDefault();
		$('#wbDeleteMessageForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("m_del_id_".length);
		var object = $('#wbmessagestable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDeleteMessageForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeleteMessageModal').modal('show');		
	});

});