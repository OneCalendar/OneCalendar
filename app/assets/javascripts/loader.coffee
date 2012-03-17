$(document).ready ->
  ###SUGGEST.suggest()
  SUGGEST.deleteSuggest()###
  SUGGEST.loadUrlDevoxxSection()
  SUGGEST.retrievePreviewResults url: $(location).attr('href').substr(0, $(location).attr('href').length - 1)