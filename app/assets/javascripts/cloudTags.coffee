
$(document).ready ->
  @cloudThis = (data) ->
    for tag, i in data
      tag = tag.charAt(0).toUpperCase() + tag.substr(1).toLowerCase()
      $("#cloudtags").append("<span class='clickTag round label'>" + tag + "</span>\n")
      $(".clickTag").hide()
      $("#cloudtags").append(" ") if i != data.length - 1

    $("span.clickTag").click ->
      $("#suggest").val(($("#suggest").val().trim() + " " + $(this).text().trim()).trim())

    $(".legend").click ->
      $(".clickTag").toggle()
      $(".legend img").toggle()
  $.ajax "/tags", success : document.cloudThis