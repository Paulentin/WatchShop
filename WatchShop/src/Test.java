import ua.nure.zabara.shop.database.DBManager;
import ua.nure.zabara.shop.database.entity.Watch;

import java.sql.SQLTransactionRollbackException;
import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        DBManager dbMan=DBManager.getInstance();
        Watch w1=new Watch();
        w1.setWatchName("EVGENSNAPPINK");
        w1.setType("mechanical");
        w1.setPrice(500);
        w1.setManufacturer_id(1);
        Watch w2 =new Watch();

        try {
            dbMan.insertManyWtchs(new ArrayList<>(Arrays.asList(w1,w2)));
        } catch (SQLTransactionRollbackException e) {
            e.printStackTrace();
        }


    }
}
