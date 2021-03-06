package dylanrose60.selfeducation;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Class with utility methods for 3 main subject fragments (Local,Public,Bookmarked)

public class FragmentUtility {

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

    public List<Subject> subToList(String subFull,boolean fromBookmarks) throws JSONException {
        List<Subject> subjectFull = new ArrayList<Subject>();
        JSONArray subArray = new JSONArray(subFull);
        for (int i=0;i<subArray.length();i++) {
            JSONObject subObject =  subArray.getJSONObject(i);
            String subName;
            if (fromBookmarks) {
                subName = subObject.getString("subject_name");
            } else {
                subName = subObject.getString("name");
            }

            String category = subObject.getString("category");
            subjectFull.add(new Subject(subName,category));
        }
        return subjectFull;
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


    public HashMap<String,List<String>> mapData(List<String> categories,List<Subject> subjects,boolean debug) {
        HashMap<String,List<String>> map = new HashMap<String,List<String>>();
        for (String category : categories) {
            List<String> tempItems = new ArrayList<String>();
            for (Subject subject : subjects) {
                String subName = subject.getSubjectName();
                String cat = subject.getCategory();
                if (category.trim() .equals(cat.trim())) {
                    tempItems.add(subName);
                    if (debug) {Log.i("itemAdded",subName);}
                }
            }
            if (tempItems.size() > 0) {
                map.put(category, tempItems);
            }
        }
        return map;
    }

    public List<String> trimCategories(List<String> categories, HashMap<String,List<String>> map) {

        for (int i = 0; i<categories.size(); i++) {
            String category = categories.get(i);
            if (!map.containsKey(category)) {
                categories.remove(category);
                Log.i("catRemoved",category);
            }
        }
        return categories;
    }

}
