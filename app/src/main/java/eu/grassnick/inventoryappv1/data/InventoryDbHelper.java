package eu.grassnick.inventoryappv1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1; //increment by 1 each time the schema gets changed
    public static final String DATABASE_NAME = "Inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
