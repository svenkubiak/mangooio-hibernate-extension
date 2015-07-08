package de.svenkubiak.mangooio.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.svenkubiak.mangooio.models.Person;
import mangoo.io.test.MangooTestInstance;
import mangoo.io.test.MangooUnit;

public class DataStoreTest extends MangooUnit {
    private static DataStore dataStore;

    public DataStoreTest() {
        dataStore = MangooTestInstance.IO.getInjector().getInstance(DataStore.class);
    }

    @Before
    public void init() {
        dataStore.truncateTable("Person");
    }

    @Test
    public void testInstert() {
        Person person = new Person("Pew", "Poh");
        dataStore.save(person);

        Person p = dataStore.findOne("FROM Person p WHERE p.firstname = 'Pew'");

        assertNotNull(p);
    }

    @Test
    public void testDelete() {
    }

    @Test
    public void testFind() {
        Person a = new Person("foo", "bar");
        dataStore.save(a);

        Person b = new Person("foo", "bar");
        dataStore.save(b);

        List<Person> persons = dataStore.find("FROM Person");

        assertNotNull(persons);
        assertEquals(2, persons.size());
    }

    @Test
    public void testFindOne() {
        Person a = new Person("foo", "bar");
        dataStore.save(a);

        Person b = dataStore.findOne("FROM Person p WHERE p.firstname = 'foo'");

        assertNotNull(b);
    }

    @Test
    public void testGetSession() {
        assertNotNull(dataStore.getSession());
    }
}