((w) ->
  w.handleHelp = (me, sibling, message) ->
    $(me).on "mouseover", -> $(me).siblings(sibling).text(message)
    $(me).on "mouseout", -> $(me).siblings(sibling).text('')
)(window)

$(document).ready ->

  window.handleHelp(".ical",".help", "Utilisez l'url brut pour windows avec Outlook ainsi que pour les autres plateforme")
  window.handleHelp(".gcal",".help", "Pour ajouter l'agenda à ceux que vous avez déjà sur google agenda ou si vous êtes sur Android")
  window.handleHelp(".webcal",".help", "Si votre navigateur supporte les URLs webcal:// comme sur MacOS ou iOS")

  window.handleHelp(".ical", ".helpDevoxx","flux Devoxx brut, marche pour windows Outlook")
  window.handleHelp(".gcal", ".helpDevoxx","flux Devoxx pour google Agenda ou Android")
  window.handleHelp(".webcal", ".helpDevoxx","flux Devoxx pour ICal Apple (ios) format webcal")