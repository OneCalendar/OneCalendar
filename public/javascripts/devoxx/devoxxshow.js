$(document).ready(function hello(){
    "use strict";

    var TEMPLATE_EVENT = $("#event").text();

    var reload;

    moment.lang('fr', {
        months : "janvier_février_mars_avril_mai_juin_juillet_août_septembre_octobre_novembre_décembre".split("_"),
        monthsShort : "janv._févr._mars_avr._mai_juin_juil._août_sept._oct._nov._déc.".split("_"),
        weekdays : "dimanche_lundi_mardi_mercredi_jeudi_vendredi_samedi".split("_"),
        weekdaysShort : "dim._lun._mar._mer._jeu._ven._sam.".split("_"),
        weekdaysMin : "Di_Lu_Ma_Me_Je_Ve_Sa".split("_"),
        longDateFormat : {
            LT : "HH:mm",
            L : "DD/MM/YYYY",
            LL : "D MMMM YYYY",
            LLL : "D MMMM YYYY LT",
            LLLL : "dddd D MMMM YYYY LT"
        },
        calendar : {
            sameDay: "[Aujourd'hui à] LT",
            nextDay: '[Demain à] LT',
            nextWeek: 'dddd [à] LT',
            lastDay: '[Hier à] LT',
            lastWeek: 'dddd [dernier à] LT',
            sameElse: 'L'
        },
        relativeTime : {
            future : "dans %s",
            past : "il y a %s",
            s : "quelques secondes",
            m : "une minute",
            mm : "%d minutes",
            h : "une heure",
            hh : "%d heures",
            d : "un jour",
            dd : "%d jours",
            M : "un mois",
            MM : "%d mois",
            y : "une année",
            yy : "%d années"
        },
        ordinal : function (number) {
            return number + (number === 1 ? 'er' : 'ème');
        },
        week : {
            dow : 1, // Monday is the first day of the week.
            doy : 4  // The week that contains Jan 4th is the first week of the year.
        }
    });

    function templateThisEvent(event) {
        event = event || {
            uid:"",tags:[],location:"",description:"",begin:0,end:0,originalStream:"",title:""
        };

        function toDateDisplayed(event) {
            var now = moment(new Date());
            var begin = moment(event.begin);
            var end = moment(event.end);
            if (begin.isAfter(now)) {
                return " Début dans %s".replace("%s",begin.fromNow(true));
            } else {
                if (end.isAfter(now)) {
                    return "Fin %s".replace("%s",end.fromNow());
                } else {
                    return "Fini %s".replace("%s",end.fromNow());
                }
            }
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
        },5*60*1000);
    }


    function load() {
        clearTimeout(reload);
        $.ajax({action:"devoxxshow",contentType:"application/ajax",success:success});
    }
    load();
});