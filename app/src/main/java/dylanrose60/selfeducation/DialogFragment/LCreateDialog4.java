package dylanrose60.selfeducation.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import dylanrose60.selfeducation.R;

@SuppressLint("NewApi")
public class LCreateDialog4 extends DialogFragment {

    private Listener listener;

    private LinearLayout dialogLayout;

    public interface Listener {
        public void getDefaultImage(String imgPath);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //this.listener = (Listener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        dialogLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.img_picker,null);
        Button imgPick = (Button) dialogLayout.findViewById(R.id.imgPickButton);
        imgPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inflate image picker
            }
        });
    }

    public void inflateImgPicker(View v) {
        Log.i("imgPicker", "Selected");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("Set default image")
                .customView(dialogLayout,true)
                .positiveText("Submit")
                .negativeText("Cancel")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeColor(Color.RED)
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

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