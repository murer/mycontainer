(function(box) {

	var old$ = null;
	if (typeof ($) !== 'undefined') {
		old$ = $;
	}
	var oldConsole = null;
	if (typeof (console) !== 'undefined') {
		oldConsole = console;
	}

	var RhinoBox = function() {
	}
	var rbox = new RhinoBox();
	rbox.fn = RhinoBox.prototype;
	rbox.fn.old$ = old$;
	rbox.fn.box = box;
	rbox.fn.oldConsole = oldConsole;
	console = {};

	rbox.fn.toArray = function(obj) {
		if (!obj) {
			return obj;
		}
		var ret = [];
		for ( var i = 0; i < obj.length; i++) {
			ret[i] = obj[i];
		}
		return ret;
	}

	console.error = function() {
		var array = rbox.toArray(arguments);
		rbox.box.logger.error(array.join(' '));
	}

	console.warn = function() {
		var array = rbox.toArray(arguments);
		rbox.box.logger.warn(array.join(' '));
	}

	console.debug = function() {
		var array = rbox.toArray(arguments);
		rbox.box.logger.debug(array.join(' '));
	}

	console.info = function() {
		var array = rbox.toArray(arguments);
		rbox.box.logger.info(array.join(' '));
	}

	console.log = function() {
		var array = toArray(arguments);
		console.info.apply(this, array);
		for (i in array) {
			var obj = array[i];
			if (obj.rhinoException || obj.javaException) {
				rbox.fn.box.logger.info("There is a exception", obj.rhinoException
						|| obj.javaException);
			}
		}
	}

	rbox.fn.extend = function(target) {
		if (arguments.length == 1) {
			return rbox.fn.extend(rbox.fn, target);
		}
		for ( var i = 1; i < arguments.length; i++) {
			var arg = arguments[i];
			for (k in arg) {
				target[k] = arg[k];
			}
		}
		return target;
	}

	rbox.fn.createClass = function(obj) {
		var ret = function() {
			this.initialize.apply(this, arguments);
		};
		rbox.fn.extend(ret.prototype, obj);
		return ret;
	}

	rbox.fn.createToString = function(obj, name) {
		var args = rbox.toArray(arguments);
		obj.prototype.toString = function() {
			var ret = '[' + name;
			for ( var i = 2; i < args.length; i++) {
				var arg = args[i];
				var value = this[arg];
				ret += ' ' + arg + '=' + value;
				if (i < args.length - 1) {
					ret += ', ';
				}
			}
			ret += ']';
			return ret;
		}
	}

	rbox.fn.load = function(name) {
		return rbox.fn.box.source(name);
	}

	$ = rhinoBox = rbox;
	
	rbox.fn.load('rhinobox/client/json2.js');
	rbox.fn.load('rhinobox/client/asserts.rhinobox.js');

	return rbox;
});