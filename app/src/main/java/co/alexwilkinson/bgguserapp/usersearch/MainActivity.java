package co.alexwilkinson.bgguserapp.usersearch;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import co.alexwilkinson.bgguserapp.utilities.CreateUserDialogFrame;
import co.alexwilkinson.bgguserapp.utilities.DBManager;
import co.alexwilkinson.bgguserapp.HeaderActivity;
import co.alexwilkinson.bgguserapp.R;
import co.alexwilkinson.bgguserapp.utilities.UserRef;
import co.alexwilkinson.bgguserapp.utilities.WebBrowserActivity;

public class MainActivity extends HeaderActivity
        implements CreateUserDialogFrame.NoticeDialogListener, CreateUserDialogFrame.OnCompleteListener{
    private EditText etFindUser;
    protected ListView lvCollection;
    protected MyListAdapter myadapter;
    private Button buBgg;
    protected int userTotal;
    private DBManager dbManager;
    protected ContentValues values;
    protected RetrieveFeed getGames;

    //title, description, image(String)
    public static ArrayList<BoardgameListItem> bgList = new ArrayList<>();

    /**
     * main method for creating the activity most operations revolve around this
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup the objects to be callable
        lvCollection = (ListView)findViewById(R.id.lvCollection);
        etFindUser = (EditText)findViewById(R.id.etFindUser);


    }

    //search button that will check the bgg API and return the user
    public void buSearch(View view) {
        String user = etFindUser.getText().toString();
        getGames = new RetrieveFeed(user);
            //add .execute().get() to force the application to run now
            getGames.execute();
    }

    //button used to save the current user selected in the list, if no prime user exists then
    //the button will throw a dialog to ask to add one and create the database
    public void buSaveUser(View view) {
        //check to see if database exists
        if(DBManager.databaseExists() ==false){ //checks to see if the database exists
            //steps needed to create and call the dialog window
            FragmentManager fm = getFragmentManager();
            CreateUserDialogFrame dialog;
            //send the current username from the search box
            dialog = CreateUserDialogFrame.setUsername(etFindUser.getText().toString());
            dialog.show(fm, "addUser");
        }
        else{
            if(bgList.size() !=0){
               boolean exists = doesUserExist(etFindUser.getText().toString());
                System.out.println(exists);


            }
            else{
                System.out.println("user not checked");
            }
            }
        }


    /**
     * Implemented from the CreateUserDialogFrame, event that is fired from the pressing of the
     * positive button.
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        Toast.makeText(this,"you chose wisely",Toast.LENGTH_LONG).show();
        System.out.println("positive");
    }

    /**
     * Implemented from the CreateUserDialogFrame, event that is fired from the pressing of the
     * negative button.
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

        Toast.makeText(this,"you chose poorly",Toast.LENGTH_LONG).show();
        System.out.println("negative");
    }

    /**
     * Implemented from the CreateUserDialogFrame, event that is fired at the end of the dialog session
     * these values are returned at the end.
     * @param username
     */
    @Override
    public void onComplete(String username) {
        if(!username.equalsIgnoreCase(etFindUser.getText().toString())){
            String user = username;
            etFindUser.setText(username);
            lvCollection.setAdapter(null);
            RetrieveFeed getGames = new RetrieveFeed(user);
            //add .execute().get() to force the application to run now
            getGames.execute();

        }

        if(userTotal !=0) {
            dbManager = new DBManager(this, username);

            /*
            Calling the user ref file to save the basic user info
             */
            UserRef userRef = new UserRef(this);
            userRef.saveData(username,userTotal);

            values = new ContentValues();
            values.put(DBManager.colUsername, username);
            values.put(DBManager.colTotalGames, userTotal);
            values.put(DBManager.colPrimaryUser, "True");

            SaveUser newUser = new SaveUser();
            System.out.println("Save users games now!");
            newUser.execute();

            long id = dbManager.insertUser(values);
            if(id > 0 ){
                Toast.makeText(this,
                        "Primary user: " +username + "was added and database created",
                        Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this,
                        "Error something blew up! Search again and try ",
                        Toast.LENGTH_LONG).show();
            }
            System.out.println("username is set to: " + username);
            Log.d("user", username);
            System.out.println(DBManager.buildGameTable);
            System.out.println(DBManager.buildUserTable);
        }
        else{
            Toast.makeText(this,"Search for the user first to ensure they exist",Toast.LENGTH_LONG).show();
        }
    }

    public Boolean doesUserExist(String user){
        boolean exists = false;
        dbManager = new DBManager(this,user);
        Cursor cursor1 = dbManager.queryUser(
                null,
                DBManager.colUsername+"=?",
                new String[]{etFindUser.getText().toString()},
                null);

        if(cursor1.moveToFirst()){
            do{

                if(cursor1.getString(cursor1.getColumnIndex(DBManager.colUsername))
                        .equalsIgnoreCase(etFindUser.getText().toString())){
                    exists = true;
                }

            }while(cursor1.moveToNext());
        }
        return exists;
    }

    /**
     * private inner class that is used to control and display the XML content in the ListView
     * add all the items from the arraylist into the ListView
     */
    private class MyListAdapter extends BaseAdapter{
        public ArrayList<BoardgameListItem>bgList;

        public MyListAdapter(ArrayList<BoardgameListItem> bgList){
            this.bgList = bgList;
        }

        @Override
        public int getCount() {
                return bgList.size();

        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LayoutInflater myInflater = getLayoutInflater();
            View myView = myInflater.inflate(R.layout.boardgame_collection_item,null);

            final BoardgameListItem adapterItem = bgList.get(i);

            TextView tvTitle = (TextView)myView.findViewById(R.id.tvTitle);
            tvTitle.setText(adapterItem.title);

            TextView tvDetails = (TextView)myView.findViewById(R.id.tvDetails);
            tvDetails.setText(adapterItem.description);

//            TextView tvImage = (TextView)myView.findViewById(R.id.tvImage);
//            tvImage.setText(adapterItem.image);

            buBgg = (Button)myView.findViewById(R.id.buBgg);
            buBgg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),WebBrowserActivity.class);
                    intent.putExtra("boardgame",adapterItem.gameID);
                    intent.putExtra("title",adapterItem.title);
                    startActivity(intent);
//                    Uri uri = Uri.parse("http://boardgamegeek.com/boardgame/" + adapterItem.gameID);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
                }
            });

            return myView;
        }
    }


    /**
     * class that is used to retive that data from the XML of Board Game Geek user collection
     *
     * public accessible methods getNameList, getDetailList, getImageList
     */
    public class RetrieveFeed extends AsyncTask{

        String bgg = "http://www.boardgamegeek.com/xmlapi2/";
        String collection = "collection?username=";
        String username = "";
        URL url;
        ContentValues values;

        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> released = new ArrayList<>();
        ArrayList<String> gameID = new ArrayList<>();
        ArrayList<Integer[]>statusList = new ArrayList<>();
//        ArrayList<String> LengthList = new ArrayList<>();
        ArrayList<String> imageList = new ArrayList<>();

        public RetrieveFeed(String username){
            this.username = username;
            try {
                url = new URL(bgg + collection + username );
            }
            catch(Exception ex){
                ex.getStackTrace();
            }

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            //create the controls to parse the XML using XmlPullParser
            try{
                XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
                xmlFactory.setNamespaceAware(true);

                XmlPullParser xpp = xmlFactory.newPullParser();

                //set the input url stream to use
                xpp.setInput(getInputStream(url), "UTF_8");


                int eventType = xpp.getEventType();

                //create a while loop to keep going until teh end of the XML document
//                while( eventType != XmlPullParser.END_DOCUMENT){
                while(eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_TAG){

                        //check for name of the game using tage
                        if(xpp.getName().equalsIgnoreCase("name")){
                            name.add(xpp.nextText());
                        }
                        //check for the board game ID in item namespace
                        else if(xpp.getName().equalsIgnoreCase("item")){
                            gameID.add(xpp.getAttributeValue(1));
                        }
                        //check the year published tag
                        else if(xpp.getName().equalsIgnoreCase("yearpublished")){
                            released.add(xpp.nextText());
                        }
//                        pull the thumnail tag
                        else if(xpp.getName().equalsIgnoreCase("thumbnail")){
                            imageList.add(xpp.nextText());
                        }
                        else if(xpp.getName().equalsIgnoreCase("items")){
                            userTotal = Integer.parseInt(xpp.getAttributeValue(0));
                        }
                        //will check all the status namespace for the items to add to the ArrayList<Integer[]>()
                        // if return zero then false one is true
                        else if(xpp.getName().equalsIgnoreCase("status")){
                            Integer[]statusElements = {
                                    Integer.parseInt(xpp.getAttributeValue(0)), //owned
                                    Integer.parseInt(xpp.getAttributeValue(4)), //wants to play
                                    Integer.parseInt(xpp.getAttributeValue(6)), //wishlist

                            };
                            statusList.add(statusElements);
                        }
                        //check the status tag and namespaces within it
//                        else if(xpp.getName().equalsIgnoreCase("status")){
//                            System.out.println(
//                                    "namespace = " + xpp.getAttributeName(1) + " = " + xpp.getAttributeValue(1) +"\n" +
//                                            "namespace  = " + xpp.getAttributeName(0) + " = " + xpp.getAttributeValue(0)
//
//                            );
//
//                        }

                    }
                    //moves the XML to the next tag to parse
                    eventType = xpp.next();
                }

                //call the method to add the values to the main BoardgameList
                bgList = addToGameList();



            }
            catch(Exception ex){
                ex.getStackTrace();
            }

            return name;
        }

        /*
        postprocessing which will link the listview to the adapter created to display the content
        A toast is also given to state how many items the user has in their list
         */
        @Override
        protected void onPostExecute(Object o) {
            if(name.size() !=0){

                myadapter = new MyListAdapter(bgList);
                lvCollection.setAdapter(myadapter);


                Toast.makeText(getApplicationContext(),
                        etFindUser.getText().toString() + " has a Total of : " +userTotal +
                                " games on board game geek",
                        Toast.LENGTH_LONG).show();

            }
            else{
                Toast.makeText(getApplicationContext(),
                        "Error data could not be retrieved, check username and connection, and try again",
                        Toast.LENGTH_LONG).show();
                etFindUser.setText("");
            }

        }

        //getter methods used to reteive values created for use later

        protected InputStream getInputStream(URL url){
            try{
                return url.openConnection().getInputStream();

            }
            catch(Exception ex){
                ex.getStackTrace();
                return null;
            }
        }

        public String getUsername(){return username;}

        public ArrayList<String> getGameList(){return name;}

        public ArrayList<String> getDetailList(){
            return released;
        }

        public ArrayList<String> getGameID(){
            return gameID;
        }

        public ArrayList<Integer[]> getStatusList(){
            return statusList;
        }

        public ArrayList<String> getimageList(){
            return imageList;
        }

