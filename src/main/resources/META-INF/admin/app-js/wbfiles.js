/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
var errorsGeneral = {
	'ERROR_FILE_NAME_LENGTH': 'File name length must be between 1 and 250 characters',
	'ERROR_FILE_FILENAME_LENGTH': 'File file path cannot be empty',
	'ERROR_FILE_INVALID': 'Invalid file data'
};

$().ready( function () {
	var wbFileValidations = { 
			fileName: [{rule: { rangeLength: { 'min': 1, 'max': 1024 } }, error: "ERROR_FILE_FILENAME_LENGTH" }]
	};
	var wbFolderValidations = { 
			fileName: [{rule: { rangeLength: { 'min': 0, 'max': 250 } }, error: "ERROR_FILE_NAME_LENGTH" }],
			directoryFlag: [{rule: { includedInto: ['1']}, error: "ERROR_FILE_INVALID" }]
	};

	$('#wbAddFileForm').wbObjectManager( { fieldsPrefix:'wb',
									  errorLabelsPrefix: 'err',
									  errorGeneral:"errgeneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { directoryFlag: 0 },
									  validationRules: wbFileValidations
									});
	$('#wfAddFolderForm').wbObjectManager( { fieldsPrefix:'wf',
		  errorLabelsPrefix: 'err',
		  errorGeneral:"errgeneral",
		  errorLabelClassName: 'errorvalidationlabel',
		  errorInputClassName: 'errorvalidationinput',
		  fieldsDefaults: { directoryFlag: 1 },
		  validationRules: wbFolderValidations
		});

	$('#wufUploadFolderForm').wbObjectManager( { fieldsPrefix:'wuf',
		  errorLabelsPrefix: 'err',
		  errorGeneral:"errgeneral",
		  errorLabelClassName: 'errorvalidationlabel',
		  errorInputClassName: 'errorvalidationinput',
		  validationRules: wbFolderValidations
		});

	$('#wbDeleteFileForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );							

	var itemsOnPage = 20;	

	var displayHandlerFile = function(fieldId, record) {
		if (fieldId=="_folder") {
			return "";
		} else
		if (fieldId=="_operations") {
			return '<a href="./webfile.html?extKey=' + encodeURIComponent(record['externalKey']) + '"><i class="icon-eye-open"></i> View </a> | <a href="#" class="wbDeleteFileClass" id="wbDeleteFile_' +encodeURIComponent(record['privkey']) + '"><i class="icon-trash"></i> Delete </a>'; 
		} else
		if (fieldId=="lastModified") {
			return escapehtml(Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} else
		if (fieldId=="size") {
			var size = parseInt(record['size']);
			if (size < 1024) {
				return size + ' bytes';
			} else 
			if (size < 1048576) {
				var f = size/1024;
				return (f.toFixed(2)) + ' KB';
			} else {
				var f = size/1048576;
				return (f.toFixed(2)) + ' MB';
			};
			
			return size;
		} else
	    if (fieldId=="contentType" || fieldId=="fileName") {
			return escapehtml(record[fieldId]);
		}	
	};
	var displayHandlerFolder = function (fieldId, record) {
		if (fieldId=="_folder") {
			return '<a href="./webfiles.html?parent=' + encodeURIComponent(record['externalKey']) + '"> &nbsp; <i class="fa fa-folder fa-lg"></i> &nbsp; </a>';
		} else
		if (fieldId=="_operations") {
			return '<a href="./webfile.html?extKey=' + encodeURIComponent(record['externalKey']) + '"><i class="icon-eye-open"></i> View </a> | <a href="#" class="wbDeleteFileClass" id="wbDeleteFile_' +encodeURIComponent(record['privkey']) + '"><i class="icon-trash"></i> Delete </a>'; 
		} else
		if (fieldId=="lastModified") {
			return escapehtml(Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} else
		if (fieldId=="fileName") {
			return escapehtml(record[fieldId]);
		} else		    
		if (fieldId=="size" || fieldId == "contentType") {
	    	return "";
	    } 
	};
	var displayLevelUp = function (fieldId, record) {
		if (fieldId=="fileName") {
			var ownerOfOwner = ((record['owner']['ownerExtKey'] || "").length > 0) ? record['owner']['ownerExtKey'] : "";
			return '<a href="./webfiles.html?parent=' + encodeURIComponent(ownerOfOwner) + '"><i class="fa fa-level-up fa-flip-horizontal fa-lg"></i>..</a>';
		} else
	    return "";	   
	};
	var displayHandler = function (fieldId, record) {
		if (record['level-up']) {
			return displayLevelUp(fieldId, record);
		} else
		if (record['directoryFlag'] == 1) {
			return displayHandlerFolder(fieldId, record); 
		} else {
			return displayHandlerFile(fieldId, record);
		}
	};
				
	var columnClick = function (table, fieldId, dir) {	
		var newUrl = window.document.location.href;
		newUrl = replaceURLParameter(newUrl, "sort_field", fieldId);
		newUrl = replaceURLParameter(newUrl, "sort_dir", dir);				
		window.document.location.href = newUrl;		
	};

	$('#wbFilesTable').wbSimpleTable( { columns: [ {display: "", fieldId:"_folder", customHandler: displayHandler},
	                                               {display: "File name", fieldId: "fileName", customHandler: displayHandler, isHtmlDisplay:true},
	                                               {display:"Content type", fieldId:"contentType", customHandler: displayHandler, isHtmlDisplay:true},
	                                               {display:"Size", fieldId:"size", customHandler: displayHandler, isHtmlDisplay:true},
	                                               {display:"Last Modified", fieldId:"lastModified", customHandler: displayHandler, isHtmlDisplay:true},
	                                               {display: "Operations", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
	                                    keyName: "privkey",
	                                    tableBaseClass: "table table-condensed table-color-header",
	                                    paginationBaseClass: "pagination",
	                                    headerColumnBaseClass: "header-uri-table",
	                                    headerColumnIdClassPrefix: "uri-table-",							 
	                                    handlerColumnClick: columnClick,
	                                    noLinesContent: "<tr> <td colspan='7'>There are no files uploaded. </td></tr>"
						});

	$('#wbAddFileForm').wbCommunicationManager();
	$('#wbDeleteFileForm').wbCommunicationManager();
	$('#wfAddFolderForm').wbCommunicationManager();
	$('#wufUploadFolderForm').wbCommunicationManager();
	
	$('#wbAddFileBtn').click( function (e) {
		e.preventDefault();
		$('#wbAddFileForm').wbObjectManager().resetFields();
		$('#wbAddFileModal').modal('show');			

	});

	$('#wbUploadFolderBtn').click( function (e) {
		e.preventDefault();
		$('#wufUploadFolderForm').wbObjectManager().resetFields();
		$('#wufUploadFolderModal').modal('show');			

	});

	$('#wbAddFolderBtn').click( function (e) {
		e.preventDefault();
		$('#wfAddFolderForm').wbObjectManager().resetFields();
		$('#wbAddFolderModal').modal('show');			

	});

	var fSuccessAdd = function ( data ) {
		$('#wbAddFileModal').modal('hide');
		$('#wbAddFolderModal').modal('hide');
		$('#wufUploadFolderModal').modal('hide');
		
		populateFiles();			
	}
	var fErrorAdd = function (data) {
		
		//$('#wbFileForm').wbObjectManager().setErrors(errors);
	}

	$('#wbAddFileForm').ajaxForm({ success: fSuccessAdd, error: fErrorAdd });

	$('.wbSaveAddFileBtnClass').click( function (e) {
		var errors = $('#wbAddFileForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if (! $.isEmptyObject(errors)) {
			e.preventDefault();
		}
		var parent = getURLParameter('parent') || "";
		$("#wbownerExtKey").val(parent);
	});

	$('#wufUploadFolderForm').ajaxForm({ success: fSuccessAdd, error: fErrorAdd });
	$('.wbSaveUploadFolderBtnClass').click( function (e) {
		var errors = $('#wufUploadFolderForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if (! $.isEmptyObject(errors)) {
			e.preventDefault();
		}
		var parent = getURLParameter('parent') || "";
		$("#wufownerExtKey").val(parent);
	});

	$('.wbSaveAddFolderBtnClass').click( function (e) {
		e.preventDefault();
		var errors = $('#wfAddFolderForm').wbObjectManager().validateFieldsAndSetLabels( errorsGeneral );
		if ($.isEmptyObject(errors)) {
			var obj = $('#wfAddFolderForm').wbObjectManager().getObjectFromFields();
			var parent = getURLParameter('parent') || "";
			obj["ownerExtKey"] = parent;
			var jsonText = JSON.stringify(obj);
			$('#wfAddFolderForm').wbCommunicationManager().ajax ( { url: "./wbfile",
															 httpOperation:"POST", 
															 payloadData:jsonText,
															 functionSuccess: fSuccessAdd,
															 functionError: fErrorAdd
															 } );
		}

	});

	$(document).on ("click", '.wbDeleteFileClass', function (e) {
		e.preventDefault();
		$('#wbDeleteFileForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDeleteFile_".length);
		var object = $('#wbFilesTable').wbSimpleTable().getRowDataWithKey(key);
		$('#wbDeleteFileForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeleteFileModal').modal('show');		
	});

	var fSuccessDelete = function ( data ) {
		$('#wbDeleteFileModal').modal('hide');	
		populateFiles();			
	}
	var fErrorDelete = function (errors, data) {
		$('#wbDeleteFileForm').wbObjectManager().setErrors(errors);
	}

	$('.webSaveDeleteBtnClass').click( function (e) {
		e.preventDefault();
		var object = $('#wbDeleteFileForm').wbObjectManager().getObjectFromFields();			
		$('#wbDeleteFileForm').wbCommunicationManager().ajax ( { url: "./wbfile/" + encodeURIComponent(object['privkey']),
														 httpOperation:"DELETE", 
														 payloadData:"",
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete
													} );
		
	});

	var populateFiles = function() {

		var page = getURLParameter('page') || 1;
		var parent = (getURLParameter('parent') || "").trim();
		if (page <= 0) page = 1;
		var index_start = (page-1)*itemsOnPage;
		var sort_dir = encodeURIComponent(getURLParameter('sort_dir') || "dsc");
		var sort_field = encodeURIComponent(getURLParameter('sort_field') || "lastModified");	
	
		var fSuccessGetAll = function (data) {
		    if (data.additional_data.owner)
		    {
		      $('#wbFileOwnerDir').html('<a href="{0}">{1}</a>/{2}'.format('./webfiles.html?parent='+encodeURIComponent(data.additional_data.owner.ownerExtKey), escapehtml(data.additional_data.ownerFullDirectoryPath), escapehtml(data.additional_data.owner.fileName)));
			}
			$('#wbFilesTable').wbSimpleTable().setRows(data.data);
			if (parent.length > 0 && !$.isEmptyObject(data['additional_data']['owner'])){
				$('#wbFilesTable').wbSimpleTable().insertRow({'level-up':1, 'owner': data['additional_data']['owner']}, 0);
				
			}
			$('#wbFilesTable').wbSimpleTable().setPagination( document.location.href, data['additional_data']['total_count'], itemsOnPage, "page");
			textItems = { "0":"", "empty":"", "1":"(1 item)", "greater_than_1": "({0} items)"};		
			$(".wbfiles-table-stats").html(escapehtml(getTextForItems(data['additional_data']['total_count'], textItems)));
			$('#spinnerTable').WBSpinner().hide();
		}
		var fErrorGetAll = function (errors, data) {
			alert(data);
			$('#spinnerTable').WBSpinner().hide();
		}
		
		$('#wbFilesTable').wbSimpleTable().addSortIconToColumnHeader(sort_field, sort_dir);
		
		var files_url = "./wbfile?sort_dir={0}&sort_field={1}&index_start={2}&count={3}&parent={4}".format(sort_dir, sort_field, index_start, itemsOnPage, encodeURIComponent(parent)); 
		
		
		$('#wbAddFileForm').wbCommunicationManager().ajax ( { url: files_url,
														 httpOperation:"GET", 
														 payloadData:"",
														 functionSuccess: fSuccessGetAll,
														 functionError: fErrorGetAll
														} );
	}
	populateFiles();
});