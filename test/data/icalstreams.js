function save(o) {
	if (!db.icalstreams.count({url:o.url}) ) {
		db.icalstreams.save(o);
	} else {
		print(o.url," already exist");
	}
}
if (db.getName() === "OneCalendar") {
	save({ "url" : "http://lacantine.org/events/feed.ics", "streamTags" : ["lacantine"] });
	save({ "url" : "http://www.parinux.org/calendar/ical", "streamTags" : ["parisnux"] });
}