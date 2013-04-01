
(function() {
	$(window).ready(function() {
		$('input').click(function() {
			$('ul li:first').html('mytest');
		});
		
		$.ajax({
			url: 'test.js', 
			success: function() {
				print('test.js executed');
			},
			error: function() {
				print('test.js not executed');
			}
		});
	});
})();
