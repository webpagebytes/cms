var errorsGeneral = {
	'WBMESSAGE_EMPTY_NAME': 'Key cannot be empty string',
	'WBMESSAGE_DUPLICATE_NAME': 'Key already exists for this locale',
	'WBMESSAGE_EMPTY_LCID': 'Locale identifier cannot be empty'
};

$().ready( function () {
	
	$('#wbAddMessageForm').wbObjectManager( { fieldsPrefix:'wba',
								  errorLabelsPrefix: 'erra',
								  errorGeneral:"errageneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  fieldsDefaults: { isTranslated: 0 },
								  validationRules: {
									'name': { rangeLength: { 'min': 1, 'max': 100 } },
									'value': { rangeLength: { 'min': 0, 'max': 2000 } }
									}
								});
	$('#wbUpdateMessageForm').wbObjectManager( { fieldsPrefix:'wbu',
								  errorLabelsPrefix: 'erru',
								  errorGeneral:"errageneral",
								  errorLabelClassName: 'errorvalidationlabel',
								  errorInputClassName: 'errorvalidationinput',
								  fieldsDefaults: { isTranslated: 0 },
								  validationRules: {
									'name': { rangeLength: { 'min': 1, 'max': 100 } },
									'value': { rangeLength: { 'min': 0, 'max': 2000 } }
									}
								});

	$('#wbDeleteMessageForm').wbObjectManager( { fieldsPrefix:'wbd',
							  errorLabelsPrefix: 'errd',
							  errorGeneral:"errageneral",
							  errorLabelClassName: 'errorvalidationlabel',
							  errorInputClassName: 'errorvalidationinput'
							 });

	var displayHandler = function (fieldId, record) {
		if ((fieldId == '_operations') && (record['diff'] != 'default')) {
			var innerHtml = "<a href='#' class='wbEditMessageLinkClass' id='m_edit_id_{0}'> <i class='icon-pencil'></i> Edit </a> | <a href='#' class='wbDeleteMessageLinkClass' id='m_del_id_{1}'> <i class='icon-trash'></i> Delete </a>".format(record['key'], record['key']);			
			return innerHtml;
		}
		if ((fieldId == '_operations') && (record['diff'] == 'default')) {
			var innerHtml = "<a href='#' class='wbAddMessageLinkClass' id='m_add_id_{0}'> <i class='icon-plus-sign'></i> Add </a>".format(record['key']);			
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
	
	var pageLcid = getURLParameter('lcid') || ""; 
	var selectedLcid = "";
	var defaultLcid = "";
	$('#wbmessagestable').wbTable( { columns: [ {display: "KEY", fieldId:"name", customHandling:true, customHandler: displayHandler}, {display: "Is translated", fieldId:"isTranslated", customHandling:true, customHandler: displayHandler}, {display: "Value", fieldId: "value", customHandling:true, customHandler: displayHandler}, 
								{display: "Operations", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
					 keyName: "key",
					 tableBaseClass: "table table-condensed",
					 paginationBaseClass: "pagination",
					 itemsPerPage: 50
					});
	
	var fFixHeightMessages = function()
	{
		$('.wbmessagescontent').css('min-height', $('.wbmessages').css('height'));
	}
	var fSuccessGetMessages = function (data) {
		$.each(data, function(index, item) {
			$('#wbmessagestable').wbTable().insertRow(item);
		});	
		fFixHeightMessages();		
	}
	
	var fErrorGetMessages = function (errors, data) {
		alert(errors);
	}
	
	var fSuccessGetSupportedLanguages = function (data) {
		var html = "";
		var selectedLanguage = "";
		for(var i=0; i<data.length; i++) {
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
			html += "<li class='{0}'> <a href='./webmessages.html?lcid={1}'> {2} ({3}) {4} </a> </li>".format(isSelectedLanguage?"active":"", escapehtml(data[i].lcid), escapehtml(data[i].name), escapehtml(data[i].lcid), isDefaultLanguage?"*":"");
		}
		$("#wbsupportedlanguages").html(html);
		var param = (selectedLanguage.length > 0) ? ("?lcid=" + escapehtml(selectedLanguage)): "";
		$("#wbmessagestable").wbCommunicationManager().ajax( { url:"./wbmessagecompare?lcid=" + escapehtml(selectedLcid) + "&dlcid=" + escapehtml(defaultLcid),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetMessages,
												 functionError: fErrorGetMessages
												} );			
	}
	var fErrorGetSupportedLanguages = function (errors, data) {
		alert(errors);
	}
	
	
	$("#wbsupportedlanguages").wbCommunicationManager().ajax ( { url:"./wbsupportedlanguages",
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetSupportedLanguages,
												 functionError: fErrorGetSupportedLanguages
												} );	
	
	$(".wbCreateMessageBtnClass").click (function (e) {
		e.preventDefault();
		$('#wbAddMessageForm').wbObjectManager().resetFields();
		
		$('#wbAddMessageModal').modal('show');
	});
	
	var fSuccessAdd = function (data) {	
		$('#wbAddMessageModal').modal('hide');
		window.location.reload();	
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
															 functionError: fErrorAdd
															 } );
		}
	});

	var fSuccessUpdate = function (data) {	
		$('#wbUpdateMessageModal').modal('hide');
		window.location.reload();
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
			$('#wbmessagesid').wbCommunicationManager().ajax ( { url: "./wbmessage/" + obj['key'],
															 httpOperation:"PUT", 
															 payloadData:jsonText,
															 wbObjectManager : $('#wbUpdateMessageForm').wbObjectManager(),
															 functionSuccess: fSuccessUpdate,
															 functionError: fErrorUpdate
															 } );
		}
	});

	var fSuccessDelete = function (data) {	
		$('#wbDeleteMessageModal').modal('hide');		
		window.location.reload();
	};
	
	var fErrorDelete = function (errors, data) {
		$('#wbDeleteMessageForm').wbObjectManager().setErrors(errors);
	};

	$(".wbSaveDeleteMessageBtnClass").click(function (e) {
		e.preventDefault();
		var obj = $('#wbDeleteMessageForm').wbObjectManager().getObjectFromFields();
		$('#wbmessagesid').wbCommunicationManager().ajax ( { url: "./wbmessage/" + obj['key'],
														 httpOperation:"DELETE", 
														 payloadData:"",
														 wbObjectManager : $('#wbUpdateMessageForm').wbObjectManager(),
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete
														 } );
	});

	$(document).on ("click", ".wbAddMessageLinkClass", function (e) {
		e.preventDefault();
		$('#wbAddMessageForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("m_add_id_".length);
		var object = $('#wbmessagestable').wbTable().getRowDataWithKey(key);
		var newObject = $.extend(true, {}, object);
		newObject['key']="";
		newObject['externalKey']="";
		newObject['isTranslated']="0";
		$('#wbAddMessageForm').wbObjectManager().populateFieldsFromObject(newObject);
		$('#wbAddMessageModal').modal('show');		
	});
	
	$(document).on ("click", ".wbEditMessageLinkClass", function (e) {
		e.preventDefault();
		$('#wbUpdateMessageForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("m_edit_id_".length);
		var object = $('#wbmessagestable').wbTable().getRowDataWithKey(key);
		$('#wbUpdateMessageForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbUpdateMessageModal').modal('show');		
	});
	
	$(document).on ("click", ".wbDeleteMessageLinkClass", function (e) {
		e.preventDefault();
		$('#wbDeleteMessageForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("m_del_id_".length);
		var object = $('#wbmessagestable').wbTable().getRowDataWithKey(key);
		$('#wbDeleteMessageForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeleteMessageModal').modal('show');		
	});

});