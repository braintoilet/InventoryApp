package eu.grassnick.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = new ViewHolder(view);

        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        float price = cursor.getFloat(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        String priceString = nf.format(price);

        viewHolder.name.setText(name);
        viewHolder.price.setText(priceString);
        viewHolder.quantity.setText(String.valueOf(quantity));
    }

    static class ViewHolder {
        @BindView(R.id.list_item_name)
        TextView name;
        @BindView(R.id.list_item_price)
        TextView price;

        @BindView(R.id.list_item_quantity)
        TextView quantity;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
