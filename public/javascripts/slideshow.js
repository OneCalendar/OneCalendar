$(document).ready(function hello(){
    "use strict";

    var TEMPLATE_EVENT = $("#event").text();
    function templateThisEvent(event) {
        event = event || {
            uid:"",tags:[],location:"",description:"",begin:0,end:0,originalStream:"",title:""
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

        $(".row").append(TEMPLATE_EVENT
            .replace(/\$title/g,event.title)
            .replace(/\$date/g,toDateDisplayed(event))
            .replace(/\$description/g,event.description)
            .replace(/\$location/g,event.location)
        );

    }

    function launchAnim() {
        var current = $(".event.highlight");
        var delay = 5000;
        var next = current.next();
        if (current.length === 0 || next.length === 0) {
            current = $(".row .event").first();
            next = current;
        }
        current.removeClass("highlight");
        $("html,body").animate({
            scrollTop:$(next).offset().top
        });
        $(next).addClass("highlight");
        setTimeout(launchAnim,delay);
    }

    function success(data) {
        data = data || [];

        function sortEvent(e1,e2) {
            return e1.begin - e2.begin;
        }

        function filterEvent(event) {
            return true;
        }

        data.sort(sortEvent).filter(filterEvent).forEach(templateThisEvent);

        setTimeout(launchAnim,0);
    }

    $.ajax({action:"slideshow",contentType:"application/ajax",success:success});

});