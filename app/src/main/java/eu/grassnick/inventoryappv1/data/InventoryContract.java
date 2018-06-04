package eu.grassnick.inventoryappv1.data;

import android.provider.BaseColumns;

public final class InventoryContract {
    private InventoryContract() {
    }

    //each class represents a table in the database
    public static final class SampleEntry implements BaseColumns {

        public final static String TABLE_NAME = "sample";

        //these are the column headers
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_OBJ_NAME = "name";
        public final static String COLUMN_OBJ_TYPE = "type";

        //constant values for column "type"
        public final static int TYPE_UNKNOWN = 0;
        public final static int TYPE_PHYSICAL = 1;
        public final static int TYPE_DIGITAL = 2;
    }
}
