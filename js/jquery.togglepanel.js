(function($) {
	
	$.fn.togglepanel = function() {
		$(this).children('h3').click(function() {
			$(this).next().toggle();
		});
	}
	
	$(window).ready(function() {
		$('.togglepanel').togglepanel();
	});
	
})(jQuery);