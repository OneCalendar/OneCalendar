(function() {
    $.ajax("/tags", {
        success: function(data) {
            for (var i = 0; i < data.length; i++) {
                var tag = data[i];
                tag = tag.charAt(0).toUpperCase() + tag.substr(1).toLowerCase();
                $("#cloudtags").append("<span class='clickTag'>" + tag + "</div>");
                $(".clickTag").hide();
                if (i != data.length - 1)
                    $("#cloudtags").append(" ");
            }

            $("span.clickTag").click(function(el) {
                $("#suggest").val(($("#suggest").val().trim() + " " + $(this).text().trim()).trim());
            });

            
            $(".legend").click(function () {
                $(".clickTag").toggle();
                $(".legend img").toggle();
            })
        }
    })
}).call(this);