function save(o) {
	if (!db.icalstreams.count({defaultTag:o.defaultTag}) ) {
		db.icalstreams.save(o);
	} else {
		print(o.defaultTag,"already exist");
	}
}
if (db.getName() === "OneCalendar") {
	save({ "url" : "http://lacantine.org/events/feed.ics", "defaultTag" : "lacantine" });
	save({ "url" : "http://www.parinux.org/calendar/ical", "defaultTag" : "parisnux" });
}