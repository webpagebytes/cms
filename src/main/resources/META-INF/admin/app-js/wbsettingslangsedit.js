/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
var errorsGeneral = {
		"WB_NO_DEFAULT_LANGUAGES": "One of the selected language must be set as the default language.",
		"WB_NO_LANGUAGES": "At least one language must be selected from the languages catalog."
};

$().ready( function () {
	
	var displayHandler = function (fieldId, record) {
		if (fieldId == 'default') {
			if (record[fieldId] == "1") {
				return '<input type="checkbox" class="js_lang_default" data-code="{0}" checked>'.format(escapehtml(record['code']));
			} else
			{
				return '<input type="checkbox" class="js_lang_default" data-code="{0}">'.format(escapehtml(record['code']));		
			}
		} else if (fieldId == 'remove') {
			return '<a href="#" data-code="{0}" class="js_lang_remove"><i class="icon-trash"></i></a>'.format(escapehtml(record['code']));
		}
		

		return "";
	};
	$('#wbSelectedLanguages').wbSimpleTable( { columns: [ {display: "Name", fieldId:"name"}, {display: "Code", fieldId:"code"}, 
	                                                      {display: "Default language", fieldId: "default", customHandler: displayHandler},
	                                                      {display: "", fieldId:"remove", customHandler: displayHandler}],
						 keyName: "code",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination",
						 itemsPerPage: 1000,
						 noLinesContent: "<tr> <td colspan='4'>There are no site urls defined. </td></tr>"
						});
						
	$(".letter").click ( function (e) {
		e.preventDefault();
		$(".langs_group").hide();
		var letterClass = "langs_" + $.trim(e.target.text);
		$("." + letterClass).show();
		$(".letter").parent().removeClass("active");
		$(e.target).parent().addClass("active");
	});	
	$("#wbSelectedLanguages").on("click", ".js_lang_default", function (e) {		
			var code = $(e.target).attr('data-code');
			var record = $('#wbSelectedLanguages').wbSimpleTable().getRowDataWithKey(code);
			var prevValue = record['default'];
			var allRecords = $('#wbSelectedLanguages').wbSimpleTable().getAllRowsData();
			for (var index in allRecords) {
				allRecords[index]['default'] = "0";
			}
			$('#wbSelectedLanguages').wbSimpleTable().setRows(allRecords);
			if (prevValue == "0") {
				record['default'] = "1";
			} else
			{
				record['default'] = "0";
			}
			$('#wbSelectedLanguages').wbSimpleTable().updateRowWithKey(record, code);

	});

	$("#wbSelectedLanguages").on("click", ".js_lang_remove", function (e) {	
		e.preventDefault();
		var code = $(e.target).parent().attr('data-code');
		$('#wbSelectedLanguages').wbSimpleTable().deleteRowWithKey(code);
		$('#enable_'+ code).prop('checked', false);
	});

	$(".langs_A").show();
	
	var clickEnableHandler = function(elem, isDefault){
		var tds = $(elem).parent().siblings();
		if (tds.length == 2) {
			var name = $(tds[0]).text();
			var code = $(tds[1]).text();
			if ($(elem).is(':checked')) {
				if (isDefault) {
					isDefault = "1";
				} else {
					isDefault = "0"
				}
				var record = {'name': name, 'code':code, 'default':isDefault };
				$('#wbSelectedLanguages').wbSimpleTable().insertRow(record);											
			} else {
				$('#wbSelectedLanguages').wbSimpleTable().deleteRowWithKey(code);
			}
			
		}		
	}
	$("input[id^='enable_']").click( function (e) {
		clickEnableHandler(e.target, false);
	});
	
	var fSuccessSupportedLanguages = function (data) {
		$.each(data.data, function(index, item) {
			var idenable = 'enable_' + item['lcid'];
			var elem = $("#"+idenable).prop("checked", true);
			clickEnableHandler(elem, item['default']=="true");
		});
	};
	var fErrorSupportedLanguages = function (data) {
		alert(data);
	};	

	
	$('#wblangcatalog').wbCommunicationManager();
	$('#wblangcatalog').wbCommunicationManager().ajax ( { url:"./wbsupportedlanguages",
		 httpOperation:"GET", 
		 payloadData:"",
		 functionSuccess: fSuccessSupportedLanguages,
		 functionError: fErrorSupportedLanguages,
		 functionAuth: authHandler
		} );

	var fSuccessSetSupportedLanguages = function(data){
		window.location.href="./websettingslangs.html";
	}
	var fErrorSetSupportedLanguages = function(data){
		for(x in data) {
			if (x in errorsGeneral) {
				$('.errorsubmit').html(escapehtml(errorsGeneral[x]));
			} else
				$('.errorsubmit').html(escapehtml(data[x]))
		}
	}

	$(".wbSubmitSupportedLanguages").click( function (e) {
		e.preventDefault();
		var allRecords = $('#wbSelectedLanguages').wbSimpleTable().getAllRowsData();
		var array = new Array();
		var hasDefault = false;
		for (var index in allRecords) {
			var item = {};
			item['lcid'] = allRecords[index]['code'];
			if (allRecords[index]['default'] == "1") {
				item['default'] = 'true';
			} else {
				item['default'] = 'false';
			}
			array.push(item);
		}
		var payload = JSON.stringify(array);
	
		$('#wblangstablediv').wbCommunicationManager().ajax ( { url:"./wbsupportedlanguages",
			 httpOperation:"PUT", 
			 payloadData: payload,
			 functionSuccess: fSuccessSetSupportedLanguages,
			 functionError: fErrorSetSupportedLanguages,
			 functionAuth: authHandler
			} );

		
	});
	

});