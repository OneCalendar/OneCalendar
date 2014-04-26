describe "the basket", ->
  it 'when click on a tag, it is added to basket', ->
    setFixtures """
      <li class="has-dropdown not-click">
          <a href="#"><i class="fi-shopping-cart oc-icon left"></i> <span id="panier-nb-item"></span></a>
          <ul id="panier" class="dropdown">
              <li>
                  <label id="panier-flux">Flux :</label>
              </li>
              <li id="panier-subscriptions">
                  <a href="#">
                      <div class="row">
                          <div class="small-4 columns"><i class="fi-social-apple oc-icon"> </i></div>
                          <div class="small-4 columns"><i class="fi-calendar oc-icon"> </i></div>
                          <div class="small-4 columns"><i class="fi-clipboard-notes oc-icon"> </i></div>
                      </div>
                  </a>
              </li>
              <li class="divider"></li>
              <li id="panier-contenu"><label>Tags :</label></li>
          </ul>
      </li>
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
    """

    callbackResponse = [
      {
        "begin":new Date("19 Apr 2012 15:35:00 +0200").getTime()
        "title":"title 1"
        "location":"location 1"
        "tags":["MYTAG"]
        "description":"lorem"
      },
      {
        "begin":new Date("20 Apr 2012 15:35:00 +0200").getTime()
        "title":"title 2"
        "location":"location 2"
        "tags":["MYTAG2"]
        "description":"lorem2"
      }
    ]


    SUGGEST.displayAllEvents callbackResponse

    previewElement = $('#previewEvents .pricing-table')

    expect( $( previewElement ).length).toEqual 2

    expect( $( previewElement[0] ).find(".round.label").first().text() ).toEqual "Mytag"
    expect( $( previewElement[0] ).find(".round.label").first() ).toHandle 'click'

    $( previewElement[0] ).find(".round.label").first().trigger "click"

    expect( $("#panier-contenu").siblings("li").find("label span.label").length).toEqual 1
    expect( $("#panier-contenu").siblings("li").find("label span.label").text()).toEqual "Mytag"
    expect($("#panier-nb-item").text()).toEqual " (1)"

    $( previewElement[0] ).find(".round.label").first().trigger "click"

    expect( $("#panier-contenu").siblings("li").find("label span.label").length ).toEqual 1
    expect( $("#panier-contenu").siblings("li").find("label span.label").text()).toEqual "Mytag"
    expect($("#panier-nb-item").text()).toEqual " (1)"

    expect( $( previewElement[1] ).find(".round.label").first().text() ).toEqual "Mytag2"
    $( previewElement[1] ).find(".round.label").first().trigger "click"

    expect( $("#panier-contenu").siblings("li").find("label span.label").length ).toEqual 2
    expect( $("#panier-contenu").siblings("li").find("label span.label").text()).toEqual "MytagMytag2"
    expect($("#panier-nb-item").text()).toEqual " (2)"

