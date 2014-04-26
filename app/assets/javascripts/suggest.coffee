##
## This file is part of OneCalendar.
##
## OneCalendar is free software: you can redistribute it and/or modify
## it under the terms of the GNU General Public License as published by
## the Free Software Foundation, either version 3 of the License, or
## (at your option) any later version.
##
## OneCalendar is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with OneCalendar.  If not, see <http://www.gnu.org/licenses/>.
##

class Panier
  unique = (previous,current,index,array) ->
    previous.push(current) if previous.indexOf(current) == -1
    previous

  content = []
  constructor: () ->

  addTag: (text) ->
    content.push(text)
    newContent = content.reduce( unique ,[])
    content.length = 0
    content.push(t) for t in newContent

  getContent: () -> content

  getContentAsUserSearch: () -> @getContent().map((a) -> a.toUpperCase()).join(" ")

panier = new Panier()

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

  displaySubscription : (userSearch) ->
      if userSearch != ""
        googleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid="
        googleCalendarLinkSuffix = "%2Fevents%2F#{userSearch}"

        applicationBaseUrl = $(location).attr('href').substr(0, $(location).attr('href').length - 1)
        applicationBaseUrl_withoutHttp = applicationBaseUrl.split("//")[1]

        $('#subscription a.ical').attr('href', "/events/#{userSearch}")
        $('#subscription a.gcal').attr('href', googleCalendarLinkPrefix + applicationBaseUrl + googleCalendarLinkSuffix)
        $('#subscription a.webcal').attr('href', "webcal://#{applicationBaseUrl_withoutHttp}/events/#{userSearch}")

        $('#subscription').show()
        $('#devoxx').hide()

  displayPreviewResult : (data) ->
     display(data.eventList,preview2display,data.size)

  displayAllEvents : (data) ->
    display(data,allEvent2display,data.length)

  displayNoResult : (searchWord) ->
    $('#previewEvents').empty()
    $( '#subscription' ).hide()
    $( '#resultSize' ).hide()
    $( '#callbackNoResult' ).text( "Le mot clé '#{searchWord}' ne donne aucun résultat dans la base OneCalendar" )
    $( '#callbackNoResult' ).show()

  retrieveAllEvents: ->
    $.ajax({
      type: 'GET'
      url: "/events",
      dataType: "json"
      success: (data) ->
        SUGGEST.displayAllEvents data
      error: () ->
        SUGGEST.displayNoResult $('#suggest').val()
    })
    
  retrievePreviewResults: ->
    $("#events").submit ->
      userSearch = $('#suggest').val().toUpperCase()

      if userSearch != ""
        $.ajax(
          {
            type: 'GET'
            url: "/event/tags/#{userSearch}",
            dataType: "json"
            success: (data) ->
              SUGGEST.displayPreviewResult data
              # TODO remove because results displayed in basket, but too much tests fails
              SUGGEST.displaySubscription userSearch
            error: (data) ->
              SUGGEST.displayNoResult $('#suggest').val()
          }
        )
      else
        $( "#subscription" ).hide()
        $( "#callbackNoResult" ).hide()

      false

  formatIcalDate: (date) ->
    begin = moment(date).zone("+0200")
    if begin.minutes() == 0
      begin.format("DD/MM/YYYY à HH[h]")
    else
      begin.format("DD/MM/YYYY à HH[h]mm")


  retrieveEventNumber: ->
    $.ajax(
      {
        type: 'GET',
        url: "/event/count",
        dataType: "json",
        success: (data) ->
          SUGGEST.displayEventNumber data
        error: (data) ->
          SUGGEST.displayEventNumber {'eventNumber':'N/A'}
      }
    )

  displayEventNumber: (data) ->
    $("#eventNumber").text(data.eventNumber)


preview2display = (event) ->
  date: SUGGEST.formatIcalDate event.event.begin
  title: event.event.title
  location: event.event.location
  description: event.event.description
  tags:tagsToCamel(event.event.tags)

toCamel = (tag) ->
  return tag.charAt(0).toUpperCase() + tag.substr(1).toLowerCase()

tagsToCamel = (tags) ->
  if tags != undefined then (toCamel tag for tag in tags) else []

allEvent2display = (event) ->
  date: SUGGEST.formatIcalDate event.begin
  title: event.title
  location: event.location
  description: event.description
  tags:tagsToCamel(event.tags)


display = (events,transformer,sizeForAll) ->
  $('#previewEvents').empty()
  $( "#callbackNoResult" ).hide()

  if sizeForAll > 4
    $("#resultSize").html("#{sizeForAll - 4} autres évènements trouvés")
  else
    $("#resultSize").html("&nbsp;")

  previewElement = $('#previewEvents')

  for i in [0..4]
    if events[i] != undefined

      event = transformer(events[i])

      tagsContent = if event.tags != undefined then ("<span class='round label'>#{tag}</span>" for tag in event.tags when tag != undefined ).join(" ") else ""

      previewElement.append( "
                         <li>
                           <ul class='pricing-table'>
                             <li class='title'>#{event.date}</li>
                             <li class='price'>#{event.title}</li>
                             <li class='description'>#{event.location}</li>
                             <li class='description oc-description oc-collapse'>#{event.description}</li>
                             <li class='text-center'> #{tagsContent} </li>
                           </ul>
                         </li>" )

    else
      previewElement.append( "
                         <span class='title'></span>
                         <span class='date'></span>
                         <span class='location'></span>" )

    $("#previewEvents .pricing-table .oc-description").mouseenter(() ->
      $(this).removeClass("oc-collapse",400,"easeOutBounce")
      return
    ).mouseleave(() ->
      $(this).addClass("oc-collapse",400,"easeOutBounce")
      return
    ).click(() ->
      $(this).toggleClass("oc-collapse",400,"easeOutBounce")
      return
    )



  addTagToBasket = () ->
    panier.addTag($(@).text())

    $("#panier-contenu").nextAll().remove()

    addTagInDom = (tag) ->
      tagBasket = ' <li><label><span class="round secondary label">' + tag+'</span></label></li>'
      $("#panier").append(tagBasket)

    addTagInDom tag for tag in panier.getContent()

    $("#panier-nb-item").text(" (" +$("#panier-contenu").siblings("li").find("label span.label").length + ")")

  previewElement.find('.round.label').click addTagToBasket

  updateBasket = ->

    setHrefAttr = (href) ->
      userSearch = panier.getContentAsUserSearch()

      href = if userSearch != "" then "#{href}#{userSearch}" else "#"
      $("#panier a").attr("href",href)

    renderApple = ->
      setHrefAttr("webcal://#{location.host}/events/")

    renderGoogle = ->
      href = "http://www.google.com/calendar/render?cid=#{location.protocol}//#{location.host}%2Fevents%2F"
      setHrefAttr(href)

    variable = $(@).find(".oc-icon")

    renderer = switch
      when variable.hasClass("fi-social-apple") then renderApple
      when variable.hasClass("fi-calendar") then renderGoogle

    renderer() if renderer

  $("#panier .oc-icon").parent().hover updateBasket


