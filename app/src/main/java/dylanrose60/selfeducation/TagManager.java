package dylanrose60.selfeducation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.IOException;

public class TagManager extends ActionBarActivity implements TagDataHandler.Listener {

    OkHttpClient client = new OkHttpClient();

    private String subject;
    private TagDataHandler tagDataHandler;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        this.subject = intent.getStringExtra("subject");
        tagDataHandler = new TagDataHandler(subject);
        tagDataHandler.setListener(this);

        buildOptionsList();
    }

    public void buildOptionsList() {
        final String[] options = {"New Tag","My Tags"};
        ListView listView = (ListView) findViewById(R.id.tag_manger_list);
        CustomOptionsAdapter adapter = new CustomOptionsAdapter(getApplicationContext(),R.layout.tag_manager_home_card,options);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0 :
                        newTag();
                        break;
                    case 1 :
                        tagList();
                        break;
                }
            }
        });
    }

    public void newTag() {
        final EditText editText = new EditText(this);
        new MaterialDialog.Builder(this)
                .title("New Tag")
                .positiveText("Create")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .customView(editText,true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String tagName = editText.getText().toString();
                        tagDataHandler.addTag(tagName);
                    }
                })
                .show();
    }

    @Override
    public void tagAdded() {
        SnackbarManager.show(Snackbar.with(getApplicationContext()).text("Tag created"),this);
    }



    public void tagList() {

    }
}
