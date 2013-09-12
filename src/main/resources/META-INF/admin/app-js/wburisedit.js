

$().ready( function () {
	var updaterFunction = function(item) {
		//return item;
		x = item.indexOf(' {');
		if (x>=0) {
			y = item.indexOf('}', x);
			if (y>=0) {
				return item.substring(x,y+1);
			}
		}
		return item;
	};
	var sourceFunction = function(query, process) {
		return ['index.html {1234ssddsjhfkldjsdsdsdsdsdsdsdsdshlakdhflkjhdfadf56}', 'done {45dfasfdasfasff67890}'];
	}
	$('#wbcresourceExternalKey').typeahead( {
		source: sourceFunction,
		items: 3,
		updater: updaterFunction
	});
});

