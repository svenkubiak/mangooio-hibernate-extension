package tests.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de.svenkubiak.mangooio.hibernate.DataStore;
import io.mangoo.core.Application;
import tests.models.Person;

/**
 *
 * @author svenkubiak
 *
 */
@SuppressWarnings("rawtypes")
public class DataStoreTest {
    private static DataStore dataStore;

    @Before
    public void init() {
        dataStore = Application.getInstance(DataStore.class);
    }

    @Test
    public void testInstert() {
        dataStore.truncateTable("Person");
        final Person person = new Person("Pew", "Poh");
        dataStore.save(person);

        final Optional result = dataStore.findOne("FROM Person p WHERE p.firstname = 'Pew'");

        assertNotNull(result.get());
    }

    @Test
    public void testUpdate() {
        dataStore.truncateTable("Person");
        final Person a = new Person("foo", "bar");
        dataStore.save(a);

        a.setLastname("bar2");
        dataStore.update(a);

        final Optional b = dataStore.findOne("FROM Person p WHERE p.firstname = 'foo'");
        Person p = (Person) b.get();
        
        assertNotNull(p);
        assertEquals(a.getLastname(), p.getLastname());
    }

    @Test
    public void testInsertOrUpdate() {
        dataStore.truncateTable("Person");
        final Person a = new Person("foo", "bar");
        dataStore.saveOrUpdate(a);

        a.setLastname("bar2");
        dataStore.saveOrUpdate(a);

        final Optional b = dataStore.findOne("FROM Person p WHERE p.firstname = 'foo'");
        Person p = (Person) b.get();

        assertNotNull(p);
        assertEquals(a.getLastname(), p.getLastname());
    }

    @Test
    public void testFind() {
        dataStore.truncateTable("Person");
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
        dataStore.truncateTable("Person");
        final Person a = new Person("foo", "bar");
        dataStore.save(a);

        final Optional b = dataStore.findOne("FROM Person p WHERE p.firstname = 'foo'");

        assertNotNull(b.get());
    }

    @Test
    public void testGetSession() {
        assertNotNull(dataStore.getSession());
    }
}