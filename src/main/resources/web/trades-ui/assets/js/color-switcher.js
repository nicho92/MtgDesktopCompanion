window.console=window.console||(function(){var c={};c.log=c.warn=c.debug=c.info=c.error=c.time=c.dir=c.profile=c.clear=c.exception=c.trace=c.assert=function(){};return c;})();jQuery(document).ready(function($){"use strict"
$("ul.colors .color1").click(function(){$("#colors").attr("href","assets/css/colors/pink.css");return false;});$("ul.colors .color2").click(function(){$("#colors").attr("href","assets/css/colors/green.css");return false;});$("ul.colors .color3").click(function(){$("#colors").attr("href","assets/css/colors/cyan.css");return false;});$("ul.colors .color4").click(function(){$("#colors").attr("href","assets/css/colors/purple.css");return false;});$("ul.colors .color5").click(function(){$("#colors").attr("href","assets/css/colors/blue.css");return false;});$("ul.colors .color6").click(function(){$("#colors").attr("href","assets/css/colors/yellow.css");return false;});$("#color-style-switcher .bottom a.settings").click(function(e){e.preventDefault();var div=$("#color-style-switcher");if(div.css("left")==="-145px"){$("#color-style-switcher").animate({left:"0px"});}else{$("#color-style-switcher").animate({left:"-145px"});}})
$("ul.colors li a").click(function(e){e.preventDefault();$(this).parent().parent().find("a").removeClass("active");$(this).addClass("active");})});jQuery('head').append('<link rel="stylesheet" id="colors" href="assets/css/colors/pink.css" type="text/css" />');jQuery('head').append('<link rel="stylesheet" href="assets/css/color-switcher.css" type="text/css" />');jQuery('body').append(''+
'<div id="color-style-switcher">'+
'<div>'+
'<h3>Color Palette</h3>'+
'<ul class="colors">'+
'<li><a class="color1 active" href="#"></a></li>'+
'<li><a class="color2" href="#"></a></li>'+
'<li><a class="color3" href="#"></a></li>'+
'<li><a class="color4" href="#"></a></li>'+
'<li><a class="color5" href="#"></a></li>'+
'<li><a class="color6" href="#"></a></li>'+
'</ul>'+
'</div>'+
'<div class="bottom"> <a href="#" class="settings"><i class="lni-cog"></i></a> </div>'+
'</div>'+
'');