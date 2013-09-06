var errorsGeneral = {
};

$().ready( function () {
	
	var displayHandler = function (fieldId, record) {
		if (fieldId=="default") {
			if (record[fieldId] == 'true') {
				return escapehtml("Default");
			} else {
				return "";
			}
		}
	};
				
	$('#wblangstablediv').wbTable( { columns: [ {display: "Language name", fieldId:"name"}, {display: "Language code", fieldId:"lcid"}, {display: "Default language", fieldId: "default", customHandling: true, customHandler: displayHandler} ],
						 keyName: "lcid",
						 tableBaseClass: "table table-condensed table-color-header",
						 paginationBaseClass: "pagination",
						 itemsPerPage: 1000
						});

	$('#wblangstablediv').wbCommunicationManager();
	
	
	var fSuccessGetLanguages = function (data) {
		$.each(data.data, function(index, item) {
			$('#wblangstablediv').wbTable().insertRow(item);
		});				

	}
	var fErrorGetLanguages = function (errors, data) {
		alert(data);
	}
	
	$('#wblangstablediv').wbCommunicationManager().ajax ( { url:"./wbsupportedlanguages",
													 httpOperation:"GET", 
													 payloadData:"",
													 functionSuccess: fSuccessGetLanguages,
													 functionError: fErrorGetLanguages
													} );

});