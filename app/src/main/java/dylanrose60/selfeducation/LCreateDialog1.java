package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

@SuppressLint("NewApi")
public class LCreateDialog1 extends DialogFragment {

    private Listener listener;

    public interface Listener {
        public void getNewLesson(String testData);
    }


    @Override
    public void onAttach(Activity parentActivity) {
        super.onAttach(parentActivity);
        this.listener = (Listener) parentActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        final EditText editText1 = new EditText(getActivity());
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("Name Your Lesson")
                .customView(editText1,true)
                .positiveText("Next")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeText("Cancel")
                .negativeColor(Color.RED)
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String lessonName = editText1.getText().toString();
                        if (lessonName.length() > 1) {
                            dialog.dismiss();
                            callListener(lessonName);
                            LCreateDialog1.this.dismiss();
                        } else {
                            editText1.setError("Enter a valid lesson name");
                        }
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                });
        MaterialDialog dialog = builder.build();
        return dialog;
    }

    public void callListener(String lesson) {
        listener.getNewLesson(lesson);
    }

}
