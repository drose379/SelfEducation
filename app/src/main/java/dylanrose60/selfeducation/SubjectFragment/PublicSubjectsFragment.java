package dylanrose60.selfeducation.SubjectFragment;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.internal.view.menu.MenuBuilder;
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
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import dylanrose60.selfeducation.Category;
import dylanrose60.selfeducation.CommunicationUtil;
import dylanrose60.selfeducation.CustomAdapter;
import dylanrose60.selfeducation.DBHelper;
import dylanrose60.selfeducation.ExpListAdapter;
import dylanrose60.selfeducation.FragmentUtility;
import dylanrose60.selfeducation.R;
import dylanrose60.selfeducation.Subject;
import dylanrose60.selfeducation.SubjectDashboard;

public class PublicSubjectsFragment extends Fragment {

    private OkHttpClient client = new OkHttpClient();
    private FragmentUtility fragUtil = new FragmentUtility();

    private Handler handler = new Handler();

    private String ownerID;
    private SwipeRefreshLayout swipeRefresh;
    private List<Subject> subjects;

    //Map needs to be made accessable from the context menu override method. Needs access to all values.
    private HashMap<String,List<String>> map = null;
    private List<String> categories = new ArrayList<String>();

    static BookmarkSubjectsFragment bookmarkFrag;

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

