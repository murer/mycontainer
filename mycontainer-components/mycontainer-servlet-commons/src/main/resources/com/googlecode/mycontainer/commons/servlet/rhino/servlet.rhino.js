(function($) {

	$.fn.Request = function(r) {
		if(r) {
			this.parse(r);
		}
	}

	$.extend($.fn.Request.prototype, {
		parse : function(req) {
			this.method = '' + req.method;
			this.uri = '' + req.requestURI;
			this.parseParameters(req);
			this.parseHeaders(req);
			this.parseContent(req);
			this.request = req;
		},
		parseParameters : function(req) {
			this.params = {};
			this.param = {};
			var params = req.getParameterMap().entrySet().iterator();
			while (params.hasNext()) {
				var entry = params.next();
				entry = {
					key : '' + entry.key,
					value : entry.value
				};
				this.params[entry.key] = [];
				this.param[entry.key] = '' + req.getParameter(entry.key);
				for ( var i in entry.value) {
					this.params[entry.key].push('' + entry.value[i]);
				}
			}
		},
		parseHeaders : function(req) {
			var names = req.getHeaderNames();
			this.headers = {};
			this.header = {};
			while (names.hasMoreElements()) {
				var name = '' + names.nextElement();
				var headers = req.getHeaders(name);
				this.headers[name] = [];
				this.header[name] = '' + req.getHeader(name);
				while (headers.hasMoreElements()) {
					var value = headers.nextElement();
					this.headers[name].push('' + value);
				}
			}
		},
		parseContent : function(req) {
			var input = req.inputStream;
			this.content = com.googlecode.mycontainer.commons.io.IOUtil.readAll(input);
		}
	});

	$.fn.Response = function(r) {
		if (r) {
			$.extend(this, r);
			if (!this.headers) {
				this.headers = {};
			}
		}
	}
	$.extend($.fn.Response.prototype, {
		send : function(resp) {
			this.sendCode(resp);
			this.sendHacks(resp);
			this.sendHeaders(resp);
			this.sendContent(resp);
		},
		sendCode : function(resp) {
			if (this.code && this.codeMessage) {
				resp.setStatus(this.code, this.codeMessage);
			} else if (this.code) {
				resp.setStatus(this.code);
			}
		},
		sendHacks: function(resp) {
			if(this.contentType) {
				resp.setContentType(this.contentType);
			}
		},
		sendHeaders : function(resp) {
			for ( var key in this.headers) {
				var values = this.headers[key];
				for ( var i in values) {
					resp.addHeader(key, values[i]);
				}
			}
		},
		sendContent : function(resp) {
			if (!this.content) {
				return;
			}
			if (typeof (this.content) == 'string') {
				resp.writer['print(java.lang.String)'](this.content);
			} else {
				resp.outputStream.write(this.content);
			}
		}
	});

})(rhinoBox);