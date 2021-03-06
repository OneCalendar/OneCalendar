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

describe 'google suggest like', ->

  beforeEach ->
    setFixtures '<input type="text" id="suggest" value="a" />'

  it '1. should call function when key press in my marvelous input', ->
    $('#suggest').on 'keyup', ->

    expect($('#suggest')).toHandle 'keyup'

  it '2. should display ul element when user press "a" key', ->
    SUGGEST.suggest()
    $('#suggest').keyup()

    expect($('#suggest + ul > li')).toHaveText("a")

  it '3. should display only one ul element when user press "a" key twice', ->
    SUGGEST.suggest()
    $('#suggest').keyup()
    $('#suggest').keyup()

    expect( '<li>a</li><li>a</li>' ).toEqual( $('#suggest + ul').html() )

  it '4. should delete ul element when marvelous input loose focus', ->
    setFixtures '''
      <input type="text" id="suggest" value="a" />
      <ul><li>a</li></ul>
    '''

    SUGGEST.deleteSuggest()
    $('#suggest').blur()

    expect( $('#suggest + ul').length ).toEqual( 0 )

  it "5. should display links when user write search word and double encoding on query to have simple encoding on onecal server", ->
    setFixtures '''
      <input type="text" id="suggest" value="a" />
      <div id="temp"></div>
      <div id="subscription" style="display:none;">
        <a class="ical"></a>
        <a class="gcal"></a>
        <a class="webcal"></a>
      </div>
    '''

    SUGGEST.displaySubscription 'A B'

    expectedGoogleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid="
    expectedGoogleCalendarLinkSuffix = "%2Fevents%2FA%2520B"
    expectedWebcalLinkPrefix = "webcal://"
    expectedWebcalLinkSuffix = "/events/A%20B"

    expect( $('#subscription').css('display') ).toEqual('block')
    expect( $('#subscription a.ical') ).toHaveAttr('href', '/events/A B')
    expect( $('#subscription a.gcal').attr('href') ).toContain(expectedGoogleCalendarLinkPrefix)
    expect( $('#subscription a.gcal').attr('href') ).toContain( expectedGoogleCalendarLinkSuffix )
    expect( $('#subscription a.webcal').attr('href') ).toContain( expectedWebcalLinkPrefix )
    expect( $('#subscription a.webcal').attr('href') ).toContain( expectedWebcalLinkSuffix )

  it "6. if user don't write search should not display links", ->
    setFixtures '''
          <input type="text" id="suggest" value="" />
          <div id="temp"></div>
          <div id="subscription" style="display:none;">
            <a class="ical"></a>
            <a class="gcal"></a>
            <a class="webcal"></a>
          </div>
        '''

    SUGGEST.displaySubscription ''

    expect( $('#subscription').css('display') ).toEqual('none')

  it "7. should call rest controller to retrieve preview result when user click on search", ->
    setFixtures '''
            <form method="get" id="events" >
              <select id="suggest" class="suggest-search" tabindex="-1" multiple="">
                <option value="a" selected="selected">a</option>
              </select>
            </form>
          '''
    urlServer = 'http://serveur'
    callbackData = {"key":"value"}

    SUGGEST.retrievePreviewResults url: urlServer

    spyOn(SUGGEST, 'displaySubscription').andCallThrough
    spyOn(SUGGEST, 'displayPreviewResult').andCallThrough

    spyOn($, 'ajax').and.callFake (params) ->
      params.success -> callbackData

    $("#events").submit()

    expect($.ajax).toHaveBeenCalled()
    expect(SUGGEST.displayPreviewResult).toHaveBeenCalled()
    expect(SUGGEST.displaySubscription).toHaveBeenCalledWith('A')

  it "8. should call displayNoResult method when callback is error", ->
    setFixtures '''
            <form method="get" id="events" >
              <select id="suggest" class="suggest-search" tabindex="-1" multiple="">
                <option value="a" selected="selected">a</option>
              </select>
            </form>
          '''

    urlServer = 'http://serveur'

    SUGGEST.retrievePreviewResults url: urlServer

    spyOn(SUGGEST, 'displayNoResult').andCallThrough
    spyOn($, 'ajax').and.callFake (params) ->
      params.error -> ""

    $("#events").submit()

    expect($.ajax).toHaveBeenCalled()
    expect(SUGGEST.displayNoResult).toHaveBeenCalledWith('A')

  it "9. should display preview", ->
    setFixtures '''
      <div class="row">
        <div class="large-12 columns">
          <hr />
          <h3>Upcoming...</h3>
          <ul class="large-block-grid-2" id="previewEvents">
            <li>
              <ul class='pricing-table'>
                <li class='title'></li>
                <li class='price'></li>
                <li class='description'></li>
                <li class='cta-button'>
                  <div class='row'>
                    <ul id='subscription' class='button-group'></ul>
                  </div>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
      <div class="row">
        <div class="large-12 columns" id="resultSize"></div>
      </div>
      <div class="row">
        <div class="large-12 columns" id="callbackNoResult"></div>
      </div>
      '''

    callbackResponse = {
    "size":"5",
    "eventList": [
      {"event":{
      "begin":new Date("19 Apr 2012 15:35:00 +0200").getTime(),
      "title":"title 1",
      "location":"location 1"
      }},
      {"event":{
      "begin":new Date("19 Apr 2012 15:35:00 +0200").getTime(),
      "title":"title 2",
      "location":"location 2"
      }},
      {"event":{
      "begin":new Date("19 Apr 2012 15:35:00 +0200").getTime(),
      "title":"title 3",
      "location":"location 3"
      }}
    ]
    }

    SUGGEST.displayPreviewResult callbackResponse

    expect( $('#resultSize') ).toHaveText( "1 autres évènements trouvés" )

    expect( $('#callbackNoResult').css('display') ).toEqual('none')

    previewElement = $('#previewEvents .pricing-table')

    expect( $( previewElement[0] ).find(".price").text() ).toContain( "title 1")
    expect( $( previewElement[0] ).find(".title").text() ).toContain( "19/04/2012 à 15h35")
    expect( $( previewElement[0] ).find(".description").text() ).toContain( "location 1")

    expect( $( previewElement[1] ).find(".price").text() ).toContain( "title 2")
    expect( $( previewElement[1] ).find(".title").text() ).toContain( "19/04/2012 à 15h35")
    expect( $( previewElement[1] ).find(".description").text() ).toContain( "location 2")

    expect( $( previewElement[2] ).find(".price").text() ).toContain( "title 3")
    expect( $( previewElement[2] ).find(".title").text() ).toContain( "19/04/2012 à 15h35")
    expect( $( previewElement[2] ).find(".description").text() ).toContain( "location 3")

  it "9.2 should display upcoming", ->
    setFixtures '''
      <div class="row">
        <div class="large-12 columns">
          <hr />
          <h3>Upcoming...</h3>
          <ul class="large-block-grid-2" id="previewEvents">
          </ul>
        </div>
      </div>
      <div class="row">
        <div class="large-12 columns" id="resultSize"></div>
      </div>
      <div class="row">
        <div class="large-12 columns" id="callbackNoResult"></div>
      </div>
      '''

    callbackResponse = [
      {
      "begin":new Date("19 Apr 2012 15:35:00 +0200").getTime()
      "title":"title 1"
      "location":"location 1"
      "tags":["MYTAG","MYTAG2"]
      "description":"lorem"
      },
      {
      "begin":new Date("19 Apr 2012 15:35:00 +0200").getTime()
      "title":"title 2"
      "location":"location 2"
      "tags":["MYTAG2"]
      },
      {
      "begin":new Date("19 Apr 2012 15:35:00 +0200").getTime()
      "title":"title 3"
      "location":"location 3"
      "tags":["MYTAG"]
      },
      {
      "begin":new Date("19 Apr 2012 15:35:00 +0200").getTime()
      "title":"title 4"
      "location":"location 4"
      },
      {
      "begin":new Date("19 Apr 2012 15:35:00 +0200").getTime()
      "title":"title 5"
      "location":"location 5"
      }
    ]

    SUGGEST.displayAllEvents callbackResponse

    expect( $('#resultSize') ).toHaveText( "1 autres évènements trouvés" )

    expect( $('#callbackNoResult').css('display') ).toEqual('none')

    previewElement = $('#previewEvents .pricing-table')

    expect( $( previewElement[0] ).find(".price").text() ).toContain( "title 1")
    expect( $( previewElement[0] ).find(".title").text() ).toContain( "19/04/2012 à 15h35")
    expect( $( previewElement[0] ).find(".description").first().text() ).toContain( "location 1")
    expect( $( previewElement[0] ).find(".description").next().text() ).toContain( "lorem")
    expect( $( previewElement[0] ).find(".round.label").length ).toEqual( 2)
    expect( $( previewElement[0] ).find(".round.label").first().text() ).toEqual( "Mytag")
    expect( $( previewElement[0] ).find(".round.label").next().text() ).toEqual( "Mytag2")

    expect( $( previewElement[1] ).find(".price").text() ).toContain( "title 2")
    expect( $( previewElement[1] ).find(".title").text() ).toContain( "19/04/2012 à 15h35")
    expect( $( previewElement[1] ).find(".description").text() ).toContain( "location 2")

    expect( $( previewElement[2] ).find(".price").text() ).toContain( "title 3")
    expect( $( previewElement[2] ).find(".title").text() ).toContain( "19/04/2012 à 15h35")
    expect( $( previewElement[2] ).find(".description").text() ).toContain( "location 3")

  it "10. should display fail", ->
    setFixtures '<div style="display:none;" id="callbackNoResult"></div>'

    SUGGEST.displayNoResult "toto"

    expect( $('#callbackNoResult').css('display') ).toEqual('block')
    expect( $('#callbackNoResult') ).toHaveText( "Le mot clé 'toto' ne donne aucun résultat dans la base OneCalendar" )

  it "11. should hide subscription div", ->
    setFixtures '<div id="subscription"></div>'

    SUGGEST.displayNoResult "toto"
    expect( $('#subscription').css('display') ).toEqual('none')

  it "12. should hide subscription & callbackNoResult div if user dont write anything when he click", ->
    setFixtures '''
                <form method="get" id="events" >
                  <select id="suggest" multiple=""><option>a</option></select>
                </form>
                <div id="subscription">
                  <a class="ical"></a>
                  <a class="gcal"></a>
                  <a class="webcal"></a>
                </div>
                <div id="callbackNoResult"></div>
            '''
    SUGGEST.retrievePreviewResults url: ""

    $("#events").submit()

    expect( $( "#callbackNoResult" ).css( 'display' ) ).toEqual( "none" )

  it "13. shoud format icalendar date format", ->
    expect( SUGGEST.formatIcalDate "2012-04-19T14:30:00.000+02:00" ).toEqual( "19/04/2012 à 14h30" )
    expect( SUGGEST.formatIcalDate "2012-04-19T14:00:00.000+02:00" ).toEqual( "19/04/2012 à 14h" )

  it "14. should display events number", ->
    setFixtures '<span id="eventNumber"></span>'

    SUGGEST.displayEventNumber({eventNumber:0})

    expect( $("#eventNumber")).toHaveText("0")
