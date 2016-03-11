package atlassian.test.com.json_generator;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atlassian.test.com.listener.ParserListener;
import atlassian.test.com.listener.TitleRetrieverListener;

/**
 * Created by Nathaniel James Lim on 3/10/2016.
 */
public class Parser implements TitleRetrieverListener{


    private JSONObject data;
    private ParserListener callback;
    private String message;

    private final String MENTIONS = "mentions";
    private final String EMOTICONS = "emoticons";
    private final String LINKS = "links";

    public Parser(String message,ParserListener callback){

        this.message = message;
        this.callback = callback;
        data = new JSONObject();

    }

    public void generateJsonString(){

        appendMentionJSON(data, message);
        appendEmoticonJSON(data, message);
        appendLinkJSON(message);

    }

    private void appendMentionJSON(JSONObject jsonData, String message){

        ArrayList<String> mentionList = getMentionList(message);

        JSONArray mentionArray=null;
        if (mentionList.size()>0){
            mentionArray = new JSONArray();
            for(String mentionItem : mentionList){
                mentionArray.put(mentionItem);
            }
        }

        try {
            jsonData.put(MENTIONS,mentionArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void appendEmoticonJSON(JSONObject jsonData, String message){

        ArrayList<String> emoticonList = getEmoticons(message);

        JSONArray emoticonArray=null;

        if(emoticonList.size()>0){
            emoticonArray = new JSONArray();
            for(String emoticonItem : emoticonList){
                emoticonArray.put(emoticonItem);
            }
        }

        try {
            jsonData.put(EMOTICONS,emoticonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void appendLinkJSON(String message){
        ArrayList<String> urlList = getURLs(message);
        new TitleRetriever(this).execute(urlList.toArray(new String[urlList.size()]));
    }

    private ArrayList<String> getMentionList(String message){

        message = message.replace("@"," @")+" ";

        ArrayList<String> list = new ArrayList<String>();
        Matcher m = Pattern.compile("\\@([a-zA-Z]+)\\W").matcher(message);
        while(m.find()) {
            list.add(m.group(1));
        }

        return list;

    }

    private ArrayList<String> getURLs(String message){

        ArrayList<String> list = new ArrayList<String>();

        Pattern urlPattern = Pattern.compile("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

        Matcher m =urlPattern.matcher(message);

        while(m.find()) {
            int matchStart = m.start(1);
            int matchEnd = m.end();
            list.add(message.substring(matchStart,matchEnd));
        }

        return list;

    }

    private ArrayList<String> getEmoticons(String message){
        ArrayList<String> list = new ArrayList<String>();

        Matcher m = Pattern.compile("\\(([a-zA-Z0-9]+)\\)").matcher(message);
        while(m.find()) {
            if(m.group(1).length()>=1 && m.group(1).length()<=15){
                list.add(m.group(1));
            }
        }

        return list;
    }

    @Override
    public void onReceiveUrlTitle(ArrayList<JSONObject> list) {

        if(list.size()>0){
            JSONArray linkPairArray = new JSONArray();

            for(JSONObject linkPair : list){
                linkPairArray.put(linkPair);
            }
            try {
                data.put(LINKS,linkPairArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        callback.onReceiveJsonString(data.toString());

    }
}
