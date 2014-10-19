package db.jpa;

import java.util.concurrent.Semaphore;

import javax.persistence.EntityManager;

import db.DBTask;

public class MergeObjectTask extends DBTask<Void> {
    private Object toMerge;

    public MergeObjectTask(Semaphore s, Object c, Object toMerge) {
	super(s, c, null);
	this.toMerge = toMerge;
    }

    @Override
    public Void call() throws Exception {
	EntityManager em = (EntityManager) connection;
	try {
	    executer.acquire();
	    em.getTransaction().begin();
	    em.merge(toMerge);
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
