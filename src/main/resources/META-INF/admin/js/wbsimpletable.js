(function ($) {

	WBSimpleTable = function ( thisElement, options ) {
		this.init( thisElement, options );
	}
		
	WBSimpleTable.prototype = 
	{
		priv: { 
			intTableClass: "__wbTblclass",
			intPaginationClass: "__wbPagclass",
			headerClass: "__wbHeaderClass"
		},
		defaults: {
			/*
			Array representing the table columns.
			Each column will have the format: {display:'column title', fieldId: 'objectFieldProperty', 'class':'ClassName'}
			display - String that will be displayed in the table header for this column
			fieldId - Each row is seen as an object, with the columns as the object properties. The value for the current column is the value of the fieldId object property 
			displayHtml: false - if display property should be interpreted as html 
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

			paginationBaseClass: "",
			
			tableItemMaxLength: 40
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
				item.headerHtml = item.headerHtml || false;
				var value = escapehtml(item.display);
				if (item.headerHtml) {
					value = item.display;
				}
				header += '<th>{0}</th>'.format(value);
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
		},
		
		getColumnHeader: function(fieldId) {
			var value = undefined;
			$.each(this.getOptions().columns, function(index, item) {
				if (item.fieldId == fieldId) {
					value = item;
					return false;
				}
			});
			return value;
				
		},
		
		updateColumnHeader: function(option) {
			// option is an item from this.getOptions().columns array
			var tempThis = this;
			
			var header ='<tr>';
			$.each(this.getOptions().columns, function(index, item) {
				item.headerHtml = item.headerHtml || false;
				var value = "";
				if (item.fieldId == option.fieldId) {
					value = escapehtml(option.display);
					if (option.headerHtml) {
						value = option.display;	
					}
				} else {
					value = escapehtml(item.display);
					if (item.headerHtml) {
						value = item.display;
					}
				}
				header += '<th>{0}</th>'.format(value);
			});
			header += '</tr>';
			$(tempThis.thisElement).children('table').children('thead').html(header);			
		},
		
		clearRows: function() {
			this.setRows();
		},
		appendRows: function(recordSet, startIndex) {
			recordSet = recordSet || [];
			var tempThis = this;
			if (startIndex == undefined || startIndex > tempThis.thisElement.data('tableData').length) {
				startIndex = tempThis.thisElement.data('tableData').length;
			}
			$.each (recordSet, function (index, item) {
				tempThis.thisElement.data('tableData').splice(startIndex+index,0, item);
			});			

			tempThis.displayTable();	
		},
		
		displayTable: function () {
			var tempThis = this;
			$(tempThis.thisElement).children('table').children('tbody').html("");
			var elements = tempThis.thisElement.data('tableData');
			var maxLength = tempThis.getOptions().textLengthToCut;
			for( var i = 0; i < elements.length; i++)
			{
				var html = '<tr>';
				var columns = tempThis.getOptions().columns;				
				for(var x = 0; x< columns.length; x++) {
					
					var value = "";
					if (columns[x].customHandler != undefined) {
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
		},
		setRows: function(recordSet) {
			recordSet = recordSet || [];
			var tempThis = this;
			$(tempThis.thisElement).children('table').children('tbody').html("");
			tempThis.thisElement.data('tableData', []);
			this.appendRows(recordSet);			
		},
		updateRow: function(row, index) {
			var tempThis = this;				
			var elements = tempThis.thisElement.data('tableData');
			if (index != undefined && index < elements.length) {
				this.thisElement.data('tableData')[index] = row;
				tempThis.displayTable();
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
				this.thisElement.data('tableData').splice(index,1);	
				tempThis.displayTable();
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
	
	$.fn.wbSimpleTable = function ( param ) {
			var $this = $(this),
			data = $this.data('wbSimpleTable');			
			var options = (typeof param == 'object') ? param : {} ; 
			if (!data) $this.data('wbSimpleTable', (data = new WBSimpleTable ($this, options)));	
			if (param == undefined) return data;
	}	
})(window.jQuery)