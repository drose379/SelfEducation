package dylanrose60.selfeducation.SubjectFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.pnikosis.materialishprogress.ProgressWheel;
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
import java.util.HashMap;
import java.util.List;

import dylanrose60.selfeducation.Category;
import dylanrose60.selfeducation.CustomAdapter;
import dylanrose60.selfeducation.DBHelper;
import dylanrose60.selfeducation.ExpListAdapter;
import dylanrose60.selfeducation.MainActivity;
import dylanrose60.selfeducation.R;
import dylanrose60.selfeducation.Subject;
import dylanrose60.selfeducation.SubjectDashboard;
import dylanrose60.selfeducation.SubjectManager;

public class MySubjectsFragment extends Fragment {

    private OkHttpClient httpClient = new OkHttpClient();
    private Handler handler = new Handler();
    private String ownerID;
    private List<Subject> mySubjects = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity.setFrag0(this);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.my_subjects_frag,root,false);
        return layout;
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
            getSubjects();
        }
         else {
            showWelcomeText();
         }
    }

    public void showWelcomeText() {
        ViewGroup layout = (ViewGroup) getView();

        ProgressWheel spinner = (ProgressWheel) layout.findViewById(R.id.spinnerAnimation);
        LinearLayout logoText = (LinearLayout) layout.findViewById(R.id.logoLayout);
        TextView welcomeText1 = (TextView) layout.findViewById(R.id.welcomeText1);
        TextView welcomeText2 = (TextView) layout.findViewById(R.id.welcomeText2);

        //Fade animations in and out using AlphaAnimation

        spinner.setVisibility(View.GONE);
        logoText.setVisibility(View.VISIBLE);
        welcomeText1.setVisibility(View.VISIBLE);
        welcomeText2.setVisibility(View.VISIBLE);
    }

    public String toJSONString(String ownerID) {
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



    public void getSubjects() {
        String json = toJSONString(ownerID);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json);
        Request.Builder rBuilder = new Request.Builder();
        rBuilder.post(body);
        rBuilder.url("http://codeyourweb.net/httpTest/index.php/getLocalSubs");
        Request request = rBuilder.build();
        Call newCall = httpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseString = response.body().string();
                try {
                    JSONObject responseObject = new JSONObject(responseString);
                    String categories = responseObject.getString("categories");
                    String subFullInfo = responseObject.getString("fullSubInfo");

                    /*
                        * Make a list out of categories
                        * Make a list containing subjbect objects (must add a category field to the subject class
                        * Create  a new HashMap instance
                        * Loop over each category
                        * Get the keySet() for the hashmap, if the hashmap keyset does not contain the current category,
                        * Create a new List<String>
                        * Inner loop:
                        * Loop over the list of subjects, get subject category for each subject, if the categry matches the current category
                        * Add it to the current list
                        * Make sure new list is created for each category
                     */

                    List<String> catList = catToList(categories);
                    List<Subject> subFullList = subToList(subFullInfo);
                    HashMap<String,List<String>> subInfoMap = mapData(catList,subFullList);

                    List<String> finalCatList = new ArrayList<String>(subInfoMap.keySet());

                    List<Category> catFullInfo = fullCatBuilder(categories);

                    buildList(subInfoMap, finalCatList, catFullInfo);
                    //Waht is difference between finalCatList and catList? (catList does not funcion w/ adapter)
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public List<String> catToList(String catString) throws JSONException {
        List<String> tempList = new ArrayList<String>();
            JSONArray catArray = new JSONArray(catString);
            for (int i=0;i<catArray.length();i++) {
                JSONObject currentObject = catArray.getJSONObject(i);
                String currentCat = currentObject.getString("category");
                tempList.add(currentCat);
            }
        return tempList;
    }

    public List<Subject> subToList(String subFull) throws JSONException {
        List<Subject> subjectFull = new ArrayList<Subject>();
        JSONArray subArray = new JSONArray(subFull);
        for (int i=0;i<subArray.length();i++) {
            JSONObject subObject =  subArray.getJSONObject(i);
            String subName = subObject.getString("name");
            String category = subObject.getString("category");
            subjectFull.add(new Subject(subName,category));
        }
        return subjectFull;
    }

    public HashMap<String,List<String>> mapData(List<String> categories,List<Subject> subjects) {
        HashMap<String,List<String>> map = new HashMap<String,List<String>>();
        for (String category : categories) {
            List<String> tempItems = new ArrayList<String>();
            for (Subject subject : subjects) {
                String subName = subject.getSubjectName();
                String cat = subject.getCategory();
                if (category .equals(cat)) {
                    tempItems.add(subName);
                }
            }
            if (tempItems.size() > 0) {
                map.put(category, tempItems);
            }
        }
        return map;
    }

    public List<Category> fullCatBuilder(String catInfo) throws JSONException {
        //Create an array list and create Category objects, then add the refs to a list
        List<Category> catList = new ArrayList<Category>();
        JSONArray catArray = new JSONArray(catInfo);
        for(int i=0;i<catArray.length();i++) {
            JSONObject catObj = catArray.getJSONObject(i);
            String catName = catObj.getString("category");
            String catDesc = catObj.getString("description");

            catList.add(new Category(catName,catDesc));
        }
        return catList;
    }


    public void buildList(final HashMap<String,List<String>> map,final List<String> categories,final List<Category> catFullInfo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout logoText = (LinearLayout) getView().findViewById(R.id.logoLayout);
                ProgressWheel loader = (ProgressWheel) getView().findViewById(R.id.spinnerAnimation);
                TextView welcomeText1 = (TextView) getView().findViewById(R.id.welcomeText1);
                TextView welcomeText2 = (TextView) getView().findViewById(R.id.welcomeText2);

                if (categories.size() > 0) {
                    logoText.setVisibility(View.GONE);
                    loader.setVisibility(View.GONE);
                    welcomeText1.setVisibility(View.GONE);
                    welcomeText2.setVisibility(View.GONE);

                    ExpandableListView expList = (ExpandableListView) getView().findViewById(R.id.expSubList);
                    ExpListAdapter adapter = new ExpListAdapter(getActivity(), map, categories, catFullInfo);
                    expList.setAdapter(adapter);
                    expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent,View v,int groupPosition,int childPosition,long id) {
                            List<String> group = map.get(categories.get(groupPosition));
                            String child = group.get(childPosition);

                            Intent newAct = new Intent(getActivity(),SubjectDashboard.class);
                            newAct.putExtra("subjectName",child);
                            startActivity(newAct);

                            return true;
                        }
                    });
                } else {
                    showWelcomeText();
                }
            }
        });
    }

