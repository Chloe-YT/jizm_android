package dao;

import java.util.List;

import pojo.Bill;
import pojo.Category;
import pojo.Periodic;

public interface PeriodicDAO {

    List<Periodic> listPeriodic();

    void addPeriodic(Periodic  periodic);

    void updatePeriodic(Periodic  periodic);

    void deletePeriodic(int id);

    List<Periodic> getAyncPeriodic();

    Long getMaxAnchor();

}
