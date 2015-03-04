package dylanrose60.selfeducation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] tabs = {"My Subjects","Public Subjects","Bookmarks"};

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0 :
                MySubjectsFragment testFrag = new MySubjectsFragment();
                testFrag.setText("Frag1");
                return testFrag;
            case 1:
                MySubjectsFragment testFrag2 = new MySubjectsFragment();
                testFrag2.setText("Frag2");
                return testFrag2;
            case 2:
                MySubjectsFragment testFrag3 = new MySubjectsFragment();
                testFrag3.setText("Frag3");
                return testFrag3;
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public String getPageTitle(int position) {
        return tabs[position];
    }

}
