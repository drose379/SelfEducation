package dylanrose60.selfeducation;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomOptionsAdapter extends ArrayAdapter<String> {

    private String[] options;

    public CustomOptionsAdapter(Context context,int layout,String[] array) {
        super(context,layout,array);
        options = array;
    }

    @Override
    public View getView(int position,View recycledView,ViewGroup parent) {
        View v = recycledView;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.tag_manager_home_card,parent,false);
        }

        TextView title = (TextView) v.findViewById(R.id.tag_options_textView);
        title.setText(options[position]);

        return v;
    }

}
