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
			wbObjectManager: undefined,
			clientData: undefined
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
		
		ajax : function ( options ) {	
			var thisElement = this;
			if (options.wbObjectManager) {
				options.wbObjectManager.setEditableFields(false);
			}
			
			$.ajax( { url: options.url , 
				  async: true,
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
					if (retObject.status == "OK") {							
						if (options.functionSuccess) {
							options.functionSuccess(retObject.payload,options.clientData);
						}
					}
					if (retObject.status == "FAIL") {
						if (options.functionError) {
							options.functionError(retObject.errors, retObject.payload, options.clientData);
						}						
					}
				  }
				});
		}		
	}

	$.fn.wbCommunicationManager = function ( params ) {
		var $this = $(this),
		data = $this.data('wbCommunicationManager');			
		var options = (typeof params == 'object') ? params : {} ; 
		if (!data) $this.data('wbCommunicationManager', (data = new WBCommunicationManager ($this, options)));	
		if (params == undefined) return data;
	}	
	

}) (window.jQuery)