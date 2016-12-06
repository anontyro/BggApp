package co.alexwilkinson.bgguserapp.userarea;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import co.alexwilkinson.bgguserapp.HeaderActivity;
import co.alexwilkinson.bgguserapp.R;
import co.alexwilkinson.bgguserapp.usersearch.BoardgameListItem;
import co.alexwilkinson.bgguserapp.usersearch.ProcessFeed;
import co.alexwilkinson.bgguserapp.usersearch.SaveCurrentUser;
import co.alexwilkinson.bgguserapp.utilities.DBManager;
import co.alexwilkinson.bgguserapp.utilities.FontManager;
import co.alexwilkinson.bgguserapp.utilities.UserRef;
import co.alexwilkinson.bgguserapp.utilities.WebBrowserActivity;

public class UserAreaMainActivity extends HeaderActivity {
    protected Spinner spUser;
    private Button refreshGames;

    protected DBManager dbManager;
    public UserRef userRef;
    public ArrayList<String>users;
    protected ArrayList<String>userGames;
    protected ListView lvUserGames;
    protected MainListAdapter myadapter;
    private Typeface iconFA;
    private String selectedUser= "";
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area_main);

        iconFA = FontManager.getTypeface(getApplicationContext(),FontManager.FONTAWESOME);

        userRef = new UserRef(this);
        String[]userData = (userRef.loadData()).split("\n");

        spUser = (Spinner)findViewById(R.id.spUserArea);
        lvUserGames = (ListView)findViewById(R.id.lvUserGames);

        refreshGames = (Button)findViewById(R.id.buUserAreaRefresh);

        progressBar = (ProgressBar)findViewById(R.id.pbUserAreaMain);
        progressBar.setVisibility(View.INVISIBLE);

        setupRefreshButton();

        users = populateUser(userData[0]);

        setupArrayAdapter();


    }

    private void setupRefreshButton(){
        refreshGames.setTypeface(iconFA);

        refreshGames.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    refreshGames.setTextColor(Color.BLACK);

                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    refreshGames.setTextColor(Color.GREEN);
                    UpdateUserGames();
//                    refreshGames.animate().rotation(180).start();

                }
                return false;
            }
        });
    }

    private void UpdateUserGames(){
        System.out.println("user selected is: "+currentUser());
        ProcessFeed processFeed = new ProcessFeed(currentUser());

        while(processFeed.getTotal() == 0) {
            try {
                processFeed.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("feed: "+ processFeed.getTotal() +" saved: "+ userGames.size());
        if(processFeed.getTotal() != userGames.size()){
            dbManager = new DBManager(this,currentUser());

            final SaveCurrentUser saveUser = new SaveCurrentUser(currentUser(),processFeed.getboardgameList(),this);
            try {
                dbManager.removeGames(currentUser());
                saveUser.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(getApplicationContext(),
                    "No need to update both libaraies are: " +processFeed.getTotal(),
                    Toast.LENGTH_LONG).show();
        }

    }
    public String currentUser(){
        return selectedUser;
    }

    private void setupArrayAdapter(){

        final TextView userTotal = (TextView)findViewById(R.id.tvUserAreaTotal);

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,users);
        spUser.setAdapter(arrayAdapter);
        spUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                lvUserGames.setAdapter(null);
                userGames = getUserGames(users.get(i));
                if(userGames.size() !=0) {
                    myadapter = new MainListAdapter(userGames);
                    lvUserGames.setAdapter(myadapter);

                    userTotal.setText("Total: "+userGames.size());
                    selectedUser = users.get(i);

                    Toast.makeText(getApplicationContext(),
                            users.get(i)+" has a total of: "+userGames.size(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    protected ArrayList getUserGames(String user){
        ArrayList<String> gameList = new ArrayList<>();

        Cursor cursor1 = dbManager.queryGame(null,null,null,null);
        if(cursor1.moveToFirst()){
            do{

                if(cursor1.getString(cursor1.getColumnIndex(DBManager.colForUsername))
                        .equalsIgnoreCase(user)){

                    gameList.add(
                            cursor1.getString(cursor1.getColumnIndex(DBManager.colTitle))+ ", "+
                                    cursor1.getString(cursor1.getColumnIndex(DBManager.colReleased)) +", "+
                                    cursor1.getString(cursor1.getColumnIndex(DBManager.colID))

                    );

                }

            }while(cursor1.moveToNext());
        }

        return gameList;
    }

    private ArrayList<String> populateUser(String mainuser){
        ArrayList<String>userList = new ArrayList<>();
        dbManager = new DBManager(this,mainuser);
        final Cursor cursor = dbManager.queryUser(null,null,null,null);

        if(cursor.moveToFirst()){
            do{

                userList.add(cursor.getString(cursor.getColumnIndex(DBManager.colUsername)));

            }while(cursor.moveToNext());
        }
        System.out.println(userList.toString());
        return userList;
    }



    private class MainListAdapter extends BaseAdapter{
        private ArrayList<String>gameList;
        public MainListAdapter(ArrayList<String>gameList){this.gameList = gameList;}

        @Override
        public int getCount() {
            return gameList.size();
        }

        @Override
        public Object getItem(int position) {return null;}

        @Override
        public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater myInflater = getLayoutInflater();
            View myView = myInflater.inflate(R.layout.boardgame_collection_item,null);

            final String[]adapterItem = gameList.get(position).split(",");

            TextView tvTitle = (TextView)myView.findViewById(R.id.tvTitle);
            tvTitle.setText(adapterItem[0]);

            TextView tvDetails = (TextView)myView.findViewById(R.id.tvDetails);
            tvDetails.setText(adapterItem[1]);

            Button buBoardGame = (Button)myView.findViewById(R.id.buBgg);
            buBoardGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),WebBrowserActivity.class);
                    intent.putExtra("boardgame",adapterItem[2]);
                    intent.putExtra("title",adapterItem[0]);
                    startActivity(intent);
                }
            });

            return myView;
        }
    }


}
