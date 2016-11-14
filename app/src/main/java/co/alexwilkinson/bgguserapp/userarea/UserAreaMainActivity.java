package co.alexwilkinson.bgguserapp.userarea;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import co.alexwilkinson.bgguserapp.R;
import co.alexwilkinson.bgguserapp.utilities.DBManager;
import co.alexwilkinson.bgguserapp.utilities.UserRef;

public class UserAreaMainActivity extends AppCompatActivity {
    protected Spinner spUser;
    protected DBManager dbManager;
    public UserRef userRef;
    public ArrayList<String>users = new ArrayList<>();
    protected ArrayList<String>userGames;
    protected ListView lvUserGames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area_main);

        userRef = new UserRef(this);

        spUser = (Spinner)findViewById(R.id.spUserArea);
        String[]userData = (userRef.loadData()).split("\n");

        lvUserGames = (ListView)findViewById(R.id.lvCollection);

        dbManager = new DBManager(this,userData[0]);
        final Cursor cursor = dbManager.queryUser(null,null,null,null);

        if(cursor.moveToFirst()){
            do{

                users.add(cursor.getString(cursor.getColumnIndex(DBManager.colUsername)));

            }while(cursor.moveToNext());
        }
        System.out.println(users.toString());

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,users);

        spUser.setAdapter(arrayAdapter);

        spUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                lvUserGames.setAdapter(null);
                userGames = getUserGames(users.get(i));
                Toast.makeText(getApplicationContext(),users.get(i),Toast.LENGTH_LONG).show();
                System.out.println(userGames.size());
                System.out.println(userGames.toString());
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


}
