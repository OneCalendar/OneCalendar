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

  displayPreviewResult : (data) ->
     $( "#callbackNoResult" ).hide()

     $("#resultSize").html("<b>nombre d'évènement(s) trouvé(s):</b> #{data.size}")

     previewElement = $('#subscription .preview')
     events = data.eventList
     eventFirst = events[0].event
     eventSecond = events[1].event
     eventThird = events[2].event

     $( previewElement[0] ).html( "
                                <b>#{eventFirst.title}</b> <br/>
                                #{SUGGEST.formatIcalDate eventFirst.date} <br/>
                                #{eventFirst.location}
                                " )
     $( previewElement[1] ).html( "
                                <b>#{eventSecond.title}</b> <br/>
                                #{SUGGEST.formatIcalDate eventSecond.date} <br/>
                                #{eventSecond.location}
                                " )
     $( previewElement[2] ).html(
                                "<b>#{eventThird.title}</b> <br/>
                                #{ SUGGEST.formatIcalDate eventThird.date} <br/>
                                #{eventThird.location}
                                " )

  displayNoResult : (searchWord) ->
    $( '#subscription' ).hide()
    $( '#callbackNoResult' ).text( "Le mot clé '#{searchWord}' ne donne aucun résultat dans la base OneCalendar" )
    $( '#callbackNoResult' ).show()

  loadUrlDevoxxSection : ->
    applicationBaseUrl = $(location).attr('href').substr(0, $(location).attr('href').length - 1)
    applicationBaseUrl_withoutHttp = applicationBaseUrl.split("//")[1]

    googleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid="
    googleCalendarLinkSuffix = "%2Fevents%2FDEVOXX"

    $('#devoxx a.gcal').attr('href', googleCalendarLinkPrefix + applicationBaseUrl + googleCalendarLinkSuffix)
    $('#devoxx a.webcal').attr('href', "webcal://#{applicationBaseUrl_withoutHttp}/events/DEVOXX")

  retrievePreviewResults: ({url}) ->
    $("#temp").click ->
      userSearch = $('#suggest').val().toUpperCase()

      if userSearch != ""
        $.ajax(
          {
            type: 'GET'
            url: "#{url}/preview/#{userSearch}",
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

  formatIcalDate: (date) ->
    dateT = date .split "T"
    dateDay = dateT[0].split "-"
    dateHour = dateT[1].split ":"
    dateGmt = dateT[1].split("+")[1].split(":")[0]

    "#{dateDay[2]} #{dateDay[1]} #{dateDay[0]} - #{dateHour[0]}:#{dateHour[1]} - GMT+#{dateGmt}"