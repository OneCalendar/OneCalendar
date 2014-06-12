describe "handle the cloud of tags", ->

  it "a tag is add in the cloud, but not visible, first character is capitalized", ->
    setFixtures "<select id='suggest'/>"
    data = ["tag1"]
    document.cloudThis(data)
    expect($("#suggest")).toContain "option[value=Tag1]"
    expect($("#suggest option")).toHaveText("Tag1")
