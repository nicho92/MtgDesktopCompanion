(function ($) {
	
	$(window).on("load", function () {
		$("#preloader").fadeOut("slow");
	});

	
	$(".back-to-top").on("click", function () {
		$("html, body").animate({
			scrollTop: 0
		}, "slow");
		return false;
	});
	
	
	$('.mobile-menu').slicknav({
		prependTo: '.navbar-header',
		parentTag: 'liner',
		allowParentLinks: true,
		duplicate: true,
		label: '',
		closedSymbol: '<i class="lni-chevron-right"></i>',
		openedSymbol: '<i class="lni-chevron-down"></i>',
	});
	
	$(window).on('scroll', function () {
		var scroll = $(window).scrollTop();
		if (scroll >= 10) {
			$(".scrolling-navbar").addClass("top-nav-collapse");
		} else {
			$(".scrolling-navbar").removeClass("top-nav-collapse");
		}
	});
	
	
	$('[data-toggle="tooltip"]').tooltip();
	
	
	
})(jQuery);
