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

  it "5. should display links when user click on event", ->
    setFixtures '''
      <input type="text" id="suggest" value="a" />
      <div id="temp"></div>
      <div id="subscription" style="display:none;">
        <a class="ical"></a>
        <a class="gcal"></a>
        <a class="webcal"></a>
      </div>
    '''

    SUGGEST.displaySubscription()
    $("#temp").click()

    expectedGoogleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid="
    expectedGoogleCalendarLinkSuffix = "%2Fevents%2FA"
    expectedWebcalLinkPrefix = "webcal://"
    expectedWebcalLinkSuffix = "/events/A"

    expect( $('#subscription').css('display') ).toEqual('block')
    expect( $('#subscription a.ical') ).toHaveAttr('href', '/events/A')
    expect( $('#subscription a.gcal').attr('href') ).toContain(expectedGoogleCalendarLinkPrefix)
    expect( $('#subscription a.gcal').attr('href') ).toContain( expectedGoogleCalendarLinkSuffix )
    expect( $('#subscription a.webcal').attr('href') ).toContain( expectedWebcalLinkPrefix )
    expect( $('#subscription a.webcal').attr('href') ).toContain( expectedWebcalLinkSuffix )

  it "6. should add devoxx url in link for devoxx section", ->
    setFixtures '''
      <div id="devoxx">
        <a class="ical"></a>
        <a class="gcal"></a>
        <a class="webcal"></a>
      </div>
    '''

    SUGGEST.loadUrlDevoxxSection()

    expect( $("#devoxx a.gcal").attr("href") ).toContain('%2Fevents%2FDEVOXX')
    expect( $("#devoxx a.webcal").attr("href") ).toContain('/events/DEVOXX')