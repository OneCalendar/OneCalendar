
$(document).ready ->
  $.ajax "/tags",
    success: ->
        for tag, i in data
            tag = tag.charAt(0).toUpperCase() + tag.substr(1).toLowerCase()
            $("#cloudtags").append("<span class='clickTag'>" + tag + "</div>")
            $(".clickTag").hide()
            $("#cloudtags").append(" ") if i != data.length - 1

        $("span.clickTag").click (el) ->
            $("#suggest").val(($("#suggest").val().trim() + " " + $(this).text().trim()).trim())


        $(".legend").click ->
            $(".clickTag").toggle()
            $(".legend img").toggle()