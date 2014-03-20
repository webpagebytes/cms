var errorsGeneral = {
	'ERROR_FILE_NAME_LENGTH': 'File name length must be between 1 and 250 characters',
	'ERROR_FILE_NAME_BAD_FORMAT': 'Invalid format for file name: allowed characters are 0-9, a-z, A-Z, -, _,. (, is not an allowed character)',
	'ERROR_FILE_FILENAME_LENGTH': 'File file path cannot be empty',
	'ERROR_FILE_ENABLED_BAD_FORMAT': 'Invalid file live value'
};

$().ready( function () {
	var wbFileValidations = { 
			name: [{rule: { rangeLength: { 'min': 0, 'max': 250 } }, error: "ERROR_FILE_NAME_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9 a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_FILE_NAME_BAD_FORMAT"}],
			filename: [{rule: { rangeLength: { 'min': 1, 'max': 1024 } }, error: "ERROR_FILE_FILENAME_LENGTH" }],
			enabled: [{rule: { includedInto: ['0', '1']}, error: "ERROR_FILE_ENABLED_BAD_FORMAT" }]
	};
	$('#wbAddFileForm').wbObjectManager( { fieldsPrefix:'wb',
									  errorLabelsPrefix: 'err',
									  errorGeneral:"errgeneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { enabled: 0 },
									  validationRules: wbFileValidations
									});
	$('#wbDeleteFileForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );							

	var itemsOnPage = 20;	

	var displayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="./webfile.html?key=' + encodeURIComponent(record['key']) + '"><i class="icon-eye-open"></i> View </a> | <a href="#" class="wbDeleteFileClass" id="wbDeleteFile_' +encodeURIComponent(record['key']) + '"><i class="icon-trash"></i> Delete </a>'; 
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
		if (fieldId=="blobKey"){
			switch (record["shortType"]) {
				case "image":
					return '<img src="{0}">'.format( encodeURI(record['thumbnailPublicUrl']) );
				default:
					return '<a href="./wbdownload/{0}">{1}</a>'.format(encodeURIComponent(record['key']),escapehtml(record['fileName']));				
			}
		}
		
	}
				
	var columnClick = function (table, fieldId, dir) {	
		var newUrl = window.document.location.href;
		newUrl = replaceURLParameter(newUrl, "sort_field", fieldId);
		newUrl = replaceURLParameter(newUrl, "sort_dir", dir);				
		window.document.location.href = newUrl;		
	}

	$('#wbFilesTable').wbSimpleTable( { columns: [ {display: "External key", fieldId: "externalKey", isHtmlDisplay:true},
	                                               {display: "Name", fieldId: "name", isHtmlDisplay:true},
	                                               {display:"Content type", fieldId:"contentType", isHtmlDisplay:true},
	                                               {display:"Size", fieldId:"size", customHandler: displayHandler, isHtmlDisplay:true},
	                                               {display:"Last Modified", fieldId:"lastModified", customHandler: displayHandler, isHtmlDisplay:true},
	                                               {display:"File", fieldId:"blobKey", customHandling: true, customHandler: displayHandler},
	                                               {display: "Operations", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
	                                    keyName: "key",
	                                    tableBaseClass: "table table-condensed table-color-header",
	                                    paginationBaseClass: "pagination",
	                                    headerColumnBaseClass: "header-uri-table",
	                                    headerColumnIdClassPrefix: "uri-table-",							 
	                                    handlerColumnClick: columnClick,
	                                    noLinesContent: "<tr> <td colspan='7'>There are no files uploaded. </td></tr>"
						});

	$('#wbAddFileForm').wbCommunicationManager();
	$('#wbDeleteFileForm').wbCommunicationManager();

	$('#wbAddFileBtn').click( function (e) {
		e.preventDefault();
		$('#wbAddFileForm').wbObjectManager().resetFields();
		$('#wbAddFileModal').modal('show');			

	});

	var fSuccessAdd = function ( data ) {
		$('#wbAddFileModal').modal('hide');
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
		$('#wbDeleteFileForm').wbCommunicationManager().ajax ( { url: "./wbfile/" + encodeURIComponent(object['key']),
														 httpOperation:"DELETE", 
														 payloadData:"",
														 functionSuccess: fSuccessDelete,
														 functionError: fErrorDelete
													} );
		
	});

	var populateFiles = function() {
		var fSuccessGetAll = function (data) {
			$('#wbFilesTable').wbSimpleTable().setRows(data.data);
			$('#wbFilesTable').wbSimpleTable().setPagination( document.location.href, data['additional_data']['total_count'], itemsOnPage, "page");
			textItems = { "0":"", "empty":"", "1":"(1 item)", "greater_than_1": "({0} items)"};		
			$(".tablestats").html(escapehtml(getTextForItems(data['additional_data']['total_count'], textItems)));
		
		}
		var fErrorGetAll = function (errors, data) {
		
		}
		
		var page = getURLParameter('page') || 1;
		if (page <= 0) page = 1;
		var index_start = (page-1)*itemsOnPage;
		var sort_dir = encodeURIComponent(getURLParameter('sort_dir') || "asc");
		var sort_field = encodeURIComponent(getURLParameter('sort_field') || "name");	
		$('#wbFilesTable').wbSimpleTable().addSortIconToColumnHeader(sort_field, sort_dir);
		
		var files_url = "./wbfile?sort_dir={0}&sort_field={1}&index_start={2}&count={3}".format(sort_dir, sort_field, index_start, itemsOnPage); 
		
		var shortType = getURLParameter('type');
		if (shortType && shortType.length) {
			files_url = replaceURLParameter(files_url,"type", shortType);
		}
	
		
		$('#wbAddFileForm').wbCommunicationManager().ajax ( { url: files_url,
														 httpOperation:"GET", 
														 payloadData:"",
														 functionSuccess: fSuccessGetAll,
														 functionError: fErrorGetAll
														} );
	}
	populateFiles();
});