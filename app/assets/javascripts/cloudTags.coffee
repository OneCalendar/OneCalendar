$(document).ready ->
	@cloudThis = (data) ->
		for tag, i in data
			tag = tag.charAt(0).toUpperCase() + tag.substr(1).toLowerCase()

			$("#suggest").append("<option value='#{tag}'>#{tag}</option>")

		$("#suggest").chosen({
			inherit_select_classes: true
		})
	$.ajax "/tags", success: document.cloudThis