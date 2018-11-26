[![Maven Central](https://img.shields.io/maven-central/v/de.svenkubiak/mangooio-hibernate-extension.svg)](http://search.maven.org/#search|ga|1|mangooio-hibernate-extension)
[![Travis Build Status](https://travis-ci.org/svenkubiak/mangooio-hibernate-extension.svg?branch=master)](http://travis-ci.org/svenkubiak/mangooio-hibernate-extension)



Hibernate extension for the mangoo I/O framework
=====================
This is an easly plugable extension for the mangoo I/O framework to work with Hibernate.

Requires Java 11.

Setup
-----

1) Add the mangooio-hibernate-extension dependency to your pom.xml:
```
	<dependency>
 		<groupId>de.svenkubiak</groupId>
		<artifactId>mangooio-hibernate-extension</artifactId>	
		<version>x.x.x</version>	
	</dependency>
```
2) Configure Hibernate in your mangoo I/O config.props, e.g.
```	
	[hibernate]
        models = de.svenkubiak.mangooio.models
        hbm2ddl.auto = create-drop
        connection.driver_class = org.apache.derby.jdbc.EmbeddedDriver
        connection.url = jdbc:derby:memory/derbydb
        connection.username = username 
        connection.password = password
        dialect = org.hibernate.dialect.DerbyTenSevenDialect
        current_session_context_class = thread
```       
3) Inject the DataStore where needed
```
    @Inject
    DataStore dataStore;
```

Usage
-----
1) Using wrapper methods
```
    Person person = new Person();
    
    dataStore.save(person);
    dataStore.update(person); 
    dataStore.saveOrUpdate(person);
```
2) Using find methods
```
    Person p = dataStore.findOne("FROM Person p WHERE p.firstname = 'Foo'");
```    
3) Using Hibernate Criteria
```
    Session session = dataStore.getSession();
    
    Criteria critera = session.createCriteria(Person.class);
    criteria.add(Restrictions.eq("firstname", "Foo"));
    Person p = (Person) critera.uniqueResult();
```
