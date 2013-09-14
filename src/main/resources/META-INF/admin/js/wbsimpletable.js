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
			var _this = this;
			$.each(this.getOptions().columns, function(index, item) {
				item.isHtmlDisplay = item.isHtmlDisplay || false;
				var value = escapehtml(item.display);
				if (item.isHtmlDisplay) {
					value = "<a href='' class='{0} {1}{2}' > {3} </a>".format(escapehtml(_this.getOptions().headerColumnBaseClass),
																	   escapehtml(_this.getOptions().headerColumnIdClassPrefix),
																	   escapehtml(item.fieldId),
																	   escapehtml(item.display)
																	   );
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
			
			$(this.thisElement).on ("click", "." + this.getOptions().headerColumnBaseClass, function (e) {
				e.preventDefault();
				if (! _this.getOptions().handlerColumnClick) return;
				
				var classList = $(this).attr('class').split(/\s+/);
				var thisElem = this;
				$(classList).each(function(index, item){
					if (item.indexOf(_this.getOptions().headerColumnIdClassPrefix) == 0){
						var field = item.substring(_this.getOptions().headerColumnIdClassPrefix.length);						
						var dir = 'asc';
						if ($(thisElem).hasClass('header-asc')) {
							dir = 'dsc';
						}
						_this.getOptions().handlerColumnClick (_this, field, dir);
						return false;
					}

				});
			});

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
				item.isHtmlDisplay = item.isHtmlDisplay || false;
				var value = "";
				if (item.fieldId == option.fieldId) {
					value = escapehtml(option.display);
					if (option.isHtmlDisplay) {
						
						if ('sortDirection' in option) {
							var icon = "icon-arrow-up";
							var sortClass = "header-asc";
							if (option.sortDirection == "dsc") {
								icon = "icon-arrow-down";
								sortClass = "header-dsc";
							}
							value = "<a href='' class='{0} {1}{2} {3}' > {4} <i class='{5}'></i> </a>".format(escapehtml(tempThis.getOptions().headerColumnBaseClass),
									   escapehtml(tempThis.getOptions().headerColumnIdClassPrefix),
									   escapehtml(option.fieldId),
									   escapehtml(sortClass),
									   escapehtml(option.display),
									   escapehtml(icon)
									   );

						} else
							value = "<a href='' class='{0} {1}{2}' > {3} </a>".format(escapehtml(tempThis.getOptions().headerColumnBaseClass),
								   escapehtml(tempThis.getOptions().headerColumnIdClassPrefix),
								   escapehtml(option.fieldId),
								   escapehtml(option.display)
								   );
						
					}
				} else {
					value = escapehtml(item.display);
					if (item.isHtmlDisplay) {
						if ('sortDirection' in item) {
							var icon = "icon-arrow-up";
							var sortClass = "header-asc";
							if (item.sortDirection == "dsc") {
								icon = "icon-arrow-down";
								sortClass = "header-dsc";
							}
							value = "<a href='' class='{0} {1}{2} {3}' > {4} <i class='{5}'></i> </a>".format(escapehtml(tempThis.getOptions().headerColumnBaseClass),
									   escapehtml(tempThis.getOptions().headerColumnIdClassPrefix),
									   escapehtml(item.fieldId),
									   escapehtml(sortClass),
									   escapehtml(item.display),
									   escapehtml(icon)
									   );

						} else
							value = "<a href='' class='{0} {1}{2}' > {3} </a>".format(escapehtml(tempThis.getOptions().headerColumnBaseClass),
								   escapehtml(tempThis.getOptions().headerColumnIdClassPrefix),
								   escapehtml(item.fieldId),
								   escapehtml(item.display)
								   );
					}
				}
				header += '<th>{0}</th>'.format(value);
			});
			header += '</tr>';
			$(tempThis.thisElement).children('table').children('thead').html(header);			
		},
		addSortIconToColumnHeader: function(field, dir)
		{
			var column = this.getColumnHeader(field);
			column['isHtmlDisplay'] = true;
			if (dir == 'asc' || dir == 'dsc'){
				column['sortDirection'] = dir;
			}
			column['fieldId'] = field;
			column.display = column.display;
			this.updateColumnHeader(column)
		},
		
		_setPagination: function(pages) {
			// pages is an array of elements like { display, link, option }, option 1 for current 
			var elem = $(this.thisElement).find(".__wbPagclass"); 
			elem.html("");
			var html = "<ul>";
			$.each (pages, function (index, item) {
				var itemClass = ""
				if (item.option == 1) {
					itemClass = "active";
				}
				html = html + "<li class='{0}'> <a href='{1}'> {2} </a> </li>".format(itemClass, escapehtml(item.link), escapehtml(item.display)); 
			});
			html += "</ul>";
			elem.html(html);			
		},
		
		setPagination: function (link, totalRecords, itemsPerPage, pageParamName){
			var countPages = Math.ceil (totalRecords / itemsPerPage);
			var currentPage = getURLParameter(pageParamName, link) || 1;
			pages = [];
			for (var i = 0;i<countPages; i++){
				var item = {}
				item['display'] = "" + (i+1);
				if ((i+1) == currentPage) {
					item['option'] = 1;
				}
				item['link'] = replaceURLParameter(link, 'page', item['display']);
				pages.push(item);
			}
			this._setPagination(pages);		
		},
		
		clearPagination: function () {
			$(this).find(".__wbPagclass").html("");
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