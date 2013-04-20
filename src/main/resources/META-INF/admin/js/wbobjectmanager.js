String.prototype.format = function() {
    var formatted = this;
    for (var i = 0; i < arguments.length; i++) {
        var regexp = new RegExp('\\{'+i+'\\}', 'gi');
        formatted = formatted.replace(regexp, arguments[i]);
    }
    return formatted;
	
};

function getURLParameter(name) {
	var value = (location.search.match(RegExp("[?|&]"+name+'=(.+?)(&|$)'))||[,undefined])[1];
	if (value != undefined) {
		return decodeURIComponent(value);
    } 
	return undefined;
};

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
	if (typeof time == 'string') {
		this.setTime((new Number(time)).valueOf());
	} else {
		this.setTime(time);
	};
	
	if (format == undefined) 
		format = "dd/mm/yyyy hh:mm:ss";
	else
		format =  format.toLowerCase();
	var d = this.getDate(), mo = this.getMonth()+1, yF = this.getFullYear(), h = this.getHours(), mi = this.getMinutes(), s = this.getSeconds();
	d = ("0" + d); d=d.substr(d.length-2);
	mo = ("0" + mo); mo=mo.substr(mo.length-2);
	h = ("0" + h); h=h.substr(h.length-2);
	mi = ("0" + mi); mi=mi.substr(mi.length-2);
	s = ("0" + s); s=s.substr(s.length-2);
	
	if (format == "dd/mm/yyyy hh:mm:ss") {
		return "{0}/{1}/{2} {3}:{4}:{5}".format(d,mo,yF,h,mi,s);
	} else
	if (format == "mm/dd/yyyy hh:mm:ss") {
		return "{0}/{1}/{2} {3}:{4}:{5}".format(mo,d,yF,h,mi,s);
	}
	return "";
}

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
					case 'text':
					case 'hidden':
					case 'password':
					case 'textarea':
					case 'select-multiple':
					case 'select-one':
						var valueToSet = "";
						if (key in tempThis.getOptions().fieldsDefaults)
							valueToSet = tempThis.getOptions().fieldsDefaults[key];
						$(value).val (valueToSet);
						break;
					case 'checkbox':
						var valueToSet = 0;
						if (key in tempThis.getOptions().fieldsDefaults)
							valueToSet = tempThis.getOptions().fieldsDefaults[key];
						$(value).attr('checked', valueToSet==1 ? true: false);
						break;
					case 'radio':
						var name = $(value).attr('name');
						tempThis.thisElement.find('input[name^=' + name + ']').attr('checked', false);
						if (key in tempThis.getOptions().fieldsDefaults)
							var valueToSet = tempThis.getOptions().fieldsDefaults[key];
							var radioElements = tempThis.thisElement.find('input[name^=' + name + '][value="' + valueToSet + '"]');
							$.each(radioElements, function (index, radioItem) {
								$(radioItem).attr('checked', true);
							});
						break;
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
				case 'hidden':
				case 'password':
				case 'textarea':
				case 'select-multiple':
				case 'select-one':
					textValue = $(element).val();
					break;
				case 'checkbox':
					isSelected = 1;
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
	

}) (window.jQuery)