package atlassian.test.com.listener;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dell on 3/11/2016.
 */
public interface TitleRetrieverListener {

    public void onReceiveUrlTitle(ArrayList<JSONObject> list);
}
