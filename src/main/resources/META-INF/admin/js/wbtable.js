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
})(window.jQuery)