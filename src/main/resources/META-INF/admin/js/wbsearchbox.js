(function ($) {

	WBSearchBox = function ( thisElement, options ) {
		this.init( thisElement, options );
	};
	
	// search toFind into the array objects and try to match against the fieldsSet fields 
	function searchInArray(array, toFind, fieldsSet) {
		var result = new Array()
		for (var i = 0; i< array.length; i++ ) {
			for (x in fieldsSet) {
				if ( array[i][fieldsSet[x]].indexOf(toFind)>=0) {
					result.push(array[i][fieldsSet[x]]);
					continue;
				}
			}
		}
		return result;
	}
	
	WBSearchBox.prototype = 
	{
		defaults: {
			classContainer: "",
			classInputText: "",
			classSearchList: "",
			searchListSize: 5,
			searchFields: undefined
		},
		getOptions: function () {
			if (! this.options) 
				return this.defaults
			else
				return this.options;
		},
		timeoutHandler: function(event) {		
			var val = this.searchBox.val();
			if (val.length == 0) {
				// no string to serach so hide the options list
				this.optionsWrapper.hide();
				return;
			}
			if (event.keyCode == 27) {
				//ESC so hide the options list
				this.optionsWrapper.hide();
				event.preventDefault();
				return;
			}
			
			// handle arrow up and arrow down
			if (event.keyCode == 40 || event.keyCode == 38) {	
				var direction, siblingsSelector;
				if (event.keyCode == 38) { // up
					direction = 'prev';
					siblingsSelector = ':not(:first-child)';
					if (this.optionsList.find('.wbselected').length == 0) {
						this.optionsList.find('li:last-child').addClass('wbselected');
						return;
					}
					
				} else if (event.keyCode == 40) { // down
					direction = 'next';
					siblingsSelector = ':not(:last-child)';
					if (this.optionsList.find('.wbselected').length == 0) {
						this.optionsList.find('li:first-child').addClass('wbselected');
						return;
					}					
				}
				this.optionsList.find('.wbselected')[direction]().addClass('wbselected').siblings(siblingsSelector).removeClass('wbselected');			
				return;
			}
			
			if (val.length>0 && event.which >0) {
				this.optionsList.empty();
				var result = searchInArray(this.dataElements, val, this.getOptions().searchFields);
				if (result.length > 0) {
					this.optionsWrapper.show();
				}				
				for(var x in result) {
					var html = "<li>{0}</li>".format(escapehtml(result[x]));
					this.optionsList.prepend(html);	
				}
				this.optionsList.find('li').mouseenter(function () {
					$(this).addClass('wbselected').siblings().removeClass('wbselected');
				});
			}
			
		},
		privPressHandler: function (event) {
			var tempThis = $(this).data('wbSearchBox');;
			setTimeout( function () { 
							tempThis.timeoutHandler(event) 
							}, 10);
		},
		
		changeListener: function (operationName, objectValue, keyName, thisInstance) {
			var tempThis = thisInstance;
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
		init: function ( thisElem, options) {
			this.thisElement = thisElem;
			this.options = $.extend ( {} , this.defaults, options );	
			this.isListVisible = false;
			var html = "<div class='wbsearchcontainer {0}'> <input type='text' class='wbsearchbox {1}'>" + 
					   "<div class='wbsearchWrapTable'><ul class='wbsearchlist {2}'> </ul> </div></div>".format(escapehtml(this.options.classContainer), escapehtml(this.options.classInputText), escapehtml(this.options.classSearchList)); 			
			this.thisElement.append(html);
			this.searchBox = $(this.thisElement).find("input")[0];
			this.searchBox = $(this.searchBox);
			this.searchBox.data('wbSearchBox', this);
			
			this.optionsWrapper = $(this.thisElement).find(".wbsearchWrapTable")[0];
			this.optionsWrapper = $(this.optionsWrapper);
			
			this.optionsList = $(this.thisElement).find("ul")[0];
			this.optionsList = $(this.optionsList);
			this.optionsList.data('wbSearchBox', this);
			$(this.searchBox).keydown (this.privPressHandler);		
			this.dataElements = new Array();
		
			$(this.searchBox).blur( function () { 
				// hide the options if focus is lost
				var tempThis = $(this).data('wbSearchBox');;
				tempThis.optionsWrapper.hide();
				});	
			}
	};
	
	$.fn.wbSearchBox = function ( param ) {
			var $this = $(this),
			data = $this.data('wbSearchBox');			
			var options = (typeof param == 'object') ? param : {} ; 
			if (!data) $this.data('wbSearchBox', (data = new WBSearchBox ($this, options)));	
			if (param == undefined) return data;
	}	
})(window.jQuery)