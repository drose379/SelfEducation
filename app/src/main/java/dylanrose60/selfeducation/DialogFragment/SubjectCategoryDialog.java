package dylanrose60.selfeducation.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dylanrose60.selfeducation.R;

@SuppressLint("NewApi")
public class SubjectCategoryDialog extends DialogFragment {

    private Listener listener;

    private Bundle subjectInfo;

    private OkHttpClient httpClient = new OkHttpClient();
    private String categoriesString = null;

    private Handler handler = new Handler();

    private LinearLayout dialogLayout;
    private RadioGroup rGroup;

    public interface Listener {
        public void subInfoComplete(Bundle subjectInfo);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (Listener) activity;
        subjectInfo = getArguments();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        dialogLayout = new LinearLayout(getActivity());

        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setLayoutParams(dialogParams);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        getCategories();
        //loop over the list, add each one as a checkbox


        rGroup = new RadioGroup(getActivity());
        dialogLayout.addView(rGroup);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title("Choose a Category");
        builder.customView(dialogLayout,true);
        builder.positiveText("Create");
        builder.negativeText("Cancel");
        builder.neutralText("Add Category");
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                //Get which category was selected, add to subjectInfo bundle, call listener from MainActivy, ready to insert to db
                int buttons = rGroup.getChildCount();
                for(int i=0;i<buttons;i++) {
                    RadioButton currentButton = (RadioButton) rGroup.getChildAt(i);
                    if (currentButton.isChecked()) {
                        String category = (String) currentButton.getText();
                        subjectInfo.putString("category",category);
                        listener.subInfoComplete(subjectInfo);
                    }
                }
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onNeutral(MaterialDialog dialog) {
                //new dialog for creating new category
            }
        });

        MaterialDialog finalDialog = builder.build();
        return finalDialog;
    }

    public void getCategories() {
        Request.Builder rBuilder = new Request.Builder();
        rBuilder.url("http://codeyourweb.net/httpTest/index.php/getCategories");
        Request request = rBuilder.build();
        Call newCall = httpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseString = response.body().string();
                categoriesString = responseString;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        buildList();
                    }
                });
            }
        });
    }

    public void buildList() {
        try {
            JSONArray categories = new JSONArray(categoriesString);
            for(int i=0;i<categories.length();i++) {
                JSONObject catObject = categories.getJSONObject(i);
                String currentCat = (String) catObject.get("category");
                RadioButton rButton = new RadioButton(getActivity());
                rButton.setText(currentCat);
                rGroup.addView(rButton);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

}
