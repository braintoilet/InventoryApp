package eu.grassnick.inventoryapp.data;

import android.content.ContentValues;

import eu.grassnick.inventoryapp.data.InventoryContract.ProductEntry;

public class InventoryItem {
    private static final String TAG = "InventoryItem";

    private long mID;
    private String mName;
    private float mPrice;
    private int mQuantity;
    private String mSupplierName;
    private String mSupplierPhone;

    public InventoryItem(long id, String name, float price, int quantity, String supplierName, String supplierPhone) {
        mID = id;
        mName = name;
        mPrice = price;
        mQuantity = quantity;
        mSupplierName = supplierName;
        mSupplierPhone = supplierPhone;
    }

    public long getID() {
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

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(ProductEntry._ID, getID());
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, getPrice());
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, getQuantity());
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, getSupplierName());
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, getSupplierPhone());
        return values;
    }
}
