
$().ready( function () {

	var displayHandlerUris = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a class="dashboardop" href="./weburiedit.html?key=' + encodeURIComponent(record['key'])+ '"><i class="icon-pencil"></i> Edit </a>'; 
		} 
	};
	var displayHandlerPages = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a class="dashboardop" href="./webpageedit.html?key={0}&externalKey={1}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key']), encodeURIComponent(record['externalKey'])); 
		}
	};
	var displayHandlerModules = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a class="dashboardop" href="./webpagemodule.html?key={0}&externalKey={1}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key']), encodeURIComponent(record['externalKey'])); 
		}
	};
	var displayHandlerArticles = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a class="dashboardop" href="webarticleedit.html?key={0}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key'])); 
		}
	};
	var displayHandlerLanguages = function (fieldId, record) {
		if (fieldId == "name") {
			return escapehtml(record); 
		}
	};
	var displayHandlerFiles = function (fieldId, record) {
		if (fieldId == "_operations") {
			return '<a class="dashboardop" href="./webfile.html?key={0}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key'])); 
		}
	};
	
	
	$('#wbtableuris').wbSimpleTable( { columns: [  {display: "", fieldId: "uri"},
	                                               {display: "", fieldId:"_operations", customHandler: displayHandlerUris}],
							 keyName: "key",
							 includeHeader: false,
							 tableBaseClass: "table table-condensed",
							 noLinesContent: "<tr> <td colspan='2'>There are no site urls defined. </td></tr>"
							});
	$('#wbtablepages').wbSimpleTable( { columns: [  {display: "", fieldId: "name"},
	                                               {display: "", fieldId:"_operations", customHandler: displayHandlerPages}],
							 keyName: "key",
							 includeHeader: false,
							 tableBaseClass: "table table-condensed",
							 noLinesContent: "<tr> <td colspan='2'>There are no site pages defined. </td></tr>"
							});
	$('#wbtablemodules').wbSimpleTable( { columns: [  {display: "", fieldId: "name"},
		                                               {display: "", fieldId:"_operations", customHandler: displayHandlerModules}],
								 keyName: "key",
								 includeHeader: false,
								 tableBaseClass: "table table-condensed",
								 noLinesContent: "<tr> <td colspan='2'>There are no page modules defined. </td></tr>"
								});
	$('#wbtablearticles').wbSimpleTable( { columns: [  {display: "", fieldId: "title"},
		                                               {display: "", fieldId:"_operations", customHandler: displayHandlerArticles}],
								 keyName: "key",
								 includeHeader: false,
								 tableBaseClass: "table table-condensed",
								 noLinesContent: "<tr> <td colspan='2'>There are no articles defined. </td></tr>"
								});
	$('#wbtablelanguages').wbSimpleTable( { columns: [  {display: "", fieldId: "name", customHandler: displayHandlerLanguages} ],
								 tableBaseClass: "table table-condensed",
								 includeHeader: false,
								 noLinesContent: "<tr> <td colspan='2'>There are no site languages defined. </td></tr>"
								});
	$('#wbtablefiles').wbSimpleTable( { columns: [  {display: "", fieldId: "name"},
		                                               {display: "", fieldId:"_operations", customHandler: displayHandlerFiles}],
								 keyName: "key",
								 includeHeader: false,
								 tableBaseClass: "table table-condensed",
								 noLinesContent: "<tr> <td colspan='2'>There are no files uploaded. </td></tr>"
								});
	
	

	var populateStats = function() {
		var populateCount = function (resp) {
			var items = $('.dashboardcount');
			$.each(items, function(index, item) {
				var id = $(item).attr('id');
				if (id.indexOf('count_') == 0) {
					var entity = id.substring('count_'.length);
					var number = resp.data[entity.toUpperCase()].additional_data.total_count;
					$(item).html(escapehtml(number));
				}
			});
		};
		var fSuccessGetStats = function (resp) {	
			
			$('#wbtableuris').wbSimpleTable().setRows(resp.data.URIS.data);						
			$('#wbtablepages').wbSimpleTable().setRows(resp.data.PAGES.data);						
			$('#wbtablemodules').wbSimpleTable().setRows(resp.data.MODULES.data);							
			$('#wbtablearticles').wbSimpleTable().setRows(resp.data.ARTICLES.data);						
			$('#wbtablelanguages').wbSimpleTable().setRows(resp.data.LANGUAGES.data);						
			$('#wbtablefiles').wbSimpleTable().setRows(resp.data.FILES.data);
			populateCount(resp);
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