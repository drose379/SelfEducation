package dylanrose60.selfeducation;

import android.graphics.Bitmap;

public class LessonPackage {

    private String lessonName;
    private Bitmap lessonImage;
    private String objective;

    public LessonPackage(String lessonName,Bitmap lessonImage) {
        this.lessonName = lessonName;
        this.lessonImage = lessonImage;
    }

    public String getName() {
        return this.lessonName;
    }
    public Bitmap getImage() {
        return this.lessonImage;
    }
    public void setObjective(String objective) {
        this.objective = objective;
    }
    public String getObjective() {
        return this.objective;
    }

}
