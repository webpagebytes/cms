/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/
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
	/*
	$('#cmssearchbox').wbCommunicationManager({async:false});
		
	var loadDataHandlerFunction = function(wbSearchBox) {
		var fSuccessGetResources = function (data) {
			console.log('received wbresources ' + data.data.length);
			$.each(data.data, function(index, item) {
				wbSearchBox.crud('insert', item, 'key');
			});							
		};
		var fErrorGetResources = function (data) {
			alert(data);
		}
		$('#cmssearchbox').wbCommunicationManager().ajax ( { url:"./wbresources",
			 async: false,
			 httpOperation:"GET", 
			 payloadData:"",
			 functionSuccess: fSuccessGetResources,
			 functionError: fErrorGetResources
			} );		
	};
	var displayHandlerFunction = function(item) {
		var type="";
		switch (item.type)
		{
			case "1": type ="site uri"; break;
			case "2": type ="site page"; break;
			case "3": type ="page module"; break;
			case "4": type ="message"; break;
			case "5": type ="article"; break;
			case "6": type ="file"; break;
			case "7": type ="global parameter"; break;	
		}
		var str=""; 
		switch (item.type)
		{
			case "1": 
			case "2": 
			case "3": 
			case "5": 
			case "6": 
				str = '<span class="itemelem itemtype">{0}</span><span class="itemelem">{1}</span><span data-clipboard-text="{1}" class="itemelem wbbtnclipboard btn-s-clipboard"></span><span class="itemelem wbbtndummy">&nbsp</span><span class="itemelem">{2}</span><div class="clear"/>'.format(escapehtml(type), escapehtml(item["key"]), escapehtml(item["name"]));
				break;
			case "7": 	
			case "4": 
				str = '<span class="itemelem itemtype">{0}</span><span class="itemelem">{1}</span><span data-clipboard-text="{1}" class="itemelem wbbtnclipboard btn-s-clipboard"></span><span class="itemelem wbbtndummy">&nbsp</span><div class="clear"/>'.format(escapehtml(type), escapehtml(item["name"]));		
				break;
		}
		return str;
	};
    var afterDisplayFunction = function(wbsearchbox) {
    	$('.btn-s-clipboard').WBCopyClipboardButoon({buttonHtml:"<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copy to clipboard</div>", basePath: getAdminPath(), selector: '.btn-s-clipboard'});
    	$('.btn-s-clipboard').WBCopyClipboardButoon().on("aftercopy", function (e) {
    		$('.btn-s-clipboard').WBCopyClipboardButoon().reset();
    		$(e.target).html("<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copied!</div>");
            wbsearchbox.getOptions().jQInputBox.focus();
    	});
    };

	
	$('#cmssearchbox').wbSearchBox({searchFields:['name','key'], classSearchList:'wbsearchresultlist' ,afterDisplayHandler: afterDisplayFunction, displayHandler: displayHandlerFunction, 
					loadDataHandler: loadDataHandlerFunction, jQInputBox: $('#cmssearchbox'), jQSearchListContainer: $('#searchResultList')});
	*/
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
													 functionError: fErrorGetLanguages,
													 functionAuth: authHandler
													} );

});