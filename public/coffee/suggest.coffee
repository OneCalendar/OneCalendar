@SUGGEST =
  suggest : ->
    $('#suggest').on 'keyup', ->
      ulAddDomElement = "<ul></ul>"
      $( @ ).after( ulAddDomElement ) if( !$( '#suggest + ul' ).length )

      liAddDomElement = "<li>#{$(@).val()}</li>"
      $( '#suggest + ul' ).append( liAddDomElement )

  deleteSuggest : ->
    $('#suggest').on "blur", ->
        $( '#suggest + ul' ).remove()
