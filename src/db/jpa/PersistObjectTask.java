package db.jpa;

import java.sql.Connection;
import java.util.concurrent.Semaphore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import db.DBTask;

public class PersistObjectTask extends DBTask {

    private Object toPersist;

    public PersistObjectTask(Semaphore s, Connection c, String warName) {
	super(s, c, null);
    }

    public PersistObjectTask(Semaphore s, Connection c, String warName, Object toPersist) {
	super(s, c, warName);
	this.toPersist = toPersist;
    }

    @Override
    public void run() {
	EntityManagerFactory emf =
		Persistence.createEntityManagerFactory("YoniMuli");
	EntityManager em = emf.createEntityManager();

	em.getTransaction().begin();
	try {
	    em.persist(toPersist);
	    em.getTransaction().commit();
	} catch (Exception e) {
	    em.getTransaction().rollback();
	} finally {
	    em.close();
	}
    }

}
