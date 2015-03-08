package dylanrose60.selfeducation;



import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import dylanrose60.selfeducation.SubjectFragment.BookmarkSubjectsFragment;
import dylanrose60.selfeducation.SubjectFragment.MySubjectsFragment;
import dylanrose60.selfeducation.SubjectFragment.PublicSubjectsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] tabs = {"My Subjects","Public Subjects","Bookmarks"};

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0 :
                MySubjectsFragment mySubFrag = new MySubjectsFragment();
                return mySubFrag;
            case 1:
                PublicSubjectsFragment publicSubFrag = new PublicSubjectsFragment();
                return publicSubFrag;
            case 2:
                BookmarkSubjectsFragment privateSubFrag = new BookmarkSubjectsFragment();
                return privateSubFrag;
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
