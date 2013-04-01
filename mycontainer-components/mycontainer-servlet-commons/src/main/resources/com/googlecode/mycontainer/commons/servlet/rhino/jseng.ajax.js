(function($) {

	$.fn.ajax = function(opts) {
		var request = $.fn.ajax.client
				.createRequest(com.googlecode.mycontainer.commons.httpclient.RequestMethod.POST);
		console.info('ajax: ' + opts.url);
		request.setUri(opts.url);
		for ( var paramName in opts.data) {
			var paramValue = opts.data[paramName];
			request.addParameter(paramName, paramValue);
		}
		var response = request.invoke();
		try {
			var code = response.getCode();
			if (code < 200 || code > 299) {
				throw "http error: " + code;
			}
			var resp = String(response.getContentAsString());
			console.info('resp', resp);
			if (opts.type == 'json') {
				resp = JSON.parse(resp);
			}
			if (opts.type == 'script') {
				resp = eval('(' + resp + ')');
			}
			if (opts.success) {
				opts.success(resp);
			}
		} finally {
			response.close();
		}
	}

	$.fn.ajax.client = new com.googlecode.mycontainer.commons.httpclient.WebClient();

})(rhinoBox);