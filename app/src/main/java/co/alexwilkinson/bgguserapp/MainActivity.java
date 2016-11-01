package co.alexwilkinson.bgguserapp;

import android.content.Intent;
import android.database.CursorJoiner;
import android.net.Uri;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    EditText etFindUser;
    ListView lvCollection;
    MyListAdapter myadapter;
    Button buBgg;

    ArrayList<String> gameNames;

    //title, description, image(String)
    public static ArrayList<BoardgameListItem> bgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvCollection = (ListView)findViewById(R.id.lvCollection);
        etFindUser = (EditText)findViewById(R.id.etFindUser);


    }

    public void buSearch(View view) {
        String user = etFindUser.getText().toString();
        RetrieveFeed getGames = new RetrieveFeed(user);
        try {
            getGames.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        gameNames = getGames.getGameList();
        System.out.println(gameNames.toString());
    }


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
                    Uri uri = Uri.parse("http://boardgamegeek.com/boardgame/" + adapterItem.gameID);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

            return myView;
        }
    }




    /**
     * class that is used to retive that data from the XML of Board Game Geek user collection
     *
     * public accessable methods getNameList, getDetailList, getImageList
     */
    public class RetrieveFeed extends AsyncTask{

        String bgg = "http://www.boardgamegeek.com/xmlapi2/";
        String collection = "collection?username=";
        String username = "";
        URL url;

        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> released = new ArrayList<>();
        ArrayList<String> gameID = new ArrayList<>();
        ArrayList<String> image = new ArrayList<>();

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
                while( eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_TAG){

                        //check for name of the game using tage
                        if(xpp.getName().equalsIgnoreCase("name")){
                            name.add(xpp.nextText());
                        }
                        //check for the board game ID in item namespace
                        else if(xpp.getName().equalsIgnoreCase("item")){
                            gameID.add(xpp.getAttributeValue(1));
                        }
                        //check the yearh published tag
                        else if(xpp.getName().equalsIgnoreCase("yearpublished")){
                            released.add(xpp.nextText());
                        }
                        //pull the thumnail tag
                        else if(xpp.getName().equalsIgnoreCase("thumbnail")){
                            image.add(xpp.nextText());
                        }
                        //check the status tag and namespaces within it
                        else if(xpp.getName().equalsIgnoreCase("status")){
                            System.out.println(
                                    "namespace = " + xpp.getAttributeName(1) + " = " + xpp.getAttributeValue(1) +"\n" +
                                            "namespace  = " + xpp.getAttributeName(0) + " = " + xpp.getAttributeValue(0)

                            );

                        }
                    }
                    eventType = xpp.next();
                }

            }
            catch(Exception ex){
                ex.getStackTrace();
            }
            return name;
        }

        /*
        a lot of additional processing will happen here. The data from the XML will be taken and added
        to the BoardgameListItem and then it will be pushed to the list view
         */
        @Override
        protected void onPostExecute(Object o) {

            if(name.size() !=0){
                bgList = new ArrayList<>();
                for(int i = 0; i<name.size();i++) {

                    bgList.add(new BoardgameListItem(
                            name.get(i),
                            released.get(i),
                            image.get(i),
                            gameID.get(i)
                    ));

                }
                System.out.println("data added to list");

                myadapter = new MyListAdapter(bgList);
                lvCollection.setAdapter(myadapter);


                Toast.makeText(getApplicationContext(),
                        etFindUser.getText().toString() + " has a Total of : " +name.size() +
                                " games on board game geek",
                        Toast.LENGTH_LONG).show();

            }
            else{
                System.out.println("Error data could not be retrieved, check your connection and try" +
                        "again");
            }

        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }


        protected InputStream getInputStream(URL url){
            try{
                return url.openConnection().getInputStream();

            }
            catch(Exception ex){
                ex.getStackTrace();
                return null;
            }
        }

        public ArrayList<String> getGameList(){
            return name;
        }

        public ArrayList<String> getDetailList(){
            return released;
        }
        public ArrayList<String> getImageList(){
            return image;
        }
    }

}
