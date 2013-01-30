$(document).ready ->
  ALL_EVENTS.hideAllDescription()
  ALL_EVENTS.listenDescriptionButton()

up_button_selector = ".description h4 .icon-chevron-up"
down_button_selector = ".description h4 .icon-chevron-down"
description_selector = ".description p"

@ALL_EVENTS =

  hideAllDescription : ->
    $(up_button_selector).hide()
    $(down_button_selector).show()
    $(description_selector).hide()

  hideDescription : (button_up)->
    $(button_up).hide()
    $(button_up).parent().parent().find(".icon-chevron-down").show()
    $(button_up).parent().parent().parent().find("p").hide()

  showDescription : (button_down) ->
    $(button_down).hide()
    $(button_down).parent().parent().find(".icon-chevron-up").show()
    $(button_down).parent().parent().parent().find("p").show()

  listenDescriptionButton : ->
    $(down_button_selector).on "click", ->
      ALL_EVENTS.showDescription(this)

    $(up_button_selector).on "click", ->
      ALL_EVENTS.hideDescription(this)