/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
var errorsGeneral = {
};

$().ready( function () {
	
	$('.btn-clipboard').WBCopyClipboardButoon({buttonHtml:"<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copy to clipboard</div>", basePath: getAdminPath(), selector: '.btn-clipboard'});
	$('.btn-clipboard').WBCopyClipboardButoon().on("aftercopy", function (e) {
		$('.btn-clipboard').WBCopyClipboardButoon().reset();
		$(e.target).html("<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copied!</div>");
	});


	var displayHandler = function (fieldId, record) {
		if (fieldId == 'lastModified') {
			return escapehtml( "Last modified: " + Date.toFormatString(record[fieldId], "today|dd/mm/yyyy hh:mm"));
		} 
		if (fieldId == 'name') {
			var innerHtml = '<a href="./webpagemodule.html?extKey=' + escapehtml(record['externalKey']) + '">' + escapehtml(record['name']) + '</a>';
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
		if (fieldId == 'name') {
			return record[fieldId];
		}
		return escapehtml(record[fieldId]);
	}
	
	$('#wbPageModuleSummary').wbDisplayObject( { fieldsPrefix: 'wbsummary', customHandler: displayHandler} );
	$('#wbPageModuleView').wbDisplayObject( { fieldsPrefix: 'wbPageModuleView', customHandler: pageModuleSourceHandler} );
	
	var pageModuleKey = getURLParameter('extKey'); 
	var fSuccessGetModule = function (data) {
		pageModuleKey = data.data["externalKey"];
		$('#wbPageModuleSummary').wbDisplayObject().display(data.data);
		$('#wbPageModuleView').wbDisplayObject().display(data.data);
		$('#spinnerTable').WBSpinner().hide();
	}
	var fErrorGetModule = function (errors, data) {
		alert(errors);
		$('#spinnerTable').WBSpinner().hide();
	}

	var externalKey = getURLParameter('extKey');;
	
	$('.wbPageModuleViewEditLink').click ( function (e) {
		e.preventDefault();
		window.location.href = "./webpagemoduleedit.html?extKey=" + encodeURIComponent(externalKey);
	} );
	
	$('#wbPageModuleSummary').wbCommunicationManager().ajax ( { url:"./wbpagemodule/ext/" + encodeURIComponent(externalKey),
												 httpOperation:"GET", 
												 payloadData:"",
												 functionSuccess: fSuccessGetModule,
												 functionError: fErrorGetModule
												} );
	
												
});