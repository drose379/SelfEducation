package dylanrose60.selfeducation;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class NewAlbum extends Fragment {

    private ViewGroup parentLayout;
    private Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (Context) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstance) {
        super.onCreateView(inflater, container, savedInstance);
        View view = inflater.inflate(R.layout.new_album,container,false);
        setListeners(view);
        this.parentLayout = container;
        return view;
    }

    public void setListeners(View view) {
        //Default image click listener
        ImageView defaultImage = (ImageView) view.findViewById(R.id.defaultImage);
        defaultImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    * Bring up option to choose from gallery or take photo with camera
                    * Set photo taken to imageview src
                    * Save data
                */
            }
        });

        //Button click listener
        Button confButton = (Button) view.findViewById(R.id.confButton);
        confButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    * Make sure all fields are filled
                    * Grab all data, including the default image url (must be uploaded first)
                    * Create new album
                 */
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
