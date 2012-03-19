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
    it("5. should display links when user write search word", function() {
      var expectedGoogleCalendarLinkPrefix, expectedGoogleCalendarLinkSuffix, expectedWebcalLinkPrefix, expectedWebcalLinkSuffix;
      setFixtures('<input type="text" id="suggest" value="a" />\n<div id="temp"></div>\n<div id="subscription" style="display:none;">\n  <a class="ical"></a>\n  <a class="gcal"></a>\n  <a class="webcal"></a>\n</div>');
      SUGGEST.displaySubscription('A');
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
    it("6. if user don't write search should not display links", function() {
      setFixtures('<input type="text" id="suggest" value="" />\n<div id="temp"></div>\n<div id="subscription" style="display:none;">\n  <a class="ical"></a>\n  <a class="gcal"></a>\n  <a class="webcal"></a>\n</div>');
      SUGGEST.displaySubscription('');
      return expect($('#subscription').css('display')).toEqual('none');
    });
    it("7. should add devoxx url in link for devoxx section", function() {
      setFixtures('<div id="devoxx">\n  <a class="ical"></a>\n  <a class="gcal"></a>\n  <a class="webcal"></a>\n</div>');
      SUGGEST.loadUrlDevoxxSection();
      expect($("#devoxx a.gcal").attr("href")).toContain('%2Fevents%2FDEVOXX');
      return expect($("#devoxx a.webcal").attr("href")).toContain('/events/DEVOXX');
    });
    it("8. should call rest controller to retrieve preview result when user click on search", function() {
      var callbackData, urlServer;
      setFixtures('<input type="text" id="suggest" value="a" />\n<div id="temp"></div>\n<div id="subscription">\n  <a class="ical"></a>\n  <a class="gcal"></a>\n  <a class="webcal"></a>\n</div>');
      urlServer = 'http://serveur';
      callbackData = {
        "key": "value"
      };
      SUGGEST.retrievePreviewResults({
        url: urlServer
      });
      spyOn(SUGGEST, 'displaySubscription').andCallThrough;
      spyOn(SUGGEST, 'displayPreviewResult').andCallThrough;
      spyOn($, 'ajax').andCallFake(function(params) {
        return params.success(function() {
          return callbackData;
        });
      });
      $("#temp").click();
      expect($.ajax).toHaveBeenCalled();
      expect(SUGGEST.displayPreviewResult).toHaveBeenCalled();
      return expect(SUGGEST.displaySubscription).toHaveBeenCalledWith('A');
    });
    it("9. should call displayNoResult method when callback is error", function() {
      var callbackData, urlServer;
      setFixtures('<input type="text" id="suggest" value="a" />\n<div id="temp"></div>\n<div id="subscription">\n  <a class="ical"></a>\n  <a class="gcal"></a>\n  <a class="webcal"></a>\n</div>');
      urlServer = 'http://serveur';
      callbackData = {
        "key": "value"
      };
      SUGGEST.retrievePreviewResults({
        url: urlServer
      });
      spyOn(SUGGEST, 'displayNoResult').andCallThrough;
      spyOn($, 'ajax').andCallFake(function(params) {
        return params.error(function() {
          return "";
        });
      });
      $("#temp").click();
      expect($.ajax).toHaveBeenCalled();
      return expect(SUGGEST.displayNoResult).toHaveBeenCalledWith('a');
    });
    it("10. should display preview", function() {
      var callbackResponse, previewElement;
      setFixtures('<div id="subscription"">\n  <p id="resultSize"></p>\n  <p class="preview"></p>\n  <p class="preview"></p>\n  <p class="preview"></p>\n  <a class="ical"></a>\n  <a class="gcal"></a>\n  <a class="webcal"></a>\n</div>\n<div id="callbackNoResult"></div>');
      callbackResponse = {
        "size": "5",
        "eventList": [
          {
            "event": {
              "date": "2012-04-19T15:35:00.000+02:00",
              "title": "title 1",
              "location": "location 1"
            }
          }, {
            "event": {
              "date": "2012-04-19T15:35:00.000+02:00",
              "title": "title 2",
              "location": "location 2"
            }
          }, {
            "event": {
              "date": "2012-04-19T15:35:00.000+02:00",
              "title": "title 3",
              "location": "location 3"
            }
          }
        ]
      };
      SUGGEST.displayPreviewResult(callbackResponse);
      expect($('#resultSize')).toHaveText("nombre d'évènement(s) trouvé(s): 5");
      expect($('#callbackNoResult').css('display')).toEqual('none');
      previewElement = $('#subscription .preview');
      expect($(previewElement[0]).text()).toContain("title 1");
      expect($(previewElement[0]).text()).toContain("19 04 2012 - 15:35 - GMT+02");
      expect($(previewElement[0]).text()).toContain("location 1");
      expect($(previewElement[1]).text()).toContain("title 2");
      expect($(previewElement[1]).text()).toContain("19 04 2012 - 15:35 - GMT+02");
      expect($(previewElement[1]).text()).toContain("location 2");
      expect($(previewElement[2]).text()).toContain("title 3");
      expect($(previewElement[2]).text()).toContain("19 04 2012 - 15:35 - GMT+02");
      return expect($(previewElement[2]).text()).toContain("location 3");
    });
    it("11. should display fail", function() {
      setFixtures('<div style="display:none;" id="callbackNoResult"></div>');
      SUGGEST.displayNoResult("toto");
      expect($('#callbackNoResult').css('display')).toEqual('block');
      return expect($('#callbackNoResult')).toHaveText("Le mot clé 'toto' ne donne aucun résultat dans la base OneCalendar");
    });
    it("12. should hide subscription div", function() {
      setFixtures('<div id="subscription"></div>');
      SUGGEST.displayNoResult("toto");
      return expect($('#subscription').css('display')).toEqual('none');
    });
    it("13. should hide subscription & callbackNoResult div if user dont write anything when he click", function() {
      setFixtures('<input type="text" id="suggest" value="" />\n<div id="temp"></div>\n<div id="subscription">\n  <a class="ical"></a>\n  <a class="gcal"></a>\n  <a class="webcal"></a>\n</div>\n<div id="callbackNoResult"></div>');
      SUGGEST.retrievePreviewResults({
        url: ""
      });
      $("#temp").click();
      expect($("#subscription").css('display')).toEqual("none");
      return expect($("#callbackNoResult").css('display')).toEqual("none");
    });
    return it("14. shoud format icalendar date format", function() {
      return expect(SUGGEST.formatIcalDate("2012-04-19T14:30:00.000+02:00")).toEqual("19 04 2012 - 14:30 - GMT+02");
    });
  });

}).call(this);
