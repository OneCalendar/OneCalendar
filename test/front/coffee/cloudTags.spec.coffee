describe "handle the cloud of tags", ->

  it "a tag is add in the cloud, but not visible, first character is capitalized", ->
    setFixtures "<div id='cloudtags'/>"
    data = ["tag1"]
    document.cloudThis(data)
    expect($("#cloudtags")).toContain "span.clickTag"
    expect($("#cloudtags span.clickTag")).not.toBeVisible()
    expect($(".clickTag")).toHaveText("Tag1")
    expect($(".clickTag")).toHandle("click")

  it "the cloud is visible on click on legend, test falsy green", ->
    setFixtures "<div class='legend'> +
     <img class='on' style='display:none'> +
     <img class='off'>  +
     </div>  +
     <div id='cloudtags'/>"

    data = ["anyTag"]
    document.cloudThis(data)
    expect($(".legend")).toHandle "click"
    expect($(".legend img.off")).toBeVisible()
    expect($(".legend img.on")).not.toBeVisible()
    $(".legend").trigger("click")
    expect($(".clickTag")).toBeVisible()
    expect($(".legend img.off")).not.toBeVisible()
    expect($(".legend img.on")).toBeVisible()

  it "when a tag is clicked, it is added to input field", ->
    setFixtures "<input id='suggest' type='text'><div class='legend'/><div id='cloudtags'/>"
    data = ["tag1"]
    document.cloudThis(data)
    expect($(".clickTag:first")).not.toBeVisible()
    $(".legend").trigger("click")
    expect($("span.clickTag").length).toBe 1
    expect($(".clickTag:first")).toBeVisible()
    expect($(".clickTag:first")).toHandle("click")
    $(".clickTag:first").trigger("click")
    expect($("#suggest")).toHaveValue("Tag1")

  it "a tag already present, a new one is added, a space is added between, the excedent spaces are removed", ->
    setFixtures "<input id='suggest' type='text'><div class='legend'/><div id='cloudtags'/>"
    data = ["tag1"]
    document.cloudThis(data)
    $("#suggest").val("  tag0  ")
    $(".legend").trigger("click")
    $(".clickTag:first").trigger("click")
    expect($("#suggest")).toHaveValue("tag0 Tag1")
