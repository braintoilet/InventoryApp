package eu.grassnick.inventoryappv1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import eu.grassnick.inventoryappv1.data.InventoryContract;
import eu.grassnick.inventoryappv1.data.InventoryDbHelper;
import eu.grassnick.inventoryappv1.data.InventoryContract.ProductEntry;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView testText;
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testText = findViewById(R.id.test_text_view);

        mDbHelper = new InventoryDbHelper(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_dummy_data:
                insertData(createDummyData());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertData(ContentValues data) {
        mDbHelper.getWritableDatabase().insert(ProductEntry.TABLE_NAME, null, data);
    }

    private Cursor readData() {
        //TODO: Needs to be changed
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        return db.rawQuery(InventoryDbHelper.SQL_GET_ALL_PRODUCTS, null);
    }

    private void printDbToLog() {
        //TODO: Print Database to log
    }

    private ContentValues createDummyData() {
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Keyboard");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 2.00);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Keyboardmakers");
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "+49 1234 56789");
        return values;
    }
}
