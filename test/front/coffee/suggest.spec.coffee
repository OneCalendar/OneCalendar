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
      <div id="subscription"></div>
    '''

    SUGGEST.displaySubscription()
    $("#temp").click()

    expectedGoogleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid="
    expectedGoogleCalendarLinkSuffix = "%2Fevents%2Fa"
    expectedWebcalLinkPrefix = "webcal://"
    expectedWebcalLinkSuffix = "/events/a"

    expect( $('#subscription > a') ).toHaveAttr('href', '/events/a')
    expect( $('#subscription > a + a').attr('href') ).toContain(expectedGoogleCalendarLinkPrefix)
    expect( $('#subscription > a + a').attr('href') ).toContain( expectedGoogleCalendarLinkSuffix )
    expect( $('#subscription > a + a + a').attr('href') ).toContain( expectedWebcalLinkPrefix )
    expect( $('#subscription > a + a + a').attr('href') ).toContain( expectedWebcalLinkSuffix )

  it "6 should display links only once", ->
    setFixtures '''
        <input type="text" id="suggest" value="a" />
        <div id="temp"></div>
        <div id="subscription"></div>
      '''

    SUGGEST.displaySubscription()
    $("#temp").click().click()

    expect( $('#subscription a').size() ).toEqual(3)

