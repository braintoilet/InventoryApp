package eu.grassnick.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.grassnick.inventoryapp.data.InventoryContract.ProductEntry;
import eu.grassnick.inventoryapp.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivity";
    private static final int URL_LOADER = 1;

    private static final String[] mProjection = {
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductEntry.COLUMN_PRODUCT_QUANTITY};

    @BindView(R.id.product_list_view)
    ListView productList;

    @BindView(R.id.main_fab)
    FloatingActionButton fab;

    @BindView(R.id.empty_view)
    View emptyView;

    private ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        productList.setEmptyView(emptyView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditProductActivity.class));
            }
        });

        getLoaderManager().initLoader(1, null, this);
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
            case R.id.action_add_product:
                //TODO: Intent to Empty EditProductActivity
                return true;
            case R.id.action_delete_all:
                getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
                return true;
            case R.id.action_add_sample:
                getContentResolver().insert(ProductEntry.CONTENT_URI, InventoryDbHelper.createDummyData());
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                return new CursorLoader(this,
                        ProductEntry.CONTENT_URI,
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
        if (data != null) {
            mCursorAdapter = new ProductCursorAdapter(this, data);
            productList.setAdapter(mCursorAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.forceLoad();
    }
}
