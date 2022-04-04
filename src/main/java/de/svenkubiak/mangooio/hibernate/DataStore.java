package de.svenkubiak.mangooio.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.mangoo.core.Config;

/**
 *
 * @author svenkubiak
 *
 */
@Singleton
@SuppressWarnings("rawtypes")
public class DataStore {
    private static final Logger LOG = LoggerFactory.getLogger(DataStore.class);
    private static final String CONFIG_PREFIX = "hibernate";
    private static final String PACKAGE = CONFIG_PREFIX + ".models";
    private final SessionFactory sessionFactory;

    @Inject
    public DataStore(Config config) {
        final Configuration configuration = new Configuration();
        for (final Entry<String, String> entry : config.getAllConfigurations().entrySet()) {
            if (entry.getKey().startsWith(CONFIG_PREFIX)) {
                configuration.setProperty(entry.getKey(), entry.getValue());
            }
        }

        List<Class> classes = new ArrayList<>();
        try (ScanResult scanResult =
                new ClassGraph()
                    .enableAnnotationInfo()
                    .enableClassInfo()
                    .acceptPackages(config.getString(PACKAGE))
                    .scan()) {
            scanResult.getClassesWithAnnotation("jakarta.persistence.Entity").forEach(c -> classes.add(c.loadClass()));
        }
        
        for (final Class clazz : classes) {
            configuration.addAnnotatedClass(clazz);
        }

        this.sessionFactory = configuration
                .buildSessionFactory(new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build());
    }

    /**
     * Creates a new session to the database
     *
     * @return A new session object
     */
    public Session getSession() {
        return this.sessionFactory.openSession();
    }

    /**
     * Retrieves multiple rows from the database
     *
     * @param hqlQuery The query to execute
     * @param clazz The resulting class
     * @param <T> T just ignore this
     * 
     * @return A list of objects from the database or an empty list if none found
     */
    @SuppressWarnings("unchecked")
    public <T> T find(String hqlQuery, Class<?> clazz) {
    	Objects.requireNonNull(hqlQuery, "hqlQuery can not be null");
    	Objects.requireNonNull(clazz, "clazz can not be null");
    	
        Session session = this.sessionFactory.openSession();
        try {
            return (T) session.createQuery(hqlQuery, clazz).getResultList();
        } catch (HibernateException e) {
            LOG.error("Failed to execute find query: " + hqlQuery, e);
        } finally {
            session.close();
        }

        return (T) new ArrayList<T>();
    }

    /**
     * Retrieves a single row from the database
     * 
     * @param hqlQuery The query to execute
     * @param clazz The resulting class
     * 
     * @return Optional, containing a single row object or null if none found
     */
    public Optional findOne(String hqlQuery, Class<?> clazz) {
    	Objects.requireNonNull(hqlQuery, "hqlQuery can not be null");
    	Objects.requireNonNull(clazz, "clazz can not be null");
    	
        Session session = this.sessionFactory.openSession();
        try {
            return session.createQuery(hqlQuery, clazz).uniqueResultOptional();
        } catch (HibernateException e) {
            LOG.error("Failed to execute find query: " + hqlQuery, e);
        } finally {
            session.close();
        }

        return Optional.empty();
    }

    /**
     * Saves an object to the database. This method is
     * transactional, meaning that any exception during persistence
     * leads to a rollback.
     *
     * @param object The object to save
     */
    public void save(Object object) {
    	Objects.requireNonNull(object, "object can not be null");
    	
        Session session = this.sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOG.error("Failed to persist entity", e);
        } finally {
            session.close();
        }
    }

    /**
     * Updates an object to the database. This method is transactional, meaning
     * that any exception during persistence leads to a rollback.
     *
     * @param object The object to save
     */
    public void update(Object object) {
    	Objects.requireNonNull(object, "object can not be null");
    	
        Session session = this.sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOG.error("Failed to persist entity", e);
        } finally {
            session.close();
        }
    }

    /**
     * Deletes an object to the database. This method is
     * transactional, meaning that any exception during persistence
     * leads to a rollback.
     *
     * @param object The object to save
     */
    public void delete(Object object) {
    	Objects.requireNonNull(object, "object can not be null");
    	
        Session session = this.sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.remove(object);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOG.error("Failed to delete entity", e);
        } finally {
            session.close();
        }
    }

    /**
     * Truncates a table with a given name
     *
     * @param name The name of the table
     */
    public void truncateTable(String name){
    	Objects.requireNonNull(name, "name can not be null");
    	
        Session session = this.sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.createMutationQuery(String.format("DELETE FROM %s", name)).executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOG.error("Failed to delete entity", e);
        } finally {
            session.close();
        }
    }
}