package dylanrose60.selfeducation;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.Random;


public class NewAlbum extends Fragment {

    private ViewGroup parentLayout;
    private Context context;

    private File imageFile;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (Context) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstance) {
        super.onCreateView(inflater, container, savedInstance);

        View view = inflater.inflate(R.layout.new_album,container,false);

        setDefaultImageListener(view);
        setConfirmListener(view);
        this.parentLayout = container;
        return view;
    }

    public void setConfirmListener(View view) {
        //Button click listener
        Button confButton = (Button) view.findViewById(R.id.confButton);
        confButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    * Make sure all fields are filled
                    * Grab all data, including the default image url (must be uploaded first)
                    * Create new album
                    * set request code to 0 to decipher difference in onActivityResult
                 */
            }
        });
    }

    public void setDefaultImageListener(View view) {
        //Default image click listener
        ImageView defaultImage = (ImageView) view.findViewById(R.id.defaultImage);
        defaultImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    * Bring up option to choose from gallery or take photo with camera (Material Dialog)
                    * Set photo taken to imageview src
                    * Save data
                */
                MaterialDialog.Builder dBuilder = new MaterialDialog.Builder(context);
                dBuilder.title("Select From");
                dBuilder.items(new String[]{"Camera", "Storage"});
                dBuilder.itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence itemSelected) {
                        switch (String.valueOf(itemSelected)) {
                            case "Camera":
                                Intent startCamera = new Intent(getActivity(),CameraAccess.class);
                                startActivity(startCamera);
                                break;

                            case "Storage":
                                //intent to grab from storage
                                break;
                        }
                    }
                });
                MaterialDialog dialog = dBuilder.build();
                dialog.show();
            }
        });

    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}
