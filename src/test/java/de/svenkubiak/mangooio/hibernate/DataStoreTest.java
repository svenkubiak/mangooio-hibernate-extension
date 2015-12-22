package de.svenkubiak.mangooio.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.svenkubiak.mangooio.models.Person;
import io.mangoo.test.Mangoo;

/**
 *
 * @author svenkubiak
 *
 */
public class DataStoreTest {
    private static DataStore dataStore;

    @BeforeClass
    public static void start() throws InterruptedException {
        dataStore = Mangoo.TEST.getInstance(DataStore.class);
    }

    @Before
    public void init() {
        dataStore.truncateTable("Person");
    }

    @Test
    public void testInstert() {
        final Person person = new Person("Pew", "Poh");
        dataStore.save(person);

        final Person p = dataStore.findOne("FROM Person p WHERE p.firstname = 'Pew'");

        assertNotNull(p);
    }

    @Test
    public void testDelete() {
    }

    @Test
    public void testFind() {
        final Person a = new Person("foo", "bar");
        dataStore.save(a);

        final Person b = new Person("foo", "bar");
        dataStore.save(b);

        final List<Person> persons = dataStore.find("FROM Person");

        assertNotNull(persons);
        assertEquals(2, persons.size());
    }

    @Test
    public void testFindOne() {
        final Person a = new Person("foo", "bar");
        dataStore.save(a);

        final Person b = dataStore.findOne("FROM Person p WHERE p.firstname = 'foo'");

        assertNotNull(b);
    }

    @Test
    public void testGetSession() {
        assertNotNull(dataStore.getSession());
    }
}