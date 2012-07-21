
describe 'icon help interaction', ->
  beforeEach ->
    setFixtures "<a class='ablock'></a><div class='help'></div>"

  it "a block that handle help, handle mouse event", ->
    document.handleHelp('.ablock','.help','my help message')
    expect($(".ablock")).toHandle "mouseover"
    expect($(".ablock")).toHandle "mouseout"

  it "on mouse over, the help message is displayed", ->
    document.handleHelp('.ablock','.help','my help message')
    $(".ablock").mouseover()
    expect($('.help')).toHaveText "my help message"

  it "on mouse out, the help message go back branck", ->
    document.handleHelp('.ablock','.help','my help message')
    $(".ablock").mouseout()
    expect($('.help')).toHaveText ""