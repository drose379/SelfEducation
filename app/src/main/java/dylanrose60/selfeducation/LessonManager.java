package dylanrose60.selfeducation;

import android.widget.EditText;

public class LessonManager {

    private String subject;
    private String lessonName;

    public LessonManager(String subject) {
        this.subject = subject;
    }

    public void setLessonName(String lesson) {
        lessonName = lesson;
    }
    public String getLessonName() {
        return lessonName;
    }




}
