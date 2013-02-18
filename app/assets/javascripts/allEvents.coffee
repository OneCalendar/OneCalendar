$(document).ready ->
  ALL_EVENTS.hideAllDescriptions()
  ALL_EVENTS.listenDescriptionsButton()
  ALL_EVENTS.listenFilterButtons()

  nbEvent = $("section.event").length
  ALL_EVENTS.displayFilterResult(nbEvent)

up_button_selector = ".description h4 .icon-chevron-up"
down_button_selector = ".description h4 .icon-chevron-down"
description_selector = ".description p"

@ALL_EVENTS =

  listenDescriptionsButton : ->
    $(down_button_selector).on "click", ->
      ALL_EVENTS.showDescription(this)

    $(up_button_selector).on "click", ->
      ALL_EVENTS.hideDescription(this)

  listenFilterButtons : ->
    $("#tags").on "keyup", ->
      ALL_EVENTS.filter()

    $("#from").on "keyup", ->
      ALL_EVENTS.filter()

    $("#to").on "keyup", ->
      ALL_EVENTS.filter()

  hideAllDescriptions : ->
    $(up_button_selector).hide()
    $(down_button_selector).show()
    $(description_selector).hide()

  hideDescription : (button_up)->
    $(button_up).hide()
    $(button_up).parents(".description").find(".icon-chevron-down").show()
    $(button_up).parents(".description").find("p").hide()

  showDescription : (button_down) ->
    $(button_down).hide()
    $(button_down).parents(".description").find(".icon-chevron-up").show()
    $(button_down).parents(".description").find("p").show()

  filter : ->
    tags = $("#tags").val()
    fromDate = $("#from").val()
    toDate = $("#to").val()

    tagsSections = ALL_EVENTS.filterByTag(tags) # tableau simple d'objets jquery
    fromDateSections = ALL_EVENTS.filterByFromDate(fromDate) # tableau simple d'objets jquery
    toDateSections = ALL_EVENTS.filterByToDate(toDate) # tableau simple d'objets jquery

    console.log toDateSections.length

    sectionsToDisplay = ALL_EVENTS.intersectArrayOfJQueryElem(tagsSections, fromDateSections)
    sectionsToDisplay = ALL_EVENTS.intersectArrayOfJQueryElem(sectionsToDisplay, toDateSections)

    ALL_EVENTS.displayFilterResult sectionsToDisplay.length

    $("section.event").hide()
    $.each(sectionsToDisplay, (k, value) ->
      $(value).show()
    )

  filterByTag : (tags) ->
    sections = $("section.event")
    fullReturn = []

    if(tags.length != 0)
      ALL_EVENTS.selectSectionsMatchingWithTags(sections, tags)
    else
      sections.each (i) ->
        fullReturn.push $(this)

      fullReturn

  filterByFromDate : (from) ->
    sections = $("section.event")
    fullReturn = []

    if(from.match("[0-9]{2}\/[0-9]{2}\/[0-9]{4}") != null)
      ALL_EVENTS.selectSectionsMatchingWithFromDate(sections, from)
    else
      sections.each (i) ->
        fullReturn.push $(this)

      fullReturn

  filterByToDate : (to) ->
    sections = $("section.event")
    fullReturn = []

    if(to.match("[0-9]{2}\/[0-9]{2}\/[0-9]{4}") != null)
      ALL_EVENTS.selectSectionsMatchingWithToDate(sections, to)
    else
      sections.each (i) ->
        fullReturn.push $(this)

      fullReturn

  intersectArrayOfJQueryElem : (array1, array2) ->
    $.grep( array1, (elem1, j) ->
      $.grep(array2, (elem2, k) ->
        elem1.get(0) == elem2.get(0)
      ).length > 0
    )

  displayFilterResult : (number) ->
    res = " résultats"
    if(number == 0 || number == 1)
       res = " résultat"

    $("#filterResult").text(number + res)

  selectSectionsMatchingWithTags : (sections, stringTags) ->
    sectionsMatching = []
    tags = stringTags.split("|").map (n) ->
      n.toLowerCase()

    sections.each (i) ->
      section = $(this)
      if(ALL_EVENTS.sectionMatchingWithTags(section, tags))
        sectionsMatching.push section

    sectionsMatching

    # eliminer la duplication avec une injection de fonction pour le sections.each
  selectSectionsMatchingWithFromDate : (sections, from) ->
    sectionsMatching = []

    sections.each (i) ->
      section = $(this)
      sectionDate = section.find(".from").text().split(" ")[0]
      sectionDateMoment = moment(sectionDate, "DD/MM/YYYY")
      fromDateMoment = moment(from, "DD/MM/YYYY")

      if(sectionDateMoment.diff(fromDateMoment) >= 0)
        sectionsMatching.push section

    sectionsMatching

  selectSectionsMatchingWithToDate : (sections, to) ->
    sectionsMatching = []

    sections.each (i) ->
      section = $(this)
      sectionDate = section.find(".to").text().split(" ")[0]
      sectionDateMoment = moment(sectionDate, "DD/MM/YYYY")
      toDateMoment = moment(to, "DD/MM/YYYY")

      if(sectionDateMoment.diff(toDateMoment) <= 0)
        sectionsMatching.push section

    sectionsMatching

  sectionMatchingWithTags : (section, tags) ->
    lis = section.find("li")

    $.grep( tags, (tag,i) ->
      incl_tags = tag.split("&")
      ALL_EVENTS.eachInclTagMatchingWithAtLeastOneLi(incl_tags, lis)
    ).length > 0

  eachInclTagMatchingWithAtLeastOneLi : (incl_tags, lis) ->
    $.grep( incl_tags, (incl_tag, j) ->
      ALL_EVENTS.eachLiMatchingWithAtLeastOneInclTag(lis, incl_tag)
    ).length == incl_tags.length

  eachLiMatchingWithAtLeastOneInclTag : (lis, incl_tag) ->
    (lis.filter (k) ->
      liContent = $(this).text().toLowerCase()
      liContent == incl_tag).length > 0