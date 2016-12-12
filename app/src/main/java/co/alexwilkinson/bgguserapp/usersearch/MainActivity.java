package co.alexwilkinson.bgguserapp.usersearch;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import java.util.concurrent.ExecutionException;

import co.alexwilkinson.bgguserapp.utilities.CreateUserDialogFrame;
import co.alexwilkinson.bgguserapp.utilities.DBManager;
import co.alexwilkinson.bgguserapp.HeaderActivity;
import co.alexwilkinson.bgguserapp.R;
import co.alexwilkinson.bgguserapp.utilities.UserRef;
import co.alexwilkinson.bgguserapp.utilities.WebBrowserActivity;

public class MainActivity extends HeaderActivity
        implements CreateUserDialogFrame.NoticeDialogListener, CreateUserDialogFrame.OnCompleteListener {
    private EditText etFindUser;
    protected ListView lvCollection;
    protected MyListAdapter myadapter;
    private Button buBgg;
    protected int userTotal;
    private DBManager dbManager;
    protected ContentValues values;
    private ProgressDialog progressDialog;

    //title, description, image(String)
    public static ArrayList<BoardgameListItem> bgList = new ArrayList<>();

    /**
     * main method for creating the activity most operations revolve around this
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup the objects to be callable
        lvCollection = (ListView) findViewById(R.id.lvCollection);
        etFindUser = (EditText) findViewById(R.id.etFindUser);

        System.out.println("database exists? "+DBManager.databaseExists());



    }

    /**
     * Core search which will check BGG for the users collection using the getGames Async method
     *
     * @param view
     */
    public void buSearch(View view) {

        String user = etFindUser.getText().toString(); //gets the username from the search box
        Context context = MainActivity.this;
        ProcessFeed getFeed = new ProcessFeed(user, context);
        try {
            getFeed.execute(MainActivity.this).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        userTotal = getFeed.getTotal();
        bgList = getFeed.getboardgameList();
        System.out.println(userTotal);
        myadapter = new MyListAdapter(bgList);
        lvCollection.setAdapter(myadapter);

    }

    /**
     * Main way to save the user data to the local database, this button checks the user has been searched
     * to ensure they exist, it will then check to see if the database exists, if not it will prompt
     * to add a prime user who this account belongs to.
     *
     * @param view
     */
    public void buSaveUser(View view) {
        //check to see if database exists
        if (DBManager.databaseExists() == false) { //checks to see if the database exists
            //steps needed to create and call the dialog window
            FragmentManager fm = getFragmentManager();
            CreateUserDialogFrame dialog;
            //send the current username from the search box
            dialog = CreateUserDialogFrame.setUsername(etFindUser.getText().toString());
            dialog.show(fm, "addUser");
        } else {
            if (bgList.size() != 0) {
                boolean exists = doesUserExist(etFindUser.getText().toString());
                System.out.println(exists);
                if (exists == false) {
                    createNewUser(etFindUser.getText().toString(), userTotal, false);
                }
            } else {
                System.out.println("user not checked");

            }
        }
    }

    /**
     * Implemented from the CreateUserDialogFrame, event that is fired from the pressing of the
     * positive button, not currently used.
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
    }

    /**
     * Implemented from the CreateUserDialogFrame, event that is fired from the pressing of the
     * negative button, no currently used.
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    /**
     * Implemented from the CreateUserDialogFrame, event that is fired at the end of the dialog session
     * these values are returned at the end.
     * @param username String that relates to the user to add.
     */
    @Override
    public void onComplete(String username) {
        //checks to make sure the username checked is no different from that originally presented
        //this is checked to prevent a fake user being added after the fact
        if (!username.equalsIgnoreCase(etFindUser.getText().toString())) {
            String user = username;
            etFindUser.setText(username);
            lvCollection.setAdapter(null);
            ProcessFeed getGames = new ProcessFeed(user);
            //add .execute().get() to force the application to run now
            try {
                getGames.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        //if the usertotal games are >0 the user will be created
        if (userTotal != 0) {
            createNewUser(username, userTotal, true);
        } else {
            Toast.makeText(this, "Search for the user first to ensure they exist", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Simple method that checks the database for the selected user, if it finds them returns
     * true else returns false.
     *
     * @param user user to be checked against the user table.
     * @return true if user exist, false if not.
     */
    public Boolean doesUserExist(String user) {
        boolean exists = false;
        dbManager = new DBManager(this, user);
        Cursor cursor1 = dbManager.queryUser(
                null,
                DBManager.colUsername + "=?",
                new String[]{etFindUser.getText().toString()},
                null);

        if (cursor1.moveToFirst()) {
            do {

                if (cursor1.getString(cursor1.getColumnIndex(DBManager.colUsername))
                        .equalsIgnoreCase(etFindUser.getText().toString())) {
                    exists = true;
                }

            } while (cursor1.moveToNext());
        }
        return exists;
    }

    /**
     * Protected method that takes the basic values and generates a user in the user table to store
     * for later.
     *
     * @param username name of searched user
     * @param userTotal the total number of games they have in their current collection
     * @param isprime will this be the prime user? generally only the first should be
     * @return
     */
    protected boolean createNewUser(String username, int userTotal, boolean isprime) {
        String userdata;
        dbManager = new DBManager(this, username);

        /*
        Calling the user ref file to save the basic user info for easy quick access of the basics
         */
        UserRef userRef = new UserRef(this);
        //TODO need to figure out why this is not updating currently with if statement
        if(DBManager.databaseExists() == false){
            userRef.saveData(username, userTotal);
        }

        values = new ContentValues();

        values.put(DBManager.colUsername, username);
        values.put(DBManager.colTotalGames, userTotal);
        values.put(DBManager.colPrimaryUser, isprime);

        //create a new async task to save the users boardgame collection
        Context context = getApplicationContext();
        SaveCurrentUser newUser = new SaveCurrentUser(username,bgList,context);
        System.out.println("Save users games now!");
        newUser.execute();

        /*
        run the insert user, will return a long valie, if it is greater than 0 the user has been
        added correctly, if not then there is a problem.
         */
        long id = dbManager.insertUser(values); //tries to insert the values
        if (id > 0) { //for success
            Toast.makeText(this,
                    "User: " + username + " was added and database",
                    Toast.LENGTH_LONG).show();
            return true;
        } else { //for failure
            Toast.makeText(this,
                    "Error something blew up! Search again and retry ",
                    Toast.LENGTH_LONG).show();
            return false;
        }

    }

    /**
     * private inner class that is used to control and display the XML content in the ListView
     * add all the items from the arraylist into the ListView
     */
    private class MyListAdapter extends BaseAdapter {
        public ArrayList<BoardgameListItem> bgList;

        /**
         * Constructor that takes the ArrayList of games to be used and added to the database.
         * @param bgList ArrayList of games stored as the BoardgameListItem object.
         */
        public MyListAdapter(ArrayList<BoardgameListItem> bgList) {
            this.bgList = bgList;
        }

        /**
         * Override Getter method to return the total number of games in the collection.
         * @return int total list of games in the collection
         */
        @Override
        public int getCount() {return bgList.size();}

        /**
         * Override method not in use
         * @param i
         * @return
         */
        @Override
        public Object getItem(int i) {
            return null;
        }

        /**
         * Override Getter method that returns the item id value.
         * @param i
         * @return long item id value
         */
        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * Override method that generates the view to be displayed in the ListView.
         * @param i item position.
         * @param view the view that will be loaded and check for the correct ListView.
         * @param viewGroup
         * @return custom View that will display the list of board games
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            //create the custom view to be used to display the list.
            LayoutInflater myInflater = getLayoutInflater();
            View myView = myInflater.inflate(R.layout.boardgame_collection_item, null);

            //the BoardgameListItem that will be used for each iteration
            final BoardgameListItem adapterItem = bgList.get(i);

            //Assigning the values from the BoardgameListItem to the componants
            TextView tvTitle = (TextView) myView.findViewById(R.id.tvTitle);
            tvTitle.setText(adapterItem.title);

            TextView tvDetails = (TextView) myView.findViewById(R.id.tvDetails);
            tvDetails.setText(adapterItem.description);

//            TextView tvImage = (TextView)myView.findViewById(R.id.tvImage);
//            tvImage.setText(adapterItem.image);

            //setting up the button that will link to the game on Board Game Geek
            buBgg = (Button) myView.findViewById(R.id.buBgg);
            buBgg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), WebBrowserActivity.class);
                    intent.putExtra("boardgame", adapterItem.gameID);
                    intent.putExtra("title", adapterItem.title);
                    startActivity(intent);
                }
            });

            return myView;
        }
    }


}
