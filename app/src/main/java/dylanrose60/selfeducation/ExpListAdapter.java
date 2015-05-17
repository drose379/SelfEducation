package dylanrose60.selfeducation;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ExpListAdapter extends BaseExpandableListAdapter {

    private Context ctxt;
    private HashMap<String,List<String>> map;
    //private List<String> categories;
    private List<Category> catFull;
    private String[] testCategories = null;

    public ExpListAdapter(Context ctxt,HashMap<String,List<String>> map,List<String> categories,List<Category> catFullInfo) {
        this.ctxt = ctxt;
        this.map = map;

        Object[] test = map.keySet().toArray();

        //Completely replace List<String> categories with testCategories (rename it to categories), make this adapter constructor not accept List<String> of categories

        testCategories = Arrays.copyOf(test,test.length,String[].class);

        //this.categories = categories;
        this.catFull = catFullInfo;
    }


    @Override
    public int getGroupCount() {
        return map.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return map.get(testCategories[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        //problem: only 2 items in array and 2 items with programming cat, programming being grabbed twice
        return testCategories[groupPosition];
    }

    @Override
    public Object getChild(int parent, int child) {
        return map.get(testCategories[parent]).get(child);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int parent, int child) {
        return child;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int parent, boolean isExpanded, View convertView, ViewGroup parentView) {
        String groupTitle = (String) getGroup(parent);
        String groupDesc = null;

        for (int i=0;i<catFull.size();i++) {
            Category currentCat = catFull.get(i);
            String catName = currentCat.getName();
            if (catName.equals(groupTitle)) {
                groupDesc = currentCat.getDescription();
            }
        }

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_parent,parentView,false);
        }

        TextView group_title = (TextView) convertView.findViewById(R.id.parentText);
        TextView group_desc = (TextView) convertView.findViewById(R.id.parentDesc);

        group_title.setText(groupTitle);
        group_desc.setText(groupDesc);

        return convertView;
    }

    @Override
    public View getChildView(int parent, int child, boolean isLastChild, View convertView, ViewGroup parentView) {
        String title = (String) getChild(parent,child);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.exp_list_child,parentView,false);
        }

        TextView childTitle = (TextView) convertView.findViewById(R.id.childText);
        childTitle.setText(title);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
