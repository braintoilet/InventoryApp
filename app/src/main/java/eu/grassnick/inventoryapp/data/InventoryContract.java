package eu.grassnick.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    private InventoryContract() {
    }

    //each class represents a table in the database
    public static final class ProductEntry implements BaseColumns {
        public static final String CONTENT_AUTHORITY = "eu.grassnick.inventoryapp";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_PRODUCTS = "products";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public final static String TABLE_NAME = "products";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name"; // STRING
        public final static String COLUMN_PRODUCT_PRICE = "price"; // REAL
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity"; // INTEGER
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name"; // STRING
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone"; // INTEGER
    }
}
