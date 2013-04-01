(function($) {

	var printValue = function(obj) {
		return '' + obj + '(' + typeof (obj) + ')';
	}
	
	$.fn.assertEquals = function(exp, obj) {
		if (exp === obj) {
			return;
		}
		throw 'expected: ' + printValue(exp) + ', but was: ' + printValue(obj);
	}

	$.fn.assertTrue = function(exp) {
		if (exp) {
			return;
		}
		throw 'expected true: ' + printValue(exp);
	}
	
	$.fn.assertFalse = function(exp) {
		if (!exp) {
			return;
		}
		throw 'expected false: ' + printValue(exp);
	}
	
})($);