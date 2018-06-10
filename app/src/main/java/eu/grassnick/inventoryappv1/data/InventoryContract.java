package eu.grassnick.inventoryappv1.data;

import android.provider.BaseColumns;

public final class InventoryContract {
    private static final String TAG = "InventoryContract";
    private InventoryContract() {
    }

    //each class represents a table in the database
    public static final class ProductEntry implements BaseColumns {
        //Projections
        public final static String[] PROJECTION_FULL = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE};

        public final static String TABLE_NAME = "products";

        //COLUMN HEADERS
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name"; // STRING
        public final static String COLUMN_PRODUCT_PRICE = "price"; // REAL
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity"; // INTEGER
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name"; // STRING
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone"; // INTEGER
        private static final String TAG = "ProductEntry";
    }
}
