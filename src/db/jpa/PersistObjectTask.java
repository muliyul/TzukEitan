package db.jpa;

import java.sql.Connection;
import java.util.concurrent.Semaphore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import db.DBTask;

public class PersistObjectTask extends DBTask<Void> {

    private Object[] toPersist;

    private PersistObjectTask(Semaphore s, Connection c) {
	super(s, c, null);
    }

    public PersistObjectTask(Semaphore s, Connection c, Object... toPersist) {
	this(s, c);
	this.toPersist = toPersist;
    }

    @Override
    public Void call() {
	EntityManagerFactory emf =
		Persistence.createEntityManagerFactory("YoniMuli");
	EntityManager em = emf.createEntityManager();

	em.getTransaction().begin();
	try {
	    for (Object o : toPersist)
		em.persist(o);
	    em.getTransaction().commit();
	} catch (Exception e) {
	    em.getTransaction().rollback();
	} finally {
	    em.close();
	}
	return null;
    }

}
