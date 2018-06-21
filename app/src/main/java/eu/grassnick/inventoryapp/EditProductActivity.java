package eu.grassnick.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.grassnick.inventoryapp.data.InventoryContract.ProductEntry;

public class EditProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 1;
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
    @BindView(R.id.edit_supplier_name)
    EditText editSupplierName;
    @BindView(R.id.edit_supplier_phone)
    EditText editSupplierPhone;
    @BindView(R.id.text_quantity)
    TextView textQuantity;
    @BindView(R.id.button_increase_quantity)
    Button buttonIncreaseQuantity;
    @BindView(R.id.button_decrease_quantity)
    Button buttonDecreaseQuantity;
    @BindView(R.id.button_call_supplier)
    Button buttonCallSupplier;
    @BindView(R.id.save_fab)
    FloatingActionButton fab;

    private Uri mUri = null;
    private boolean mProductChanged = false;
    private int mQuantity;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductChanged = true;
            return false;
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_call_supplier:
                    callSupplier();
                    break;
                case R.id.button_increase_quantity:
                    mQuantity = Integer.parseInt(textQuantity.getText().toString()) + 1;
                    textQuantity.setText(String.valueOf(mQuantity));
                    break;
                case R.id.button_decrease_quantity:
                    mQuantity = Integer.parseInt(textQuantity.getText().toString());
                    //only decrease if not zero
                    if (mQuantity != 0) {
                        mQuantity--;
                        textQuantity.setText(String.valueOf(mQuantity));
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        ButterKnife.bind(this);

        //Set OnCliCkListener for the increase, decrease, the FloatingActionButton and callSupplier button
        buttonDecreaseQuantity.setOnClickListener(mOnClickListener);
        buttonIncreaseQuantity.setOnClickListener(mOnClickListener);
        buttonCallSupplier.setOnClickListener(mOnClickListener);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });

        //Set OnTouchListener for the relevant views
        buttonIncreaseQuantity.setOnTouchListener(mTouchListener);
        buttonDecreaseQuantity.setOnTouchListener(mTouchListener);
        editName.setOnTouchListener(mTouchListener);
        editPrice.setOnTouchListener(mTouchListener);
        editSupplierPhone.setOnTouchListener(mTouchListener);
        editSupplierName.setOnTouchListener(mTouchListener);

        Intent callingIntent = getIntent();
        //If the intent delivers a uri set mUri
        if (callingIntent.hasExtra("uri")) {
            mUri = callingIntent.getParcelableExtra("uri");
            switchToEditMode();
        } else {
            switchToEditMode();
        }

        //Init the CursorLoader
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //If were in Add mode hide the delete option
        if (mUri == null)
            menu.findItem(R.id.action_delete_product).setVisible(false);
        else
            menu.findItem(R.id.action_delete_product).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_product:
                saveProduct();
                return true;
            case R.id.action_delete_product:
                if (deleteProduct() > 0)
                    super.onBackPressed();
                return true;
            case android.R.id.home:
                if (!mProductChanged) {
                    NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                    return true;
                }

                // Create OnClickListener
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                    }
                };

                // Show a dialog that notifies the user
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (!mProductChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // close current activity
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        // Create and show
        builder.create();
        builder.show();
    }

    private void switchToEditMode() {
        // check if were editing a existing product or add a new one
        if (mUri == null) {
            setTitle(R.string.title_activity_add_product); //Set ActionBar title
            buttonCallSupplier.setVisibility(View.INVISIBLE); //Hide the call supplier button
            editPrice.setText(floatToCurrencyString(0.0f));//Set to 0 with device currencyformat
        } else {
            setTitle(R.string.title_activity_edit_product); //Set ActionBar title
            buttonCallSupplier.setVisibility(View.VISIBLE); //Show the call supplier button
        }

        invalidateOptionsMenu();
    }

    private void saveProduct() {
        String name = editName.getText().toString();
        String quantityString = textQuantity.getText().toString();
        String priceString = textQuantity.getText().toString();
        String supplierPhone = editSupplierPhone.getText().toString();
        String supplierName = editSupplierName.getText().toString();

        //Return if empty
        if (TextUtils.isEmpty(name)
                || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(supplierPhone)
                || TextUtils.isEmpty(supplierName)) {
            //Notify the user
            Toast.makeText(this, R.string.toast_cant_save, Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, Float.valueOf(priceString));
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.valueOf(quantityString));
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);

        if (mUri == null) {
            //insert new product
            mUri = getContentResolver().insert(ProductEntry.CONTENT_URI, contentValues);
            if (mUri != null) {
                switchToEditMode();
            }
        } else {
            // update the product
            int rowsUpdated = getContentResolver().update(mUri, contentValues, null, null);
            if (rowsUpdated != 0)
                getContentResolver().notifyChange(mUri, null);
        }
    }

    private int deleteProduct() {
        if (mUri != null) {
            int rowsDeleted = getContentResolver().delete(mUri, null, null);
            if (rowsDeleted > 0) {
                getContentResolver().notifyChange(mUri, null);
                return rowsDeleted;
            }
        }
        return 0;
    }

    private void callSupplier() {
        //If in View mode and if the number is valid, call intent to phone app
        if (mUri != null) {
            String number = editSupplierPhone.getText().toString();
            if (isValidPhoneNumber(number)) {
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + number)));
            } else {
                Toast.makeText(this, R.string.toast_enter_valid_phone, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri == null)
            return null;

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
            long id = data.getLong(data.getColumnIndex(ProductEntry._ID));
            String name = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
            float price = data.getFloat(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            String quantity = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            String supplierName = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME));
            String supplierPhone = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE));

            // Populate layout with the data from the Cursor
            editName.setText(name);
            editPrice.setText(floatToCurrencyString(price)); //Format price before setting it
            textQuantity.setText(quantity);
            editSupplierName.setText(supplierName);
            editSupplierPhone.setText(supplierPhone);

            mUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
            data.setNotificationUri(getContentResolver(), mUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Clear Input Fields
        editName.setText(R.string.empty_string);
        editPrice.setText(R.string.empty_string);
        textQuantity.setText(R.string.empty_string);
        editSupplierName.setText(R.string.empty_string);
        editSupplierPhone.setText(R.string.empty_string);

        //Reload
        loader.forceLoad();
    }

    /* HELPER METHODS */
    private boolean isValidPhoneNumber(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private String floatToCurrencyString(float amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(amount);
    }
}
