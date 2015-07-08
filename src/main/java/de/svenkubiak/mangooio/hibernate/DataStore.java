package de.svenkubiak.mangooio.hibernate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import mangoo.io.configuration.Config;

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

    private SessionFactory sessionFactory;

    @Inject
    public DataStore(Config config) {
        Configuration configuration = new Configuration();
        for (Entry<String, String> entry : config.getAllConfigurations().entrySet()) {
            if (entry.getKey().startsWith(CONFIG_PREFIX)) {
                configuration.setProperty(entry.getKey(), entry.getValue());
            }
        }

        List<Class> classes = getClassesForPackage(config.getString(PACKAGE));
        for (Class clazz : classes) {
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
     * @return A list of objects from the database or an empty list if none found
     */
    @SuppressWarnings("unchecked")
    public <T> T find(String hqlQuery) {
        Session session = this.sessionFactory.openSession();
        try {
            return (T) session.createQuery(hqlQuery).list();
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
     * @return A single row object or null if none found
     */
    @SuppressWarnings("unchecked")
    public <T> T findOne(String hqlQuery) {
        Session session = this.sessionFactory.openSession();

        try {
            return (T) session.createQuery(hqlQuery).uniqueResult();
        } catch (HibernateException e) {
            LOG.error("Failed to execute find query: " + hqlQuery, e);
        } finally {
            session.close();
        }

        return null;
    }

    /**
     * Saves an object to the database. This method is
     * transactional, meaning that any exception during persistence
     * leads to a rollback.
     *
     * @param object The object to save
     */
    public void save(Object object) {
        Session session = this.sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.save(object);
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
        Session session = this.sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.delete(object);
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
        Session session = this.sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery(String.format("DELETE FROM %s", name));
            query.executeUpdate();
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
     * Get all model class for a specific package for entity mapping
     *
     * Source found at:
     * http://stackoverflow.com/questions/10910510/get-a-array-of-class-files-inside-a-package-in-java
     *
     * Yes!!!111ELF this is still necessary in Hibernate 4.x as there is no way of telling
     * hibernate where to find the annotated classes in a specific package. There is a
     * configuration method called addPackage() but it is not clear what it does and doesn't
     * do what it's name promises.
     *
     * See: http://stackoverflow.com/questions/28097847/hibernate-4-3-x-load-all-entity-annotated-classes
     *
     * @param pkgname The package name to scan in
     * @return A list of annotated entity classes
     */
    @SuppressWarnings("all")
    private List<Class> getClassesForPackage(String pkgname) {
        List<Class> classes = new ArrayList<Class>();

        File directory = null;
        String fullPath;
        String relPath = pkgname.replace('.', '/');
        URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);

        if (resource == null) {
            throw new RuntimeException("No resource for " + relPath);
        }
        fullPath = resource.getFile();

        try {
            directory = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
        } catch (IllegalArgumentException e) {
            directory = null;
        }

        if (directory != null && directory.exists()) {
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                if (files[i].endsWith(".class")) {
                    String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("ClassNotFoundException loading " + className);
                    }
                }
            }
        } else {
            JarFile jarFile = null;
            try {
                String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
                jarFile = new JarFile(jarPath);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                        String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
                        try {
                            classes.add(Class.forName(className));
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("ClassNotFoundException loading " + className);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(pkgname + " (" + directory + ") does not appear to be a valid package", e);
            } finally {
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        LOG.error("Failed to close jarFile", e);
                    }
                }
            }
        }

        return classes;
    }
}