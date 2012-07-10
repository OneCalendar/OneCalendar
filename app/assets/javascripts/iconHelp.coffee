
$(document).ready ->
  handleHelp = (clzz,message) ->
    $(clzz).mouseover ->  $(this).siblings(".help").text(message)
    .mouseout -> $(this).siblings(".help").text("")

  handleHelpDevoxx = (clzz,message) ->
    $(clzz).mouseover ->$(this).siblings(".helpDevoxx").text(message)
    mouseout -> $(this).siblings(".helpDevoxx").text("")

  handleHelp(".ical", "Utilisez l'url brut pour windows avec Outlook ainsi que pour les autres plateforme");
  handleHelp(".gcal", "Pour ajouter l'agenda à ceux que vous avez déjà sur google agenda ou si vous êtes sur Android");
  handleHelp(".webcal", "Si votre navigateur supporte les URLs webcal:// comme sur MacOS ou iOS");

  handleHelpDevoxx(".ical", "flux Devoxx brut, marche pour windows Outlook");
  handleHelpDevoxx(".gcal", "flux Devoxx pour google Agenda ou Android");
  handleHelpDevoxx(".webcal", "flux Devoxx pour ICal Apple (ios) format webcal");
