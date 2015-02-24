package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;

@SuppressLint("NewApi")
public class LCreateDialog1 extends DialogFragment {

    private Listener listener;

    public interface Listener {
        public void getData(String testData);
    }


    @Override
    public void onAttach(Activity parentActivity) {
        super.onAttach(parentActivity);
        this.listener = (Listener) parentActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("Test title")
                .content("DialogFragment Body")
                .positiveText("Continue")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        callListener();
                    }
                });
        MaterialDialog dialog = builder.build();
        return dialog;
    }

    public void callListener() {
        listener.getData("Testing!");
    }

}
