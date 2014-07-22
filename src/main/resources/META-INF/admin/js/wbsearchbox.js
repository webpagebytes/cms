(function ($) {

	WBSearchBox = function ( jQElement, options ) {
		this.init( jQElement, options );
	};
	
	// search toFind into the array objects and try to match against the fieldsSet fields 
	function searchInArray(array, toFind, fieldsSet, count) {
		var result = new Array()
		for (var i = 0; i< array.length; i++ ) {
			for (x in fieldsSet) {
				if ( array[i][fieldsSet[x]].indexOf(toFind)>=0) {
					result.push(array[i]);
					if (count == 0) return result;
					count-=1;
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
			searchFields: [],
			displayHandler: undefined,
			emptySearchResult: "No results found"
		},
		getOptions: function () {
			if (! this.options) 
				return this.defaults
			else
				return this.options;
		},
		timeoutHandler: function(elem, event) {		
			var val = $(elem.searchBox).val();
			console.log(event.which);
			if (val.length == 0) {
				// no string to search so hide the options list
				this.optionsWrapper.hide();
				return;
			}
			if (event.keyCode == 27) {
				//ESC so hide the options list
				this.optionsWrapper.hide();
				return;
			}

			if (event.keyCode == 13) {
				//ENTER so hide the options list
				this.optionsWrapper.hide();
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
				var result = searchInArray(this.dataElements, val, this.getOptions().searchFields, this.getOptions().searchListSize);
				this.optionsWrapper.show();
				for(var x in result) {
					var html = "";
					if (this.getOptions().displayHandler) {
						html = this.getOptions().displayHandler(result[x]);
					}
					this.optionsList.prepend("<li>{0}</li>".format(html));	
				}
				if (result.length == 0) {
					this.optionsList.prepend("<li>{0}</li>".format(this.getOptions().emptySearchResult));
				}
				this.optionsList.find('li').mouseenter(function () {
					$(this).addClass('wbselected').siblings().removeClass('wbselected');
				});
			}
			
		},
		privPressHandler: function (event) {
			//event.preventDefault();
			var tempThis = $(this).data('wbSearchBox');
			setTimeout( function() {
				tempThis.timeoutHandler(tempThis, event) }, 10);
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
		init: function ( jQElem, options) {
			this.jQElement = jQElem;
			this.options = $.extend ( {} , this.defaults, options );	
			this.isListVisible = false;
			var html = ("<div class='wbsearchcontainer {0}'> <input type='text' class='wbsearchbox {1}'>" + 
					   "<div class='wbsearchWrapTable'><ul class='wbsearchlist {2}'> </ul> </div></div>").format(escapehtml(this.options.classContainer), escapehtml(this.options.classInputText), escapehtml(this.options.classSearchList)); 			
			this.jQElement.append(html);
			this.searchBox = $(this.jQElement).find("input")[0];
			this.searchBox = $(this.searchBox);
			this.searchBox.data('wbSearchBox', this);
			
			this.optionsWrapper = $(this.jQElement).find(".wbsearchWrapTable")[0];
			this.optionsWrapper = $(this.optionsWrapper);
			this.optionsWrapper.hide();
			
			this.optionsList = $(this.jQElement).find("ul")[0];
			this.optionsList = $(this.optionsList);
			this.optionsList.data('wbSearchBox', this);
			$(this.searchBox).on("keydown", this.privPressHandler);		
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