package ua.nure.zabara.shop.database;


import org.apache.log4j.Logger;


import ua.nure.zabara.shop.database.entity.Customer;
import ua.nure.zabara.shop.database.entity.Manufacturer;
import ua.nure.zabara.shop.database.entity.Watch;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class DBManager {

    final static Logger LOG= Logger.getLogger(DBManager.class.getName());

    private final String connectionURL;

    ////////////////////////////////////////////////////
    //SQL_INSERT
    private static final String SQL_INSERT_CUSTOMER=
            "INSERT INTO Customer (idCustomer,fullname,discounts,password)VALUES(DEFAULT,?,?,?)";
    private static final String SQL_INSERT_MANUFACTURER=
           "INSERT INTO Manufacturer (idManufacturer,BrandName,Country)VALUES(DEFAULT,?,?)";
    private static final String SQL_INSERT_WATCH=
            "INSERT INTO Watch (idWatch,type,price,manufacturer,WatchName)VALUES(DEFAULT,?,?,?,?)";

    ////////////////////////////////////////////////////
    //SQL_GET
    private static final String SQL_GET_CUSTOMER_BY_ID ="SELECT * FROM Customer WHERE idCustomer = ?";
    private static final String SQL_GET_ALL_CUSTOMERS = "SELECT * FROM Customer";
    private static final String SQL_GET_MANUFACTURER_BY_ID = "SELECT  * FROM Manufacturer WHERE idManufacturer = ?" ;
    private static final String SQL_GET_ALL_MANUFACTURERS =  "SELECT * FROM  Manufacturer";
    private static final String SQL_GET_WATCH_BY_ID = "SELECT * FROM Watch WHERE idWatch = ?" ;
    private static final String SQL_GET_ALL_WATCHES = "SELECT * FROM Watch" ;
    private static final String SQL_GET_WATCH_BY_TYPE = "SELECT * FROM Watch WHERE type = ?";
    private static final String SQL_GET_WATCH_BY_TYPE_AND_PRICE =
            "SELECT * FROM Watch WHERE type=? AND ?<price AND price<?;";



    private DBManager(){
       Properties props = new Properties();
       try {
           props.load(new FileInputStream("db.properties"));
       } catch (IOException ex) {

           LOG.error("Cannot find app.properties!!!", ex);
           throw new IllegalStateException("Cannot find db.properties!!!", ex);
       }
       connectionURL =  props.getProperty("db.url");
   }
    /*
    * BillPughSingleton when DBManagerHelper is loaded into memory
    * only when someone calls getInstance so u don't need to synchronization
    */
    private static class DBManagerHelper{
        private static final DBManager INSTANCE=new DBManager();
    }

    public static DBManager getInstance(){
        return DBManagerHelper.INSTANCE;
   }
    //////////////////////////////////
    //End of singleton

    public Connection getConnection() throws SQLException {

        Connection con = DriverManager.getConnection(connectionURL);
        con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        return con;
   }

    private void close(AutoCloseable ac) {
        if (ac != null) {
            try {
                ac.close();
              //  logger.debug("connection closed ::"+ ac);
            } catch (Exception ex) {
                // (1) write to log
            }
        }

    }

    private void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                // (1) write to log
            }
        }

    }


