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