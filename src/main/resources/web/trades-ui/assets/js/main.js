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
	
	
	$('[data-toggle="tooltip"]').tooltip()
	
	var newproducts = $("#new-products");
	newproducts.owlCarousel({
		autoplay: true,
		nav: true,
		autoplayHoverPause: true,
		smartSpeed: 350,
		dots: false,
		margin: 30,
		loop: true,
		navText: ['<i class="lni-chevron-left"></i>', '<i class="lni-chevron-right"></i>'],
		responsiveClass: true,
		responsive: {
			0: {
				items: 1,
			},
			575: {
				items: 2,
			},
			991: {
				items: 3,
			}
		}
	});
	
	
	var categoriesslider = $("#categories-icon-slider");
	categoriesslider.owlCarousel({
		autoplay: true,
		nav: true,
		autoplayHoverPause: true,
		smartSpeed: 350,
		dots: false,
		margin: 30,
		loop: true,
		navText: ['<i class="lni-chevron-left"></i>', '<i class="lni-chevron-right"></i>'],
		responsiveClass: true,
		responsive: {
			0: {
				items: 1,
			},
			575: {
				items: 2,
			},
			991: {
				items: 5,
			}
		}
	});
	
})(jQuery);
