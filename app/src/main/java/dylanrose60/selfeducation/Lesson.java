package dylanrose60.selfeducation;

public class Lesson {

    private String lessonName;
    private int priority;

    public Lesson(String lesson,int priority) {
        lessonName = lesson;
        this.priority = priority;
    }

    public String getLessonName() {
        return lessonName;
    }

    public int getPriority() {
        return priority;
    }

}
