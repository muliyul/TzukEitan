package db.jpa;

import java.util.concurrent.Semaphore;

import javax.persistence.EntityManager;

import db.DBTask;

public class PersistObjectTask extends DBTask<Void> {
    private Object toPersist;

    public PersistObjectTask(Semaphore s, Object c, Object toPersist) {
	super(s, c, null);
	this.toPersist = toPersist;
    }

    @Override
    public Void call() throws Exception {
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
