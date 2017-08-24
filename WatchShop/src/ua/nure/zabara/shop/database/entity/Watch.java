package ua.nure.zabara.shop.database.entity;

import ua.nure.zabara.shop.database.DBManager;

public class Watch extends Entity{
    private String type;
    private double price;
    private int manufacturer_id;
    private String watchName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getManufacturer_id() {
        return manufacturer_id;
    }

    public void setManufacturer_id(int manufacturer_id) {
        this.manufacturer_id = manufacturer_id;
    }

    public String getWatchName() {
        return watchName;
    }

    public void setWatchName(String watchName) {
        this.watchName = watchName;
    }

    @Override
    public String toString() {
        Manufacturer man=DBManager.getInstance().getManufacturerByID(id);
        return "Watch model "+ watchName+" it's id:"+id+" price: "+price+"UAH(GRIVNA)\n Manufacturer: "+man.getBrandName();
    }
}
