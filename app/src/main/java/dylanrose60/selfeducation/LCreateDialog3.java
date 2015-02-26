package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

@SuppressLint("NewApi")
public class LCreateDialog3 extends DialogFragment {

    private LinearLayout dialogLayout;
    private OkHttpClient client = new OkHttpClient();
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        dialogLayout = new LinearLayout(getActivity());

        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setLayoutParams(dialogParams);
    }

    public Dialog onCreateDialog(Bundle savedInstance) {
        getTags(false);
        ProgressWheel loadWheel = new ProgressWheel(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80,80);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        loadWheel.setLayoutParams(params);
        loadWheel.setBarColor(getResources().getColor(R.color.ColorPrimary));
        loadWheel.spin();
        dialogLayout.addView(loadWheel);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("Tag your new lesson")
                .customView(dialogLayout,true);
        Dialog dialog = builder.build();
        return dialog;
    }

    public void getTags(final boolean newTag){
        Request.Builder builder = new Request.Builder();
        builder.url("http://codeyourweb.net/httpTest/index.php/getTags");
        Request ready = builder.build();
        Call call = client.newCall(ready);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String responseBody = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialogLayout.getChildAt(0).setVisibility(View.GONE);
                        addToDialog(responseBody);
                    }
                });
            }
        });
    }

    public String[] buildTagArray(String tagJson) {
         try {
            JSONArray array = new JSONArray(tagJson);
            String[] tagArray = new String[array.length()];

            for (int i = 0;i < array.length();i++) {
                String tag = array.getJSONObject(i).getString("tag_name");
                tagArray[i] = tag;
            }
                return tagArray;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    public void addToDialog(String JSON) {
        String[] tags = buildTagArray(JSON);
        for (String tag : tags) {
            CheckBox checkbox = new CheckBox(getActivity());
            checkbox.setText(tag);
            dialogLayout.addView(checkbox);
        }
    }

}



//Method for getting tags

//When tags request is complete, call a method w/ a loop that FIRST, HIDES THE LOADER. THEN: addds each checkbox to view
//DialogLayout must be a field (accessable throughout entire class)
