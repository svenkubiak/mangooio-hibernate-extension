[![Maven Central](https://img.shields.io/maven-central/v/de.svenkubiak/mangooio-hibernate-extension.svg)](http://search.maven.org/#search|ga|1|mangooio-hibernate-extension)
[![Travis Build Status](https://travis-ci.org/svenkubiak/mangooio-hibernate-extension.svg?branch=master)](http://travis-ci.org/svenkubiak/mangooio-hibernate-extension)
[![Flattr this repository](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=svenkubiak&url=https://github.com/svenkubiak/mangooio-hibernate-module&title=mangoo-mongodb-module&language=en&tags=github&category=software)



Hibernate extension for the mangoo I/O framework
=====================
This is an easly plugable extension for the mangoo I/O framework to work with Hibernate.

Setup
-----

1) Add the mangooio-hibernate-extension dependency to your pom.xml:

    <dependency>
        <groupId>de.svenkubiak</groupId>
        <artifactId>mangooio-hibernate-extension</artifactId>
        <version>x.x.x</version>
    </dependency>

2) Configure Hibernate in your mangoo I/O application.yaml, e.g.
	
    hibernate:
        models     : de.svenkubiak.mangooio.models
        hbm2ddl:
            auto            : create-drop
        connection:
            driver_class    : org.apache.derby.jdbc.EmbeddedDriver
            url             : jdbc:derby:memory/derbydb
            username        : 
            password        : 
        dialect     : org.hibernate.dialect.DerbyTenSevenDialect
        current_session_context_class : thread
        
3) Inject the DataStore where needed

	@Inject
	DataStore dataStore;