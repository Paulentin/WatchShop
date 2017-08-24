package ua.nure.zabara.shop.database.entity;

public class Customer extends Entity {


    private String fullname;

    private String password;

    private String discounts;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDiscounts() {
        return discounts;
    }

    public void setDiscounts(String discounts) {
        this.discounts = discounts;
    }

    @Override
    public String toString() {
        return "Customer: " + fullname+ " id: "+id + " pass: "+password;
    }
}





