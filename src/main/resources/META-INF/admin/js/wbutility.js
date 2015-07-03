/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/

if (!String.prototype.format) {
	String.prototype.format = function() {
	    var formatted = this;
	    for (var i = 0; i < arguments.length; i++) {
	        var regexp = new RegExp('\\{'+i+'\\}', 'gi');
	        formatted = formatted.replace(regexp, arguments[i]);
	    }
	    return formatted;	
	};
};

if (!String.prototype.trim) {
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/gm,'');
	}
};

if (!String.prototype.startsWith) {
  Object.defineProperty(String.prototype, 'startsWith', {
    enumerable: false,
    configurable: false,
    writable: false,
    value: function(searchString, position) {
      position = position || 0;
      return this.lastIndexOf(searchString, position) === position;
    }
  });
};

function getURLParameter(name, url) {
	url = url || window.location.href;
	var value = (url.match(RegExp("[?|&]"+name+'=(.*?)(&|$)'))||[,undefined])[1];
	if (value != undefined) {
		return decodeURIComponent(value);
    } 
	return undefined;
};

function replaceURLParameter(url, param, newValue) {
	url = url || "";
	var index = url.indexOf('?');
	var _url = url, url_ = "";
	if (index>0){
		_url = url.substring(0, index);
		url_ = url.substring(index);
	}
	var search = /([^&\?=]+)=?([^&#]*)/g
	var params = {}, match;
	
	while (match = search.exec(url_)) {
		params[match[1]]= match[2];
	}
	params[encodeURIComponent(param)] = encodeURIComponent(newValue);
	if (_url.length>0) {
		_url = _url + "?";
	}
	for (x in params) {
		_url = _url + x + "=" + params[x] + "&";
	}
	return _url;
}

function guid() {
    function _p8(s) {
        var p = (Math.random().toString(16)+"000000000").substr(2,8);
        return s ? "-" + p.substr(0,4) + "-" + p.substr(4,4) : p ;
    }
    return _p8() + _p8(true) + _p8(true) + _p8();
}

function removeURLParameter(url, param) {
	url = url || "";
	var index = url.indexOf('?');
	var _url = url, url_ = "";
	if (index>0){
		_url = url.substring(0, index);
		url_ = url.substring(index);
	}
	var search = /([^&\?=]+)=?([^&#]*)/g
	var params = {}, match;
	
	while (match = search.exec(url_)) {
		params[match[1]]= match[2];
	}
	delete params[encodeURIComponent(param)];
	if (_url.length>0) {
		_url = _url + "?";
	}
	for (x in params) {
		_url = _url + x + "=" + params[x] + "&";
	}
	return _url;
}

function getTextForItems(count, textMap) {
	textMap = textMap || {};	
	if ('any' in textMap) {
		return textMap['any'].format(count);
	}
	if ('empty' in textMap && count == undefined) {
		return textMap['empty'].format(count);
	}	
	if (count>1 && 'greater_than_1' in textMap) {
		return textMap['greater_than_1'].format(count);
	}
	return textMap['' + count].format(count)
}

function getAdminPath() {
	var pos = location.pathname.lastIndexOf('/');
	var adminPath = pos > 0 ? location.pathname.substring(0, pos) : "";
	return location.protocol + '//' + location.host + adminPath;
}

function transferProperties(objKeys, objValues)
{
	var result = {};
	for(var prop in objKeys) {
		if (objKeys[prop] in objValues) {
			result[prop] = objValues[objKeys[prop]];
		}
	}
	return result;
}
function escapehtml(html) {
        if (html===0) html = "0";
        html = html || "";
        var escaped = "" + html;
        var findReplace = [[/&/g, "&amp;"], [/</g, "&lt;"], [/>/g, "&gt;"], [/"/g, "&quot;"], [/'/g, "&apos;"]]
        for(var item in findReplace) {
             escaped = escaped.replace(findReplace[item][0], findReplace[item][1]);
        }
        return escaped;
}

Date.prototype.toFormatString = function (time, format) {
	var val = new Date();
	if (typeof time == 'string') {
		val.setTime((new Number(time)).valueOf());
	} else {
		val.setTime(time);
	};
	var now = new Date();
	if (format == undefined) 
		format = "dd/mm/yyyy hh:mm:ss";
	else
		format =  format.toLowerCase();
	var d = val.getDate(), mo = val.getMonth()+1, yF = val.getFullYear(), h = val.getHours(), mi = val.getMinutes(), s = val.getSeconds();
	var mo_ = mo;
	var d_ = d;
	d = ("0" + d); d=d.substr(d.length-2);
	mo = ("0" + mo); mo=mo.substr(mo.length-2);
	h = ("0" + h); h=h.substr(h.length-2);
	mi = ("0" + mi); mi=mi.substr(mi.length-2);
	s = ("0" + s); s=s.substr(s.length-2);
	
	if (format == "today|dd/mm/yyyy hh:mm") {
		if (now.getFullYear()==yF && ((now.getMonth()+1)==mo_) && now.getDate()==d_) {
			// it's the same date as today
			return 'Today {0}:{1}'.format (h,mi);
		} else
			return "{0}/{1}/{2} {3}:{4}".format(d,mo,yF,h,mi);
	} else
	if (format == "dd/mm/yyyy hh:mm:ss") {
		return "{0}/{1}/{2} {3}:{4}".format(d,mo,yF,h,mi);
	} else
	if (format == "mm/dd/yyyy hh:mm:ss") {
		return "{0}/{1}/{2} {3}:{4}".format(mo,d,yF,h,mi);
	} else
		if (format == "ddmmyyyy_hhmm") {
		return "{0}{1}{2}_{3}{4}".format(d,mo,yF,h,mi);
	}
	return "";
}
Date.toFormatString = Date.prototype.toFormatString;

String.prototype.escapehtml = function (){
	var html = this;
	return escapehtml(html);
}

if (!Array.prototype.indexOf) {
    Array.prototype.indexOf = function (searchElement /*, fromIndex */ ) {
        "use strict";
        if (this == null) {
            throw new TypeError();
        }
        var t = Object(this);
        var len = t.length >>> 0;
        if (len === 0) {
            return -1;
        }
        var n = 0;
        if (arguments.length > 1) {
            n = Number(arguments[1]);
            if (n != n) { // shortcut for verifying if it's NaN
                n = 0;
            } else if (n != 0 && n != Infinity && n != -Infinity) {
                n = (n > 0 || -1) * Math.floor(Math.abs(n));
            }
        }
        if (n >= len) {
            return -1;
        }
        var k = n >= 0 ? n : Math.max(len - Math.abs(n), 0);
        for (; k < len; k++) {
            if (k in t && t[k] === searchElement) {
                return k;
            }
        }
        return -1;
    }
}

(function ($) {
	authHandler = function (record) {
		if (!record) return;
		if (!('userIdentifier' in record) || (record.userIdentifier.length == 0)) {
			window.location.href= record.loginPageUrl;
			return;
		}		
		var htmlProfile = "";
		var htmlLogout = "";
		if ('profileUrl' in record && record.profileUrl.length>0) {
			htmlProfile = "<li><a target='_blank' href='{0}'>Profile</a></li>".format(escapehtml(record.profileUrl));
		}	
		if ('logoutUrl' in record && record.logoutUrl.length>0) {
			htmlLogout = "<li><a href='{0}'>Logout</a></li>".format(escapehtml(record.logoutUrl));
		}	
		
		var html = ("<li class='dropdown'><a href='#' class='dropdown-toggle' data-toggle='dropdown'>{0} <b class='caret'></b> </a> " +
		           "<ul class='dropdown-menu'>{1}{2}</ul> </li>").format(htmlProfile, htmlLogout);
		$("#authmenu").html(html);
}
}) (window.jQuery);

(function ($) {

	WBObjectManager = function ( thisElement, options ) {
		this.init( thisElement, options );
	}
	WBObjectManager.prototype = 
	{
		defaults: { 
			fieldsPrefix: 'wb', 
			errorLabelsPrefix: 'err',
			generalError: '',
			fieldsDefaults: {},
			validationRules: {},
			errorLabelClassName: '',
			errorInputClassName: ''
		},
		
		init: function ( thisElement, options ) {			
			this.thisElement = $(thisElement);
			this.options = $.extend ( {} , this.defaults, options );
			if ( 'fieldsDefaults' in options) {
				this.resetFields();
			}
		},
		getOptions: function () {
			if (! this.options) 
				return this.defaults
			else
				return this.options;
		},
		
		getObjectFromFields: function ( ) {
			var object = {};
			var elements = this.thisElement.find('input[id^="' + this.getOptions().fieldsPrefix + '"]' + ',textarea[id^="' + this.getOptions().fieldsPrefix + '"]' + ',select[id^="' + this.getOptions().fieldsPrefix + '"]');
			var tempThis = this;
			$.each( elements, function (index, value) {
				var key = $(value).attr('id').substring( tempThis.getOptions().fieldsPrefix.length );
				switch (value.type) {
					case 'text':
					case 'file':
					case 'hidden':
					case 'password':
					case 'textarea':
					case 'select-multiple':
					case 'select-one':
						object[key] = $(value).val();
						break;
					case 'checkbox':
					
					case 'radio':
						var name = $(value).attr('name');
						var checked = tempThis.thisElement.find('input[name^=' + name + ']:checked');
						if (checked.length == 1) {
							object[name] = $(checked[0]).attr('value');
						}							
						break;
				}
			} );
			return object;				
		},
		populateFieldsFromObject: function (object) {
			var elements = this.thisElement.find('input[id^="' + this.getOptions().fieldsPrefix + '"]' + ',textarea[id^="' + this.getOptions().fieldsPrefix + '"]' + ',select[id^="' + this.getOptions().fieldsPrefix + '"]');
			var tempThis = this;
			$.each( elements, function (index, value) {
				var key = $(value).attr('id').substring( tempThis.getOptions().fieldsPrefix.length );
				switch (value.type) {
					case 'text':
					case 'file':
					case 'hidden':
					case 'password':
					case 'textarea':
					case 'select-multiple':
					case 'select-one':
						if (key in object) {
							$(value).val(object[key]);
						}
						break;
					case 'checkbox':
					
					case 'radio':
						if (key in object) {
							var fieldValue = object[key];
							var name = $(value).attr('name');
							var items = tempThis.thisElement.find('input[name^=' + name + '][value="' + fieldValue + '"]');
							$.each( items, function (index,item) {
								$(item).attr('checked', true);
							});
						}
						break;
				}
				
			});
		
		},
		setEditableFields: function (enable) {
			var elements = this.thisElement.find('input, button, select');
			var tempThis = this;
			$.each( elements, function (index, item) {
				if (enable == false)
					$(item).attr("disabled", "disabled");
				else
					$(item).removeAttr("disabled");
			});
		},
		resetFields: function () {
			var elements = this.thisElement.find('input[id^="' + this.getOptions().fieldsPrefix + '"]' + ',textarea[id^="' + this.getOptions().fieldsPrefix + '"]' + ',select[id^="' + this.getOptions().fieldsPrefix + '"]');
			var tempThis = this;
			$.each( elements, function (index, value) {
				var key = $(value).attr('id').substring( tempThis.getOptions().fieldsPrefix.length );
				switch (value.type) {
					case 'radio': {
						var name = $(value).attr('name');
						if (key in tempThis.getOptions().fieldsDefaults)
							var valueToSet = tempThis.getOptions().fieldsDefaults[key];
							var radioElements = tempThis.thisElement.find('input[name^=' + name + '][value="' + valueToSet + '"]');
							$.each(radioElements, function (index, radioItem) {
								$(radioItem).attr('checked', true);
							});
						break;
						}
					case 'text':
					case 'file':
					case 'hidden':
					case 'password':
					case 'textarea':
					case 'select-multiple':
					case 'select-one': {
						var valueToSet = "";
						if (key in tempThis.getOptions().fieldsDefaults)
							valueToSet = tempThis.getOptions().fieldsDefaults[key];
						$(value).val (valueToSet);
						break; 
						}
					case 'checkbox': {
						var valueToSet = 0;
						if (key in tempThis.getOptions().fieldsDefaults)
							valueToSet = tempThis.getOptions().fieldsDefaults[key];
						$(value).attr('checked', valueToSet==1 ? true: false);
						break;
						}
				}				
			} );
			
			tempThis.resetErrors();
		},
		convertErrors: function (errorIds, errorTexts) {
			var result = {};
			$.each (errorIds, function (key, error) {
				if (error in errorTexts) {
					result[key] = errorTexts[error];
				} else {
					result[key] = errorIds[key];
				}
			});
			return result;
		},	
		setErrors: function ( errors ) {
			errors = errors || {} ;
			var labelElements = this.thisElement.find('[id^="' + this.getOptions().errorLabelsPrefix + '"]');
			var tempThis = this;
			var errorLabelClass = tempThis.getOptions().errorLabelClassName;
			var errorInputClass = tempThis.getOptions().errorInputClassName;
			$.each ( labelElements, function (index, value) {
				var key = $(value).attr('id').substring( tempThis.getOptions().errorLabelsPrefix.length );
				if (key in errors) {
					$(value).html( escapehtml (errors[key]) );
					if (errorLabelClass != "" && false == $(value).hasClass(errorLabelClass))
					{
						$(value).addClass(errorLabelClass);
					}					
				} else {
					$(value).html("");
					if (errorLabelClass != "")
					{
						$(value).removeClass(errorLabelClass);
					}
				}
			});
			
			var generalErrorItems = this.thisElement.find('[id="' + this.getOptions().generalError + '"]')
			if (generalErrorItems.length > 0) {
				if (errors[""]) {
					$(generalErrorItems).html( escapehtml(errors[""]));
					if (errorLabelClass != "" && false == $(generalErrorItems).hasClass(errorLabelClass))
					{
						$(generalErrorItems).addClass(errorLabelClass);
					}										
				} else {
					$(generalErrorItems).html("");
					if (errorLabelClass != "")
					{
						$(generalErrorItems).removeClass(errorLabelClass);
					}					
				}
			};
			var inputElements = this.thisElement.find('input[id^="' + this.getOptions().fieldsPrefix + '"]' + ',select[id^="' + this.getOptions().fieldsPrefix + '"]');
			$.each( inputElements, function (index, value) {
				var key = $(value).attr('id').substring( tempThis.getOptions().fieldsPrefix.length );
				if (key in errors) {
					if (errorInputClass != "" && false == $(value).hasClass(errorInputClass))
					{
						$(value).addClass(errorInputClass);
					}					
				} else {
					if (errorInputClass != "")
					{
						$(value).removeClass(errorInputClass);
					}
				}
			});
			
		},
		
		resetErrors: function ( ) {
			this.setErrors( );
		},
		
		validateFields: function ( ) {
			var returnObj = { };
			var validationRules = this.getOptions().validationRules;
			if (! validationRules) returnObj;			
			var tempThis = this;			
			$.each( validationRules , function (index, validationItemsForField) {
				
				key = tempThis.getOptions().fieldsPrefix + index;
				var elements = tempThis.thisElement.find('input[id^="' + key + '"]' + ',select[id^="' + key + '"]');
				$.each (elements, function ( i, elemVal) {
						var vres = tempThis.validateElement(elemVal, validationItemsForField);
						if (! $.isEmptyObject(vres)) {
							returnObj[index] = vres;
						}
				});				
			});
			return returnObj;
		},
		
		validateFieldsAndSetLabels: function ( errors ) {
			errors = errors || {};
			this.resetErrors();
			var validationErrors = this.validateFields();
			var textErrors = {};
			$.each(validationErrors, function (fieldName, fieldErrors) {
				for (index in fieldErrors) {
					if (! (fieldName in textErrors)) {					
						if (fieldErrors[index] in errors) {
							textErrors[fieldName] = errors[fieldErrors[index]];
							break;
						}
					}						
				}				
			});
			this.setErrors(textErrors);
			return textErrors;
		},
		
		validateElement: function (element, validations) {
			var textValue = undefined;
			var numberValue = undefined;
			var isSelected = undefined;
			var tempThis = this;
			var type = element.type || $(element).attr('type');
			switch (type) {
				case 'text':
				case 'file':
				case 'hidden':
				case 'password':
				case 'textarea':
				case 'select-multiple':
				case 'select-one':
					textValue = $(element).val();
					break;
				case 'checkbox':
				case 'radio':
					var name = $(element).attr('name');
					var checked = tempThis.thisElement.find('input[name^=' + name + ']:checked');
					if (checked.length == 1) {
							textValue = $(checked[0]).attr('value');
							isSelected = 1;
					}							
					break;

			}
			if (textValue && false  == isNaN(textValue)) {
				numberValue = new Number(textValue);
			}
			
			var errors = [];
			$.each (validations, function (index, validation) {			
				
				var obj = { 'value':textValue , 'validationValue': validation['rule'] };
				var key = "";
				for(var prop in validation['rule']) {
					key = prop;
					break;
				}
				if (key == 'length') {
					if (textValue.length != validation['rule'][key]) {
							errors.push(validation['error']);
						}
				}
				if (key == 'minLength') {
					if (textValue.length < validation['rule'][key]) {
							errors.push(validation['error']);
						}
				}
				if (key == 'maxLength') {
					if (textValue.length > validation['rule'][key]) {
							errors.push(validation['error']);
						}
				}
				if (key == 'rangeLength') {
					if (textValue.length > validation['rule'][key].max || textValue.length < validation['rule'][key].min) {
							errors.push(validation['error']);
					}
				}
				if (key == 'min') {
					if (! numberValue || numberValue < validation['rule'][key]) {
						obj['value'] = numberValue;
						errors.push(validation['error']);
					}
				}
				if (key == 'max') {
					if (! numberValue || numberValue > validation['rule'][key]) {
						obj['value'] = numberValue;
						errors.push(validation['error']);
					}
				}
				if (key == 'range') {
					if (! numberValue || numberValue < validation['rule'][key].min || numberValue > validation['rule'][key].max) {
						obj['value'] = numberValue;
						errors.push(validation['error']);
					}
				}
				if (key == 'equal') {
					if (! numberValue || numberValue != validation['rule'][key]) {
						obj['value'] = numberValue;
						errors.push(validation['error']);
					}
				}
				if (key == 'notEqual') {
					if (! numberValue || numberValue == validation['rule'][key]) {
						obj['value'] = numberValue;
						errors.push(validation['error']);
					}
				}
				if (key == 'startsWith') {
					if (0 != textValue.toLowerCase().indexOf(validation['rule'][key].toLowerCase())) {
						errors.push(validation['error']);
					}
				}
				if (key == 'endsWith') {
					if ( (textValue.length - validation['rule'][key].length) != textValue.toLowerCase().indexOf(validation['rule'][key].toLowerCase())) {
							errors.push(validation['error']);
						}
				}
				if (key == 'notContains') {
					if ( textValue.toLowerCase().indexOf(validation['rule'][key].toLowerCase()) >= 0) {
							errors.push(validation['error']);
						}
				}
				if (key == 'contains') {
					if ( textValue.toLowerCase().indexOf(validation['rule'][key].toLowerCase()) == -1) {
							errors.push(validation['error']);
						}
				}
				if (key == 'includedInto') {
					if (-1 == $.inArray(textValue, validation['rule'][key]))
					{	
						errors.push(validation['error']);
					}
				}
				if (key == 'notIncludedInto') {
					if (-1 != $.inArray(textValue, validation['rule'][key]))
					{	
						errors.push(validation['error']);
					}
				}
				if (key == 'customRegexp') {
					var r = new RegExp(validation['rule']['customRegexp'].pattern, validation['rule']['customRegexp'].modifiers);
					var b = r.test(textValue);
					if (false == b) {
						errors.push(validation['error']);
					}
				}			
			});
			return errors;
		}
	}
	$.fn.wbObjectManager = function ( param ) {
			var $this = $(this),
			data = $this.data('wbObjectManager');			
			var options = (typeof param == 'object') ? param : {} ; 
			if (!data) $this.data('wbObjectManager', (data = new WBObjectManager ($this, options)));	
			if (param == undefined) return data;
	}	
}) (window.jQuery);


(function ($) {

	WBCopyClipboardButoon = function ( thisElement, options ) {
		this.init( thisElement, options );
	}
	WBCopyClipboardButoon.prototype = 
	{
		defaults: { 
			basePath: undefined,
			selector: undefined,
			buttonHtml: undefined
		},
		buttons: undefined,
		
		reset: function () {
			var buttonHtml = this.options.buttonHtml;
			var _buttons = $.find(this.options.selector);
			$.each (_buttons , function (index, elem) {	
				$(elem).html(buttonHtml);
			});		
		},
		init: function ( thisElement, options ) {			
			this.thisElement = $(thisElement);
			this.options = $.extend ( {} , this.defaults, options );					
			var swfzc = this.options.basePath + '/_/zeroclipboard/ZeroClipboard.swf';
			ZeroClipboard.config( { swfPath: swfzc } );
			var zcButtons = $.find(this.options.selector);
			var _buttons = new Array();
			var buttonHtml = this.options.buttonHtml;
			$.each (zcButtons, function (index, elem) {
				var zc = new ZeroClipboard(elem);
				_buttons.push(zc);
				$(elem).html(buttonHtml);
			});
			this.buttons = _buttons;

		},
		getOptions: function () {
			if (! this.options) 
				return this.defaults
			else
				return this.options;
		},
		on: function (eventname, handler) {
			$.each (this.buttons , function (index, elem) {
				elem.on(eventname, handler);
			});
		}

	}
	$.fn.WBCopyClipboardButoon = function ( param ) {
			var $this = $(this),
			data = $this.data('wbCopyClipboardButton');			
			var options = (typeof param == 'object') ? param : {} ; 
			if (!data) $this.data('wbCopyClipboardButton', (data = new WBCopyClipboardButoon ($this, options)));	
			if (param == undefined) return data;
	}	

}) (window.jQuery);

(function ($) {

	WBDisplayObject = function ( thisElement, options ) {
		this.init( thisElement, options );
	}
	WBDisplayObject.prototype = 
	{
		defaults: { 
			fieldsPrefix: 'wb',
			customHandler: undefined
		},
		
		init: function ( thisElement, options ) {			
			this.thisElement = $(thisElement);
			this.options = $.extend ( {} , this.defaults, options );
		},
		getOptions: function () {
			if (! this.options) 
				return this.defaults
			else
				return this.options;
		},

		
		display: function (object) {
			var elements = this.thisElement.find('[id^="' + this.getOptions().fieldsPrefix + '"]');
			var tempThis = this;
			$.each( elements, function (index, value) {
				var key = $(value).attr('id').substring( tempThis.getOptions().fieldsPrefix.length );
					var htmlField = ""; 
					if (tempThis.getOptions().customHandler) {
						htmlField = (tempThis.getOptions().customHandler)(key, object);
					} else {		
						if (key in object) {
							htmlField = escapehtml(object[key]);
						}
					}
					switch (value.type) {
					case 'text':
					case 'file':
					case 'hidden':
					case 'password':
					case 'textarea':
					case 'select-multiple':
					case 'select-one':
						$(value).val(htmlField);
						break;
					default:
						$(value).html(htmlField);
					}				
			})
		}
	};
	
	$.fn.wbDisplayObject = function ( param ) {
			var $this = $(this),
			data = $this.data('wbDisplayObject');			
			var options = (typeof param == 'object') ? param : {} ; 
			if (!data) $this.data('wbDisplayObject', (data = new WBDisplayObject ($this, options)));	
			if (param == undefined) return data;
	}	

}) (window.jQuery);

(function ($) {
	WBCommunicationManager = function ( thisElement, options ) {
		this.init( thisElement, options );
	}
	
	WBCommunicationManager.prototype = 
	{
		defaults: { 
			url: "",
			functionSuccess: undefined,
			functionError: undefined,
			functionAuth: undefined,
			wbObjectManager: undefined,
			clientData: undefined,
			async: true
		},
		
		init: function ( thisElement, options ) {			
			this.thisElement = $(thisElement);
			this.options = $.extend ( {} , this.defaults, options );
		},
		
		getOptions: function () {
			if (! this.options) 
				return this.defaults
			else
				return this.options;
		},
		
		ajaxArray: function (array, allCompletionFunc, allErrorFunc)
		{
			var thisElement = this;
		
			// create a closure
			var createAjax = function (options) {
				return $.ajax( { url: options.url , 
					  async: options.async,
				      contentType: 'application/json',
				  	  data: options.payloadData,
					  type: options.httpOperation,
					  dataType:"html",
					  error: function () {
						if (options.wbObjectManager) {
							options.wbObjectManager.setEditableFields(true);
						}
						// TBD define a communication issue
					  },
					  success: function(data) {
						if (options.wbObjectManager) {
							options.wbObjectManager.setEditableFields(true);
						}
						data = data || "{}";
						var retObject = JSON.parse(data);
						if ('auth' in retObject) {
							if (options.functionAuth) {
								options.functionAuth(retObject.auth);
							}
						} 						
						if (retObject.status == "OK" || retObject.status == "200") {							
							if (options.functionSuccess) {
								options.functionSuccess(retObject.payload,options.clientData);
							}
						}
						if (retObject.status == "FAIL" || retObject.status == "400") {
							if (options.functionError) {
								options.functionError(retObject.errors, retObject.payload, options.clientData);
							}						
						}
					  }
					});
			} ;
			var ajaxArray = [];
			for (x in array) {
				var options = array[x];
				if (options.wbObjectManager) {
					options.wbObjectManager.setEditableFields(false);
				}
				var ajx = createAjax(options);
				ajaxArray.push(ajx);
			}
			if (allCompletionFunc || allErrorFunc) {
				$.when.apply($, ajaxArray).then( allCompletionFunc, allErrorFunc);
			}
		},
		ajax : function ( options ) {	
			var array = [options ];
			this.ajaxArray(array);
		}		
	}
	$.fn.wbCommunicationManager = function ( params ) {
		var $this = $(this),
		data = $this.data('wbCommunicationManager');			
		var options = (typeof params == 'object') ? params : {} ; 
		if (!data) $this.data('wbCommunicationManager', (data = new WBCommunicationManager ($this, options)));	
		if (params == undefined) return data;
	}	
}) (window.jQuery);


(function ($) {

	WBTable = function ( thisElement, options ) {
		this.init( thisElement, options );
	}
	
	function notifyOperation(wbtable, operationName, object, keyName) {
		$.each(wbtable.notifiers, function(index, aNotifier) {
			aNotifier(operationName, object, keyName, wbtable.notifiersData[index]);
		});
	}
	
	WBTable.prototype = 
	{
		priv: { 
			intTableClass: "__wbTblclass",
			intPaginationClass: "__wbPagclass",
			wbinsert: 'insert',
			wbupdate: 'update',
			wbdelete: 'delete',
			wbdeleteAll: 'deleteAll'
		},
		defaults: {
			/*
			Array representing the table columns.
			Each column will have the format: {display:'column title', fieldId: 'objectFieldProperty'}
			display - String that will be displayed in the table header for this column
			fieldId - Each row is seen as an object, with the columns as the object properties. The value for the current column is the value of the fieldId object property 
			customHandling - spacifies if the html data for this column is constructed from the row object or by a customhandler callback. Value type is boolean. 
			customHandler - callback used to populate the column. The signature is function (fieldid, rowData). This isapplied only if customHandling = true
			*/
			columns : [], 
			/*
			Class that will be applied to the current table html element. Multiple classes can be specified i.e 'table-stripped table-bordered'
			*/
			tableBaseClass: "",
			/*
			If one of the row object properties identifies in a unique way the current row object among all table row objects then the property name can be specified as keyName.
			Find, update, delete operations on rows exists based on row key value.
			*/
			keyName: undefined,
			/*
			How many items to display per a table page
			*/
			itemsPerPage: 10,
			
			paginationBaseClass: "",
			
			textLengthToCut: 40
			},
		getOptions: function () {
			if (! this.options) 
				return this.defaults
			else
				return this.options;
		},
		init: function( thisElement, options) { 
			this.thisElement = thisElement;
			this.options = $.extend ( {} , this.defaults, options );	
			var header ='<tr>';
			$.each(this.getOptions().columns, function(index, item) {
				header += ('<th>' + escapehtml(item.display) + '</th>');
			});
			header += '</tr>';
			var html = ("<table class='{0} {1}'> <thead>{2}</thead><tbody></tbody></table> " +
					   "<div class='__wbPagclass {3}'> </div> ").format(this.priv.intTableClass, 
															  escapehtml(this.getOptions().tableBaseClass), 
															  header,
															  escapehtml(this.getOptions().paginationBaseClass));
			$(thisElement).html(html);
			
			var tableData = new Array();
			this.thisElement.data('tableData', tableData);
			
			this.notifiers = new Array();
			this.notifiersData = new Array();
			this.currentPageIndex = 0;
		},
		getPageFromIndex: function (recordIndex) {
			// given a record index, in which page this record belongs ?
			if (this.getOptions().itemsPerPage>0) {
				return Math.floor(recordIndex / this.getOptions().itemsPerPage);
			} else			
				return 0;
		},
		clearRows: function() {
			this.setRows();
		},
		clearNotifiers: function () {
			this.notifiers = [];
			this.notifiersData = [];
		},
		removeNotifier: function (notifierData) {
			var tempThis = this;
			$.each( tempThis.notifierData, function (index, item) {
				if (notifierData == item) {
					tempThis.notifiers.splice(index, 1);
					tempThis.notifiersData.splice(index, 1);
					return;
				}
			});
		},
		addNotifier: function (notifier, notifierData) {
			this.notifiers.push(notifier);
			this.notifiersData.push(notifierData);
		},
		appendRows: function(recordSet, startIndex) {
			recordSet = recordSet || [];
			var tempThis = this;
			if (startIndex == undefined || startIndex > tempThis.thisElement.data('tableData').length) {
				startIndex = tempThis.thisElement.data('tableData').length;
			}
			$.each (recordSet, function (index, item) {
				var bAdded = false;
				tempThis.thisElement.data('tableData').splice(startIndex+index,0, item);
				notifyOperation(tempThis, tempThis.priv.wbinsert, item, tempThis.getOptions().keyName);											
			});			
			var start = tempThis.currentPageIndex*tempThis.getOptions().itemsPerPage;
			if (startIndex < start + tempThis.getOptions().itemsPerPage) {
				tempThis.displayTable();
			}
			tempThis.displayPagination();
			
		},
		displayTable: function (pageIndex) {
			var tempThis = this;
			if (pageIndex == undefined) {
				pageIndex = tempThis.currentPageIndex;
			}
			$(tempThis.thisElement).children('table').children('tbody').html("");
			var elements = tempThis.thisElement.data('tableData');
			var start = pageIndex*tempThis.getOptions().itemsPerPage;
			var maxLength = tempThis.getOptions().textLengthToCut;
			for( var i = start; (i < elements.length) && i < (start + tempThis.getOptions().itemsPerPage); i++)
			{
				var html = '<tr>';
				var columns = tempThis.getOptions().columns;				
				for(var x = 0; x< columns.length; x++) {
					
					var value = "";
					if (columns[x].customHandling == true && columns[x].customHandler) {
						value = (columns[x].customHandler)(columns[x].fieldId, elements[i]);
					} else {
						value = "" + elements[i][columns[x].fieldId];
						if (value.length > maxLength) {
							value = value.substring(0,maxLength);
							value += "...";
						}
						value = escapehtml(value);

					}
					html += ('<td>' + value + '</td>');
				}
				html += '</tr>';	
				$(tempThis.thisElement).children('table').children('tbody').append(html);
			}
			tempThis.currentPageIndex = pageIndex;
		},
		displayPagination: function (pageIndex) {
			pageIndex = pageIndex || this.currentPageIndex;
			var tempThis = this;
			var countPages = Math.ceil (tempThis.thisElement.data('tableData').length / tempThis.getOptions().itemsPerPage);
			var html = '<ul>';
			var startIndex = 0;
			var endIndex = countPages;
			for (var i = startIndex; i< countPages; i++) {
				if (i != pageIndex) {
					html += '<li> <a href="#"> {0} </a> </li>'.format(i+1);
				} else {
					html += '<li class="active"> <a href="#">{0}</a></li>'.format(i+1);
				}				
			}
			html += '</ul>';
			$(tempThis.thisElement).children('.__wbPagclass').html(html);
			$(tempThis.thisElement).children('.__wbPagclass').data('tempThis', this);
			$(tempThis.thisElement).find('.__wbPagclass a').click (function () { 
				if ($(this).parent().hasClass('active')) {
					return;
				}
				var tempThis = $(this).parent().parent().parent().data('tempThis');
				tempThis.changePage(parseInt($(this).text()) - 1);
			});
		},
		changePage: function (pageIndex) {
			this.displayTable(pageIndex);
			this.displayPagination(pageIndex);
		},
		setRows: function(recordSet) {
			recordSet = recordSet || [];
			var tempThis = this;
			$(tempThis.thisElement).children('table').children('tbody').html("");
			this.currentPageIndex = 0;
			var len = tempThis.thisElement.data('tableData').length;
			tempThis.thisElement.data('tableData', []);
			notifyOperation(tempThis, tempThis.priv.wbdeleteAll, undefined, undefined);			
			this.appendRows(recordSet);			
		},
		updateRow: function(row, index) {
			var tempThis = this;				
			var elements = tempThis.thisElement.data('tableData');
			if (index != undefined && index < elements.length) {
				this.thisElement.data('tableData')[index] = row;
				notifyOperation(tempThis, tempThis.priv.wbupdate, row, tempThis.getOptions().keyName);
				var start = tempThis.currentPageIndex* tempThis.getOptions().itemsPerPage;
				if ((index >= start) && (index < start + tempThis.getOptions().itemsPerPage)) {
					tempThis.displayTable();
				}
			}							
		},
		insertRow: function(row, startIndex) {
			var recordSet = [ row ];
			this.appendRows(recordSet, startIndex);
		},
		deleteRow: function(index) {
			var tempThis = this;
			if (index != undefined && index < this.thisElement.data('tableData').length) {
				var itemData = this.thisElement.data('tableData')[index];
				notifyOperation(tempThis, tempThis.priv.wbdelete, itemData, tempThis.getOptions().keyName);
				this.thisElement.data('tableData').splice(index,1);	
				var start = tempThis.currentPageIndex* tempThis.getOptions().itemsPerPage;
				if (index < start + tempThis.getOptions().itemsPerPage) {
					tempThis.displayTable();
				}

			}				
		},
		getRowData: function (index) {
			var array = this.thisElement.data('tableData');
			if (0<=index && index < array.length) {
				return array[index];
			}			
			return undefined;
		},
		getAllRowsData: function () {
			var array = this.thisElement.data('tableData');
			return array;
		},		
		findIndexWithKey: function(key) {
			var tempThis = this;
			var array = this.getAllRowsData();
			var idx = -1;
			$.each (array, function (index, value) {
				if (value[tempThis.getOptions().keyName] == key) {
					idx = index;
					return false;
				}			
			});
			return idx;	
		},
		updateRowWithKey: function (row, key) {
			var idx = this.findIndexWithKey(key);
			if (idx >=0) {
				this.updateRow(row, idx);
				return idx;
			}
			return -1;
		},
		deleteRowWithKey: function (key) {
			var idx = this.findIndexWithKey(key);
			if (idx >=0) {
				this.deleteRow(idx);
				return idx;
			}
			return -1;
		},
		getRowDataWithKey: function (key) {
			var idx = this.findIndexWithKey(key);
			if (idx >=0) {
				return this.getRowData(idx);
			}
			return undefined;			
		}
		
	};
	
	$.fn.wbTable = function ( param ) {
			var $this = $(this),
			data = $this.data('wbTable');			
			var options = (typeof param == 'object') ? param : {} ; 
			if (!data) $this.data('wbTable', (data = new WBTable ($this, options)));	
			if (param == undefined) return data;
	}	
})(window.jQuery);

(function ($) {

	WBSpinner = function ( thisElement, options ) {
		
		var myCurrentTime = function (){
			if (window.performance && window.performance.now) {
				return window.performance.now();
			} else
			{
				return (new Date()).getTime();
			}
		};

		this.defaults = { 
				imageFile: "",
				contentElem: undefined,
				delayDisplay: 0,
				visible: false
			};
			
		this.init = function ( thisElement, options ) {			
				this.thisElement = $(thisElement);
				this.displayTime = 0;
				this.options = $.extend ( {} , this.defaults, options );
				if ( false == this.getOptions().visible) {
					this.hide();
				} else {
					this.show();
				}	
			};
			
		this.getOptions = function () {
				if (! this.options) 
					return this.defaults
				else
					return this.options;
			};
		this.visible = function () {
				return this.getOptions().visible;
			};
		this.show = function ( ) {	
				this.options.visible = true;
				if (this.displayTime == 0) {
					this.displayTime = myCurrentTime();
				}
				$(this.thisElement).show();
				if (this.getOptions().contentElem)
					$(this.getOptions().contentElem).hide();
			};
		this.hide = function ( ) {
				this.options.visible = false;
				if (this.getOptions().delayDisplay>0) {
					var diff = myCurrentTime() - this.displayTime;
					if (diff < this.getOptions().delayDisplay) {
						var timerHandler = undefined;
						var X = this;
						var timeOutFunc = function() {
							diff = myCurrentTime() - X.displayTime;
							if (diff >= X.getOptions().delayDisplay)
							{
								if (timerHandler) {
									clearInterval(timerHandler);
								}
								X.displayTime = 0;
								$(X.thisElement).hide();
								if (X.getOptions().contentElem)
									$(X.getOptions().contentElem).show();
							}
						}
						timerHandler = setInterval(timeOutFunc, 50);
					} else
					{
						this.displayTime = 0;
						$(this.thisElement).hide();
						if (this.getOptions().contentElem)
							$(this.getOptions().contentElem).show();
					}
				} else {
					this.displayTime = 0;
					$(this.thisElement).hide();
					if (this.getOptions().contentElem)
						$(this.getOptions().contentElem).show();				
				}
			}
		this.toggle = function ( ) {	
				
				if (true == this.getOptions().visible) {
					this.hide();
				} else {
					this.show();
				}
			};
		this.init( thisElement, options );
	
	};

	$.fn.WBSpinner = function ( params ) {
		var $this = $(this),
		data = $this.data('wbSpinner');			
		var options = (typeof params == 'object') ? params : {} ; 
		if (!data) $this.data('wbSpinner', (data = new WBSpinner ($this, options)));	
		if (params == undefined) return data;
	}		


}) (window.jQuery);

(function ($) {

	WBSearchBox = function ( jQElement, options ) {
		this.init( jQElement, options );
	};
	
	WBSearchBox.prototype = 
	{
		defaults: {
			classSearchList: "",
			searchListSize: 5,
			searchFields: [],
			displayHandler: undefined,
			afterDisplayHandler: undefined,
			selectHandler: undefined,
			emptySearchResult: "No results found",
			delaySearch: 300,
			loadDataHandler: undefined,
			jQInputBox: undefined,
			jQSearchListContainer: undefined
		},
		lastKeyPressTimestamp: 0,
		
		getOptions: function () {
			if (! this.options) 
				return this.defaults
			else
				return this.options;
		},
		searchInArray: function(array, toFind, fieldsSet, count) {
			var result = new Array()
			for (var i = 0; i< array.length; i++ ) {
				for (x in fieldsSet) {
					if ( array[i][fieldsSet[x]].indexOf(toFind)>=0) {
						result.push(array[i]);
						if (count == 0) return result;
						count-=1;
						break;
					}
				}
			}
			return result;
		},
	
		privKeyDownHandler: function (event, searchBox)
		{
			if (event.keyCode == 27) {
				//ESC so hide the options list
				searchBox.optionsWrapper.hide();
				return true;
			}

			if (event.keyCode == 13) {
				if (searchBox.optionsWrapper.is(":visible")) {
					var lis = searchBox.optionsList.find('.wbsearchboxsel');
					if (lis.length == 1) {
						var record = $(lis[0]).data('rec');
						if (searchBox.options.selectHandler) {
							searchBox.options.selectHandler(record, searchBox);
						}
						
					}
				}
				return true;
			}
			if (event.keyCode == 40) {	
				if (! searchBox.optionsWrapper.is(":visible")) {					
					if (searchBox.optionsList.find('li').length > 0) {
						searchBox.showResults();
						return true;
					}
				}
			}
			
			// handle arrow up and arrow down
			if (event.keyCode == 40 || event.keyCode == 38) {	
				var direction, siblingsSelector;
				if (event.keyCode == 38) { // up
					direction = 'prev';
					siblingsSelector = ':not(:first-child)';
					if (searchBox.optionsList.find('.wbsearchboxsel').length == 0) {
						searchBox.optionsList.find('li:last-child').addClass('wbsearchboxsel');
						return true;
					}
					
				} else if (event.keyCode == 40) { // down
					direction = 'next';
					siblingsSelector = ':not(:last-child)';
					if (searchBox.optionsList.find('.wbsearchboxsel').length == 0) {
						searchBox.optionsList.find('li:first-child').addClass('wbsearchboxsel');
						return true;
					}					
				}
				searchBox.optionsList.find('.wbsearchboxsel')[direction]().addClass('wbsearchboxsel').siblings(siblingsSelector).removeClass('wbsearchboxsel');			
				return true;
			}			
		},
		
		timeoutHandler: function(elem, event) {		
			var val = $(elem.searchBox).val();
			if (val.length == 0) {
				// no string to search so hide the options list
				this.optionsWrapper.hide();
				return;
			}
			
			if (this.privKeyDownHandler(event, this)) {
				return;
			}
			
			if (val.length>0 && event.which >0) {
				var timestamp = new Date();
				var timestampPrev = this.lastKeyPressTimestamp;
				if (timestamp - timestampPrev < this.getOptions().delaySearch) {
					return;
				}
				this.optionsList.empty();
				this.crud('deleteAll');
				if (this.options.loadDataHandler) {
					this.options.loadDataHandler(this);
				}
				var result = this.searchInArray(this.dataElements, val, this.getOptions().searchFields, this.getOptions().searchListSize);
				this.showResults();
				for(var x in result) {
					var html = "";
					if (this.getOptions().displayHandler) {
						html = this.getOptions().displayHandler(result[x]);
					}
					var id = guid();
					this.optionsList.prepend("<li id='{0}'>{1}</li>".format(id, html));
					$('#'+id).data('rec', result[x]);
				}
				if (result.length == 0) {
					this.optionsList.prepend("<li>{0}</li>".format(this.getOptions().emptySearchResult));
				}
				if (this.options.afterDisplayHandler) {
					this.options.afterDisplayHandler(this);
				}

				this.optionsList.find('li').mouseenter(function () {
					$(this).addClass('wbsearchboxsel').siblings().removeClass('wbsearchboxsel');
				});
			}
			
		},
		showResults: function() {
			this.optionsWrapper.show();
			this.optionsWrapper.data('wbSearchBoxFocus', false);
			this.searchBox.data('wbSearchBoxFocus', false);		
		},
		
		hideResults: function() {
			this.optionsWrapper.hide();
		},		
		privPressHandler: function (event) {
			var tempThis = $(this).data('wbSearchBox');
			tempThis.lastKeyPressTimestamp = new Date();	
			setTimeout( function() {
				tempThis.timeoutHandler(tempThis, event) }, tempThis.getOptions().delaySearch);
		},
		
		crud: function (operationName, objectValue, keyName) {
			var tempThis = this;
			if (operationName == 'insert') {
				tempThis.dataElements.push(objectValue);
				return;
			} else if (operationName == 'delete') {
				for (var i = 0; i < tempThis.dataElements.length; i++) {
					if (tempThis.dataElements[keyName] == objectValue[keyName]) {
						tempThis.dataElements.splice(i, 1);
						return;
					}
				}
			} else if (operationName == 'update') {
				for (var i = 0; i < tempThis.dataElements.length; i++) {
					if (tempThis.dataElements[keyName] == objectValue[keyName]) {
						tempThis.dataElements[i] = objectValue;
						return;
					}
				}			
			} else if (operationName == 'deleteAll') {
				tempThis.dataElements.splice(0);
				return;
			}
			
		},
		privTimerLostFocus : function(searchBox) {
			var inputBoxFocus = searchBox.searchBox.data('wbSearchBoxFocus') || false;
			var resultsListFocus = searchBox.optionsWrapper.data('wbSearchBoxFocus') || false;
			if (! (inputBoxFocus || resultsListFocus)) {
				searchBox.optionsWrapper.hide();
			}			
		},
		privLostFocus: function(event) {
			$(event.target).data('wbSearchBoxFocus', false);
			var searchBox = $(event.target).data('wbSearchBox');
			if (! searchBox) return;
			setTimeout( function() { searchBox.privTimerLostFocus(searchBox) }, 200);
		},
		privOnFocus: function(event) {
			$(event.target).data('wbSearchBoxFocus', true);
		},
		init: function ( jQElem, options) {
			this.jQElement = jQElem;
			this.options = $.extend ( {} , this.defaults, options );	
			this.isListVisible = false;
			var html = "<ul class=' {0}'> </ul>".format(escapehtml(this.options.classSearchList)); 			
			$(this.options.jQSearchListContainer).html(html);
			this.searchBox = $(this.options.jQInputBox);
			this.searchBox.data('wbSearchBox', this);
			
			this.optionsWrapper = $(this.options.jQSearchListContainer);
			this.optionsWrapper.attr("tabindex", "-1");
			
			this.optionsWrapper.hide();
			
			this.optionsList = $(this.optionsWrapper).find("ul")[0];
			this.optionsList = $(this.optionsList);
			this.optionsList.data('wbSearchBox', this);
			$(this.searchBox).on("keydown", this.privPressHandler);
			$(this.searchBox).on("blur", this.privLostFocus);
			$(this.searchBox).on("focus", this.privOnFocus);
			
			this.dataElements = new Array();
			
			var tempSearchBox = this;
			$(this.optionsWrapper).on("keydown", function (event) { 
				// hide the options if ESC on optionsWrapper
				tempSearchBox.privKeyDownHandler(event, tempSearchBox);
				
				});
			this.optionsWrapper.data('wbSearchBox', this);
			$(this.optionsWrapper).on("blur", this.privLostFocus);
			$(this.optionsWrapper).on("focus", this.privOnFocus);
			}
	};
	
	$.fn.wbSearchBox = function ( param ) {
			var $this = $(this),
			data = $this.data('wbSearchBox');			
			var options = (typeof param == 'object') ? param : {} ; 
			if (!data) $this.data('wbSearchBox', (data = new WBSearchBox ($this, options)));	
			if (param == undefined) return data;
	}	
})(window.jQuery);

$().ready( function () {
	
	$('#cmssearchbox').wbCommunicationManager({async:false});
	
	var loadDataHandlerFunction = function(wbSearchBox) {
		var fSuccessGetResources = function (data) {
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
		var anchorElem = "";
		switch (item.type)
		{
			case "1": 
				anchorElem = "<a href='./weburiedit.html?extKey={0}'>{1}</a>".format(encodeURIComponent(item["rkey"]), escapehtml(item["name"]));
				break;
			case "2": 
				anchorElem = "<a href='./webpage.html?extKey={0}'>{1}</a>".format(encodeURIComponent(item["rkey"]), escapehtml(item["name"]));
				break;
			case "3": 
				anchorElem = "<a href='./webpagemodule.html?extKey={0}'>{1}</a>".format(encodeURIComponent(item["rkey"]), escapehtml(item["name"]));
				break;
			case "5":
				anchorElem = "<a href='./webarticleedit.html?extKey={0}'>{1}</a>".format(encodeURIComponent(item["rkey"]), escapehtml(item["name"]));
				break;
			case "6":
				anchorElem = "<a href='./webfile.html?extKey={0}'>{1}</a>".format(encodeURIComponent(item["rkey"]), escapehtml(item["name"]));
				break;
		}
		switch (item.type)
		{
			case "1": 
			case "2": 
			case "3": 
			case "5":
			case "6":
				str = '<span class="itemelem itemtype">{0}</span><span class="itemelem">{1}</span><span data-clipboard-text="{1}" class="itemelem wbbtnclipboard btn-s-clipboard"></span><span class="itemelem wbbtndummy">&nbsp</span><span class="itemelem">{2}</span><div class="clear"/>'.format(escapehtml(type), escapehtml(item["rkey"]), anchorElem);
				break;
			case "7": 	
			case "4": 
				str = '<span class="itemelem itemtype">{0}</span><span class="itemelem">{1}</span><span data-clipboard-text="{1}" class="itemelem wbbtnclipboard btn-s-clipboard"></span><span class="itemelem wbbtndummy">&nbsp</span><div class="clear"/>'.format(escapehtml(type), escapehtml(item["name"]));		
				break;
		}
		return str;
	};
	
	var selectHandlerFunction = function (item, searchBox) {
		var newUrl = "";
		switch (item.type)
		{
			case "1": 
				newUrl = "./weburiedit.html?extKey={0}".format(encodeURIComponent(item["rkey"]));
				break;
			case "2": 
				newUrl = "./webpage.html?extKey={0}".format(encodeURIComponent(item["rkey"]));
				break;
			case "3": 
				newUrl = "./webpagemodule.html?extKey={0}".format(encodeURIComponent(item["rkey"]));
				break;
			case "5":
				newUrl = "./webarticleedit.html?extKey={0}".format(encodeURIComponent(item["rkey"]));
				break;
			case "6":
				newUrl = "./webfile.html?extKey={0}".format(encodeURIComponent(item["rkey"]));
				break;
		}
		if (newUrl.length>0) {
			window.location.href = newUrl;
		}		
	};
	
    var afterDisplayFunction = function(wbsearchbox) {
    	$('.btn-s-clipboard').WBCopyClipboardButoon({buttonHtml:"<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copy to clipboard</div>", basePath: getAdminPath(), selector: '.btn-s-clipboard'});
    	$('.btn-s-clipboard').WBCopyClipboardButoon().on("aftercopy", function (e) {
    		$('.btn-s-clipboard').WBCopyClipboardButoon().reset();
    		$(e.target).html("<i class='fa fa-paste'></i><div class='wbclipboardtooltip'>Copied!</div>");
            wbsearchbox.getOptions().jQInputBox.focus();
    	});
    };

	
	$('#cmssearchbox').wbSearchBox({searchFields:['name','rkey'], classSearchList:'wbsearchresultlist' ,afterDisplayHandler: afterDisplayFunction, displayHandler: displayHandlerFunction, selectHandler: selectHandlerFunction,
					loadDataHandler: loadDataHandlerFunction, jQInputBox: $('#cmssearchbox'), jQSearchListContainer: $('#searchResultList')});
	
});
