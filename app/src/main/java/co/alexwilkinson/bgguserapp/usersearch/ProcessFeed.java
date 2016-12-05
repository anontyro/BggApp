package co.alexwilkinson.bgguserapp.usersearch;

import android.os.AsyncTask;
import android.os.Process;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alex on 05/12/2016.
 */

public class ProcessFeed extends AsyncTask{

    private ArrayList<BoardgameListItem>boardggameList;
    private String username;
    private String bgg = "http://www.boardgamegeek.com/xmlapi2/";
    private String collection = "collection?username=";
    private URL url;

    public ProcessFeed(String username){
        this.username = username;
        try{
            url = new URL(bgg+collection+username);
        }catch(Exception ex){
            ex.getStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String bgTitle,bgID,published,thumbnail,total;
        Integer[]statusElements;

        try{
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            xmlFactory.setNamespaceAware(true);

            XmlPullParser xpp = xmlFactory.newPullParser();

            xpp.setInput(getInputStream(url), "UTF_8");

            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG){


                    if(xpp.getName().equalsIgnoreCase("name")){
                        bgTitle = xpp.nextText();
                    }
                    else if(xpp.getName().equalsIgnoreCase("item")){
                        bgID = xpp.getAttributeValue(1);
                    }
                    else if(xpp.getName().equalsIgnoreCase("yearpublished")){

                    }
                    else if(xpp.getName().equalsIgnoreCase("thumbnail")){

                    }
                    else if(xpp.getName().equalsIgnoreCase("item")){

                    }
                    else if(xpp.getName().equalsIgnoreCase("status")){

                    }

                }
            }

        }catch(Exception ex){

        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    protected InputStream getInputStream(URL url){
        try{
            return url.openConnection().getInputStream();
        }catch (IOException ex){
            ex.getStackTrace();
            return null;
        }
    }




}
