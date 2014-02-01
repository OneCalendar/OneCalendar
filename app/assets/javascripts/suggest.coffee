##
## This file is part of OneCalendar.
##
## OneCalendar is free software: you can redistribute it and/or modify
## it under the terms of the GNU General Public License as published by
## the Free Software Foundation, either version 3 of the License, or
## (at your option) any later version.
##
## OneCalendar is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with OneCalendar.  If not, see <http://www.gnu.org/licenses/>.
##

@SUGGEST =

  suggest : ->
    $('#suggest').on 'keyup', ->
      ulAddDomElement = "<ul></ul>"
      $( @ ).after( ulAddDomElement ) if( !$( '#suggest + ul' ).length )

      liAddDomElement = "<li>#{$(@).val()}</li>"
      $( '#suggest + ul' ).append( liAddDomElement )

  deleteSuggest : ->
    $('#suggest').on "blur", ->
      $( '#suggest + ul' ).remove()

  displaySubscription : (userSearch) ->
      if userSearch != ""
        googleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid="
        googleCalendarLinkSuffix = "%2Fevents%2F#{userSearch}"

        applicationBaseUrl = $(location).attr('href').substr(0, $(location).attr('href').length - 1)
        applicationBaseUrl_withoutHttp = applicationBaseUrl.split("//")[1]

        $('#subscription a.ical').attr('href', "/events/#{userSearch}")
        $('#subscription a.gcal').attr('href', googleCalendarLinkPrefix + applicationBaseUrl + googleCalendarLinkSuffix)
        $('#subscription a.webcal').attr('href', "webcal://#{applicationBaseUrl_withoutHttp}/events/#{userSearch}")

        $('#subscription').show()
        $('#cloudtags').hide()
        $(".legend").hide()
        $('#devoxx').hide()

  displayPreviewResult : (data) ->
     $('#previewEvents').empty()
     $( "#callbackNoResult" ).hide()

     console.log data

     if data.size > 4
       $("#resultSize").html("#{data.size - 4} autres évènements trouvés")
     else
       $("#resultSize").html("&nbsp;")

     previewElement = $('#previewEvents')
     events = data.eventList

     for i in [0..4]
       if events[i] != undefined
         previewElement.append( "
           <li>
             <ul class='pricing-table'>
               <li class='title'>#{SUGGEST.formatIcalDate events[i].event.date}</li>
               <li class='price'>#{events[i].event.title}</li>
               <li class='description'>#{events[i].event.location}</li>
               <li class='cta-button'>
                 <div class='row'>
                   <ul id='subscription' class='button-group'>
                     <div class='large-4 columns'>
                       <li><a href='#' class='left ical'><img alt='flux texte' title='flux texte' src='/assets/images/text.png' class='large-centered'/></a></li>
                     </div>
                     <div class='large-4 columns'>
                       <li><a href='#' class='centered gcal'><img alt='flux google agenda' title='flux google agenda'
                                 src='http://www.google.com/calendar/images/ext/gc_button6_fr.gif' class='large-centered'/></a></li>
                     </div>
                     <div class='large-4 columns'>
                       <li><a href='#' class='right webcal'><img alt='flux ical apple' title='flux ical apple'
                               src='/assets/images/iCal_Icon.jpeg' class='large-centered'/></a></li>
                     </div>
                   </ul>
                 </div>
               </li>
             </ul>
           </li>" )
       else
         previewElement.append( "
           <span class='title'></span>
           <span class='date'></span>
           <span class='location'></span>" )

  displayNoResult : (searchWord) ->
    $('#previewEvents').empty()
    $( '#subscription' ).hide()
    $( '#resultSize' ).hide()
    $( '#callbackNoResult' ).text( "Le mot clé '#{searchWord}' ne donne aucun résultat dans la base OneCalendar" )
    $( '#callbackNoResult' ).show()

  loadUrlDevoxxSection : ->
    applicationBaseUrl = $(location).attr('href').substr(0, $(location).attr('href').length - "agilefrance".length - 1)
    applicationBaseUrl_withoutHttp = applicationBaseUrl.split("//")[1]

    googleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid="
    googleCalendarLinkSuffix = "%2Fevents%2FAGILEFRANCE"

    $('#devoxx a.gcal').attr('href', googleCalendarLinkPrefix + applicationBaseUrl + googleCalendarLinkSuffix)
    $('#devoxx a.webcal').attr('href', "webcal://#{applicationBaseUrl_withoutHttp}/events/AGILEFRANCE")

  retrievePreviewResults: ->
    $("#events").submit ->
      userSearch = $('#suggest').val().toUpperCase()

      if userSearch != ""
        $.ajax(
          {
            type: 'GET'
            url: "/event/tags/#{userSearch}",
            dataType: "json"
            success: (data) ->
              SUGGEST.displayPreviewResult data
              SUGGEST.displaySubscription userSearch
            error: (data) ->
              SUGGEST.displayNoResult $('#suggest').val()
          }
        )
      else
        $( "#subscription" ).hide()
        $( "#callbackNoResult" ).hide()

      false

  formatIcalDate: (date) ->
    dateT = date .split "T"
    dateDay = dateT[0].split "-"
    dateHour = dateT[1].split ":"
    dateGmt = dateT[1].split("+")[1].split(":")[0]

    "#{dateDay[2]} #{dateDay[1]} #{dateDay[0]} - #{dateHour[0]}:#{dateHour[1]}"

  retrieveEventNumber: ->
    $.ajax(
      {
        type: 'GET',
        url: "/event/count",
        dataType: "json",
        success: (data) ->
          SUGGEST.displayEventNumber data
        error: (data) ->
          SUGGEST.displayEventNumber {'eventNumber':'N/A'}
      }
    )

  displayEventNumber: (data) ->
    $("#eventNumber").text(data.eventNumber)

