package co.alexwilkinson.bgguserapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Alex on 10/11/2016.
 */

public class UserRef {
    SharedPreferences sharedRef;

    public UserRef(Context context){
        sharedRef = context.getSharedPreferences("userRef", Context.MODE_PRIVATE);
    }

    public void saveData(String username, int totalGames){
        SharedPreferences.Editor editor = sharedRef.edit();
        String games = String.valueOf(totalGames);
        editor.putString("username", username);
        editor.putString("totalGames", games);
        editor.commit();
    }

    public String loadData(){
        String filecontent= sharedRef.getString("username","No user created");
        filecontent +="\n" + sharedRef.getString("totalGames", "No games added");
        return filecontent;
    }
}
