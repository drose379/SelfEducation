package dylanrose60.selfeducation.SubjectFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dylanrose60.selfeducation.R;

public class PublicSubjectsFragment extends Fragment {


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
}
