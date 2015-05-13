package dylanrose60.selfeducation;

import android.graphics.Bitmap;

public class LessonPackage {

    private String lessonName;
    private Bitmap lessonImage;
    private String objective;

    public LessonPackage(String lessonName,String objective) {
        this.lessonName = lessonName;
        this.objective = objective;
        this.lessonImage = lessonImage;
    }

    public String getName() {
        return this.lessonName;
    }
    public Bitmap getImage() {
        return this.lessonImage;
    }

    public String getObjective() {
        return this.objective;
    }

}
