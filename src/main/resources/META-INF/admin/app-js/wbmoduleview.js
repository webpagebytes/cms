var errorsGeneral = {
};

$().ready( function () {
	
	var swfzc = getAdminPath() + '/zeroclipboard/ZeroClipboard.swf';
	ZeroClipboard.setDefaults( { moviePath: swfzc } );
	var zcButtons = $.find('.btn-clipboard');
	$.each (zcButtons, function (index, elem) {
		var zc = new ZeroClipboard(elem);
	});


	var tableDisplayHandler = function (fieldId, record) {
		if (fieldId=="_operations") {
			return '<a href="#" class="wbEditParameterClass" id="wbEditParam_' + escapehtml(record['key']) + '"><i class="icon-pencil"></i> Edit </a> | <a href="#" class="wbDeleteParameterClass" id="wbDelParam_' + escapehtml(record['key'])+ '"><i class="icon-trash"></i> Delete </a>'; 
		} else
		if (fieldId=="lastModified") {
			var date = new Date();
			return date.toFormatString(record[fieldId], "dd/mm/yyyy hh:mm:ss");
		}
	}
				
	$('#wbPageModuleParametersTable').wbTable( { columns: [ {display: "Id", fieldId:"key"}, {display: "External Id", fieldId:"externalKey"}, {display: "Name", fieldId: "name"}, {display: "Value", fieldId: "value"},
									{display:"Last Modified", fieldId:"lastModified", customHandling:true, customHandler: tableDisplayHandler}, {display: "Edit/delete", fieldId:"_operations", customHandling:true, customHandler: tableDisplayHandler}],
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
			var innerHtml = '<a href="./webpagemodule.html?key=' + escapehtml(record['key']) + '">' + escapehtml(record['name']) + '</a>';
			return innerHtml;
		}		
		return record[fieldId];
	}
	var pageModuleSourceHandler = function (fieldId, record) {
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
		if (fieldId == 'htmlSource') {
			return record[fieldId]; // the htmlSource is displayed in a textarea element
		}
		return escapehtml(record[fieldId]);
	}
	
	$('#wbPageModuleSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	$('#wbPageModuleView').wbDisplayObject( { fieldsPrefix: 'wbPageModuleView', customHandler: pageModuleSourceHandler} );
	
	var fSuccessGetPage = function (data) {
		$('#wbPageModuleSummary').wbDisplayObject().display(data);
		$('#wbPageModuleView').wbDisplayObject().display(data);
	}
	var fErrorGetPage = function (errors, data) {
		alert(errors);
	}

	var pageKey = getURLParameter('key'); 
	var pageModuleExternalKey = getURLParameter('externalKey');;
	
	$('.wbPageModuleViewEditLink').click ( function (e) {
		e.preventDefault();
		window.location.href = "./webpagemoduleedit.html?key=" + pageKey;
	} );
	
	$('#wbPageModuleSummary').wbCommunicationManager().ajax ( { url:"./wbpagemodule/" + pageKey,
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetPage,
												 functionError: fErrorGetPage
												} );
	
												
});