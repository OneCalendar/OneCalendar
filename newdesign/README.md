OneCalendar with Foundation
===========================

This piece of design has been produced with [yeoman]. You will need nodejs and yeoman.

    sudo npm install -g yeoman

Run
---

Start the local server:

    grunt server


Build
-----

To produce the static site, use:

    grunt

This will produce the files in `./dist`.

Deploy
------

Drop `./dist` in a simple web container !

Done
----

1. Generate classic webapp

        yo webapp:generator

1. Add foundation dependencies in `bower.json`

        "dependencies": {
          "modernizr": "~2.6.2",
          "foundation-icons": "master",
          "components-foundation": "~4.1.2"
        },

1. Implement design in `index.html`

  [yeoman]: http://yeoman.io

