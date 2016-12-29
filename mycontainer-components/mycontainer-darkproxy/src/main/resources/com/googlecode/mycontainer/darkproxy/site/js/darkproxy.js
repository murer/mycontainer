(function(w, $) {
	
	w.darkproxy = {}
	
	w.darkproxy.conns = function() {
		$.getJSON('s/conns', function(conns) {
			w.conns = conns;
			console.log('conns', conns)
		});
	}
	
	w.darkproxy.loadRequestMeta = function(id) {
		$.ajax({ 
			url: 's/request.json',
			data: { 'id':  id },
			success: function(meta) {
				w.meta = meta;
				console.log('meta', w.meta)
			}
		})
	}
	
	w.darkproxy.loadRequestBody = function(id) {
		$.ajax({ 
			url: 's/request.body',
			data: { 'id':  id },
			success: function(meta) {
				w.meta = meta;
				console.log('body', w.meta)
			}
		})
	}
	
	
})(window, jQuery)