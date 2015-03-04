package dylanrose60.selfeducation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MySubjectsFragment extends Fragment {

    public String testNum;

    public void setText(String text) {
        testNum = text;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.my_subjects_frag,root,false);
        TextView text = (TextView) layout.findViewById(R.id.fragText);
        text.setText(testNum);
        return layout;
    }

}
