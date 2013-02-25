$(document).ready(function hello(){
    "use strict";

    var TEMPLATE_EVENT = $("#event").text();
    var timeout = {
        lastTimeout:null,lastId:""
    };

    var reload;

    function templateThisEvent(event) {
        event = event || {
            uid:"",tags:[],location:"",begin:0,end:0,originalStream:"",title:""
        };

        function toDateDisplayed(event) {
            var begin = moment(event.begin);
            var end = moment(event.end);
            if (end.diff(begin,"days") === 0) {
                return begin.format("DD/MM/YYYY HH:mm") + " - " + end.format("HH:mm");
            } else {
                return begin.format("DD/MM/YYYY HH:mm") + " - " + end.format("DD/MM/YYYY HH:mm");
            }
        }

        $("#rows").append(TEMPLATE_EVENT
            .replace(/\$title/g,event.title)
            .replace(/\$date/g,toDateDisplayed(event))
            .replace(/\$location/g,event.location)
        );

    }
    var DELAY = 5000;

    function launchAnim() {
        var current = $(".event.highlight");

        var next = current.next();
        if (current.length === 0 || next.length === 0) {
            current = $("#rows").find(".event").first();
            next = current;
        }
        current.removeClass("highlight");
        $("html,body").animate({
            scrollTop:$(next).offset().top
        });
        $(next).addClass("highlight");
        timeout.lastTimeout = setTimeout(launchAnim,DELAY);
    }

    function success(data) {
        data = data || [];

        function sortEvent(e1,e2) {
            return e1.begin - e2.begin;
        }

        $("#rows").empty();
        data.sort(sortEvent).forEach(templateThisEvent);

        launchAnim();

        reload = setTimeout(function () {
            load();
        },30*60*1000);
    }


    function load() {
        clearTimeout(reload);
        clearTimeout(timeout.lastTimeout);
        $.ajax({action:"devoxxshow",contentType:"application/ajax",success:success});
    }
    load();
});