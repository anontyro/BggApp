package co.alexwilkinson.bgguserapp.usersearch;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import co.alexwilkinson.bgguserapp.utilities.DBManager;

/**
 * Created by Alex on 5/12/2016.
 */

public class SaveCurrentUser extends AsyncTask {
    private ArrayList<BoardgameListItem> bgList = new ArrayList<>();
    private String username="";
    protected DBManager dbManager;
    private Context context;

    public SaveCurrentUser(String username,ArrayList<BoardgameListItem>bgList, Context context) {
        this.bgList = bgList;
        this.username = username;
        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        if(bgList.size() == 0){
            cancel(true);
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        addUser();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    protected void addUser() {
        dbManager = new DBManager(context,username);


        ContentValues values = new ContentValues();

        for (int i = 0; i < bgList.size(); i++) {

            final BoardgameListItem item = bgList.get(i);

            //list of values to add to the database compiled
            values.put(DBManager.colForUsername, username);
            values.put(DBManager.colTitle, item.title);
            values.put(DBManager.colReleased, item.description);
            values.put(DBManager.colImage, item.image);
            values.put(DBManager.colID, item.gameID);
            values.put(DBManager.colBggPage, "https://boardgamegeek.com/boardgame/" + item.gameID);
            values.put(DBManager.colOwned, item.owned);
            values.put(DBManager.colWantToPlay, item.wantsToPlay);
            values.put(DBManager.colWishlist, item.wishlist);

            dbManager.insertGame(values);

        }

    }












}
