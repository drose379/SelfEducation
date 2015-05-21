package dylanrose60.selfeducation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TitleTextView extends TextView {

    private Context context;

    public TitleTextView(Context context) {
        super(context,null);
        this.context = context;
        Typeface customFont = Typeface.createFromAsset(context.getAssets(),"mohave.otf");
        this.setTypeface(customFont);
    }

    public TitleTextView(Context context,AttributeSet attrs) {
        super(context,attrs);

        TypedArray atributeInfo = context.obtainStyledAttributes(attrs,R.styleable.TitleTextView);

        int attr = atributeInfo.getIndex(0);
        boolean  boldType = atributeInfo.getBoolean(attr,false);

        Log.i("boldValue",String.valueOf(boldType));

        Typeface customFont;
        customFont = Typeface.createFromAsset(context.getAssets(), "sourceSans_reg.ttf");

        if (boldType) {
            //customFont = Typeface.createFromAsset(context.getAssets(), "sourceSans_reg.ttf");
        } else {
            //customFont = Typeface.createFromAsset(context.getAssets(), "sourceSans_reg.ttf");
        }

        this.setTypeface(customFont);
    }




}
