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
			dataType: 'json',
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
			dataType: 'text',
			success: function(body) {
				w.body = body;
				console.log('body', w.body)
			}
		})
	}
	
	w.darkproxy.saveRequestMeta = function(id) {
		$.ajax({
			method: 'POST',
			url: 's/request.json?id=' + id,
			contentType: 'application/json; charset=UTF-8',
			data: JSON.stringify(w.meta)
		})
	}
	
	w.darkproxy.saveRequestBody = function(id) {
		$.ajax({
			method: 'POST',
			url: 's/request.body?id=' + id,
			contentType: 'application/text; charset=UTF-8',
			data: w.body
		})
	}
	
	w.darkproxy.requestProceed = function(id) {
		$.getJSON('s/request/proceed?id=' + id);
	}
	
	w.darkproxy.loadResponseMeta = function(id) {
		$.ajax({ 
			url: 's/response.json',
			data: { 'id':  id },
			dataType: 'json',
			success: function(meta) {
				w.meta = meta;
				console.log('meta', w.meta)
			}
		})
	}
	
	w.darkproxy.loadResponseBody = function(id) {
		$.ajax({ 
			url: 's/response.body',
			data: { 'id':  id },
			dataType: 'text',
			success: function(body) {
				w.body = body;
				console.log('body', w.body)
			}
		})
	}
	
	w.darkproxy.saveResponseMeta = function(id) {
		$.ajax({
			method: 'POST',
			url: 's/response.json?id=' + id,
			contentType: 'application/json; charset=UTF-8',
			data: JSON.stringify(w.meta)
		})
	}
	
	w.darkproxy.saveResponseBody = function(id) {
		$.ajax({
			method: 'POST',
			url: 's/response.body?id=' + id,
			contentType: 'application/text; charset=UTF-8',
			data: w.body
		})
	}
	
	w.darkproxy.responseProceed = function(id) {
		$.getJSON('s/response/proceed?id=' + id);
	}
	
})(window, jQuery)