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

  displaySubscription : ->
    $('#temp').click ->
      googleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid="
      googleCalendarLinkSuffix = "%2Fevents%2F#{$('#suggest').val()}"

      userSearch = $('#suggest').val()

      $('#subscription a.ical').attr('href', "/events/#{userSearch}")
      $('#subscription a.gcal').attr('href', googleCalendarLinkPrefix + googleCalendarLinkSuffix)
      $('#subscription a.webcal').attr('href', "webcal://...../events/#{userSearch}")

      $('#subscription').show()