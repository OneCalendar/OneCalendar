# One Calendar To Meet Them All

You can view it for real on the site site : [onecalendar.valtech.fr](http://onecalendar.valtech.fr)

## The problem we solve

Have ever looking for events on a specific subject ?
You had to realize that there were many differents sources, not so well structured, and rarely available directly since your diary. I think about for example [Open Diary](http://www.agendadulibre.org/) [Agile Diary](http://www.agenda-agile.org/), some meetups, [JDuchess Diary](https://sites.google.com/site/duchessfr/calendrier-conferences) and for so much others communities it could exist.
I found rather complex and spend to much time to stay up to date of all the events, and always warned too late to register.

## What it does

The idea is to simplify all this by proposing a **diaries aggregator**, which is going to tag them, to store it to be able to consult, filter and extract. To start, under the shape of iCal, standard IETF flow, for schedule.

## Benefits

One of the major benefit is to propose with a standard format like [iCal](http://www.ietf.org/rfc/rfc2445.txt). By this way, with a minimal web application, and any compatible device, we can propose our service.
We use a NoSQL database to be able to evolve our data model without script migration. We use this NoSQL database to build our categories index.

## Drawbacks

The categorisation of the events, the extraction of the tags is like building an index with a very specific dictionary, so we have to build everything to make it work as expected.
Like this dictionary is so specific, we have to build a synonym database.
Those two points are not addressed at the moment.

## High level design

web client that can handle specifically ical url scheme. It consult our web application to filter bellow his tastes. And we deliver him an iCal file that can be refreshed on its terminal. The ical is generate from the extract with those criterias for the database.
It's a classical web layered application.

* Very classical web client
* Web Server
    * View
    * Controller
    * Service
    * DAO
* Database

# For developpers

### Tests js
The js tests doesn't compile themselfs [for now] (http://github.com/ValtechTechno/OneCalendar/issues/53), so to transpile those scripts

`coffee -o test/front/javascripts/ -w -c test/front/coffee/`

launch a browser on

`test/front/jasmine/SpecRunner.html`

### To launch the application
* Have to installed somewhere play2
* Have it in your path
* Have mongo2 started
    * `mongod --dbpath ./data --noprealloc --smallfiles --nojournal --rest`
* To initialize the database
    * connect to mongo and load the datas
    * `mongo OneCalendar test/data/icalstreams.js`
* to launch the application, as any play app
    * `play`
    * `$ compile`
    * `$ test`
    * `$ run`