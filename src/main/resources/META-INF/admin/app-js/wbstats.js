
$().ready( function () {

	var displayHandlerUris = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a href="./weburiedit.html?key=' + encodeURIComponent(record['key'])+ '"><i class="icon-pencil"></i> Edit </a>'; 
		} 
	};
	var displayHandlerPages = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a href="./webpageedit.html?key={0}&externalKey={1}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key']), encodeURIComponent(record['externalKey'])); 
		}
	};
	var displayHandlerModules = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a href="./webpagemodule.html?key={0}&externalKey={1}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key']), encodeURIComponent(record['externalKey'])); 
		}
	};
	var displayHandlerArticles = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a href="webarticleedit.html?key={0}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key'])); 
		}
	};
	var displayHandlerLanguages = function (fieldId, record) {
		if (fieldId == "name") {
			return escapehtml(record); 
		}
	};
	var displayHandlerFiles = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a href="./webfile.html?key={0}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key'])); 
		}
	};
	
	
	$('#wbtableuris').wbSimpleTable( { columns: [  {display: "", fieldId: "uri"},
	                                               {display: "", fieldId:"_operations", customHandler: displayHandlerUris}],
							 keyName: "key",
							 tableBaseClass: "table table-condensed",
							 noLinesContent: "<tr> <td colspan='2'>There are no site urls defined. </td></tr>"
							});
	$('#wbtablepages').wbSimpleTable( { columns: [  {display: "", fieldId: "name"},
	                                               {display: "", fieldId:"_operations", customHandler: displayHandlerPages}],
							 keyName: "key",
							 tableBaseClass: "table table-condensed",
							 noLinesContent: "<tr> <td colspan='2'>There are no site pages defined. </td></tr>"
							});
	$('#wbtablemodules').wbSimpleTable( { columns: [  {display: "", fieldId: "name"},
		                                               {display: "", fieldId:"_operations", customHandler: displayHandlerModules}],
								 keyName: "key",
								 tableBaseClass: "table table-condensed",
								 noLinesContent: "<tr> <td colspan='2'>There are no site pages defined. </td></tr>"
								});
	$('#wbtablearticles').wbSimpleTable( { columns: [  {display: "", fieldId: "title"},
		                                               {display: "", fieldId:"_operations", customHandler: displayHandlerArticles}],
								 keyName: "key",
								 tableBaseClass: "table table-condensed",
								 noLinesContent: "<tr> <td colspan='2'>There are no site pages defined. </td></tr>"
								});
	$('#wbtablelanguages').wbSimpleTable( { columns: [  {display: "", fieldId: "name", customHandler: displayHandlerLanguages} ],
								 tableBaseClass: "table table-condensed",
								 noLinesContent: "<tr> <td colspan='2'>There are no site pages defined. </td></tr>"
								});
	$('#wbtablefiles').wbSimpleTable( { columns: [  {display: "", fieldId: "name"},
		                                               {display: "", fieldId:"_operations", customHandler: displayHandlerFiles}],
								 keyName: "key",
								 tableBaseClass: "table table-condensed",
								 noLinesContent: "<tr> <td colspan='2'>There are no files uploaded. </td></tr>"
								});
	
	

	var populateStats = function() {
		var fSuccessGetStats = function (resp) {		
			$('#wbtableuris').wbSimpleTable().setRows(resp.data.URIS.data);						
			$('#wbtablepages').wbSimpleTable().setRows(resp.data.PAGES.data);						
			$('#wbtablearticles').wbSimpleTable().setRows(resp.data.ARTICLES.data);						
			$('#wbtablelanguages').wbSimpleTable().setRows(resp.data.LANGUAGES.data);						
			$('#wbtablefiles').wbSimpleTable().setRows(resp.data.FILES.data);						
			
			$('#spinnerTable').WBSpinner().hide();			
		}
		var fErrorGetStats = function (errors, data) {
			alert(data);
			$('#spinnerTable').WBSpinner().hide();
		}
				
		var urlString = "./wbstatistics?entity=uris&entity=languages&entity=pages&entity=modules&entity=articles&entity=files&entity=globalparams&count=2&index_start=0&"; 
		$('#wbbodycontent').wbCommunicationManager().ajax ( { url: urlString,
														 httpOperation:"GET", 
														 payloadData:"",
														 functionSuccess: fSuccessGetStats,
														 functionError: fErrorGetStats
														} );
	}
	populateStats();
});