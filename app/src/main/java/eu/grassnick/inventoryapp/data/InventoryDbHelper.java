package eu.grassnick.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import eu.grassnick.inventoryapp.data.InventoryContract.ProductEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {
    public static final String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
            + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
            + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL DEFAULT 0.00, "
            + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT, "
            + ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT" + ");";


    public static final int DATABASE_VERSION = 1; //increment by 1 each time the schema gets changed
    public static final String DATABASE_NAME = "Inventory.db";
    public static final String SQL_GET_ALL_PRODUCTS = "SELECT * FROM " + ProductEntry.TABLE_NAME;
    private static final String TAG = "InventoryDbHelper";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        Log.d(TAG, "onCreate: SQL_CREATE_PRODUCTS_TABLE == " + SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to upgrade until DATABASE_VERSION gets updated
    }

    public ArrayList<InventoryItem> getList(Cursor cursor) {
        ArrayList<InventoryItem> list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                InventoryItem item = new InventoryItem(cursor.getInt(cursor.getColumnIndex(ProductEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME)),
                        cursor.getFloat(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME)),
                        cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE)));

                Log.d(TAG, "getList: Row: " + item.getCSVString());
                list.add(item);
                cursor.moveToNext();
            }
        }

        if (!cursor.isClosed())
            cursor.close();

        return list;
    }

    public static ContentValues createDummyData() {
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Keyboard");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 2.00);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Keyboardmakers");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, "+49 1234 56789");
        return values;
    }
}
