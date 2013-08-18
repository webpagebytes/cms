var errorsGeneral = {
	'ERROR_PAGE_MODULENAME_LENGTH': 'Web page module name length must be between 1 and 250 characters ',
	'ERROR_PAGE_MODULENAME_BAD_FORMAT': 'Invalid format for web page module name: allowed characters are 0-9, a-z, A-Z, -, _,. (, is not an allowed character)'
};

$().ready( function () {
	var wbPageModuleValidations = { 
		name: [{rule: { rangeLength: { 'min': 1, 'max': 250 } }, error: "ERROR_PAGE_MODULENAME_LENGTH" }, {rule:{customRegexp:{pattern:"^[0-9a-zA-Z_.-]*$", modifiers:"gi"}}, error:"ERROR_PAGE_MODULENAME_BAD_FORMAT"}]
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
				
	$('#wbPageModulesTable').wbTable( { columns: [ {display: "External Id", fieldId:"externalKey"}, {display: "Name", fieldId: "name"}, 
									{display:"Last Modified", fieldId:"lastModified", customHandling: true, customHandler: displayHandler}, {display: "Operations", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination"
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
		$('#wbPageModulesTable').wbTable().insertRow(data);			
	}
	var fErrorAdd = function (errors, data) {
		$('#wbAddPageModuleForm').wbObjectManager().setErrors(errors);
	}

	var fSuccessDuplicate = function ( data ) {
		$('#wbDuplicatePageModuleModal').modal('hide');
		$('#wbPageModulesTable').wbTable().insertRow(data);			
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
		var object = $('#wbPageModulesTable').wbTable().getRowDataWithKey(key);
		$('#wbDeletePageModuleForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeletePageModuleModal').modal('show');		
	});

	$(document).on ("click", '.wbDuplicatePageModuleClass', function (e) {
		e.preventDefault();
		$('#wbDuplicatePageModuleForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDuplicatePageModule_".length);
		var object = $('#wbPageModulesTable').wbTable().getRowDataWithKey(key);
		$('#wbDuplicatePageModuleForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDuplicatePageModuleModal').modal('show');		
	});

	var fSuccessDelete = function ( data ) {
		$('#wbDeletePageModuleModal').modal('hide');	
		$('#wbPageModulesTable').wbTable().deleteRowWithKey(data["key"]);
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

	var fSuccessGetModules = function (data) {
		$.each(data, function(index, item) {
			$('#wbPageModulesTable').wbTable().insertRow(item);
		});				

	}
	var fErrorGetModules = function (errors, data) {
	
	}
	
	$('#wbAddPageModuleForm').wbCommunicationManager().ajax ( { url:"./wbpagemodule",
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessGetModules,
													 functionError: fErrorGetModules
													} );

});