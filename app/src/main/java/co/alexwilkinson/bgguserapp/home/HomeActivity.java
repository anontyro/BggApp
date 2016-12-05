package co.alexwilkinson.bgguserapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.alexwilkinson.bgguserapp.userarea.UserAreaMainActivity;
import co.alexwilkinson.bgguserapp.utilities.DBManager;
import co.alexwilkinson.bgguserapp.HeaderActivity;
import co.alexwilkinson.bgguserapp.R;
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


        if(DBManager.databaseExists() ==true){
            buCreateUser.setVisibility(View.GONE);
            if(userData.contains("No user created")) {

            }
        }

        if(!userData.contains("No user created")) {
            String[]dataArray = userData.split("\n");
            System.out.println(userData.toString());
            tvUser = (TextView)findViewById(R.id.tvUser);

            tvUser.setText("Welcome back "+dataArray[0] + " you currently have " +dataArray[1]
                    + " games in your library"
            );
        }

        if(userData.contains("No user created")){
            buUserHome.setEnabled(false);
            buUpdateUser.setEnabled(false);
        }
    }
}
