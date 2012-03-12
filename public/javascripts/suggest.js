(function() {

  this.SUGGEST = {
    suggest: function() {
      return $('#suggest').on('keyup', function() {
        var liAddDomElement, ulAddDomElement;
        ulAddDomElement = "<ul></ul>";
        if (!$('#suggest + ul').length) $(this).after(ulAddDomElement);
        liAddDomElement = "<li>" + ($(this).val()) + "</li>";
        return $('#suggest + ul').append(liAddDomElement);
      });
    },
    deleteSuggest: function() {
      return $('#suggest').on("blur", function() {
        return $('#suggest + ul').remove();
      });
    }
  };

}).call(this);
