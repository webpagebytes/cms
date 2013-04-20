var errorsGeneral = {
};

$().ready( function () {
	$('#wbAddPageModuleForm').wbObjectManager( { fieldsPrefix:'wba',
									  errorLabelsPrefix: 'erra',
									  errorGeneral:"errageneral",
									  errorLabelClassName: 'errorvalidationlabel',
									  errorInputClassName: 'errorvalidationinput',
									  fieldsDefaults: { isTemplateSource: 0 },
									  validationRules: {
										'name': { rangeLength: { 'min': 1, 'max': 100 } }
									  }
									});
	$('#wbDeletePageModuleForm').wbObjectManager( { fieldsPrefix: 'wbd',
									 errorGeneral:"errdgeneral",
									 errorLabelsPrefix: 'errd',
									 errorLabelClassName: 'errorvalidationlabel',
									} );							

	var displayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="./webpagemodule.html?key=' + escapehtml(record['key']) + '&externalKey=' + escapehtml(record['externalKey']) + '"><i class="icon-pencil"></i> Edit </a> | <a href="#" class="wbDeletePageModuleClass" id="wbDeletePageModule_' +record['key']+ '"><i class="icon-trash"></i> Delete </a>'; 
		} else
		if (fieldId=="lastModified") {
			var date = new Date();
			return date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		}
	}
				
	$('#wbPageModulesTable').wbTable( { columns: [ {display: "Id", fieldId:"key"}, {display: "External Id", fieldId:"externalKey"}, {display: "Name", fieldId: "name"}, 
									{display:"Last Modified", fieldId:"lastModified", customHandling: true, customHandler: displayHandler}, {display: "Edit/delete", fieldId:"_operations", customHandling:true, customHandler: displayHandler}],
						 keyName: "key",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination"
						});

	$('#wbAddPageModuleForm').wbCommunicationManager();
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

	$(document).on ("click", '.wbDeletePageModuleClass', function (e) {
		e.preventDefault();
		$('#wbDeletePageModuleForm').wbObjectManager().resetFields();
		var key = $(this).attr('id').substring("wbDeletePageModule_".length);
		var object = $('#wbPageModulesTable').wbTable().getRowDataWithKey(key);
		$('#wbDeletePageModuleForm').wbObjectManager().populateFieldsFromObject(object);
		$('#wbDeletePageModuleModal').modal('show');		
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
		$('#wbDeletePageModuleForm').wbCommunicationManager().ajax ( { url: "./wbpagemodule/" + escapehtml(object['key']),
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