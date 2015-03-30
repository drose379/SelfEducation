package dylanrose60.selfeducation;

public class Subject {

    private String subjectName;
    private String category;

    public Subject(String name,String category) {
        subjectName = name;
        this.category = category;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getCategory() {return category; }

}
