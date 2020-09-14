package me.oska.plugins.openjpa;

import me.oska.plugins.logger.Logger;
import me.oska.plugins.openjpa.action.FindOrCreateCallback;
import me.oska.plugins.openjpa.async.AsyncCallBackExceptionHandler;
import me.oska.plugins.openjpa.async.AsyncCallBackList;
import me.oska.plugins.openjpa.async.AsyncCallBackObject;
import me.oska.plugins.openjpa.exception.EntityManagerNotInitializedException;
import me.oska.plugins.openjpa.exception.EntityNotFoundException;
import me.oska.plugins.openjpa.exception.RunicException;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class AbstractRepository<T> {
    private static Logger log = new Logger("AbstractRepository");
    private Class<T> entityClass;
    private static EntityManagerFactory entityManagerFactory;

    private static int DATABASE_PORT = 5432;
    private static String DATABASE_HOST = "localhost";
    private static String DATABASE_USERNAME = "postgres";
    private static String DATABASE_PASSWORD = "oskang09";
    private static String DATABASE_SCHEMA = "oskarpg";
    private static String getConnectionString() {
        return "jdbc:postgresql://" + DATABASE_HOST + ":" + DATABASE_PORT + "/" + DATABASE_SCHEMA;
    }

    // Native ( PostgreSQL ) : Listen to channel
    public static Runnable listen(String channel, long intervalInMilli, Consumer<PGNotification> consumer) throws SQLException {
        Connection connection =  DriverManager.getConnection(getConnectionString(), DATABASE_USERNAME, DATABASE_PASSWORD);
        PGConnection postgres = (PGConnection) connection;
        Statement stmt = connection.createStatement();
        stmt.execute("LISTEN " + channel);
        stmt.close();

        Runnable action = () -> log.withTracker("POSTGRESQL LISTEN - " + channel, () -> {
            while(true) {
                Statement read = connection.createStatement();
                read.execute("SELECT 1");
                read.close();
                Arrays.stream(postgres.getNotifications()).forEach(consumer);
                Thread.sleep(intervalInMilli);
            }
        });
        Thread thread = new Thread(action);
        thread.start();
        return () -> thread.interrupt();
    }

    public static void setupRepository() {
        new AbstractRepository();
    }

    private AbstractRepository() {
        setupEntityManagerFactory();
    }

    public AbstractRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public synchronized void create(T entity) throws RunicException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.persist(entity);

        entityTransaction.commit();
        entityManager.close();
    }

    public void createAsync(T entity, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler) {
        new Thread(() -> {
            try {
                create(entity);
            } catch (RunicException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    public synchronized T edit(T entity) throws RunicException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.merge(entity);

        entityTransaction.commit();
        entityManager.close();
        return entity;
    }

    public void editAsync(T entity, AsyncCallBackObject<T> asyncCallBackObject, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler) {
        new Thread(() -> {
            try {
                asyncCallBackObject.done(edit(entity));
            } catch (RunicException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    public synchronized void remove(T entity) throws RunicException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.remove(entityManager.merge(entity));

        entityTransaction.commit();
        entityManager.close();
    }

    public void removeAsync(T entity, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler) {
        new Thread(() -> {
            try {
                remove(entity);
            } catch (RunicException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    public synchronized T findOrCreate(Object id, FindOrCreateCallback<T> focc) throws RunicException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            entity = focc.entity();
            create(entity);
        }
        return entity;
    }

    public void findOrCreateAsync(Object id, FindOrCreateCallback<T> focc, AsyncCallBackObject<T> asyncCallBackObject, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler) {
        new Thread(() -> {
            try {
                asyncCallBackObject.done(findOrCreate(id, focc));
            } catch (RunicException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    public synchronized T find(Object id) throws RunicException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        T e = entityManager.find(entityClass, id);

        entityTransaction.commit();
        entityManager.close();
        if (e == null) {
            throw new EntityNotFoundException(getClass());
        }
        return e;
    }

    public void findAsync(Object id, AsyncCallBackObject<T> asyncCallBackObject, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler) {
        new Thread(() -> {
            try {
                asyncCallBackObject.done(find(id));
            } catch (RunicException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    public synchronized List<T> findAll() throws RunicException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        CriteriaQuery<T> cq = entityManager.getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        List<T> e = entityManager.createQuery(cq).getResultList();

        entityTransaction.commit();
        entityManager.close();
        return e;
    }

    public void findAllAsync(AsyncCallBackList<T> asyncCallBackObjectList, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler) {
        new Thread(() -> {
            try {
                asyncCallBackObjectList.done(findAll());
            } catch (RunicException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    public void namedQuerySingleAsync(String queryName, AsyncCallBackObject<T> asyncCallBackObject, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler,Object... arguments) {
        new Thread(() -> {
            try {
                asyncCallBackObject.done(namedQuerySingle(queryName, arguments));
            } catch (RunicException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    public synchronized T namedQuerySingle(String queryName, Object... arguments) throws RunicException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        TypedQuery<T> query = entityManager.createNamedQuery(queryName, entityClass);
        for (int i = 1; i <= arguments.length; i++ ) {
            query.setParameter(i, arguments[i-1]);
        }
        T result = query.getSingleResult();
        if (result == null) {
            throw new EntityNotFoundException(getClass());
        }

        entityTransaction.commit();
        entityManager.close();
        return result;
    }

    public void namedQueryListAsync(String queryName, AsyncCallBackList<T> asyncCallBackObject, AsyncCallBackExceptionHandler asyncCallBackExceptionHandler,Object... arguments) {
        new Thread(() -> {
            try {
                asyncCallBackObject.done(namedQueryList(queryName, arguments));
            }catch (RunicException e) {
                asyncCallBackExceptionHandler.error(e);
            }
        }).start();
    }

    public synchronized List<T> namedQueryList(String queryName, Object... arguments) throws RunicException {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        TypedQuery<T> query = entityManager.createNamedQuery(queryName, entityClass);
        for (int i = 1; i <= arguments.length; i++ ) {
            query.setParameter(i, arguments[i-1]);
        }
        List<T> lists = query.getResultList();

        entityTransaction.commit();
        entityManager.close();
        return lists;
    }

    protected EntityManager getEntityManager() throws RunicException {
        setupEntityManagerFactory();
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            return entityManagerFactory.createEntityManager();
        } else {
            throw new EntityManagerNotInitializedException();
        }
    }

    private void setupEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            Properties properties = new Properties();
            properties.put("openjpa.ConnectionDriverName", "org.postgresql.Driver");
            properties.put("openjpa.ConnectionURL", getConnectionString());
            properties.put("openjpa.ConnectionUserName", DATABASE_USERNAME);
            properties.put("openjpa.ConnectionPassword", DATABASE_PASSWORD);
            properties.put("openjpa.DynamicEnhancementAgent", true);
            properties.put("openjpa.RuntimeUnenhancedClasses", "supported");
            properties.put("openjpa.Log", "SQL=TRACE");
            properties.put("openjpa.ConnectionFactoryProperties", "PrintParameters=true");
            properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            entityManagerFactory = Persistence.createEntityManagerFactory("persistence-unit", properties);
        }
    }

    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
