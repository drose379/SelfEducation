package dylanrose60.selfeducation.DialogFragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dylanrose60.selfeducation.R;

public class StockImageListAdapter extends ArrayAdapter<Drawable> {

    private LCreateDialog4 parentDialog;

    private List<Drawable> images;
    private Context context;

    public StockImageListAdapter(LCreateDialog4 dialog,Context context,List<Drawable> images,int layout) {
        super(context,layout,images);
        this.parentDialog = dialog;
        this.images = images;
        this.context = context;
    }

    @Override
    public View getView(int position,View recycledView,ViewGroup parent) {

        Log.i("imgPos",String.valueOf(position));

        View v = recycledView;

        if (recycledView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.stock_image_layout, parent, false);
        }

        ImageView stockImageView = (ImageView) v.findViewById(R.id.stockImageView);
        final Drawable stockImage = images.get(position);
        stockImageView.setImageDrawable(stockImage);

        final int pos = getPosition(stockImage);

        stockImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Working fine
                Log.i("imgClick",String.valueOf(pos));

                String drawableURI;

                switch (pos) {
                    case 0:
                        drawableURI = "stock_1";
                        StockImageListAdapter.this.parentDialog.setStockDrawable(stockImage,drawableURI);
                        break;
                    case 1:
                        drawableURI = "stock_2";
                        StockImageListAdapter.this.parentDialog.setStockDrawable(stockImage,drawableURI);
                        break;
                    case 2:
                        drawableURI = "stock_3";
                        StockImageListAdapter.this.parentDialog.setStockDrawable(stockImage,drawableURI);
                        break;
                    case 3:
                        drawableURI = "stock_4";
                        StockImageListAdapter.this.parentDialog.setStockDrawable(stockImage,drawableURI);
                        break;
                }

            }

            //Need to callback to LCreateDialog4 with the image that was clicked (drawableURI) and build lesson (need seperate lesson build method for stock image users

        });


        return v;
    }

}
