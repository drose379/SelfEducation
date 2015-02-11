package dylanrose60.selfeducation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubjectList extends ActionBarActivity {

    OkHttpClient client = new OkHttpClient();
    List<Subject> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_listview);
        ListView list = (ListView) findViewById(R.id.subjectList);
        registerForContextMenu(list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("Select a subject");

        viewController();
        getSubjects();
    }

    public void viewController() {
        ListView listElement = (ListView) findViewById(R.id.subjectList);
        listElement.setVisibility(View.GONE);
    }

    public void getSubjects() {
        final ListView listElement = (ListView) findViewById(R.id.subjectList);
        final ProgressBar spinAnimation = (ProgressBar) findViewById(R.id.spinAnimation1);
        final LinearLayout logoText = (LinearLayout) findViewById(R.id.logoLayout);

        Request.Builder builder = new Request.Builder();
        builder.url("http://codeyourweb.net/httpTest/index.php/getSubjects");
        Request readyRequest = builder.build();
        Call call = client.newCall(readyRequest);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(final Response response) throws IOException {
                listElement.post(new Runnable() {
                                     @Override
                                     public void run() {
                                         spinAnimation.setVisibility(View.GONE);
                                         logoText.setVisibility(View.GONE);
                                         listElement.setVisibility(View.VISIBLE);
                                     }
                                 });
                String responseString = response.body().string();
                try {
                    List subjectList = toArray(responseString);
                    buildList(subjectList);
                } catch (JSONException e) {
                    //Handle errors
                }
            }
            @Override
            public void onFailure(Request request,IOException e) {

            }
        });
    }

    //Change this method to add Subject objects to the list, each subject object will hold the values subject and start_date, and have methods to return them
    public List<Subject> toArray(String jsonString) throws JSONException {
        JSONArray json = new JSONArray(jsonString); //Need to pass in the real JSON String to here
        List<Subject> list = new ArrayList<Subject>();
        for(int i = 0;i<json.length();i++) {
            JSONObject jObject = json.getJSONObject(i);
            list.add(new Subject(jObject.getString("name"),jObject.getString("start_date"),jObject.getInt("lesson_count")));
        }
        return list;
    }

    public void buildList(final List array) {
        View listViewLayout = findViewById(R.id.subject_home);
        final ListView listView = (ListView) findViewById(R.id.subjectList);
        final ArrayAdapter<Subject> adapter = new CustomAdapter(getApplicationContext(),R.layout.subject_card_view,array);
        listViewLayout.post(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
                list = array;
                    //registerForContextMenu(listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent,View view,int position,long id) {
                        Subject selectedSubject = list.get(position);
                        String subjectName = selectedSubject.getSubjectName();
                        Intent newAct = new Intent(getApplicationContext(),SubjectDashboard.class);
                        newAct.putExtra("subjectName",subjectName);
                        startActivity(newAct);
                    }
                });
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu,v,info);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subject_list_menu,menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Subject selectedSubject = list.get(info.position);
        String subjectName = selectedSubject.getSubjectName();
        switch(item.getItemId()) {
            case R.id.delete :
                deleteSubject(subjectName);
                return true;
            default:
                return false;
        }
    }

    public void deleteSubject(String subject) {
        Toast.makeText(getApplicationContext(),"Deleted: " + subject,Toast.LENGTH_LONG).show();

        //String passable = createJson(subject);
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody rBody = RequestBody.create(mediaType,subject);

        Request.Builder rBuilder = new Request.Builder();
        rBuilder.post(rBody);
        rBuilder.url("http://codeyourweb.net/httpTest/index.php/deleteSubject");
        Request request = rBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                getSubjects();
            }
        });
    }

    public String createJson(String subject) {
            JSONStringer json = new JSONStringer();
            try {
                json.key("subjectName");
                json.value(subject);
                return json.toString();
            } catch (JSONException e) {
                throw new RuntimeException();
            }

    }

}
