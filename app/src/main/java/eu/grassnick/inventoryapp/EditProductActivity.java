package eu.grassnick.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import eu.grassnick.inventoryapp.data.InventoryContract.ProductEntry;

public class EditProductActivity extends AppCompatActivity {

    private static final String[] mProjection = {
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductEntry.COLUMN_PRODUCT_QUANTITY,
            ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
            ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE};

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        //If uriString has a value, query the database and populate the layout with the results
        if (intent.hasExtra("uri")) {
            mUri = intent.getParcelableExtra("uri");
            if (mUri != null) {
                Cursor result = getContentResolver().query(mUri, mProjection, null, null, null);
                if (result != null) {
                    //TODO: Populate the layout with the data from the cursor

                    if (!result.isClosed())
                        result.close();
                }
            }
        }
    }
}
