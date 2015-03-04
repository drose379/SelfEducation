package dylanrose60.selfeducation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static String DBName = "subjects";
    private String tableName = "subject_info";
    private String col1 = "ID";
    private String col2 = "subject";

    public DBHelper(Context context)  {
        super(context,DBName,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //DB schema here
        String subjectTable = "CREATE TABLE " + tableName + "(" + col1 + " INTEGER PRIMARY KEY, " + col2 + " TEXT);";
        database.execSQL(subjectTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,int oldSchema,int newSchema) {
        //if ever need to upgrade schema
    }

}
