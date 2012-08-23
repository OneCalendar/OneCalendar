(function(j,k,l){var h=k.audio&&k.video,r=!1;if(h)j=document.createElement("video"),k.videoBuffered="buffered"in j,r="loop"in j,l.capturingEvents("play,playing,waiting,paused,ended,durationchange,loadedmetadata,canplay,volumechange".split(",")),k.videoBuffered||(l.addPolyfill("mediaelement-native-fix",{f:"mediaelement",test:k.videoBuffered,d:["dom-support"]}),l.reTest("mediaelement-native-fix"));jQuery.webshims.register("mediaelement-core",function(c,e,o,p,j){var f=e.mediaelement,q=e.cfg.mediaelement,
l=function(a,b){var a=c(a),g={src:a.attr("src")||"",elem:a,srcProp:a.prop("src")};if(!g.src)return g;var d=a.attr("type");if(d)g.type=d,g.container=c.trim(d.split(";")[0]);else if(b||(b=a[0].nodeName.toLowerCase(),"source"==b&&(b=(a.closest("video, audio")[0]||{nodeName:"video"}).nodeName.toLowerCase())),d=f.getTypeForSrc(g.src,b))g.type=d,g.container=d;if(d=a.attr("media"))g.media=d;return g},i=swfobject.hasFlashPlayerVersion("9.0.115"),m=!i&&"postMessage"in o&&h,y=function(){e.ready("mediaelement-swf",
function(){if(!f.createSWF)e.modules["mediaelement-swf"].test=c.noop,e.reTest(["mediaelement-swf"],h)})},v=function(){var a;return function(){!a&&m&&(a=!0,e.loader.loadScript("https://www.youtube.com/player_api"),c(function(){e.polyfill("mediaelement-yt")}))}}(),w=function(){i?y():v();c(function(){e.loader.loadList(["track-ui"])})};e.addPolyfill("mediaelement-yt",{test:!m,d:["dom-support"]});f.mimeTypes={audio:{"audio/ogg":["ogg","oga","ogm"],'audio/ogg;codecs="opus"':"opus","audio/mpeg":["mp2","mp3",
"mpga","mpega"],"audio/mp4":"mp4,mpg4,m4r,m4a,m4p,m4b,aac".split(","),"audio/wav":["wav"],"audio/3gpp":["3gp","3gpp"],"audio/webm":["webm"],"audio/fla":["flv","f4a","fla"],"application/x-mpegURL":["m3u8","m3u"]},video:{"video/ogg":["ogg","ogv","ogm"],"video/mpeg":["mpg","mpeg","mpe"],"video/mp4":["mp4","mpg4","m4v"],"video/quicktime":["mov","qt"],"video/x-msvideo":["avi"],"video/x-ms-asf":["asf","asx"],"video/flv":["flv","f4v"],"video/3gpp":["3gp","3gpp"],"video/webm":["webm"],"application/x-mpegURL":["m3u8",
"m3u"],"video/MP2T":["ts"]}};f.mimeTypes.source=c.extend({},f.mimeTypes.audio,f.mimeTypes.video);f.getTypeForSrc=function(a,b){if(-1!=a.indexOf("youtube.com/watch?")||-1!=a.indexOf("youtube.com/v/"))return"video/youtube";var a=a.split("?")[0].split("."),a=a[a.length-1],g;c.each(f.mimeTypes[b],function(b,c){if(-1!==c.indexOf(a))return g=b,!1});return g};f.srces=function(a,b){a=c(a);if(b)a.removeAttr("src").removeAttr("type").find("source").remove(),c.isArray(b)||(b=[b]),b.forEach(function(b){var c=
p.createElement("source");"string"==typeof b&&(b={src:b});c.setAttribute("src",b.src);b.type&&c.setAttribute("type",b.type);b.media&&c.setAttribute("media",b.media);a.append(c)});else{var b=[],g=a[0].nodeName.toLowerCase(),d=l(a,g);d.src?b.push(d):c("source",a).each(function(){d=l(this,g);d.src&&b.push(d)});return b}};c.fn.loadMediaSrc=function(a,b){return this.each(function(){b!==j&&(c(this).removeAttr("poster"),b&&c.attr(this,"poster",b));f.srces(this,a);c(this).mediaLoad()})};f.swfMimeTypes="video/3gpp,video/x-msvideo,video/quicktime,video/x-m4v,video/mp4,video/m4p,video/x-flv,video/flv,audio/mpeg,audio/aac,audio/mp4,audio/x-m4a,audio/m4a,audio/mp3,audio/x-fla,audio/fla,youtube/flv,jwplayer/jwplayer,video/youtube".split(",");
f.canThirdPlaySrces=function(a,b){var g="";if(i||m)a=c(a),b=b||f.srces(a),c.each(b,function(a,b){if(b.container&&b.src&&(i&&-1!=f.swfMimeTypes.indexOf(b.container)||m&&"video/youtube"==b.container))return g=b,!1});return g};var n={};f.canNativePlaySrces=function(a,b){var g="";if(h){var a=c(a),d=(a[0].nodeName||"").toLowerCase();if(!n[d])return g;b=b||f.srces(a);c.each(b,function(b,c){if(c.type&&n[d].prop._supvalue.call(a[0],c.type))return g=c,!1})}return g};if(h&&i&&!q.preferFlash){var s=function(a){var b=
a.target.parentNode;!q.preferFlash&&(c(a.target).is("audio, video")||b&&c("source:last",b)[0]==a.target)&&e.ready("mediaelement-swf",function(){setTimeout(function(){if(!c(a.target).closest("audio, video").is(".nonnative-api-active"))q.preferFlash=!0,p.removeEventListener("error",s,!0),c("audio, video").mediaLoad()},20)})};p.addEventListener("error",s,!0);c.webshims.ready("DOM",function(){c("audio, video").each(function(){this.error&&s({target:this})})})}f.setError=function(a,b){b||(b="can't play sources");
c(a).pause().data("mediaerror",b);e.warn("mediaelementError: "+b);setTimeout(function(){c(a).data("mediaerror")&&c(a).trigger("mediaerror")},1)};var x=function(){var a;return function(b,c,d){e.ready(i?"mediaelement-swf":"mediaelement-yt",function(){f.createSWF?f.createSWF(b,c,d):a||(a=!0,w(),x(b,c,d))});!a&&m&&!f.createSWF&&v()}}(),t=function(a,b,c,d,e){c||!1!==c&&b&&"third"==b.isActive?(c=f.canThirdPlaySrces(a,d))?x(a,c,b):e?f.setError(a,!1):t(a,b,!1,d,!0):(c=f.canNativePlaySrces(a,d))?b&&"third"==
b.isActive&&f.setActive(a,"html5",b):e?(f.setError(a,!1),b&&"third"==b.isActive&&f.setActive(a,"html5",b)):t(a,b,!0,d,!0)},z=/^(?:embed|object|datalist)$/i,u=function(a,b){var g=e.data(a,"mediaelementBase")||e.data(a,"mediaelementBase",{}),d=f.srces(a),h=a.parentNode;clearTimeout(g.loadTimer);c.data(a,"mediaerror",!1);if(d.length&&h&&!(1!=h.nodeType||z.test(h.nodeName||"")))b=b||e.data(a,"mediaelement"),t(a,b,q.preferFlash||j,d)};c(p).bind("ended",function(a){var b=e.data(a.target,"mediaelement");
(!r||b&&"html5"!=b.isActive||c.prop(a.target,"loop"))&&setTimeout(function(){!c.prop(a.target,"paused")&&c.prop(a.target,"loop")&&c(a.target).prop("currentTime",0).play()},1)});r||e.defineNodeNamesBooleanProperty(["audio","video"],"loop");["audio","video"].forEach(function(a){var b=e.defineNodeNameProperty(a,"load",{prop:{value:function(){var a=e.data(this,"mediaelement");u(this,a);h&&(!a||"html5"==a.isActive)&&b.prop._supvalue&&b.prop._supvalue.apply(this,arguments)}}});n[a]=e.defineNodeNameProperty(a,
"canPlayType",{prop:{value:function(b){var d="";h&&n[a].prop._supvalue&&(d=n[a].prop._supvalue.call(this,b),"no"==d&&(d=""));!d&&i&&(b=c.trim((b||"").split(";")[0]),-1!=f.swfMimeTypes.indexOf(b)&&(d="maybe"));return d}}})});e.onNodeNamesPropertyModify(["audio","video"],["src","poster"],{set:function(){var a=this,b=e.data(a,"mediaelementBase")||e.data(a,"mediaelementBase",{});clearTimeout(b.loadTimer);b.loadTimer=setTimeout(function(){u(a);a=null},9)}});o=function(){e.addReady(function(a,b){c("video, audio",
a).add(b.filter("video, audio")).each(function(){c.browser.msie&&8<e.browserVersion&&c.prop(this,"paused")&&!c.prop(this,"readyState")&&c(this).is('audio[preload="none"][controls]:not([autoplay])')?c(this).prop("preload","metadata").mediaLoad():u(this);if(h){var a,b,f=this,i=function(){var a=c.prop(f,"buffered");if(a){for(var b="",d=0,e=a.length;d<e;d++)b+=a.end(d);return b}},j=function(){var a=i();a!=b&&(b=a,c(f).triggerHandler("progress"))};c(this).bind("play loadstart progress",function(c){"progress"==
c.type&&(b=i());clearTimeout(a);a=setTimeout(j,999)}).bind("emptied stalled mediaerror abort suspend",function(c){"emptied"==c.type&&(b=!1);clearTimeout(a)})}})})};k.track?e.defineProperty(TextTrack.prototype,"shimActiveCues",{get:function(){return this._shimActiveCues||this.activeCues}}):c(function(){e.loader.loadList(["track-ui"])});h?(e.isReady("mediaelement-core",!0),o(),e.ready("WINDOWLOAD mediaelement",w)):e.ready("mediaelement-swf",o)})})(jQuery,Modernizr,jQuery.webshims);
