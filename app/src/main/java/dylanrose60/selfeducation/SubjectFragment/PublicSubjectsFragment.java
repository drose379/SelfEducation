package dylanrose60.selfeducation.SubjectFragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
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

import dylanrose60.selfeducation.CustomAdapter;
import dylanrose60.selfeducation.DBHelper;
import dylanrose60.selfeducation.R;
import dylanrose60.selfeducation.Subject;
import dylanrose60.selfeducation.SubjectDashboard;

public class PublicSubjectsFragment extends Fragment {

    private OkHttpClient client = new OkHttpClient();
    private Handler handler = new Handler();
    private String ownerID;
    private SwipeRefreshLayout swipeRefresh;
    private List<Subject> subjects;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup root,Bundle savedInstance) {
        View view = inflater.inflate(R.layout.public_subject_frag,root,false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLocalSubjects();
    }

    public void getLocalSubjects() {
        DBHelper dbClient = new DBHelper(getActivity());
        SQLiteDatabase db = dbClient.getReadableDatabase();
        String query = "SELECT ID FROM owner_id";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            ownerID = cursor.getString(cursor.getColumnIndex("ID"));
            getPublicSubjects();
        } else {
            //Called if user has no personal subjects that need to be removed from PUBLIC list
            getAllPublicSubjects();
        }
    }

    public String toJSONString() {
        JSONStringer json = new JSONStringer();
        try {
            json.object();
            json.key("owner_id");
            json.value(ownerID);
            json.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public void getPublicSubjects() {
        String json = toJSONString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/getPublicSubject1");
        Request request = builder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onResponse(final Response response) throws IOException {
               final String responseString = response.body().string();
                try {
                    /*
                        *Use responseString to build Lists out of two arrays sent from server
                        *Call method that gets passed both bookmark list and subject list
                        *Method removes all items found in the bookmarks list from the public subjects list
                        *Returns the new list without bookmarked items
                    */
                    final JSONObject master = new JSONObject(responseString);
                    //Get sub arrays ("bookmarks","subjects")
                    final String subjects = master.get("subjects").toString();
                    final String bookmarks = master.get("bookmarks").toString();
                    /*
                        * Create List out of subjects and out of bookmarks
                        * Send both Lists to method that removes each bookmark from the subject list
                        * The method described above will return the modified (final) List, ready to be passed to buildList()
                    */
                    List<String> bookmarkList = toBookmarkList(bookmarks);
                    List<String> subjectNameList = getSubjectNames(subjects);
                    List<Subject> subjectFullList = toArray(subjects);

                    //Loop over bookmarks instead of all subjects
                    for (int i=0;i<bookmarkList.size();i++) {
                        String currentBookmark = bookmarkList.get(i);
                        if (subjectNameList.contains(currentBookmark)) {
                            subjectNameList.remove(currentBookmark);
                        }
                    }
                    Log.i("finalSubList",subjectNameList.toString());

                    /*
                        * Start with request of all public subject NAMES (not full info), put into a list (only something the user didnt create)
                        * In the same request as above, also grab all users bookamrks and put into List
                        * Use bookmarks List and loop over, checking if the bookmarks are found in the subject name list, if they are, remove them
                        * Use the subject name array after all bookmarks are removed to make another request to get full info for remaining subjects
                    */
                    //buildList(finalSubList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request,IOException e) {

            }
        });
    }

    public void getAllPublicSubjects() {
        Request.Builder builder = new Request.Builder();
        builder.url("http://codeyourweb.net/httpTest/index.php/getPublicSubject2");
        Request request = builder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                String responseString = response.body().string();
                try {
                    List<Subject> subjectList = toArray(responseString);
                    buildList(subjectList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(Request request,IOException e) {

            }
        });
    }

    public List<Subject> toArray(String jsonString) throws JSONException {
        JSONArray json = new JSONArray(jsonString); //Need to pass in the real JSON String to here
        List<Subject> list = new ArrayList<Subject>();
        for(int i = 0;i<json.length();i++) {
            JSONObject jObject = json.getJSONObject(i);
            list.add(new Subject(jObject.getString("name"),jObject.getString("start_date"),jObject.getInt("lesson_count")));
        }
        subjects = list;
        return list;
    }

    public List toBookmarkList(String bookmarkString) throws JSONException {
        JSONArray json = new JSONArray(bookmarkString);
        List<String> bookmarkList = new ArrayList<>();
        for (int i = 0; i < json.length();i++) {
            JSONObject currentBookmark = json.getJSONObject(i);
            String bookmarkValue = currentBookmark.getString("subject_name");
            bookmarkList.add(bookmarkValue);
        }
        return bookmarkList;
    }

    public List getSubjectNames(String subjects) throws JSONException {
        List<String> subNames = new ArrayList();
        JSONArray json = new JSONArray(subjects);
        for (int i=0;i<json.length();i++) {
            JSONObject currentSub = json.getJSONObject(i);
            String subName = currentSub.getString("name");
            subNames.add(subName);
        }
        return subNames;
    }

    public void buildList(final List array) {
        ViewGroup layout = (ViewGroup) getView();

        //Build listview

        final ListView listView = (ListView) layout.findViewById(R.id.publicSubjectList);
        registerForContextMenu(listView);
        final ArrayAdapter<Subject> adapter = new CustomAdapter(getActivity(),R.layout.subject_card_view,array);
        swipeRefresh = (SwipeRefreshLayout) layout.findViewById(R.id.publicSwipeView);
        swipeRefresh.setColorSchemeResources(R.color.ColorPrimary, R.color.ColorMenuAccent, R.color.ColorSubText);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
                final List<Subject> list = array;
                //registerForContextMenu(listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent,View view,int position,long id) {
                        Subject selectedSubject = list.get(position);
                        String subjectName = selectedSubject.getSubjectName();
                        Intent newAct = new Intent(getActivity(),SubjectDashboard.class);
                        newAct.putExtra("subjectName",subjectName);
                        startActivity(newAct);
                    }
                });
                //Check if refresh is going, if it is, shut it off
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PublicSubjectsFragment.this.onStart();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu,v,info);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.public_sub_menu,menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Subject selectedSubject = subjects.get(info.position);
        String subjectName = selectedSubject.getSubjectName();
        switch(item.getItemId()) {
            case R.id.bookmark :
                addBookmark(subjectName);
                return true;
            case R.id.hide :
                //Create method to send subject and owner info to hidden table on web server
            default:
                return false;
        }
    }

    public String bookmarkToJSON(String subject) {
        JSONStringer jsonStringer = new JSONStringer();
        try {
            jsonStringer.object();
            jsonStringer.key("owner_id");
            jsonStringer.value(ownerID);
            jsonStringer.key("subject");
            jsonStringer.value(subject);
            jsonStringer.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStringer.toString();
    }

    public void addBookmark(final String subject) {
        String json = bookmarkToJSON(subject);

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/newBookmark");
        Request request = builder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                bookmarkSuccess(subject);
                PublicSubjectsFragment.this.onStart();
            }
        });
    }

    public void bookmarkSuccess(final String subject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SnackbarManager.show(Snackbar.with(getActivity())
                        .text("Bookmark Created")
                        .actionLabel("Go")
                        .actionColor(Color.GREEN)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                goToSubject(subject);
                            }
                        }), getActivity());
            }
        });

    }

    public void goToSubject(String subject) {
        Intent newAct = new Intent(getActivity(),SubjectDashboard.class);
        newAct.putExtra("subjectName",subject);
        startActivity(newAct);
    }

}
