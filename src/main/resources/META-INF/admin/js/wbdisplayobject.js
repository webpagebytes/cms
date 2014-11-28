/*
* Copyright 2014 Webpagebytes
* http://www.apache.org/licenses/LICENSE-2.0.txt
*/ 
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

}) (window.jQuery)