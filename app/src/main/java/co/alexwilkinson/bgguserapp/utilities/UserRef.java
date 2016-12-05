package co.alexwilkinson.bgguserapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Simple SharedRef that stores the basic details of the prime user for quick ref and easy
 * customisation
 * Created by Alex on 10/11/2016.
 */

public class UserRef {
    SharedPreferences sharedRef;

    public UserRef(Context context){
        sharedRef = context.getSharedPreferences("userRef", Context.MODE_PRIVATE);
    }

    /**
     * Save call that allows user data to be saved to the shared ref easily.
     * @param username String of the prime user BGG username.
     * @param totalGames int of the prime users total games.
     */
    public void saveData(String username, int totalGames){
        SharedPreferences.Editor editor = sharedRef.edit();
        String games = String.valueOf(totalGames);
        editor.putString("username", username);
        editor.putString("totalGames", games);
        editor.commit();
    }

    /**
     * Load call that will return the username and total games as a string seperated by a new line.
     * @return
     */
    public String loadData(){
        String filecontent= sharedRef.getString("username","No user created");
        filecontent +="\n" + sharedRef.getString("totalGames", "No games added");
        return filecontent;
    }

}
