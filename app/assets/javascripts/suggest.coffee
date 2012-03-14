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

      $('#subscription a').remove()
      googleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid="
      googleCalendarLinkSuffix = "%2Fevents%2F#{$('#suggest').val()}"

      userSearch = $('#suggest').val()
      $('#subscription').append "<a href='/events/#{userSearch}'>a</a>
      <a href='#{googleCalendarLinkPrefix + googleCalendarLinkSuffix}'>b</a>
      <a href='webcal://...../events/a'>c</a>"