    static void setBookmarkFrag(BookmarkSubjectsFragment frag) {
        bookmarkFrag = frag;
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
            Random rand = new Random();
            int randID = rand.nextInt(1000000);
            SQLiteDatabase writableDB = dbClient.getWritableDatabase();
            String insertID = "INSERT INTO owner_id (ID) VALUES ('"+randID+"')";
            writableDB.execSQL(insertID);
            getLocalSubjects();
        }
    }

    public void getPublicSubjects() {
        String json = null;

        List<String> key = new ArrayList<String>();
        List<String> value = new ArrayList<String>();

        key.add("owner_id");
        value.add(ownerID);

        try {
            json = CommunicationUtil.toJSONString(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/getSubNames");
        Request request = builder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onResponse(final Response response) throws IOException {
                final String responseString = response.body().string();
                try {
                    /*
                        * Start with request of all public subject NAMES (not full info), put into a list (only something the user didnt create) TEST
                        * In the same request as above, also grab all users bookamrks and put into List
                        * Use bookmarks List and loop over, checking if the bookmarks are found in the subject name list, if they are, remove them
                        * Use the subject name array after all bookmarks are removed to make another request to get full info for remaining subjects
                    */

                    final JSONObject master = new JSONObject(responseString);
                    //Get sub arrays ("bookmarks","subjects")
                    final String subjects = master.get("subjects").toString();
                    final String bookmarks = master.get("bookmarks").toString();
                    final String hiddenSubs = master.get("hidden").toString();

                    Log.i("allSubs",subjects);

                    List<String> bookmarkList = toBookmarkList(bookmarks);
                    List<String> subjectNameList = getSubjectNames(subjects);
                    List<String> hiddenNameList = getHiddenNames(hiddenSubs);

                    //Remove all items that are bookmarked from list
                    for (int i=0;i<bookmarkList.size();i++) {
                        String currentBookmark = bookmarkList.get(i);
                        if (subjectNameList.contains(currentBookmark)) {
                            subjectNameList.remove(currentBookmark);
                        }
                    }
                    //Remove all items that are "hidden" from list
                    for (int i=0;i<hiddenNameList.size();i++) {
                        String currentItem = hiddenNameList.get(i);
                        if (subjectNameList.contains(currentItem)) {
                            subjectNameList.remove(currentItem);
                        }
                    }
                    //Get full info on any subject that has passed through checks
                    getFinalSubjects(subjectNameList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request,IOException e) {

            }
        });
    }



    public void getFinalSubjects(List<String> subNames) {
        JSONArray jsonArray = new JSONArray(subNames);
        String json = jsonArray.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json);
        Request.Builder builder = new Request.Builder();
        builder.url("http://codeyourweb.net/httpTest/index.php/getPubSubFull1");
        builder.post(body);
        Request request = builder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseString = response.body().string();
                try {
                    JSONObject responseObject = new JSONObject(responseString);

                    String subInfo = responseObject.getString("subjectInfo");
                    String categories = responseObject.getString("catInfo");

                    List<String> catList = fragUtil.catToList(categories);
                    List<Subject> fullSubList = fragUtil.subToList(subInfo,false);
                    HashMap<String,List<String>> map = fragUtil.mapData(catList, fullSubList,false);
                    //Need to set map to field so context menu has access to it
                    setContextMenuData(map,catList);

                    List<Category> fullCatInfo = fragUtil.fullCatBuilder(categories);

                    buildList(map,catList,fullCatInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setContextMenuData(HashMap<String,List<String>> map,List<String> categories) {
        this.map = map;
        this.categories = categories;
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

    public List getHiddenNames(String hiddenItems) throws JSONException {
        List<String> hiddenSubs = new ArrayList();
        JSONArray json = new JSONArray(hiddenItems);
        for (int i=0;i<json.length();i++) {
            JSONObject currentSub = json.getJSONObject(i);
            String subName = currentSub.getString("subject_name");
            hiddenSubs.add(subName);
        }
        return hiddenSubs;
    }

    public void buildList(final HashMap<String,List<String>> map,final List<String> categories,final List<Category> catFullInfo) {
        swipeRefresh = (SwipeRefreshLayout) getView().findViewById(R.id.publicSwipeView);
        swipeRefresh.setColorSchemeResources(R.color.ColorPrimary, R.color.ColorMenuAccent, R.color.ColorSubText);

        final ExpandableListView expList = (ExpandableListView) getView().findViewById(R.id.expSubList);
        final ExpListAdapter adapter = new ExpListAdapter(getActivity(),map,categories,catFullInfo);
        handler.post(new Runnable() {
            @Override
            public void run() {
                expList.setAdapter(adapter);

        /*
                expList.setOnItemLongClickListener(new ExpandableListView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView parent,View v,int position,long id) {
                        ExpandableListView expListInside = (ExpandableListView) parent;

                        //Get type that was selected (Parent or child)
                        long pos = expListInside.getExpandableListPosition(position);
                        int type = expListInside.getPackedPositionType(pos);

                        //Only fire if child was long pressed
                        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                            //Get position of parent and child in order to grab value from HashMap
                            int parentPos = expListInside.getPackedPositionGroup(pos);
                            int childPos = expListInside.getPackedPositionChild(pos);

                            final String selectedSub = map.get(categories.get(parentPos)).get(childPos);
                            final String selectedCat = categories.get(parentPos);

                            //Inflate the menu
                            String[] menuOptions = {"Move To Bookmarks","Hide"};
                            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
                            builder.items(menuOptions);
                            builder.itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                    if (i == 0) {
                                        //need to pass category too
                                        addBookmark(selectedSub,selectedCat);
                                    } else {
                                        hideSubject(selectedSub);
                                    }
                                }
                            });
                            MaterialDialog menu = builder.build();
                            menu.show();
                        } else {
                            Log.i("childLong","Parent long pressed");
                        }
                        return true;
                    }
                });

        */

                expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView view,View v,int group,int child,long id) {
                        List<String> groupList = map.get(categories.get(group));
                        //List<String> allCategories = new ArrayList<String>(map.keySet());

                        //Need to use the same map list as the one being displayed on the fragment list

                        String category = categories.get(group);
                        String subject = groupList.get(child);

                        Intent newAct = new Intent(getActivity(),SubjectDashboard.class);
                        Bundle selectedInfo = new Bundle();
                        selectedInfo.putString("category",category);
                        Log.i("selCategory",category);
                        selectedInfo.putString("subName",subject);
                        selectedInfo.putString("ownerID",ownerID);
                        selectedInfo.putInt("subType",1);
                        newAct.putExtra("selectedInfo",selectedInfo);

                        SubjectDashboard.setPublicSubFrag(PublicSubjectsFragment.this);

                        startActivity(newAct);

                        return true;
                    }
                });
            }
        });



        handler.post(new Runnable() {
            @Override
            public void run() {
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
                bookmarkFrag.onStart();
            }
        });
    }

    public void hideSubject(String subject) {
        //Hide the subject
        //Refresh the list
        //Make sure to remove hidden subjects from the master subject list, (just like bookmarks are removed from the master array)
        String json = subjectToJson(subject);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json);
        Request.Builder rBuilder = new Request.Builder();
        rBuilder.post(body);
        rBuilder.url("http://codeyourweb.net/httpTest/index.php/hideSubject");
        Request request = rBuilder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                PublicSubjectsFragment.this.onStart();
            }
        });
    }


    public String subjectToJson(String subject) {
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

/*

    public void addBookmark(final String subject,final String category) {
        //Inflate dialog asking bookmark prefs
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout dialogLayout = (LinearLayout) inflater.inflate(R.layout.bookmark_prefs,null);



        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getActivity());
        dialogBuilder.title("Bookmark Preferences");
        dialogBuilder.customView(dialogLayout,true);
        dialogBuilder.positiveText("Save");
        dialogBuilder.negativeText("Cancel");
        dialogBuilder.positiveColor(getResources().getColor(R.color.ColorSubText));
        dialogBuilder.negativeColor(Color.RED);
        dialogBuilder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                //Get radio selections, save them to strings
                //Create json bundle from all bookmark info
                //Send it to server to be sorted
                String subscribe;
                String publicLessons;

                RadioGroup rGroup1 = (RadioGroup) dialogLayout.findViewById(R.id.question1);
                RadioGroup rGroup2 = (RadioGroup) dialogLayout.findViewById(R.id.question2);

                int selectedID1 = rGroup1.getCheckedRadioButtonId();
                int selectedID2 = rGroup2.getCheckedRadioButtonId();

                RadioButton selectedButton1 = (RadioButton) dialogLayout.findViewById(selectedID1);
                RadioButton selectedButton2 = (RadioButton) dialogLayout.findViewById(selectedID2);

                String selectedItem1 = (String) selectedButton1.getText();
                String selectedItem2 = (String) selectedButton2.getText();

                subscribe = selectedItem1;
                publicLessons = selectedItem2;

                //Bundle up all info and make request

                //Need to create random bookmarkID
                Random rand = new Random();
                int bookmarkId = rand.nextInt(1000000);

                List<String> keys = new ArrayList<String>();
                List<String> values = new ArrayList<String>();

                keys.add("subName");
                keys.add("category");
                keys.add("ownerID");
                keys.add("bookmarkID");
                keys.add("subscribe");
                keys.add("publicLessons");

                values.add(subject);
                values.add(category);
                values.add(ownerID);
                values.add(String.valueOf(bookmarkId));
                values.add(subscribe);
                values.add(publicLessons);

                try {
                    String bookmarkInfo = CommunicationUtil.toJSONString(keys, values);
                    createBookmark(bookmarkInfo,subject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            public void onNegative(MaterialDialog dialog) {
                dialog.dismiss();
            }
        });

        MaterialDialog dialog = dialogBuilder.build();
        dialog.show();

    }
*/
    public void createBookmark(String bookmarkInfo,final String bookmarkName) {
        //make request
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),bookmarkInfo);
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
                bookmarkSuccess(bookmarkName);
                PublicSubjectsFragment.this.onStart();
                bookmarkFrag.onStart();
            }
        });
    }

    public void bookmarkSuccess(final String bookmark) {
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
                                goToSubject(bookmark);
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
