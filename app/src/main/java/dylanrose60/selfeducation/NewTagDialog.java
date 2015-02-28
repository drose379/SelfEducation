package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

@SuppressLint("NewApi")
public class NewTagDialog extends DialogFragment {

    public interface Listener {
        public void newTag(Boolean newTag);
    }

    private Listener listener;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        listener = (Listener) getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        final EditText editText1 = new EditText(getActivity());

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("New Tag")
                .customView(editText1,true)
                .positiveText("Add")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeText("Back")
                .negativeColor(Color.RED)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        TagDataHandler tagHandler = new TagDataHandler();
                        String newTag = editText1.getText().toString();
                        if (newTag.length() > 1) {
                            tagHandler.addTag(newTag);
                            listener.newTag(true);
                        } else {
                            editText1.setError("Enter a valid tag");
                        }
                    }
                });
        MaterialDialog dialog = builder.build();
        return dialog;
    }

}
