$(function() {
    function handleHelp(clzz, message) {
        $(clzz)
            .mouseover(function() {
                $(this).siblings(".help").text(message);
            })
            .mouseout(function() {
                $(this).siblings(".help").text("");
            });
    }

    handleHelp(".ical", "Utilisez l'url brut pour windows avec Outlook ainsi que pour les autres plateforme");
    handleHelp(".gcal", "Pour ajouter l'agenda à ceux que vous avez déjà sur google agenda ou si vous êtes sur Android");
    handleHelp(".webcal", "Si votre navigateur supporte les URLs webcal:// comme sur MacOS ou iOS");
});