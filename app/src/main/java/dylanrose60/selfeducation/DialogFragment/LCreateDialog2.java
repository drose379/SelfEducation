package dylanrose60.selfeducation.DialogFragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import dylanrose60.selfeducation.R;

@SuppressLint("NewApi")
public class LCreateDialog2 extends DialogFragment {

    private Listener listener;

    public interface Listener {
        public void getObjectives(List<String> objectives);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (Listener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {

        final LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.new_lesson_layout, null);

        final EditText editText1 = new EditText(getActivity());
        dialogLayout.addView(editText1);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("Lesson Objectives")
                .customView(dialogLayout, true)
                .positiveText("Next")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeText("Cancel")
                .negativeColor(Color.RED)
                .neutralText("Add")
                .neutralColor(getResources().getColor(R.color.ColorPrimary))
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //Send dialog (View) to method inside frag to pull EditText data
                        List<String> objectives = getObjectiveValues((ViewGroup)dialog.getCustomView());
                        if (objectives.size() == 0) {
                            Animation wiggle = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.wiggle);
                            editText1.setAnimation(wiggle);
                        } else {
                            listener.getObjectives(objectives);
                            LCreateDialog2.this.dismiss();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        EditText editText = new EditText(getActivity());
                        LinearLayout layout = (LinearLayout) dialog.getCustomView();
                        layout.addView(editText);
                    }
                });
        MaterialDialog dialog = builder.build();
        return dialog;
    }

    public List<String> getObjectiveValues(ViewGroup layout) {
        List<String> objectives = new ArrayList<>();

        int objectiveCount = layout.getChildCount();

        for (int i = 0; i < objectiveCount;i++) {
            EditText currentEditText = (EditText) layout.getChildAt(i);
            String objective = currentEditText.getText().toString();
            if (objective.length() > 1) {
                objectives.add(i, objective);
            }
        }
        return objectives;
    }

}
