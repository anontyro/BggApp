package co.alexwilkinson.bgguserapp;

import android.database.CursorJoiner;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    EditText etFindUser;
    ListView lvCollection;
    MyListAdapter myadapter;
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

            TextView tvImage = (TextView)myView.findViewById(R.id.tvImage);
            tvImage.setText(adapterItem.image);

            return myView;
        }
    }




    /**
     * class that is used to retive that data from the XML of Board Game Geek user collection
     *
     * public accessable methods getNameList, getDetailList, getImageList
     */
    public class RetrieveFeed extends AsyncTask{

        String bgg = "https://www.boardgamegeek.com/xmlapi/";
        String collection = "collection/";
        String username = "";
        URL url;
        String rawXML = "";

        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> released = new ArrayList<>();
        ArrayList<String> image = new ArrayList<>();

        public RetrieveFeed(String username){
            this.username = username;
            try {
                url = new URL(bgg + collection + username);
            }
            catch(Exception ex){
                ex.getStackTrace();
            }

        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try{
                XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
                xmlFactory.setNamespaceAware(false);

                XmlPullParser xpp = xmlFactory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");

                rawXML = xpp.getText();

                int eventType = xpp.getEventType();
                while( eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_TAG){

                        if(xpp.getName().equalsIgnoreCase("name")){
                            name.add(xpp.nextText());
                        }
                        else if(xpp.getName().equalsIgnoreCase("yearpublished")){
                            released.add(xpp.nextText());
                        }
                        else if(xpp.getName().equalsIgnoreCase("thumbnail")){
                            image.add(xpp.nextText());
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
            System.out.println(name.size());
            System.out.println(name.toString());
            System.out.println(released.toString());
            System.out.println(image.toString());

            if(name.size() !=0){
                bgList = new ArrayList<>();
                for(int i = 0; i<name.size();i++) {

                    bgList.add(new BoardgameListItem(
                            name.get(i), released.get(i),image.get(i)));

                }
                System.out.println("data added to list");

                myadapter = new MyListAdapter(bgList);
                lvCollection.setAdapter(myadapter);

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
