
var WBLanguages = {
		"ar":"Arabic",
		"ar_AE":"Arabic (United Arab Emirates)",
		"ar_BH":"Arabic (Bahrain)",
		"ar_DZ":"Arabic (Algeria)",
		"ar_EG":"Arabic (Egypt)",
		"ar_IQ":"Arabic (Iraq)",
		"ar_JO":"Arabic (Jordan)",
		"ar_KW":"Arabic (Kuwait)",
		"ar_LB":"Arabic (Lebanon)",
		"ar_LY":"Arabic (Libya)",
		"ar_MA":"Arabic (Morocco)",
		"ar_OM":"Arabic (Oman)",
		"ar_QA":"Arabic (Qatar)",
		"ar_SA":"Arabic (Saudi Arabia)",
		"ar_SD":"Arabic (Sudan)",
		"ar_SY":"Arabic (Syria)",
		"ar_TN":"Arabic (Tunisia)",
		"ar_YE":"Arabic (Yemen)",
		"be":"Belarusian",
		"be_BY":"Belarusian (Belarus)",
		"bg":"Bulgarian",
		"bg_BG":"Bulgarian (Bulgaria)",
		"ca":"Catalan",
		"ca_ES":"Catalan (Spain)",
		"cs":"Czech",
		"cs_CZ":"Czech (Czech Republic)",
		"da":"Danish",
		"da_DK":"Danish (Denmark)",
		"de":"German",
		"de_AT":"German (Austria)",
		"de_CH":"German (Switzerland)",
		"de_DE":"German (Germany)",
		"de_LU":"German (Luxembourg)",
		"el":"Greek",
		"el_CY":"Greek (Cyprus)",
		"el_GR":"Greek (Greece)",
		"en":"English",
		"en_AU":"English (Australia)",
		"en_CA":"English (Canada)",
		"en_GB":"English (United Kingdom)",
		"en_IE":"English (Ireland)",
		"en_IN":"English (India)",
		"en_MT":"English (Malta)",
		"en_NZ":"English (New Zealand)",
		"en_PH":"English (Philippines)",
		"en_SG":"English (Singapore)",
		"en_US":"English (United States)",
		"en_ZA":"English (South Africa)",
		"es":"Spanish",
		"es_AR":"Spanish (Argentina)",
		"es_BO":"Spanish (Bolivia)",
		"es_CL":"Spanish (Chile)",
		"es_CO":"Spanish (Colombia)",
		"es_CR":"Spanish (Costa Rica)",
		"es_DO":"Spanish (Dominican Republic)",
		"es_EC":"Spanish (Ecuador)",
		"es_ES":"Spanish (Spain)",
		"es_GT":"Spanish (Guatemala)",
		"es_HN":"Spanish (Honduras)",
		"es_MX":"Spanish (Mexico)",
		"es_NI":"Spanish (Nicaragua)",
		"es_PA":"Spanish (Panama)",
		"es_PE":"Spanish (Peru)",
		"es_PR":"Spanish (Puerto Rico)",
		"es_PY":"Spanish (Paraguay)",
		"es_SV":"Spanish (El Salvador)",
		"es_US":"Spanish (United States)",
		"es_UY":"Spanish (Uruguay)",
		"es_VE":"Spanish (Venezuela)",
		"et":"Estonian",
		"et_EE":"Estonian (Estonia)",
		"fi":"Finnish",
		"fi_FI":"Finnish (Finland)",
		"fr":"French",
		"fr_BE":"French (Belgium)",
		"fr_CA":"French (Canada)",
		"fr_CH":"French (Switzerland)",
		"fr_FR":"French (France)",
		"fr_LU":"French (Luxembourg)",
		"ga":"Irish",
		"ga_IE":"Irish (Ireland)",
		"hi_IN":"Hindi (India)",
		"hr":"Croatian",
		"hr_HR":"Croatian (Croatia)",
		"hu":"Hungarian",
		"hu_HU":"Hungarian (Hungary)",
		"in":"Indonesian",
		"in_ID":"Indonesian (Indonesia)",
		"is":"Icelandic",
		"is_IS":"Icelandic (Iceland)",
		"it":"Italian",
		"it_CH":"Italian (Switzerland)",
		"it_IT":"Italian (Italy)",
		"iw":"Hebrew",
		"iw_IL":"Hebrew (Israel)",
		"ja_JP":"Japanese (Japan)",
		"ja":"Japanese",
		"ja_JP":"Japanese (Japan,JP)",
		"ko":"Korean",
		"ko_KR":"Korean (South Korea)",
		"lt":"Lithuanian",
		"lt_LT":"Lithuanian (Lithuania)",
		"lv":"Latvian",
		"lv_LV":"Latvian (Latvia)",
		"mk":"Macedonian",
		"mk_MK":"Macedonian (Macedonia)",
		"ms":"Malay",
		"ms_MY":"Malay (Malaysia)",
		"mt":"Maltese",
		"mt_MT":"Maltese (Malta)",
		"nl":"Dutch",
		"nl_BE":"Dutch (Belgium)",
		"nl_NL":"Dutch (Netherlands)",
		"no":"Norwegian",
		"no_NO":"Norwegian (Norway,Nynorsk)",
		"no_NO":"Norwegian (Norway)",
		"pl":"Polish",
		"pl_PL":"Polish (Poland)",
		"pt":"Portuguese",
		"pt_BR":"Portuguese (Brazil)",
		"pt_PT":"Portuguese (Portugal)",
		"ro":"Romanian",
		"ro_RO":"Romanian (Romania)",
		"ru":"Russian",
		"ru_RU":"Russian (Russia)",
		"sk":"Slovak",
		"sk_SK":"Slovak (Slovakia)",
		"sl":"Slovenian",
		"sl_SI":"Slovenian (Slovenia)",
		"sq":"Albanian",
		"sq_AL":"Albanian (Albania)",
		"sr":"Serbian",
		"sr_BA":"Serbian (Bosnia and Herzegovina)",
		"sr_CS":"Serbian (Serbia and Montenegro)",
		"sr_ME":"Serbian (Montenegro)",
		"sr_RS":"Serbian (Serbia)",
		"sv":"Swedish",
		"sv_SE":"Swedish (Sweden)",
		"th":"Thai",
		"th_TH":"Thai (Thailand,TH)",
		"th_TH":"Thai (Thailand)",
		"tr":"Turkish",
		"tr_TR":"Turkish (Turkey)",
		"uk":"Ukrainian",
		"uk_UA":"Ukrainian (Ukraine)",
		"vi":"Vietnamese",
		"vi_VN":"Vietnamese (Vietnam)",
		"zh":"Chinese",
		"zh_CN":"Chinese (China)",
		"zh_HK":"Chinese (Hong Kong)",
		"zh_SG":"Chinese (Singapore)",
		"zh_TW":"Chinese (Taiwan)"
};

