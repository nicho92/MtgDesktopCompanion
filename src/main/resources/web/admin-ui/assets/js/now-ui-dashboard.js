/*!

 =========================================================
 * site.product_name - vsite.current_version
 =========================================================

 * Product Page: site.link_tim
 * Copyright site.year Creative Tim (http://www.creative-tim.com)

 * Designed by www.invisionapp.com Coded by www.creative-tim.com

 =========================================================

 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 */

/*!

 =========================================================
 * Now UI Dashboard - v1.5.0
 =========================================================

 * Product Page: https://www.creative-tim.com/product/now-ui-dashboard
 * Copyright 2019 Creative Tim (http://www.creative-tim.com)

 * Designed by www.invisionapp.com Coded by www.creative-tim.com

 =========================================================

 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 */

(function() {
  isWindows = navigator.platform.indexOf('Win') > -1 ? true : false;

  if (isWindows) {
    // if we are on windows OS we activate the perfectScrollbar function
    var ps = new PerfectScrollbar('.sidebar-wrapper');
    var ps2 = new PerfectScrollbar('.main-panel');

    $('html').addClass('perfect-scrollbar-on');
  } else {
    $('html').addClass('perfect-scrollbar-off');
  }
})();

transparent = true;
transparentDemo = true;
fixedTop = false;

navbar_initialized = false;
backgroundOrange = false;
sidebar_mini_active = false;
toggle_initialized = false;

var is_iPad = navigator.userAgent.match(/iPad/i) != null;
var scrollElement = navigator.platform.indexOf('Win') > -1 ? $(".main-panel") : $(window);

seq = 0, delays = 80, durations = 500;
seq2 = 0, delays2 = 80, durations2 = 500;

$(document).ready(function() {

  if ($('.full-screen-map').length == 0 && $('.bd-docs').length == 0) {
    // On click navbar-collapse the menu will be white not transparent
    $('.collapse').on('show.bs.collapse', function() {
      $(this).closest('.navbar').removeClass('navbar-transparent').addClass('bg-white');
    }).on('hide.bs.collapse', function() {
      $(this).closest('.navbar').addClass('navbar-transparent').removeClass('bg-white');
    });
  }

  $navbar = $('.navbar[color-on-scroll]');
  scroll_distance = $navbar.attr('color-on-scroll') || 500;

  // Check if we have the class "navbar-color-on-scroll" then add the function to remove the class "navbar-transparent" so it will transform to a plain color.
  if ($('.navbar[color-on-scroll]').length != 0) {
    nowuiDashboard.checkScrollForTransparentNavbar();
    $(window).on('scroll', nowuiDashboard.checkScrollForTransparentNavbar)
  }

  $('.form-control').on("focus", function() {
    $(this).parent('.input-group').addClass("input-group-focus");
  }).on("blur", function() {
    $(this).parent(".input-group").removeClass("input-group-focus");
  });

  // Activate bootstrapSwitch
  $('.bootstrap-switch').each(function() {
    $this = $(this);
    data_on_label = $this.data('on-label') || '';
    data_off_label = $this.data('off-label') || '';

    $this.bootstrapSwitch({
      onText: data_on_label,
      offText: data_off_label
    });
  });
});

$(document).on('click', '.navbar-toggle', function() {
  $toggle = $(this);

  if (nowuiDashboard.misc.navbar_menu_visible == 1) {
    $('html').removeClass('nav-open');
    nowuiDashboard.misc.navbar_menu_visible = 0;
    setTimeout(function() {
      $toggle.removeClass('toggled');
      $('#bodyClick').remove();
    }, 550);

  } else {
    setTimeout(function() {
      $toggle.addClass('toggled');
    }, 580);

    div = '<div id="bodyClick"></div>';
    $(div).appendTo('body').click(function() {
      $('html').removeClass('nav-open');
      nowuiDashboard.misc.navbar_menu_visible = 0;
      setTimeout(function() {
        $toggle.removeClass('toggled');
        $('#bodyClick').remove();
      }, 550);
    });

    $('html').addClass('nav-open');
    nowuiDashboard.misc.navbar_menu_visible = 1;
  }
});

$(window).resize(function() {
  // reset the seq for charts drawing animations
  seq = seq2 = 0;

  if ($('.full-screen-map').length == 0 && $('.bd-docs').length == 0) {

    $navbar = $('.navbar');
    isExpanded = $('.navbar').find('[data-toggle="collapse"]').attr("aria-expanded");
    if ($navbar.hasClass('bg-white') && $(window).width() > 991) {
      if (scrollElement.scrollTop() == 0) {
        $navbar.removeClass('bg-white').addClass('navbar-transparent');
      }
    } else if ($navbar.hasClass('navbar-transparent') && $(window).width() < 991 && isExpanded != "false") {
      $navbar.addClass('bg-white').removeClass('navbar-transparent');
    }
  }
  if (is_iPad) {
    $('body').removeClass('sidebar-mini');
  }
});

nowuiDashboard = {
  misc: {
    navbar_menu_visible: 0
  },

  showNotification: function(from, align) {
    color = 'primary';

    $.notify({
      icon: "now-ui-icons ui-1_bell-53",
      message: "Welcome to <b>Now Ui Dashboard</b> - a beautiful freebie for every web developer."

    }, {
      type: color,
      timer: 8000,
      placement: {
        from: from,
        align: align
      }
    });
  }


};

function hexToRGB(hex, alpha) {
  var r = parseInt(hex.slice(1, 3), 16),
    g = parseInt(hex.slice(3, 5), 16),
    b = parseInt(hex.slice(5, 7), 16);

  if (alpha) {
    return "rgba(" + r + ", " + g + ", " + b + ", " + alpha + ")";
  } else {
    return "rgb(" + r + ", " + g + ", " + b + ")";
  }
}