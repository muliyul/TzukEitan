package db;

import java.time.LocalDate;
import java.util.concurrent.Future;

import launchers.EnemyLauncher;
import launchers.IronDome;
import launchers.LauncherDestructor;
import missiles.DefenseDestructorMissile;
import missiles.EnemyMissile;
import model.War;

/**
 * Class representing a database connection.
 * @author Muli
 *
 */
public interface DBConnection {
    
    public Future<Boolean> checkWarName(String warName);

    public void addNewWar(War w);

    public void endWar(War w);

    public void addLauncher(EnemyLauncher l);

    public void addMissile(EnemyMissile m);

    public void interceptMissile(EnemyMissile m, IronDome id);

    public void interceptLauncher(EnemyLauncher l, DefenseDestructorMissile defenseDestructorMissile);

    public void addIronDome(IronDome id);

    public void addLauncherDestructor(LauncherDestructor ld);

    public Future<String[]> getWarNamesByDate(LocalDate startDate, LocalDate endDate);
    
    public Future<long[]> getWarStats(String warName);
    
    public void closeDB();
}
