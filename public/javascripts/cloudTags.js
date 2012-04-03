(function() {
    $.ajax("/tags", {
        success: function(data) {
            $("#cloudtags").css({height:"40px",overflow:"hidden"});

            for (var i = 0; i < data.length; i++) {
                var tag = data[i];
                tag = tag.charAt(0).toUpperCase() + tag.substr(1).toLowerCase();
                $("#cloudtags").append("<span class='clickTag'>" + tag + "</div>");
                if (i != data.length - 1)
                    $("#cloudtags").append(" ");
            }

            $("span.clickTag").click(function(el) {
                $("#suggest").val(($("#suggest").val().trim() + " " + $(this).text().trim()).trim());
            });

            $("#cloudtags .elipse").click(function () {
                if ($("#cloudtags").attr("style")) {
                    $("#cloudtags").removeAttr("style");
                } else {
                    $("#cloudtags").css({overflow:"hidden",height:"40px"});
                }
            })
        }
    })
}).call(this);