$().ready( function () {

	var textToLength = function (val) {
		val = val || "";
		var maxLen = 30;
		if (val.length > maxLen) {
			val = val.substring(0, maxLen);
			val = val + "...";
		}
		return val;
	};

	var displayHandlerUris = function (fieldId, record) {
		if ("more" in record) {
			if (fieldId == "uri") {
				return '<a href="./weburis.html">more...</a>';
			}
			return "";
		} 
		if (fieldId == "_operations") {
			return '<a class="dashboardop" href="./weburiedit.html?key=' + encodeURIComponent(record['key'])+ '"><i class="icon-pencil"></i> Edit </a>'; 
		} else {
			return textToLength(escapehtml(record[fieldId]));
		}
	};
	var displayHandlerPages = function (fieldId, record) {
		if ("more" in record) {
			if (fieldId == "name") {
				return '<a href="./webpages.html">more...</a>';
			}
			return "";
		} 

		if (fieldId == "_operations") {
			return '<a class="dashboardop" href="./webpageedit.html?key={0}&externalKey={1}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key']), encodeURIComponent(record['externalKey'])); 
		} else {
			return textToLength(escapehtml(record[fieldId]));
		}
	};
	var displayHandlerModules = function (fieldId, record) {
		if ("more" in record) {
			if (fieldId == "name") {
				return '<a href="./webpagemodules.html">more...</a>';
			}
			return "";
		} 
		if (fieldId == "_operations") {
			return '<a class="dashboardop" href="./webpagemodule.html?key={0}&externalKey={1}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key']), encodeURIComponent(record['externalKey'])); 
		} else {
			return textToLength(escapehtml(record[fieldId]));
		}
	};
	var displayHandlerArticles = function (fieldId, record) {
		if ("more" in record) {
			if (fieldId == "title") {
				return '<a href="./webarticles.html">more...</a>';
			}
			return "";
		} 

		if (fieldId == "_operations") {
			return '<a class="dashboardop" href="webarticleedit.html?key={0}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key'])); 
		}else {
			return textToLength(escapehtml(record[fieldId]));
		}
	};
	var displayHandlerLanguages = function (fieldId, record) {
		if ("more" in record) {
			if (fieldId == "name") {
				return '<a href="./websettingslangs.html">more...</a>';
			}
			return "";
		} 

		if (fieldId == "name") {
			return textToLength(escapehtml(record[fieldId])); 
		}
	};
	var displayHandlerFiles = function (fieldId, record) {
		if ("more" in record) {
			if (fieldId == "name") {
				return '<a href="./webfiles.html">more...</a>';
			}
			return "";
		} 

		if (fieldId == "_operations") {
			return '<a class="dashboardop" href="./webfile.html?key={0}"><i class="icon-pencil"></i> Edit </a>'.format(encodeURIComponent(record['key'])); 
		} else {
			return textToLength(escapehtml(record[fieldId]));
		}
	};
	
	
	$('#wbtableuris').wbSimpleTable( { columns: [  {display: "", fieldId: "uri", customHandler: displayHandlerUris},
	                                               {display: "", fieldId:"_operations", customHandler: displayHandlerUris}],
							 keyName: "key",
							 includeHeader: false,
							 tableBaseClass: "table table-condensed",
							 noLinesContent: "<tr> <td colspan='2'>There are no site urls defined. </td></tr>"
							});
	$('#wbtablepages').wbSimpleTable( { columns: [  {display: "", fieldId: "name", customHandler: displayHandlerPages},
	                                               {display: "", fieldId:"_operations", customHandler: displayHandlerPages}],
							 keyName: "key",
							 includeHeader: false,
							 tableBaseClass: "table table-condensed",
							 noLinesContent: "<tr> <td colspan='2'>There are no site pages defined. </td></tr>"
							});
	$('#wbtablemodules').wbSimpleTable( { columns: [  {display: "", fieldId: "name", customHandler: displayHandlerModules},
		                                               {display: "", fieldId:"_operations", customHandler: displayHandlerModules}],
								 keyName: "key",
								 includeHeader: false,
								 tableBaseClass: "table table-condensed",
								 noLinesContent: "<tr> <td colspan='2'>There are no page modules defined. </td></tr>"
								});
	$('#wbtablearticles').wbSimpleTable( { columns: [  {display: "", fieldId: "title", customHandler: displayHandlerArticles},
		                                               {display: "", fieldId:"_operations", customHandler: displayHandlerArticles}],
								 keyName: "key",
								 includeHeader: false,
								 tableBaseClass: "table table-condensed",
								 noLinesContent: "<tr> <td colspan='2'>There are no articles defined. </td></tr>"
								});
	$('#wbtablelanguages').wbSimpleTable( { columns: [  {display: "", fieldId: "name", customHandler: displayHandlerLanguages} ],
								 tableBaseClass: "table table-condensed",
								 includeHeader: false,
								 noLinesContent: "<tr> <td colspan='1'>There are no site languages defined. </td></tr>"
								});
	$('#wbtablefiles').wbSimpleTable( { columns: [  {display: "", fieldId: "name", customHandler: displayHandlerFiles},
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
		var populateLanguages = function (data) {
			var langRecs = [];
			if (data.defaultLanguage in WBLanguages) {
				langRecs.push({"name": WBLanguages[data.defaultLanguage]})
			}
			
			$.each (data.languages, function(index, item) {
				if (item in WBLanguages && item != data.defaultLanguage && langRecs.length<2) {
					langRecs.push({"name": WBLanguages[item]})
				}
			});
			$('#wbtablelanguages').wbSimpleTable().setRows(langRecs);
			if (langRecs.length > 0) {
				$('#wbtablelanguages').wbSimpleTable().insertRow({'more':''});
			}
		};
		var fSuccessGetStats = function (resp) {	
			
			$('#wbtableuris').wbSimpleTable().setRows(resp.data.URIS.data);	
			if ($('#wbtableuris').wbSimpleTable().length() > 0) {
				$('#wbtableuris').wbSimpleTable().insertRow({'more':''});
			}
			$('#wbtablepages').wbSimpleTable().setRows(resp.data.PAGES.data);						
			if ($('#wbtablepages').wbSimpleTable().length() > 0) {
				$('#wbtablepages').wbSimpleTable().insertRow({'more':''});
			}
			$('#wbtablemodules').wbSimpleTable().setRows(resp.data.MODULES.data);							
			if ($('#wbtablemodules').wbSimpleTable().length() > 0) {
				$('#wbtablemodules').wbSimpleTable().insertRow({'more':''});
			}
			$('#wbtablearticles').wbSimpleTable().setRows(resp.data.ARTICLES.data);						
			if ($('#wbtablearticles').wbSimpleTable().length() > 0) {
				$('#wbtablearticles').wbSimpleTable().insertRow({'more':''});
			}
			populateLanguages(resp.data.LANGUAGES.data);
			
			$('#wbtablefiles').wbSimpleTable().setRows(resp.data.FILES.data);
			if ($('#wbtablefiles').wbSimpleTable().length() > 0) {
				$('#wbtablefiles').wbSimpleTable().insertRow({'more':''});
			}

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