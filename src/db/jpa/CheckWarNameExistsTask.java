package db.jpa;

import java.util.concurrent.Semaphore;

import javax.persistence.EntityManager;

import model.War;
import db.DBTask;

public class CheckWarNameExistsTask extends DBTask<Boolean> {

    public CheckWarNameExistsTask(Semaphore s, Object c, String warName) {
	super(s, c, warName);
    }

    @Override
    public Boolean call() throws Exception {
	EntityManager em = (EntityManager)connection;
        return em.find(War.class, warName) != null;
    }
}
