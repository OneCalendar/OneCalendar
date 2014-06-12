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


@SUGGEST =

	suggest: ->
		$('#suggest').on 'keyup', ->
			ulAddDomElement = "<ul></ul>"
			$(@).after(ulAddDomElement) if( !$('#suggest + ul').length )

			liAddDomElement = "<li>#{$(@).val()}</li>"
			$('#suggest + ul').append(liAddDomElement)

	deleteSuggest: ->
		$('#suggest').on "blur", ->
			$('#suggest + ul').remove()

	displaySubscription : (userSearch) ->
		if userSearch != ""
			googleCalendarLinkPrefix = "http://www.google.com/calendar/render?cid="
			googleCalendarLinkSuffix = "/events/"+encodeURIComponent(userSearch)

			applicationBaseUrl = $(location).attr('href').substr(0, $(location).attr('href').length - 1)
			applicationBaseUrl_withoutHttp = applicationBaseUrl.split("//")[1]

			$('#subscription a.ical').attr('href', "/events/#{userSearch}")
			$('#subscription a.gcal').attr('href', googleCalendarLinkPrefix + encodeURIComponent(applicationBaseUrl + googleCalendarLinkSuffix))
			$('#subscription a.webcal').attr('href', "webcal://#{applicationBaseUrl_withoutHttp}/events/"+encodeURIComponent(userSearch))

			$('#subscription').show()
			$('#devoxx').hide()

	displayPreviewResult: (data) ->
		display(data.eventList, preview2display, data.size)

	displayAllEvents: (data) ->
		display(data, allEvent2display, data.length)

	displayNoResult: (searchWord) ->
		$('#previewEvents').empty()
		$('#subscription').hide()
		$('#resultSize').hide()
		$('#callbackNoResult').text("Le mot clé '#{searchWord}' ne donne aucun résultat dans la base OneCalendar")
		$('#callbackNoResult').show()

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
			concat = ""
			$(".suggest-search .search-choice span").each () -> concat = concat + " " + $(this).text()
			userSearch = concat.trim().toUpperCase()

			if userSearch != ""
				$.ajax(
					{
						type: 'GET'
						url: "/event/tags/#{userSearch}",
						dataType: "json"
						success: (data) ->
							SUGGEST.displayPreviewResult data
							SUGGEST.displaySubscription userSearch
						error: (data) ->
							SUGGEST.displayNoResult userSearch
					})
			else
				$("#subscription").hide()
				$("#callbackNoResult").hide()
			false

	formatIcalDate: (date) ->
		begin = moment(date).zone "+0200"
		if begin.minutes() == 0
			begin.format "DD/MM/YYYY à HH[h]"
		else
			begin.format "DD/MM/YYYY à HH[h]mm"


	retrieveEventNumber: ->
		$.ajax(
			{
				type: 'GET',
				url: "/event/count",
				dataType: "json",
				success: (data) ->
					SUGGEST.displayEventNumber data
				error: (data) ->
					SUGGEST.displayEventNumber {'eventNumber': 'N/A'}
			}
		)

	displayEventNumber: (data) ->
		$("#eventNumber").text(data.eventNumber)


preview2display = (event) ->
	date: SUGGEST.formatIcalDate event.event.begin
	title: event.event.title
	location: event.event.location
	description: event.event.description
	tags: tagsToCamel(event.event.tags)

toCamel = (tag) ->
	return tag.charAt(0).toUpperCase() + tag.substr(1).toLowerCase()

tagsToCamel = (tags) ->
	if tags != undefined then (toCamel tag for tag in tags) else []

allEvent2display = (event) ->
	date: SUGGEST.formatIcalDate event.begin
	title: event.title
	location: event.location
	description: event.description
	tags: tagsToCamel(event.tags)


display = (events, transformer, sizeForAll) ->
	$('#previewEvents').empty()
	$("#callbackNoResult").hide()

	if sizeForAll > 4
		$("#resultSize").html("#{sizeForAll - 4} autres évènements trouvés")
	else
		$("#resultSize").html("&nbsp;")

	previewElement = $('#previewEvents')

	for i in [0..4]
		if events[i] != undefined

			event = transformer(events[i])

			tagsContent = if event.tags != undefined then ("<span class='round label secondary'>#{tag}</span>" for tag in event.tags when tag != undefined ).join(" ") else ""

			previewElement.append("
			                         <li>
			                           <ul class='pricing-table'>
			                             <li class='title'>#{event.date}</li>
			                             <li class='price'>#{event.title}</li>
			                             <li class='description'>#{event.location}</li>
			                             <li class='description oc-description oc-collapse'>#{event.description}</li>
			                             <li class='text-center'> #{tagsContent} </li>

			                                   <li class='cta-button'>
			                                     <div class='row'>
			                                       <ul id='subscription' class='button-group'>
			                                         <div class='large-4 columns'>
			                                           <li><a href='#' class='left ical button secondary'><i class='fi-clipboard-notes oc-icon'/> To&nbsp;copy </a></li>
			                                         </div>
			                                         <div class='large-4 columns'>
			                                           <li><a href='#' class='centered gcal button secondary'><i class='fi-calendar oc-icon'/> Google </a></li>
			                                         </div>
			                                         <div class='large-4 columns'>
			                                           <li><a href='#' class='right webcal button secondary'><i class='fi-social-apple oc-icon'/> Apple </a></li>
			                                         </div>
			                                       </ul>
			                                     </div>
			                                    </li>
			                           </ul>
			                         </li>")
		else
			previewElement.append("
			                         <span class='title'></span>
			                         <span class='date'></span>
			                         <span class='location'></span>")

		$("#previewEvents .pricing-table .oc-description").mouseenter(() ->
			$(this).removeClass("oc-collapse", 400, "easeOutBounce")
			return
		).mouseleave(() ->
			$(this).addClass("oc-collapse", 400, "easeOutBounce")
			return
		).click(() ->
			$(this).toggleClass("oc-collapse", 400, "easeOutBounce")
			return
		)
