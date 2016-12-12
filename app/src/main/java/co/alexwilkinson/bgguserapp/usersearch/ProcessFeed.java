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
 * ASync Task used to pull the code from the internet and process it
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
    private ArrayList<Integer[]> statusList = new ArrayList<>();

    /**
     * Single argument constructor that takes the username to be processed and checked on the API
     * @param username String valid BGG username
     */
    public ProcessFeed(String username){
        this.username = username;
        try{
            url = new URL(bgg+collection+username); //api2 collection call string
        }catch(Exception ex){
            ex.getStackTrace();
        }
    }

    /**
     * Overload constructor that takes two arugments the Username and the application context
     * to be used for the loading screen
     * @param username String valid username for BGG
     * @param context application class.this call for the loading
     */
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

    /**
     * Main processing task for the class
     * @param params parms not currently in use
     * @return returns the ArrayList of BoardgameItems
     */
    @Override
    protected Object doInBackground(Object[] params) {
        //strings to be used for assignment
        String bgTitle = "",bgID = "",published = "",thumbnail = "";

        try{
            //creating the XML factory and pull classes
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            xmlFactory.setNamespaceAware(true);

            XmlPullParser xpp = xmlFactory.newPullParser(); //assign the pull parser

            xpp.setInput(getInputStream(url), "UTF_8"); //set the url to the xml parser input

            int eventType = xpp.getEventType(); //the int ref pulled from the event used for the loop

            //main while loop running over the process
            while(eventType != XmlPullParser.END_DOCUMENT){ //check it hasn't ended yet
                if(eventType == XmlPullParser.START_TAG){ //check for the start of the document

                    //if statement to pull the board game name
                    if(xpp.getName().equalsIgnoreCase("name")){
                        bgTitle = xpp.nextText();
                    }
                    //pull the unique board game id stored on BGG
                    else if(xpp.getName().equalsIgnoreCase("item")){
                        bgID = xpp.getAttributeValue(1);
                    }
                    //pulls the year the game was released
                    else if(xpp.getName().equalsIgnoreCase("yearpublished")){
                        published = xpp.nextText();
                    }
                    //pulls the BGG image link URL
                    else if(xpp.getName().equalsIgnoreCase("thumbnail")){
                        thumbnail = xpp.nextText();
                    }
                    //pulls the users total collection size
                    else if(xpp.getName().equalsIgnoreCase("items")){
                        total = Integer.parseInt(xpp.getAttributeValue(0));
                    }
                    //checke the status column and pulls the owned, wants to play and wishlist
                    else if(xpp.getName().equalsIgnoreCase("status")){
                        statusElements = new Integer[]{
                                Integer.parseInt(xpp.getAttributeValue(0)), //owned
                                Integer.parseInt(xpp.getAttributeValue(4)), //wants to play
                                Integer.parseInt(xpp.getAttributeValue(6)), //wishlist
                        };
                        statusList.add(statusElements);
                    }
                    //ensures that all the data is added  together to prevent repeats being stored
                    if(!bgTitle.equalsIgnoreCase("") &&
                            !bgID.equalsIgnoreCase("")&&
                            !published.equalsIgnoreCase("")&&
                            !thumbnail.equalsIgnoreCase("")
                            ) {

                        boardggameList.add(new BoardgameListItem(
                                bgTitle, published, bgID,thumbnail
                        ));
                        //clear everything
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
        //loops over the list to add the status elements later, this saves significantly on
        //memory usage as this task requires more runtime than the simple ones previously
        for(int i =0; i < boardggameList.size(); i++){
            BoardgameListItem item = boardggameList.get(i);
            Integer[] status = statusList.get(i);

            item.addStatus(status[0],status[1],status[2]);
        }

        return boardggameList;
    }

    /**
     * Override method that will preform tasks after the main task is completed, currently setup
     * just for the loading dialog
     * @param o object to pass
     */
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     * not currently in use
     * @param values
     */
    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    /**
     * protected method that will connect the URL connection.
     * @param url valid URL object
     * @return
     */
    protected InputStream getInputStream(URL url){
        try{
            return url.openConnection().getInputStream();
        }catch (IOException ex){
            ex.getStackTrace();
            return null;
        }
    }

    /**
     * Getter method that will return the total number of games in the users collection
     * @return int total number of games the user owns currently
     */
    public int getTotal(){return total;}

    /**
     * Getter method that returns the ArrayList object of BoardGameItem
     * @return ArrayList containing the BoardgameListItem
     */
    public ArrayList<BoardgameListItem>getboardgameList(){return boardggameList;}




}
