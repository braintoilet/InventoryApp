package eu.grassnick.inventoryappv1.data;

public class InventoryItem {
    private static final String TAG = "InventoryItem";

    private int mID;
    private String mName;
    private float mPrice;
    private int mQuantity;
    private String mSupplierName;
    private String mSupplierPhone;

    public InventoryItem(int id, String name, float price, int quantity, String supplierName, String supplierPhone) {
        mID = id;
        mName = name;
        mPrice = price;
        mQuantity = quantity;
        mSupplierName = supplierName;
        mSupplierPhone = supplierPhone;
    }

    public int getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public float getPrice() {
        return mPrice;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public String getSupplierName() {
        return mSupplierName;
    }

    public String getSupplierPhone() {
        return mSupplierPhone;
    }
}
