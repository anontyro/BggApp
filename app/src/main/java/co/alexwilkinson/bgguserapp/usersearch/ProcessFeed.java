package co.alexwilkinson.bgguserapp.usersearch;

import android.app.ProgressDialog;
import android.content.Context;
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

    private ArrayList<BoardgameListItem>boardggameList = new ArrayList<>();
    private String username;
    private String bgg = "http://www.boardgamegeek.com/xmlapi2/";
    private String collection = "collection?username=";
    private URL url;
    private int total = 0;
    private Integer[]statusElements;
    private ProgressDialog progressDialog;
    private Context context;
    ArrayList<Integer[]> statusList = new ArrayList<>();

    public ProcessFeed(String username){
        this.username = username;
        try{
            url = new URL(bgg+collection+username);
        }catch(Exception ex){
            ex.getStackTrace();
        }
    }
    public ProcessFeed(String username, Context context){
        this.username = username;
        this.context = context;
        try{
            url = new URL(bgg+collection+username);
        }catch(Exception ex){
            ex.getStackTrace();
        }
    }

//TODO try to get the loading dialog working within this
    @Override
    protected void onPreExecute() {
        if(context!= null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("loading...");
            progressDialog.show();
        }

    }

    @Override
    protected Object doInBackground(Object[] params) {
        String bgTitle = "",bgID = "",published = "",thumbnail = "";

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
                        published = xpp.nextText();
                    }
                    else if(xpp.getName().equalsIgnoreCase("thumbnail")){
                        thumbnail = xpp.nextText();
                    }
                    else if(xpp.getName().equalsIgnoreCase("items")){
                        total = Integer.parseInt(xpp.getAttributeValue(0));
                    }
                    else if(xpp.getName().equalsIgnoreCase("status")){
                        statusElements = new Integer[]{
                                Integer.parseInt(xpp.getAttributeValue(0)), //owned
                                Integer.parseInt(xpp.getAttributeValue(4)), //wants to play
                                Integer.parseInt(xpp.getAttributeValue(6)), //wishlist
                        };
                        statusList.add(statusElements);
                    }
                    if(!bgTitle.equalsIgnoreCase("") &&
                            !bgID.equalsIgnoreCase("")&&
                            !published.equalsIgnoreCase("")&&
                            !thumbnail.equalsIgnoreCase("")
                            ) {


                        boardggameList.add(new BoardgameListItem(
                                bgTitle, published, bgID,thumbnail
                        ));
                        bgID = "";
                        published="";
                        thumbnail="";
                    }
                }
                eventType = xpp.next();

            }

        }catch(Exception ex){
            ex.getStackTrace();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i =0; i < boardggameList.size(); i++){
            BoardgameListItem item = boardggameList.get(i);
            Integer[] status = statusList.get(i);

            item.addStatus(status[0],status[1],status[2]);
        }

        return boardggameList;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(progressDialog != null){
            progressDialog.dismiss();
        }
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

    public int getTotal(){return total;}

    public ArrayList<BoardgameListItem>getboardgameList(){return boardggameList;}




}
