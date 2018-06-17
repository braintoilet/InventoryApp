package eu.grassnick.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.grassnick.inventoryapp.data.InventoryContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder viewHolder = new ViewHolder(view);

        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        float price = cursor.getFloat(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        String priceString = nf.format(price);

        viewHolder.name.setText(name);
        viewHolder.price.setText(priceString);
        viewHolder.quantity.setText(String.valueOf(quantity));
        viewHolder.sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing if quantity is 0
                if (quantity == 0)
                    return;

                //Reduce quantity by 1 and update the database
                int id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
                Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                ContentValues contentValues = new ContentValues();
                contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);

                context.getContentResolver().update(uri, contentValues, null, null);
            }
        });
    }

    static class ViewHolder {
        @BindView(R.id.list_item_name)
        TextView name;
        @BindView(R.id.list_item_price)
        TextView price;

        @BindView(R.id.list_item_quantity)
        TextView quantity;

        @BindView(R.id.list_item_sell_button)
        Button sellButton;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
