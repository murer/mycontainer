(function($) {

	var ret = null;
	$.ajax({
		url: 'echo/test.js',
		data: {
			m: '{ a:3, b:4 }'
		},
		type: 'script',
		success: function(resp) {
			ret = resp;
		}
	});
	$.assertEquals(3, ret.a);
	$.assertEquals(4, ret.b);
	
});