//////////////////////////////////////////////////////////////////////////////
/**
 * Block of methods for insertion into database
 * */

    public boolean insertCustomer(Connection con,Customer cmr){
        boolean result=false;

        PreparedStatement psmnt;
        ResultSet rs=null;
        try {
            if(con.isClosed()){
                con=getConnection();
            }
            psmnt=con.prepareStatement(SQL_INSERT_CUSTOMER, Statement.RETURN_GENERATED_KEYS);
            int k=1;

            psmnt.setString(k++, cmr.getFullname());
            psmnt.setString(k++, cmr.getDiscounts());
            psmnt.setString(k++, cmr.getPassword());

            if (psmnt.executeUpdate() > 0) {
                rs = psmnt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    cmr.setId(id);
                    result = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(con);
            close(rs);

        }
        return result;
    }

    public boolean insertManyCustmrs(List<Customer> cmrs) throws SQLException {
        boolean result=true;
        Connection con=null;
        try {
            con=getConnection();
            con.setAutoCommit(false);

            for(Customer c:cmrs){
                result&=insertCustomer(con,c);

                if(result==false){
                    rollback(con);
                    return result;
                }
            }
            con.commit();
        }catch (SQLException ex){
            rollback(con);
            throw  new SQLTransactionRollbackException("couldnt ROLLBACK");
        }finally {
            con.setAutoCommit(true);
        }
        return result;
    }

    public boolean insertOneManufacturer(Connection con, Manufacturer man){


        boolean result=false;

        PreparedStatement psmnt;
        ResultSet rs=null;
        try {
            if(con.isClosed()){
                con=getConnection();
            }
            psmnt=con.prepareStatement(SQL_INSERT_MANUFACTURER, Statement.RETURN_GENERATED_KEYS);
            int k=1;

            psmnt.setString(k++, man.getBrandName());
            psmnt.setString(k++, man.getCountry());

            if (psmnt.executeUpdate() > 0) {
                rs = psmnt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    man.setId(id);
                    result = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(con);
            close(rs);

        }

        return result;

    }

    public boolean insertManyManufac(List<Manufacturer> mans) throws SQLTransactionRollbackException {
        boolean result=true;
        Connection con=null;
        try {
            con=getConnection();
            con.setAutoCommit(false);

            for(Manufacturer m:mans){
                result&=insertOneManufacturer(con,m);

                if(result==false){
                    rollback(con);
                    return result;
                }
            }
            con.commit();

        }catch (SQLException ex){
            rollback(con);
            throw new SQLTransactionRollbackException("couldnt ROLLBACK");
        }
        return result;
    }

    public boolean insertWatch(Connection con, Watch wch){


        boolean result=false;

        PreparedStatement psmnt;
        ResultSet rs=null;
        try {

            if(con.isClosed()){
                con=getConnection();
            }
            psmnt=con.prepareStatement(SQL_INSERT_WATCH, Statement.RETURN_GENERATED_KEYS);
            int k=1;

            psmnt.setString(k++, wch.getType());
            psmnt.setDouble(k++, wch.getPrice());
            psmnt.setInt(k++, wch.getManufacturer_id());
            psmnt.setString(k++,wch.getWatchName());

            if (psmnt.executeUpdate() > 0) {
                rs = psmnt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    wch.setId(id);
                    result = true;
                    System.out.println(result);
                    return result;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(rs);
        }
        return result;
    }

    public boolean insertManyWtchs(List<Watch> watches) throws SQLTransactionRollbackException {
        boolean result=true;
        Connection con = null;
        try {
            con=getConnection();
            con.setAutoCommit(false);

            for(Watch w:watches){
                result&=insertWatch(con,w);

                if(result==false){

                    return result;
                }
            }
            con.commit();

        }catch (SQLException ex){
            rollback(con);
            throw new SQLTransactionRollbackException("couldnt ROLLBACK");
        }finally {
            close(con);
        }
        return result;
    }


    ///////////////////////////////////////////////////////////////
    /**
     * Block of code for taking data out of DB
     * */
    Connection getCon;

    public Customer getCustomerByID(int id)  {
        Customer cust = null;
        PreparedStatement pstmnt;
        ResultSet rs = null;
        try {

            if(getCon.isClosed()){
                getCon=getConnection();
            }
            pstmnt=getCon.prepareStatement(SQL_GET_CUSTOMER_BY_ID);
            pstmnt.setInt(1,id);

            rs=pstmnt.executeQuery();
            if(rs.next()){
                cust = extractCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {

            try {
                rs.close();
                getCon.close();
            } catch (SQLException e) {
                System.err.println("Couldn't close connection or resultset");
                e.printStackTrace();
            }
        }

    return cust;
    }
    public List<Customer> getAllCustomers()  {
        List<Customer> lCus= new ArrayList<>();
        
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_GET_ALL_CUSTOMERS)) {

            while (rs.next()) {
                lCus.add(extractCustomer(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lCus;
    }
    private Customer extractCustomer(ResultSet rs) throws SQLException {
        Customer cust=new Customer();
        cust.setId(rs.getInt("idCustomer"));
        cust.setFullname(rs.getString("fullname"));
        cust.setPassword(rs.getString("password"));
        cust.setDiscounts(rs.getString("discounts"));

        return cust;
    }

    public Manufacturer getManufacturerByID(int id){
        Manufacturer man = null;
        PreparedStatement pstmnt;
        ResultSet rs = null;
        try {
            getCon=getConnection();
            pstmnt=getCon.prepareStatement(SQL_GET_MANUFACTURER_BY_ID);
            pstmnt.setInt(1,id);

            rs=pstmnt.executeQuery();
            if(rs.next()){
                man = extractManufacturer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {

            try {
                rs.close();
                getCon.close();
            } catch (SQLException e) {
                System.err.println("Couldn't close connection or resultset");
                e.printStackTrace();
            }
        }

        return man;
    }
    public List<Manufacturer> getAllManfrs(){
        List<Manufacturer> lMan= new ArrayList<>();
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_GET_ALL_MANUFACTURERS)) {

            while (rs.next()) {
                lMan.add(extractManufacturer(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                getCon.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return lMan;
    }
    private Manufacturer extractManufacturer(ResultSet rs) throws SQLException {
        Manufacturer man = new Manufacturer();

        man.setId(rs.getInt("idManufacturer"));
        man.setCountry(rs.getString("Country"));
        man.setBrandName(rs.getString("BrandName"));

        return man;
    }

    public Watch getWatchByID(int id){

        Watch watch = null;
        PreparedStatement pstmnt;
        ResultSet rs = null;

        try {

            getCon=getConnection();
            pstmnt=getCon.prepareStatement(SQL_GET_WATCH_BY_ID);
            pstmnt.setInt(1,id);

            rs=pstmnt.executeQuery();
            if(rs.next()){
                watch = extractWatch(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {

            try {
                rs.close();
                getCon.close();
            } catch (SQLException e) {
                System.err.println("Couldn't close connection or resultset");
                e.printStackTrace();
            }
        }
        return watch;
    }
    public List<Watch> getAllWatches(){

        List<Watch> lWatch=new ArrayList<>();

        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_GET_ALL_WATCHES)) {

            while (rs.next()) {
                lWatch.add(extractWatch(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return lWatch;
    }
    public List<Watch> getWatchByType(String type){

        List<Watch> lWatch= new ArrayList<>();
        PreparedStatement pstmnt;
        ResultSet rs = null;

        try {

            getCon=getConnection();
            pstmnt=getCon.prepareStatement(SQL_GET_WATCH_BY_TYPE);
            pstmnt.setString(1,type);
            rs=pstmnt.executeQuery();

            while (rs.next()){
                lWatch.add(extractWatch(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {

                rs.close();
                getCon.close();

            } catch (SQLException e) {
                System.err.println("Couldn't close connection or resultset");
                e.printStackTrace();
            }
        }
        return lWatch;
    }
    public List<Watch> getWatchByTypeAndPrice(String type, int minPrice, int maxPrice){


        List<Watch> lWatch= new ArrayList<>();
        PreparedStatement pstmnt;
        ResultSet rs = null;

        try {

            getCon=getConnection();
            pstmnt=getCon.prepareStatement(SQL_GET_WATCH_BY_TYPE_AND_PRICE);
            pstmnt.setString(1,type);
            pstmnt.setInt(2,minPrice);
            pstmnt.setInt(3,maxPrice);

            rs=pstmnt.executeQuery();

            while (rs.next()){
                lWatch.add(extractWatch(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {

                rs.close();
                getCon.close();

            } catch (SQLException e) {
                System.err.println("Couldn't close connection or resultset");
                e.printStackTrace();
            }
        }
        return lWatch;
    }
    private Watch extractWatch(ResultSet rs) throws SQLException {
        Watch w=new Watch();

        w.setId(rs.getInt("idWatch"));
        w.setWatchName(rs.getString("WatchName"));
        w.setManufacturer_id(rs.getInt("manufacturer"));
        w.setPrice(rs.getDouble("price"));
        w.setType(rs.getString("type"));

        return w;
    }

}
