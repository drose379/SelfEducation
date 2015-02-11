package dylanrose60.selfeducation;

public class Subject {

    public String subjectName;
    public String startDate;
    public int lessonCount;

    public Subject(String name,String date,int lessonCount) {
        subjectName = name;
        startDate = date;
        this.lessonCount = lessonCount;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getStartDate() {
        return startDate;
    }

    public int getLessonCount() { return lessonCount; }

}
