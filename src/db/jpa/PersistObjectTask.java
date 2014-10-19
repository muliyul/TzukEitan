package db.jpa;

import java.util.concurrent.Semaphore;

import javax.persistence.EntityManager;
import db.DBTask;

public class PersistObjectTask extends DBTask<Void> {

    private Object toPersist;

    private PersistObjectTask(Semaphore s, EntityManager c) {
	super(s, c, null);
    }

    public PersistObjectTask(Semaphore s, EntityManager c, Object toPersist) {
	this(s, c);
	this.toPersist = toPersist;
    }

    @Override
    public Void call() {
	EntityManager em = (EntityManager) connection;
	try {
	    executer.acquire();
	    em.getTransaction().begin();
	    em.persist(toPersist);
	    em.getTransaction().commit();
	} catch (Exception e) {
	    e.printStackTrace();
	    em.getTransaction().rollback();
	} finally {
	    executer.release();
	}
	return null;
    }

}