//        public ArrayList<String> getLengthList(){return lengthList;}


        /*
        Method to add all of the values to the BoardgameListItem to be displayed in the ListView
         */
        private ArrayList<BoardgameListItem> addToGameList(){
            bgList = new ArrayList<>(name.size());
            for(int i = 0; i<name.size();i++) {

                bgList.add(new BoardgameListItem(
                        name.get(i),
                        released.get(i),
                        gameID.get(i)
                ));

            }
            System.out.println("data added to list");
            System.out.println("values: "+values);
            return bgList;
        }


    }

    public class SaveUser extends AsyncTask{
        String username = getGames.getUsername();
        ArrayList<String> gameList = getGames.getGameList();
        ArrayList<String> gameDetailList = getGames.getDetailList();
        ArrayList<String> gameIDList = getGames.getGameID();
        ArrayList<Integer[]> gameStatusList = getGames.getStatusList();
        ArrayList<String> gameImageList = getGames.getimageList();
        String addUserQuery;
        @Override
        protected void onPreExecute() {
            if(gameList.size() == 0){
                cancel(true);
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            addUser();
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Object o) {


        }
        /**
         * method that will take the all of the data stored from the XML
         * and start to get it ready to commit to the database.
         * @return
         */


        protected void addUser(){


            ContentValues values = new ContentValues();
            System.out.println("Started to add values " + gameList.size());
            for(int i = 0; i<gameList.size();i++){
                String wishlist = "false";
                String wantsToPlay = "false";
                String owned = "false";
                Integer[]statusValues = gameStatusList.get(i);
                if(statusValues[0] !=0) {
                    owned = "true";
                }
                if(statusValues[1] !=0) {
                    wantsToPlay = "true";
                }
                if(statusValues[2] !=0) {
                    wishlist = "true";
                }

//                addUserQuery += "INSERT INTO " + DBManager.tableGames
//                        + " ('"+DBManager.colForUsername+"'," +
//                        "'"+DBManager.colTitle+"'," +
//                        "'"+DBManager.colReleased+"'," +
//                        "'"+DBManager.colImage+"'," +
//                        "'"+DBManager.colID+"'," +
//                        "'"+DBManager.colBggPage+"'," +
//                        "'"+DBManager.colOwned+"'," +
//                        "'"+DBManager.colWantToPlay+"'," +
//                        "'"+DBManager.colWishlist+"') "
//
//                        +"VALUES"+
//
//                        "('"+username+"'," +
//                        "'"+gameList.get(i)+"'," +
//                        "'"+gameDetailList.get(i)+"'," +
//                        "'"+gameImageList.get(i)+"'," +
//                        "'"+gameIDList.get(i)+"'," +
//                        "'"+"https://boardgamegeek.com/boardgame/"+gameIDList.get(i)+"'," +
//                        "'"+owned+"'," +
//                        "'"+wantsToPlay+"'," +
//                        "'"+wishlist+"'); \n"
//                ;



                values.put(DBManager.colForUsername, username);
                values.put(DBManager.colTitle, gameList.get(i));
                values.put(DBManager.colReleased, gameDetailList.get(i));
                values.put(DBManager.colImage, gameImageList.get(i));
                values.put(DBManager.colID, gameIDList.get(i));
                values.put(DBManager.colBggPage, "https://boardgamegeek.com/boardgame/"+gameIDList.get(i));
                values.put(DBManager.colOwned, owned);
                values.put(DBManager.colWantToPlay, wantsToPlay);
                values.put(DBManager.colWishlist, wishlist);

                long id = dbManager.insertGame(values);
            }
//            dbManager.queryGameTable(addUserQuery);



        }


    }

}
