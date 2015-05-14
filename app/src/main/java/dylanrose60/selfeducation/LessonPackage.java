package dylanrose60.selfeducation;

import android.graphics.Bitmap;

import java.util.List;

public class LessonPackage {

    private String lessonName;
    private List<String> tags;

    public LessonPackage(String lessonName,List<String> tags) {
        this.lessonName = lessonName;
        this.tags = tags;
    }

    public String getName() {
        return this.lessonName;
    }

    public String getTags() {
        String tagsString = tags.toString();
        String test = tagsString.replace("[","");
        String finalString = test.replace("]","");
        return finalString;
    }


}