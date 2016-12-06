package co.alexwilkinson.bgguserapp.home;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.alexwilkinson.bgguserapp.userarea.UserAreaMainActivity;
import co.alexwilkinson.bgguserapp.utilities.DBManager;
import co.alexwilkinson.bgguserapp.HeaderActivity;
import co.alexwilkinson.bgguserapp.R;
import co.alexwilkinson.bgguserapp.utilities.FontManager;
import co.alexwilkinson.bgguserapp.utilities.UserRef;
import co.alexwilkinson.bgguserapp.usersearch.MainActivity;

/**
 * the main splash screen for where users can be directed
 */
public class HomeActivity extends HeaderActivity implements View.OnClickListener{
    Button buSearchUser, buUserHome, buCreateUser, buUpdateUser;
    TextView tvUser;
    UserRef userRef;
    String userData;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        userRef = new UserRef(this);

        buSearchUser = (Button)findViewById(R.id.buSearchUser);
        buSearchUser.setOnClickListener(this);

        buUserHome = (Button)findViewById(R.id.buUserArea);
        buUserHome.setOnClickListener(this);

        buCreateUser = (Button)findViewById(R.id.buCreateUser);
        buCreateUser.setOnClickListener(this);

        buUpdateUser = (Button)findViewById(R.id.buPrimeUpdate);
        buUpdateUser.setOnClickListener(this);

        checkPrimeUserExists();



    }

    @Override
    public void onClick(View view){
        int value = view.getId();
        if(value == R.id.buSearchUser){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

        }
        else if(value == R.id.buUserArea){
            Intent intent = new Intent(getApplicationContext(), UserAreaMainActivity.class);
            startActivity(intent);

        }else if(value == R.id.buPrimeUpdate){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("request","update prime");
            startActivity(intent);

        }else if(value == R.id.buCreateUser){

        }
    }

    public void checkPrimeUserExists(){
        userData = userRef.loadData();


        if(DBManager.databaseExists() ==true) {
            buCreateUser.setVisibility(View.GONE);
            if (userData.contains("No user created")) {
                String prime = whoIsPrime();
                String[] primeArray = prime.split("\n");
                userRef.saveData(primeArray[0], Integer.parseInt(primeArray[1]));
            }else{
                String[] dataArray = userData.split("\n");
                System.out.println(userData.toString());

                String prime = whoIsPrime();
                String[] primeArray = prime.split("\n");
                if (!dataArray[0].equalsIgnoreCase(primeArray[0])) {
                    userRef.saveData(primeArray[0], Integer.parseInt(primeArray[1]));
                }

                tvUser = (TextView) findViewById(R.id.tvUser);

                tvUser.setText("Welcome back " + dataArray[0] + " you currently have " + dataArray[1]
                        + " games in your library"
                );
            }
        }

        if(userData.contains("No user created")){
            buUserHome.setEnabled(false);
            buUpdateUser.setEnabled(false);
        }
    }

    public String whoIsPrime(){
        String output = "";

        dbManager = new DBManager(this, "");

        Cursor cursor = dbManager.queryUser(null,DBManager.colPrimaryUser+" =?" ,
                new String[]{"1"},null);

        if (cursor.moveToFirst()){
            do{
                output = cursor.getString(cursor.getColumnIndex(DBManager.colUsername)) + "\n";
                output += cursor.getString(cursor.getColumnIndex(DBManager.colTotalGames));
            }while(cursor.moveToNext());
        }

        return output;
    }






















}
