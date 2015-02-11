package dylanrose60.selfeducation;

import android.widget.EditText;

public class LessonManager {

    private String subject;
    private String lessonName;
    private String[] objectives;

    public LessonManager(String subject) {
        this.subject = subject;
    }

    public void setLessonName(String lesson) {
        lessonName = lesson;
    }
    public String getLessonName() {
        return lessonName;
    }

    public void setObjectives(String[] objectives) {
        this.objectives = objectives;
    }




}
