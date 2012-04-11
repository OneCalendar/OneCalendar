# One Calendar To Meet Them All


Le site : [onecalendar.valtech.fr](http://onecalendar.valtech.fr)

## Pour les développeurs


## Tests js
Les tests js ne se compile pas seuls, [pour l'instant](http://github.com/ValtechTechno/OneCalendar/issues/53), alors pour les compiler

`coffee -o test/front/javascripts/ -w -c test/front/coffee/`

lancer un navigateur sur

`test/front/jasmine/SpecRunner.html`

## Pour lancer l'application
* avoir play2 d'installer qq part
* avoir mongo2 de lancer
    * `mongod --dbpath ./data --noprealloc --smallfiles --nojournal --rest`
    * `play`
    * `$ compile`
    * `$ test`
    * `$ run`
