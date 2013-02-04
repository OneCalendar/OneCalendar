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
      ALL_EVENTS.filterByTag(this.value)

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

  filterByTag : (tags) ->
    sections = $("section.event")

    if(tags.length != 0)
      sections.hide()

      result = ALL_EVENTS.displaySectionsMatchingWithTags(sections, tags)

      ALL_EVENTS.displayFilterResult(result)

    else
      sections.show()
      ALL_EVENTS.displayFilterResult(sections.length)

  displayFilterResult : (number) ->
    res = " résultats"
    if(number == 0 || number == 1)
       res = " résultat"

    $("#filterResult").text(number + res)

  displaySectionsMatchingWithTags : (sections, stringTags) ->
    result = 0
    tags = stringTags.split("|").map (n) ->
      n.toLowerCase()

    sections.each (i) ->
      section = $(this)
      if(ALL_EVENTS.sectionMatchingWithTags(section, tags))
        result += 1
        section.show()

    result

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

  ###retrieveLi_matchingWithTags : (lis, tags) ->
    lis.filter (j) ->
      liContent = $(this).text().toLowerCase()
      $.grep(tags, (n,i) -> n == liContent).length > 0###
