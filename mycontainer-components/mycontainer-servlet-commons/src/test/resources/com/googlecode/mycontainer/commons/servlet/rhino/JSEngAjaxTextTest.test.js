(function($) {

	var ret = null;
	$.ajax({
		url: 'echo/test.txt',
		data: {
			m: 'my message'
		},
		success: function(resp) {
			ret = resp;
		}
	});
	$.assertEquals("my message", ret);
	
});
