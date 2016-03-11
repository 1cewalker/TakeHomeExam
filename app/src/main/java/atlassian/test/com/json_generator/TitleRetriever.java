package atlassian.test.com.json_generator;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atlassian.test.com.listener.TitleRetrieverListener;

/**
 * Created by Nathaniel James Lim on 3/11/2016.
 */
public class TitleRetriever extends AsyncTask<String,Void,ArrayList<JSONObject>> {


    private final Pattern TITLE_TAG =
            Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

    private TitleRetrieverListener callback;
    private final String URL = "url";
    private final String TITLE = "title";

    public TitleRetriever(TitleRetrieverListener callback) {
        this.callback = callback;
    }

    @Override
    protected ArrayList<JSONObject> doInBackground(String... params) {

        ArrayList<JSONObject> list = new ArrayList<JSONObject>();

        for(int i=0;i<params.length;i++){

            try {
                JSONObject linkPair = new JSONObject();
                try {

                    linkPair.put(URL,params[i]);
                    linkPair.put(TITLE, getPageTitle(params[i]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(linkPair);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<JSONObject> linkPairList) {
        super.onPostExecute(linkPairList);

        callback.onReceiveUrlTitle(linkPairList);
    }

    private String getPageTitle(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Matcher matcher = TITLE_TAG.matcher(response.toString());
            if (matcher.find()) {
                String title =  matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim();
                return Html.fromHtml(title).toString();
            }
            else
                return "";
        } else {
            return "";
        }
    }
}
