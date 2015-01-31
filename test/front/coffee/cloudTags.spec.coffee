describe "handle the cloud of tags", ->

  it "a tag is add in the cloud, but not visible, first character is capitalized", ->
    setFixtures "<select id='suggest'/>"
    data = ["tag1"]
    window.cloudThis(data)
    console.log($("#suggest").html())
    expect($("#suggest")).toContainElement "option[value=Tag1]"

    expect($("#suggest option")).toHaveText("Tag1")
