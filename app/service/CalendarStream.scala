package service

import models.Event
import models.builder.EventBuilder
import org.joda.time.DateTime

/**
 * User: ugo
 * Date: 12/03/12
 */

class CalendarStream {
    def buildCalendar: String = {
        "BEGIN:VCALENDAR\n" +
            "PRODID:-//Google Inc//Google Calendar 70.9054//EN\n" +
            "VERSION:2.0\n" +
            "CALSCALE:GREGORIAN\n" +
            "METHOD:PUBLISH\n" +
            "X-WR-CALNAME:Devoxx France 2012\n" +
            "X-WR-TIMEZONE:Europe/Paris\n" +
            "X-WR-CALDESC:La conférence se déroule du mercredi 18 au vendredi 20 avril 2\n" +
            " 012 au centre de conférence de l'hôtel Marriott Paris Rive-Gauche. Pour la \n" +
            " première année\\, l'équipe d'organisation vise 700 personnes. Un espace d'ex\n" +
            " position de 660 m2 est prévu pour les partenaires de l'événement. 4 salles \n" +
            " de conférence de 270 places permettent de suivre des présentations d'une he\n" +
            " ure sur 4 thèmes majeurs.\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART:20120419T113000Z\n" +
            "DTEND:20120419T114500Z\n" +
            "DTSTAMP:20120309T130626Z\n" +
            "UID:nmncj8q7osm6a1odhmiglkij04@google.com\n" +
            "CREATED:20120309T130618Z\n" +
            "DESCRIPTION:Speaker(s) : Olivier Croisier\\n" +
            "\\n" +
            "Le mécanisme de sérialisation \n" +
            " de Java est flexible et puissant\\, mais ses performances peuvent être consi\n" +
            " dérablement améliorées en appliquant certaines astuces.\\n" +
            "En instrumentant v\n" +
            " os classes au chargement\\, la librairie Seren vous permet de bénéficier imm\n" +
            " édiatement de ces optimisations\\, de manière totalement transparente pour v\n" +
            " otre code.\\n" +
            "Aucun compilateur n'a été blessé pendant le développement de ce\n" +
            " tte librairie (ou presque).\n" +
            "LAST-MODIFIED:20120309T130618Z\n" +
            "LOCATION:Salle E. Fitzgerald & L. Armstrong\n" +
            "SEQUENCE:0\n" +
            "STATUS:CONFIRMED\n" +
            "SUMMARY:Seren\\, la serialisation sous steroides !\n" +
            "TRANSP:OPAQUE\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR"
    }
    
    def search(tags: List[String]) : List[ Event ]  = {
        if (tags.contains("java")) {
            stubEvents
        } else {
            null
        }
    }

    def stubEvents: List[ Event ] = {
        List( stub )
    }
    
    val stub: Event = new EventBuilder()
        .title( "Seren, la serialisation sous steroides !" )
        .begin( new DateTime)
        .end( new DateTime().plusHours(2) )
        .location( "Salle E. Fitzgerald & L. Armstrong" )
        .description( "Speaker(s) : Olivier Croisier\n" +
        "\n" +
        "Le mécanisme de sérialisation" +
        " de Java est flexible et puissant, mais ses performances peuvent être consi" +
        " dérablement améliorées en appliquant certaines astuces.\n" +
        "En instrumentant v" +
        " os classes au chargement, la librairie Seren vous permet de bénéficier imm" +
        " édiatement de ces optimisations, de manière totalement transparente pour votre code.\n" +
        "Aucun compilateur n'a été blessé pendant le développement de ce" +
        " tte librairie (ou presque)." )
        .toEvent
}