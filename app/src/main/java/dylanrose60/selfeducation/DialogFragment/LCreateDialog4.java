package dylanrose60.selfeducation.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import android.net.Uri;

import dylanrose60.selfeducation.R;

@SuppressLint("NewApi")
public class LCreateDialog4 extends DialogFragment {

    private Listener listener;

    private LinearLayout dialogLayout;
    private String imgUriString;

    public interface Listener {
        public void getDefaultImage(String imgPath);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (Listener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        dialogLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.img_picker,null);
        Button imgPick = (Button) dialogLayout.findViewById(R.id.imgPickButton);
        imgPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inflateImgPicker = new Intent();
                inflateImgPicker.setType("image/*");
                inflateImgPicker.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(inflateImgPicker,"Select image"),1);
            }
        });
    }

    public void onActivityResult(int requestCode,int resultCode,Intent intent) {
        ImageView testImg = (ImageView) dialogLayout.findViewById(R.id.testImg);
        testImg.setVisibility(View.VISIBLE);

        Uri imgUri = intent.getData();
        //Need URI as string to save to db. To show img, use URI.parse(string) later
        imgUriString = String.valueOf(intent.getData());

        testImg.setImageURI(imgUri);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("Set default image")
                .customView(dialogLayout,true)
                .positiveText("Finish")
                .negativeText("Cancel")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeColor(Color.RED)
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //Make sure imgUriString is not null, if it is, do not move on
                        //if it is NOT null, send to listener which will create the lesson
                        dialog.dismiss();
                        listener.getDefaultImage(imgUriString);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        return dialog;
    }

}