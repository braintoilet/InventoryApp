package eu.grassnick.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.grassnick.inventoryapp.data.InventoryContract.ProductEntry;
import eu.grassnick.inventoryapp.data.InventoryItem;

public class EditProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 1;
    private static final int MODE_VIEW = 1;
    private static final int MODE_EDIT = 2;
    private static final int MODE_ADD = 3;
    private static final String[] mProjection = {
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductEntry.COLUMN_PRODUCT_QUANTITY,
            ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
            ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE};
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_price)
    EditText editPrice;
    @BindView(R.id.edit_quantity)
    EditText editQuantity;
    @BindView(R.id.edit_supplier_name)
    EditText editSupplierName;
    @BindView(R.id.edit_supplier_phone)
    EditText editSupplierPhone;
    private int mCurrentMode = MODE_ADD;
    private Uri mUri = ProductEntry.CONTENT_URI;
    private InventoryItem mCurrentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        //If the intent delivers a uri set mUri and init the CursorLoader
        if (intent.hasExtra("uri")) {
            mUri = intent.getParcelableExtra("uri");
            mCurrentMode = MODE_VIEW;
            switchToViewMode();
        } else {
            mCurrentMode = MODE_ADD;
            switchToEditMode();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        //Check which mode is currently active, and inflate the according menu
        switch (mCurrentMode) {
            case MODE_ADD:
                inflater.inflate(R.menu.add_menu, menu);
                return true;
            case MODE_EDIT:
                inflater.inflate(R.menu.edit_menu, menu);
                return true;
            case MODE_VIEW:
                inflater.inflate(R.menu.view_menu, menu);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (mCurrentMode) {
            case MODE_ADD:
                if (item.getItemId() == R.id.action_save_product)
                    saveProduct();
                return true;
            case MODE_EDIT:
                switch (item.getItemId()) {
                    case R.id.action_save_product:
                        saveProduct();
                        break;
                    case R.id.action_delete_product:
                        deleteProduct();
                        break;
                }
                return true;
            case MODE_VIEW:
                switch (item.getItemId()) {
                    case R.id.action_edit_product:
                        switchToEditMode();
                        break;
                    case R.id.action_delete_product:
                        deleteProduct();
                        break;
                }
                return true;
            default:
                return false;
        }

    }

    private void switchToEditMode() {
        // check if were editing a existing product or add a new one
        if (mCurrentItem != null) {
            fillLayout();
            mCurrentMode = MODE_EDIT;
            getActionBar().setTitle(R.string.title_edit_product);
        } else {
            mCurrentMode = MODE_ADD;
            getActionBar().setTitle(R.string.title_add_product);
        }

        //Enable editText's
        editName.setEnabled(true);
        editPrice.setEnabled(true);
        editQuantity.setEnabled(true);
        editSupplierName.setEnabled(true);
        editSupplierPhone.setEnabled(true);

        invalidateOptionsMenu();
    }

    private void switchToViewMode() {
        //Change title
        getActionBar().setTitle(R.string.title_view_product);

        //fill layout
        fillLayout();

        //disable editText's
        editName.setEnabled(false);
        editPrice.setEnabled(false);
        editQuantity.setEnabled(false);
        editSupplierName.setEnabled(false);
        editSupplierPhone.setEnabled(false);

        mCurrentMode = MODE_VIEW;
        invalidateOptionsMenu();
    }

    private void fillLayout() {
        if (mCurrentItem != null) {
            //Format the price
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            nf.setMaximumFractionDigits(2);
            String priceString = nf.format(mCurrentItem.getPrice());

            //Populate layout with data from mCurrentItem
            editName.setText(mCurrentItem.getName());
            editPrice.setText(priceString);
            editQuantity.setText(mCurrentItem.getName());
            editSupplierName.setText(mCurrentItem.getName());
            editSupplierPhone.setText(mCurrentItem.getName());
        }
    }

    private void saveProduct() {
        String name = editName.getText().toString();
        int quantity = Integer.parseInt(editQuantity.getText().toString());
        float price = Float.parseFloat(editQuantity.getText().toString());
        String supplierPhone = editSupplierPhone.getText().toString();
        String supplierName = editSupplierName.getText().toString();

        //check if the entered values are valid
        if (name.length() <= 0) {
            Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_LONG);
            return;
        }

        if (quantity < 0)
            quantity = 0;

        if (price < 0.0f)
            price = 0.0f;

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);


        if (mCurrentMode == MODE_ADD) {
            //insert new product
            mUri = getContentResolver().insert(ProductEntry.CONTENT_URI, contentValues);
        } else if (mCurrentMode == MODE_EDIT) {
            long id = getContentResolver().update(mUri, contentValues, null, null);
            mCurrentItem = new InventoryItem(id, name, price, quantity, supplierName, supplierPhone);
        }
        getLoaderManager().getLoader(URL_LOADER).forceLoad();
        switchToViewMode();
    }

    private int deleteProduct() {
        if (mCurrentItem != null) {
            return getContentResolver().delete(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, mCurrentItem.getID()), null, null);
        }
        return 0;
    }

    private void callSupplier() {
        //If in View mode and if the number is valid, call intent to phone app
        if (mCurrentMode == MODE_VIEW) {
            String number = mCurrentItem.getSupplierPhone();
            if (isValidMobile(number)) {

            }
        }
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        mProjection,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int id = data.getInt(data.getColumnIndex(ProductEntry._ID));
            String name = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
            float price = data.getFloat(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            int quantity = data.getInt(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            String supplierName = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME));
            String supplierPhone = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE));

            if (supplierName == null)
                supplierName = "";
            if (supplierPhone == null)
                supplierName = "";

            mCurrentItem = new InventoryItem(id, name, price, quantity, supplierName, supplierPhone);

            if (!data.isClosed())
                data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.forceLoad();
    }
}
