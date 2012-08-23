$(document).ready ->
  @handleHelp = (me, sibling, message) ->
    $(me).on "mouseover", -> $(me).siblings(sibling).text(message)
    $(me).on "mouseout", -> $(me).siblings(sibling).text('')

  @handleHelp(".ical",".help", "Utilisez l'url brut pour windows avec Outlook ainsi que pour les autres plateforme")
  @handleHelp(".gcal",".help", "Pour ajouter l'agenda à ceux que vous avez déjà sur google agenda ou si vous êtes sur Android")
  @handleHelp(".webcal",".help", "Si votre navigateur supporte les URLs webcal:// comme sur MacOS ou iOS")

  @handleHelp(".ical", ".helpDevoxx","flux Devoxx brut, marche pour windows Outlook")
  @handleHelp(".gcal", ".helpDevoxx","flux Devoxx pour google Agenda ou Android")
  @handleHelp(".webcal", ".helpDevoxx","flux Devoxx pour ICal Apple (ios) format webcal")