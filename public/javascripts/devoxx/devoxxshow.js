$(document).ready(function hello(){
    "use strict";

    var TEMPLATE_EVENT = $("#event").text();

    var reload;

    function templateThisEvent(event) {
        event = event || {
            uid:"",tags:[],location:"",description:"",begin:0,end:0,originalStream:"",title:""
        };

        function toDateDisplayed(event) {
            var begin = moment(event.begin);
            var end = moment(event.end);
            return begin.format("DD/MM/YYYY  HH:mm") + " - " + end.format("HH:mm");
        }

        $("#rows").append(TEMPLATE_EVENT
            .replace(/\$title/g,event.title)
            .replace(/\$date/g,toDateDisplayed(event))
            .replace(/\$location/g,event.location)
        );

    }

    function success(data) {
        data = data || [];

        function sortEvent(e1,e2) {
            return e1.begin - e2.begin;
        }

        $("#rows").empty();
        data.sort(sortEvent).forEach(templateThisEvent);

        reload = setTimeout(function () {
            load();
        },30*60*1000);
    }


    function load() {
        clearTimeout(reload);
        $.ajax({action:"devoxxshow",contentType:"application/ajax",success:success});
    }
    load();
});