/*

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu,v,info);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.subject_list_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String subjectName = null;
        if (mySubjects != null && mySubjects.size() > 0) {
            //Subject selectedSubject = mySubjects.get(info.);
            Log.i("itemPosition",String.valueOf(info.position));
            Log.i("itemPosition",mySubjects.get(info.position).getSubjectName());
            subjectName = mySubjects.get(info.position).getSubjectName();
        }
            switch (item.getItemId()) {
                case R.id.delete:
                    deleteConfirm(subjectName);
                    return true;

                default:
                    return false;
            }

    }

    public void deleteConfirm(final String subject) {
        SnackbarManager.show(Snackbar.with(getActivity())
                .text("Are you sure?")
                .actionLabel("Confirm")
                .actionColor(Color.RED)
                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        snackbar.dismiss();
                        deleteSubject(subject);
                    }
                }), getActivity());
    }

    public void deleteSubject(String subject) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody rBody = RequestBody.create(mediaType,subject);

        Request.Builder rBuilder = new Request.Builder();
        rBuilder.post(rBody);
        rBuilder.url("http://codeyourweb.net/httpTest/index.php/deleteSubject");
        Request request = rBuilder.build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                deletedCallback();
            }
        });
    }

    public void deletedCallback() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SnackbarManager.show(Snackbar.with(getActivity()).text("Deleted Successfully"),getActivity());
            }
        });
        onStart();
    }

    */

}
