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
    save({ "url" : "http://www.agendadulibre.org/ical.php?region=12", "streamTags" : ["libre"] });
    save({ "url" : "http://www.google.com/calendar/ical/qde8vmooe48vsm1ma3i9je88q8%40group.calendar.google.com/public/basic.ics", "streamTags" : ["jug","java"] });



    //scala
    save({ "url" : "http://www.google.com/calendar/ical/37brrdprc60e70e9pdal7mmtus%40group.calendar.google.com/public/basic.ics", "streamTags" : ["scala", "PSUG"] });

    //Agile
    save({ "url" : "http://www.meetup.com/Agile-Coach-Camp-France/events/ical/Agile+Coach+Camp+France/fead.ics", "streamTags" : ["Development","Agile Methods","Lean Software Development","Agile Coaching","Agile Software","Agile Project Management","Agile Software Development"] });
    save({ "url" : "http://www.meetup.com/AGILE-NET-FRANCE/events/ical/AGILE+.NET+FRANCE/ical.ics", "streamTags" : ["Agile Software","Méthode Agile","Agile Methods","Microsoft .NET Technologies","Agile Software Development","Windows Azure Platform","Open ALM - Application Lifecycle Management","Microsoft Technology","Agile Coaching","Product management in agile","SQL Server Azure","Microsoft .Net User Group","Agile Games"] });
    save({ "url" : "http://www.meetup.com/Agile-Play-Ground/events/ical/Agile+Play+Ground/", "streamTags" : ["Agile Software","Software Development","Agile Software Development","Lean Software Development","Scrum Master","Scrum Development","Agilité et jeux"] });
    save({ "url" : "http://www.meetup.com/paris-software-craftsmanship/events/ical/Paris+Software+Craftsmanship+Community/", "streamTags" : ["Software Development","Software Craftsmanship","Test Driven Development","Agile Software Development","Software Developers","Programming","Domain Driven Design","Design Patterns","Behavior Driven Development","agile testing"] });
    save({ "url" : "http://www.meetup.com/lean-startup-france/events/ical/Lean+Startup+France/", "streamTags" : ["Agile Project Management","Agile Software","Business Strategy and Networking","Entrepreneur","Startup Ventures","Entrepreneurship","Lean Software Development","Lean Startup","Customer Development"] });
    save({ "url" : "http://www.meetup.com/frenchsug/events/ical/French+Scrum+User+Group/", "streamTags" : ["Scrum ","Agile Software"] });
    save({ "url" : "http://www.meetup.com/Lean-Startup-User-Group/events/ical/Lean+Startup+Paris/", "streamTags" : ["Internet Professionals","Web Technology","Lean Startup","Technology Startups","MVP","Entrepreneur"] });
    save({ "url" : "http://agenda-agile.org/ical.php?region=all", "streamTags" : ["agile"] });

    //js
    save({ "url" : "http://www.meetup.com/EmberJS-Paris/events/ical/EmberJS-Paris/", "streamTags" : ["Web Standards","JavaScript","Web Development","Programming","HTML5","Ember JS","Rich Internet"] });
    save({ "url" : "http://www.meetup.com/Sencha-Paris/events/ical/Sencha+Paris/", "streamTags" : ["Web Standards","Sencha Touch","Web Technology","Web Development","Sencha","Sencha Ext JS","Sencha ExtJS","JavaScript","PhoneGap","Mobile Web","mobile applications"] });

    //GPU
    save({ "url" : "http://www.meetup.com/HPC-GPU-Supercomputing-Group-of-Paris-Meetup/events/ical/HPC+%26+GPU+Supercomputing+Group+of+Paris+Meetup/", "streamTags" : ["High Performance Computing","HPC","High Scalability Computing","Big Data","GPU","GPU Programming","Cloud Computing","Distributed Systems","distributed computing","Supercomputing","Parallel Programming","CUDA: Compute Unified Device Architecture","OpenCL","Open Source","Software Developers"] });
    save({ "url" : "http://www.meetup.com/OpenStack-France/events/ical/OpenStack+France/", "streamTags" : ["Cloud Computing","Web Development","Open Source","Cloud Storage","OpenStack","Building private cloud with OpenStack","OpenStack - Quantum","Cloud Networking"] });
    save({ "url" : "http://www.meetup.com/Paris-Cloud-Computing-Group/events/ical/Paris+Cloud+Computing+Group/", "streamTags" : ["High Scalability Computing","SaaS and Cloud Computing","Virtualization and Cloud Computing","SaaS: Software as a Service","Amazon Web Services","Web Technology","Cloud Computing","IAAS and Cloud Computing","IaaS: Infrastructure as a Service","Open Source","OpenStack","PaaS and Cloud Computing","PaaS: Platform as a Service","Cloud","System Adminstrators"] });
    save({ "url" : "http://www.meetup.com/Paris-Salesforce-Developer-User-Group/events/ical/Paris+Salesforce+Developer+User+Group/", "streamTags" : ["Software Developers","Cloud Computing","Programming","Mobile Development","Heroku","PaaS and Cloud Computing","mobile application development","Salesforce.com Developers","Force.com","Apex Development","Apex"] });

    //Groovy
    save({ "url" : "http://www.meetup.com/Paris-Groovy-Grails/events/ical/Paris+Groovy+and+Grails+User+Group/", "streamTags" : ["Groovy Programming Language","Grails"] });

    //ruby
    save({ "url" : "http://meetup.rubyparis.org/events/ical/Paris.rb/", "streamTags" : ["Programming","Agile Software","Ruby On Rails","Ruby","Open Source","Freelances à Paris","Freelance"] });

    save({ "url" : "http://meetup.paug.fr/events/ical/Paris+Android+User+Group/", "streamTags" : ["Android Development", "mobile applications", "Android App Marketing", "Android Enthusiasts", "Android Fans and Users", "Google", "Android Application Development", "Mobile Social", "Mobile Development", "Mobile Technology", "Android developers", "Software Developers", "Java", "Google Technology User Group", "android"] });
    save({ "url" : "http://www.meetup.com/Paris-Android-LiveCode/events/ical/Paris+Android+LiveCode/", "streamTags" : ["Mobile Technology", "android", "Android Development", "Android Enthusiasts", "Java", "Android developers", "Android Application Development", "Android SDK", "Android Fans and Users", "mobile applications", "Eclipse IDE"] });
    save({ "url" : "http://www.meetup.com/Corona-SDK-Paris-Meetup/events/ical/Corona+SDK+-+Paris+Meetup/", "streamTags" : ["Software Developers", "Corona SDK", "Programming", "Mobile Technology", "Mobile Development", "mobile applications", "iOS Development", "iPad Developers", "Android developers", "Android Application Development", "Lua Programming", "iPhone App Developers"] });
    save({ "url" : "http://www.meetup.com/paris-titanium/events/ical/Paris+Titanium/", "streamTags" : ["Développer des apps iPhone avec Titanium", "Développer des apps Android avec Titanium", "Développer des apps Blackberry avec Titanium", "Utiliser ses comp. web pour créer des apps mobiles", "Développement d'applications mobiles", "Appcelerator Titanium", "JavaScript", "Développeurs iPhone", "Développeurs Android", "Développement d'applications iPad"] });
    save({ "url" : "http://www.meetup.com/gdg-paris/events/ical/GDG+Paris+-+Google+Developer+Group/", "streamTags" : ["Java", "Android developers", "Google Technology User Group", "Google Web Toolkit (GWT)", "HTML5", "Google", "Google Chrome", "Google Apps Script", "App Engine"] });

    save({ "url" : "http://www.meetup.com/altnetfr/events/ical/Alt.Net+France/", "streamTags" : ["ALT.NET", "Agile Software Development", ".NET", "C#", "Web Development", "Test Driven Development", "Microsoft", "Programming", "Software Craftsmanship"] });
    save({ "url" : "http://www.meetup.com/windowsapps/events/ical/Windows+Apps/", "streamTags" : ["Mobile Technology", "Silverlight", "mobile applications", "C#", "Windows Phone", "XAML", "HTML5", "Windows", ".NET", "Windows Mobile", "Windows 8 Development", "Windows 8 Metro App Development", "Windows Phone Developers", "Windows Phone 7", "Mobile Development"] });
    save({ "url" : "http://www.meetup.com/Cassandra-Paris-Meetup/events/ical/Cassandra+Paris+Meetup/", "streamTags" : ["Data Analytics", "Mobile Development", "Web Development", "Mobile Technology", "Cassandra", "Big Data", "NoSQL", "hadoop", "Cloud Computing", "Java", "Open Source", "High Scalability Computing", "Programming", "Software Developers", "Apache Lucene and Solr Open Source Search"] });
    save({ "url" : "http://meetupparis.hackshackers.com/events/ical/Hacks-Hackers+Paris/", "streamTags" : ["HacksandHackers"] });
    save({ "url" : "http://www.meetup.com/78-Hackers/events/ical/78+Hackers/", "streamTags" : ["PHP", "Open Source", "Python", "JavaScript", "Web Technology", "JQuery", "Programming", "Programming Languages", "Functional Programming", "Machine Learning", "NLP/Neurolinguistic Programming", "Science and Scientific Research and Technology", "HTML5", "Artificial Intelligence Applications", "mongoDB"] });

    save({ "url" : "http://www.meetup.com/ParisHackers/events/ical/ParisHackers/", "streamTags" : ["Social Networking", "Professional Networking", "Self-Improvement", "Business Strategy and Networking", "Meeting New People", "Small Business", "New Technology", "Internet & Technology", "Programming", "Web Technology", "Software Developers", "Entrepreneur", "Android developers", "Mobile Technology", "Mobile Development"] });
    save({ "url" : "http://www.meetup.com/ParisEmbedded/events/ical/ParisEmbedded/", "streamTags" : ["Programming", "Hacking", "Makerspaces", "Arduino", "New Technology", "Linux", "Makers", "Software Developers", "hackerspaces", "Microcontrollers", "Hardware Engineering", "electronics", "Embedded Systems Programming"] });
    save({ "url" : "http://www.meetup.com/Paris-MongoDB-User-Group/events/ical/Paris+MongoDB+User+Group/", "streamTags" : ["mongoDB", "NoSQL", "Programming", "Open Source", "Ruby", "Python", "PHP"] });
    save({ "url" : "http://www.meetup.com/Paris-Datageeks/events/ical/Paris+Datageeks/", "streamTags" : ["Linked Data", "Machine Learning", "Natural Language Processing", "Big Data", "Semantic Web", "hadoop", "NoSQL"] });
    save({ "url" : "http://www.meetup.com/Paris-Riak-Meetup/events/ical/Paris+Riak+Meetup/", "streamTags" : ["Database Professionals", "Erlang Programming", "Programming", "Web Development", "Web Technology", "Internet Professionals", "New Technology", "Software Developers", "Riak", "NoSQL", "MapReduce", "Cloud Computing"] });

    save({ "url" : "http://www.meetup.com/graphdb-france/events/ical/Graph+Database+-+Paris/", "streamTags" : ["Data Analytics", "Data Visualization", "Neo4j", "Data", "Graph Databases", "Big Data", "NoSQL"] });
    save({ "url" : "http://www.meetup.com/BeMyAppfrance/events/ical/BeMyApp+WeekEnds+France/", "streamTags" : ["mobile applications", "Programming", "iPhone Development", "iPhone App Developers", "Internet & Technology", "hackathon", "Web Design", "Web Designers and Developers", "Android developers", "iPhone developers", "Windows Mobile", "HTML5", "Startup & Enterprenurs", "iOS Development"] });
    save({ "url" : "http://www.meetup.com/web-new-media-paris/events/ical/The+Web+%26+New+Media+Professionals/", "streamTags" : ["Interaction Design", "Internet Professionals", "E-Business Owners", "Marketing", "Entrepreneur", "Web Design", "Social Media Marketing", "Web Startups", "Writers, Artists & Musicians", "Internet Marketing", "Graphical User Interfaces", "Web Communications", "Web Technology", "Blogger", "Business Strategy and Networking"] });
    save({ "url" : "http://www.meetup.com/push-realtime/events/ical/Push+and+realtime+on+mobile+and+web/", "streamTags" : ["Software Developers", "New Technology", "Mobile Technology", "iPhone developers", "Android developers", "Push Notifications", "XMPP", "Mobile Development", "Real-Time Web"] });
    save({ "url" : "http://www.meetup.com/France-HTML5-User-Group/events/ical/France+HTML5+User+Group/", "streamTags" : ["Internet y tecnología", "Mobile Development", "Web Standards", "Mobile Technology", "Internet Marketing", "New Technology", "Open Source", "Web Design", "HTML5", "CSS", "JavaScript", "Web Technology", "css3", "HTML", "Web Development"] });

    //Company
    save({ "url" : "http://www.sqli.com/fre/sqlical/show", "streamTags" : ["sqli"] });

    save({ "url" : "http://www.meetup.com/gsummitx-France/events/ical/gsummitx+-+Gamification+in+Paris/", "streamTags" : ["Gamification","Gaming","Social Gaming","Engagement","Customer Loyalty & Rewards Programs","Gabe Zichermann","Human Resources","Web Technology","Game Development","Game Design","Business Strategy","Mobile Technology","Paris","Internet Marketing","Social Media Marketing"] });
    save({ "url" : "http://www.meetup.com/SemanticCampParis/events/ical/SemanticCamp+Paris/", "streamTags" : ["Linked Data","SPARQL","Web Development","Semantic Web","Web Standards","Collaboration & Knowledge Sharing","Internet & Technology","Knowledge Management"] });
    save({ "url" : "http://www.meetup.com/Wordpress-Developers-Paris/events/ical/Paris+Wordpress+Developer+Meetup/", "streamTags" : ["Web Design","Search Engine Optimization","WordPress Users","Wordpress Customization","Wordpress Help","PHP","WordPress Small Business Web Sites","WordPress","Web Content Management Systems","WordPress as a Content Management System (CMS)","Blog"] });
    save({ "url" : "http://www.meetup.com/Paris-Arduino/events/ical/Paris+Arduino/", "streamTags" : ["Makerspaces","Hacking","Microcontrollers","hackerspaces","Artificial Intelligence","electronics","Do It Yourself","Robotics","Arduin","Arduino","Arduino and Robotics","Arduino Netduino and Robotics","Arduino Uno","Programming"] });
    save({ "url" : "http://www.meetup.com/ParisAPI/events/ical/Paris+API/", "streamTags" : ["Open Source","Software Developers","Web Development","Web Application","APIs","Web & Mobile development","Developers"] });
    save({ "url" : "http://www.meetup.com/Paris-iOS-LiveCode/events/ical/Paris+iOS+LiveCode/", "streamTags" : ["iPhone Development","iPhone App Developers","Mobile Development","iPad Developers","iOS","iOS Development","IPhone IPad IOS developers","iPhone developers","LiveCode","Objective C","Xcode Users","Apple Mobile Device Application Development","mobile applications","Mobile Apps"] });
    save({ "url" : "http://www.meetup.com/PhoneGap-Paris/events/ical/PhoneGap+Paris/", "streamTags" : ["Open Source","JavaScript","Mobile Technology","PhoneGap","Sencha Touch","Android developers","iPhone developers","Bada developers","HTML5"] });
    save({ "url" : "http://www.meetup.com/Django-Paris/events/ical/Django+Paris/", "streamTags" : ["Web Design","Software Developers","Internet Professionals","Django","Programming","Python","Web Development","Web Technology"] });
    save({ "url" : "http://www.meetup.com/drupal-france-francophonie/events/ical/Drupal+France+et+Francophonie/", "streamTags" : ["Programming","Web Content Management Systems","Learning Drupal","Drupal","Drupal themers","Drupal Developers","Drupal Designers","Drupal Users","PHP","CMS"] });

    //save({ "url" : "", "streamTags" : [""] });
}