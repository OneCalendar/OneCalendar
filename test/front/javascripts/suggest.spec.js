(function() {

  describe('google suggest like', function() {
    beforeEach(function() {
      return setFixtures('<input type="text" id="suggest" value="a" />');
    });
    it('1. should call function when key press in my marvelous input', function() {
      $('#suggest').on('keyup', function() {});
      return expect($('#suggest')).toHandle('keyup');
    });
    it('2. should display ul element when user press "a" key', function() {
      SUGGEST.suggest();
      $('#suggest').keyup();
      return expect($('#suggest + ul > li')).toHaveText("a");
    });
    it('3. should display only one ul element when user press "a" key twice', function() {
      SUGGEST.suggest();
      $('#suggest').keyup();
      $('#suggest').keyup();
      return expect('<li>a</li><li>a</li>').toEqual($('#suggest + ul').html());
    });
    return it('4. should delete ul element when marvelous input loose focus', function() {
      setFixtures('<input type="text" id="suggest" value="a" />\n<ul><li>a</li></ul>');
      SUGGEST.deleteSuggest();
      $('#suggest').blur();
      return expect($('#suggest + ul').length).toEqual(0);
    });
  });

}).call(this);
