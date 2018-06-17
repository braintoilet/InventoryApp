package eu.grassnick.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Objects;

import eu.grassnick.inventoryapp.data.InventoryContract.ProductEntry;

public class InventoryProvider extends ContentProvider {
    private static final String TAG = "ProductProvider";

    public static final int PRODUCTS = 1000;
    public static final int PRODUCT_ID = 1001;
    public InventoryDbHelper dbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/eu.grassnick.inventoryapp/products";

    static {
        sUriMatcher.addURI(ProductEntry.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductEntry.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                Uri result = insertProduct(uri, contentValues);
                if (result != null)
                    Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                return result;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        //Check that the Name, the Price and the Quantity are set
        String name = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        float price = contentValues.getAsFloat(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantity = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        if (name == null)
            throw new IllegalArgumentException("Product requires a name");
        if (price < 0.0)
            throw new IllegalArgumentException("Price cant be negative.");
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity cant be negative.");

        long id = dbHelper.getWritableDatabase().insert(ProductEntry.TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int result;
        switch (match) {
            case PRODUCTS:
                result = updateProduct(uri, contentValues, selection, selectionArgs);
                if (result != 0)
                    Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                return result;

            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                result = updateProduct(uri, contentValues, selection, selectionArgs);
                if (result != 0)
                    Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                return result;

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //return 0 if there are no values present
        if (values.size() == 0)
            return 0;

        //Check if Name is valid
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null)
                throw new IllegalArgumentException("Product requires a name");
            else if (name.length() < 1)
                throw new IllegalArgumentException("Product requires a proper name.");
        }

        //Check if quantity is valid
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            int quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity < 0)
                throw new IllegalArgumentException("Quantity cant be negative.");
        }

        //Check if price is valid
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            float price = values.getAsFloat(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (price < 0.0)
                throw new IllegalArgumentException("Price cant be negative.");
        }

        return dbHelper.getWritableDatabase().update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                int result = dbHelper.getWritableDatabase().delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (result != 0)
                    Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                return result;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                result = dbHelper.getWritableDatabase().delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (result != 0)
                    Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                return result;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
