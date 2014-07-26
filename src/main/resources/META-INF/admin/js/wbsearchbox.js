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
			console.log('wbsearchbox - search in array');
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
					if (this.optionsList.find('.wbsearchboxsel').length == 0) {
						this.optionsList.find('li:last-child').addClass('wbsearchboxsel');
						return;
					}
					
				} else if (event.keyCode == 40) { // down
					direction = 'next';
					siblingsSelector = ':not(:last-child)';
					if (this.optionsList.find('.wbsearchboxsel').length == 0) {
						this.optionsList.find('li:first-child').addClass('wbsearchboxsel');
						return;
					}					
				}
				this.optionsList.find('.wbsearchboxsel')[direction]().addClass('wbsearchboxsel').siblings(siblingsSelector).removeClass('wbsearchboxsel');			
				return;
			}
			
			if (val.length>0 && event.which >0) {
				var timestamp = new Date();
				var timestampPrev = this.lastKeyPressTimestamp;
				console.log('diff ' + (timestamp-timestampPrev));
				if (timestamp - timestampPrev < this.getOptions().delaySearch) {
					console.log(' return ' + (timestamp - timestampPrev));
					return;
				}
				this.optionsList.empty();
				this.crud('deleteAll');
				if (this.options.loadDataHandler) {
					this.options.loadDataHandler(this);
				}
				var result = this.searchInArray(this.dataElements, val, this.getOptions().searchFields, this.getOptions().searchListSize);
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
					$(this).addClass('wbsearchboxsel').siblings().removeClass('wbsearchboxsel');
				});
			}
			
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
		init: function ( jQElem, options) {
			this.jQElement = jQElem;
			this.options = $.extend ( {} , this.defaults, options );	
			this.isListVisible = false;
			var html = "<ul class=' {0}'> </ul>".format(escapehtml(this.options.classSearchList)); 			
			$(this.options.jQSearchListContainer).html(html);
			this.searchBox = $(this.options.jQInputBox);
			this.searchBox.data('wbSearchBox', this);
			
			this.optionsWrapper = $(this.options.jQSearchListContainer);
			this.optionsWrapper.hide();
			
			this.optionsList = $(this.optionsWrapper).find("ul")[0];
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