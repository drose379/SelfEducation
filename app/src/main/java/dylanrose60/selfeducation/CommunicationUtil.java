package dylanrose60.selfeducation;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.List;

public class CommunicationUtil {

    public static String toJSONString(List<String> keys,List<String> values) throws JSONException {
        JSONStringer jsonFinal = new JSONStringer();
        jsonFinal.object();
        if (keys.size() == values.size()) {
            for (int i=0;i<keys.size();i++) {
                jsonFinal.key(keys.get(i));
                jsonFinal.value(values.get(i));
            }
            jsonFinal.endObject();
        } else {
            //throw exception
        }
        return jsonFinal.toString();
    }

}
