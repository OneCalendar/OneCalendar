describe "handle the cloud of tags", ->

  it "a tag is add in the cloud, but not visible, first character is capitalized", ->
    setFixtures "<div id='cloudtags'/>"
    data = ["tag1"]
    document.cloudThis(data)
    expect($("#cloudtags")).toContain "span.clickTag"
    expect($("#cloudtags span.clickTag")).not.toBeVisible
    expect($(".clickTag")).toHaveText("Tag1")
    expect($(".clickTag")).toHandle("click")

  it "when multiple tags, each are separated by space", ->
      setFixtures "<div id='cloudtags'/>"
      data = ["tag1","tag2"]
      document.cloudThis(data)
      expect($("#cloudtags")).toContain "span.clickTag"
      expect($("#cloudtags span.clickTag")).not.toBeVisible
      expect($("#cloudtags span.clickTag").length).toBe 2
      expect($("#cloudtags").html()).toMatch("span> <span")

  it "the cloud is visible on click on legend, test falsy green", ->
    setFixtures "<div class='legend'> +
     <img class='on' style='display:none'> +
     <img class='off'>  +
     </div>  +
     <div id='cloudtags'/>"

    data = ["anyTag"]
    document.cloudThis(data)
    expect($(".legend")).toHandle "click"
    expect($(".legend img.off")).isVisible
    expect($(".legend img.on")).not.isVisible
    $(".legend").click()
    expect($(".clickTag")).toBeVisible()
    expect($(".legend img.off")).not.isVisible
    expect($(".legend img.on")).isVisible

  it "when a tag is clicked, it is added to input field", ->
    setFixtures "<input id='suggest' type='text'><div class='legend'/><div id='cloudtags'/>"
    data = ["tag1"]
    document.cloudThis(data)
    expect($(".clickTag:first")).not.isVisible
    $(".legend").click()
    expect($("span.clickTag").length).toBe 1
    expect($(".clickTag:first")).isVisible
    expect($(".clickTag:first")).toHandle("click")
    $(".clickTag:first").click()
    expect($("#suggest")).toHaveValue("Tag1")

  it "a tag already present, a new one is added, a space is added between, the excedent spaces are removed", ->
    setFixtures "<input id='suggest' type='text'><div class='legend'/><div id='cloudtags'/>"
    data = ["tag1"]
    document.cloudThis(data)
    $("#suggest").val("  tag0  ")
    $(".legend").click()
    $(".clickTag:first").click()
    expect($("#suggest")).toHaveValue("tag0 Tag1")