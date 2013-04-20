var errorsGeneral = {
};

$().ready( function () {
	
	var displayHandler = function (fieldId, record) {
		if (fieldId=="enable_") {
				return "<input type='checkbox' id='enable_" + record['lcid'] + "'>";
			} 
		if (fieldId=="default_") {
				return "<input type='checkbox' id='default_" + record['lcid'] + "'>";
			} 	
	};
				
	$('#wblangstablediv').wbTable( { columns: [ {display: "Enable language", fieldId:"enable_", customHandling: true, customHandler: displayHandler}, 
												{display: "Language name", fieldId:"name"}, 
												{display: "Language code", fieldId:"lcid"}, 
												{display: "Default language", fieldId: "default_", customHandling: true, customHandler: displayHandler} ],
						 keyName: "lcid",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination",
						 itemsPerPage: 1000
						});

	$('#wblangstablediv').wbCommunicationManager();
	
	var fSuccessSupportedLanguages = function (data) {
		$.each(data, function(index, item) {
			var idenable = '#enable_' + item['lcid'];
			$(idenable).prop('checked', true);
			if (item['default'] == 'true') {
				var iddefault = '#default_' + item['lcid'];
				$(iddefault).prop('checked', true);
			}			
		});
	};
	var fErrorSupportedLanguages = function (data) {
		alert(data);
	};	
	var fSuccessGetLanguages = function (data) {
		$.each(data, function(index, item) {
			$('#wblangstablediv').wbTable().insertRow(item);
		});				
		$('#wblangstablediv').wbCommunicationManager().ajax ( { url:"./wbsupportedlanguages",
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessSupportedLanguages,
													 functionError: fErrorSupportedLanguages
													} );
		
	};
	var fErrorGetLanguages = function (errors, data) {
		alert(data);
	};
	
	$('#wblangstablediv').wbCommunicationManager().ajax ( { url:"./wblanguages",
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessGetLanguages,
													 functionError: fErrorGetLanguages
													} );

	var fSuccessSetSupportedLanguages = function(data) {
		window.location.href = './websettingslangs.html';
	};

	var fErrorSetSupportedLanguages = function(data) {
		alert(data);
	};
	$('.wbCancelLanguagesBtnClass').click ( function (e) {
		e.preventDefault();
		window.location.href = './websettingslangs.html';
	});
	
	$('.wbEditLanguagesBtnClass').click ( function (e) {
		e.preventDefault();
		var langs = new Array();
		var elements = $('input[id^=enable_]');
		$.each (elements, function(index, elem) {
			if ($(elem).is(":checked")) {
				var id = $(elem).attr('id').substr('enable_'.length);		
				var langItem = {};
				langItem['lcid'] = id;
				
				var iddefault = 'default_' + id;
				if ($("#" + iddefault).is(":checked")) {
					langItem['default'] = 'true';
				} else {
					langItem['default'] = 'false';
				}
				langs.push(langItem);
			}
		});
		var payload = JSON.stringify(langs);
		
		$('#wblangstablediv').wbCommunicationManager().ajax ( { url:"./wbsupportedlanguages",
											 httpOperation:"PUT", 
											 payloadData: payload,
											 functionSuccess: fSuccessSetSupportedLanguages,
											 functionError: fErrorSetSupportedLanguages
											} );

		
	});	
});