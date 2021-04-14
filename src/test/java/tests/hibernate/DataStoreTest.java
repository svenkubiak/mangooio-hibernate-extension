package tests.hibernate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.svenkubiak.mangooio.hibernate.DataStore;
import io.mangoo.core.Application;
import io.mangoo.test.TestRunner;
import tests.models.Person;
import tests.models.submodels.Car;

/**
 *
 * @author svenkubiak
 *
 */
@ExtendWith({TestRunner.class})
@SuppressWarnings("rawtypes")
public class DataStoreTest {
    private static DataStore dataStore;

    @BeforeAll
    public static void init() {
        dataStore = Application.getInstance(DataStore.class);
    }

    @Test
    public void testInstert() {
        dataStore.truncateTable("Person");
        final Person person = new Person("Pew", "Poh");
        dataStore.save(person);

        final Optional result = dataStore.findOne("FROM Person p WHERE p.firstname = 'Pew'");

        assertTrue(result.isPresent());
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

        assertTrue(b.isPresent());
    }
    
    @Test
    public void testSubPackage() {
        dataStore.truncateTable("Car");
        final Car c = new Car("Tesla");
        dataStore.save(c);

        final Optional car = dataStore.findOne("FROM Car c WHERE c.name = 'Tesla'");

        assertTrue(car.isPresent());
    }

    @Test
    public void testGetSession() {
        assertNotNull(dataStore.getSession());
    }
}