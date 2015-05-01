package dylanrose60.selfeducation.DialogFragment;

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

    private List<Drawable> images;
    private Context context;

    public StockImageListAdapter(Context context,List<Drawable> images,int layout) {
        super(context,layout,images);
        this.images = images;
        this.context = context;
    }

    @Override
    public View getView(int position,View recycledView,ViewGroup parent) {
        View v = recycledView;

        if (recycledView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.stock_image_layout, parent, false);
        }

        ImageView stockImageView = (ImageView) v.findViewById(R.id.stockImageView);
        Drawable stockImage = images.get(position);
        stockImageView.setImageDrawable(stockImage);

        final int pos = getPosition(stockImage);

        stockImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Working fine
                Log.i("imgClick",String.valueOf(pos));
            }
        });

        return v;
    }

}
