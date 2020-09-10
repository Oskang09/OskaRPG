package me.oska.plugins.openjpa;

import me.oska.plugins.openjpa.action.FindOrCreateCallback;
import me.oska.plugins.openjpa.async.AsyncCallBackExceptionHandler;
import me.oska.plugins.openjpa.async.AsyncCallBackList;
import me.oska.plugins.openjpa.async.AsyncCallBackObject;
import me.oska.plugins.openjpa.exception.EntityManagerNotInitializedException;
import me.oska.plugins.openjpa.exception.EntityNotFoundException;
import me.oska.plugins.openjpa.exception.RunicException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Properties;

public class AbstractRepository<T> {
    private Class<T> entityClass;
    private static EntityManagerFactory entityManagerFactory;

    private static int DATABASE_PORT = 5432;
    private static String DATABASE_HOST = "localhost";
    private static String DATABASE_USERNAME = "postgres";
    private static String DATABASE_PASSWORD = "oskang09";
    private static String DATABASE_SCHEMA = "oskarpg";

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

        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        List<T> e = getEntityManager().createQuery(cq).getResultList();

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
            properties.put("openjpa.ConnectionURL", "jdbc:postgresql://" + DATABASE_HOST + ":" + DATABASE_PORT + "/" + DATABASE_SCHEMA);
            properties.put("openjpa.ConnectionUserName", DATABASE_USERNAME);
            properties.put("openjpa.ConnectionPassword", DATABASE_PASSWORD);
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
