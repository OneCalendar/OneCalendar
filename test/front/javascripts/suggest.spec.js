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
    it('4. should delete ul element when marvelous input loose focus', function() {
      setFixtures('<input type="text" id="suggest" value="a" />\n<ul><li>a</li></ul>');
      SUGGEST.deleteSuggest();
      $('#suggest').blur();
      return expect($('#suggest + ul').length).toEqual(0);
    });
    it("5. should display links when user click on event", function() {
      var expectedGoogleCalendarLinkPrefix, expectedGoogleCalendarLinkSuffix, expectedWebcalLinkPrefix, expectedWebcalLinkSuffix;
      setFixtures('<input type="text" id="suggest" value="a" />\n<div id="temp"></div>\n<div id="subscription" style="display:none;">\n  <a class="ical"></a>\n  <a class="gcal"></a>\n  <a class="webcal"></a>\n</div>');
      SUGGEST.displaySubscription();
      $("#temp").click();
      expectedGoogleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid=";
      expectedGoogleCalendarLinkSuffix = "%2Fevents%2FA";
      expectedWebcalLinkPrefix = "webcal://";
      expectedWebcalLinkSuffix = "/events/A";
      expect($('#subscription').css('display')).toEqual('block');
      expect($('#subscription a.ical')).toHaveAttr('href', '/events/A');
      expect($('#subscription a.gcal').attr('href')).toContain(expectedGoogleCalendarLinkPrefix);
      expect($('#subscription a.gcal').attr('href')).toContain(expectedGoogleCalendarLinkSuffix);
      expect($('#subscription a.webcal').attr('href')).toContain(expectedWebcalLinkPrefix);
      return expect($('#subscription a.webcal').attr('href')).toContain(expectedWebcalLinkSuffix);
    });
    return it("6. should add devoxx url in link for devoxx section", function() {
      setFixtures('<div id="devoxx">\n  <a class="ical"></a>\n  <a class="gcal"></a>\n  <a class="webcal"></a>\n</div>');
      SUGGEST.loadUrlDevoxxSection();
      expect($("#devoxx a.gcal").attr("href")).toContain('%2Fevents%2FDEVOXX');
      return expect($("#devoxx a.webcal").attr("href")).toContain('/events/DEVOXX');
    });
  });

}).call(this);
