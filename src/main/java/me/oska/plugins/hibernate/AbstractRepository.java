package me.oska.plugins.hibernate;

import com.google.gson.Gson;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.hibernate.action.FindOrCreateCallback;
import me.oska.plugins.hibernate.async.AsyncCallBackExceptionHandler;
import me.oska.plugins.hibernate.async.AsyncCallBackList;
import me.oska.plugins.hibernate.async.AsyncCallBackObject;
import me.oska.plugins.hibernate.exception.EntityManagerNotInitializedException;
import me.oska.plugins.hibernate.exception.EntityNotFoundException;
import me.oska.plugins.hibernate.exception.RunicException;
import org.bukkit.plugin.java.JavaPlugin;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class AbstractRepository<T> {
    private static Logger log;
    private static EntityManagerFactory entityManagerFactory;
    private Class<T> entityClass;

    private static final int DATABASE_PORT = 5432;
    private static final String DATABASE_HOST = "localhost";
    private static final String DATABASE_USERNAME = "postgres";
    private static final String DATABASE_PASSWORD = "oskang09";
    private static final String DATABASE_SCHEMA = "oskarpg";
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

        AtomicBoolean atomic = new AtomicBoolean(true);
        Runnable action = () -> log.withTracker("POSTGRESQL LISTEN - " + channel, () -> {
            do {
                Statement read = connection.createStatement();
                read.execute("SELECT 1");
                read.close();

                PGNotification[] notifications = postgres.getNotifications();
                if (notifications != null) {
                    Arrays.stream(notifications).forEach(consumer);
                }

                Thread.sleep(intervalInMilli);
            } while (atomic.get());
        });

        Thread thread = new Thread(action);
        thread.start();
        return () -> {
            atomic.set(false);
            thread.interrupt();
        };
    }

    public static void register(JavaPlugin plugin) {
        log = new Logger("AbstractRepository");
        new AbstractRepository<>();
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
                if (asyncCallBackExceptionHandler != null) {
                    asyncCallBackExceptionHandler.error(e);
                }
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
                T newEntity = edit(entity);
                if (asyncCallBackObject != null) {
                    asyncCallBackObject.done(newEntity);
                }
            } catch (RunicException e) {
                if (asyncCallBackExceptionHandler != null) {
                    asyncCallBackExceptionHandler.error(e);
                }
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
                if (asyncCallBackExceptionHandler != null) {
                    asyncCallBackExceptionHandler.error(e);
                }
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
        for (int i = 1; i <= arguments.length; i++  ) {
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
            properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
            properties.put("hibernate.connection.url", getConnectionString());
            properties.put("hibernate.connection.username", DATABASE_USERNAME);
            properties.put("hibernate.connection.password", DATABASE_PASSWORD);
            properties.put("hibernate.dialect", "me.oska.plugins.hibernate.PostgresDialect");
            properties.put("hibernate.show_sql", true);
            properties.put("hibernate.format_sql", true);
            properties.put("hibernate.hbm2ddl.auto", "update");
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
