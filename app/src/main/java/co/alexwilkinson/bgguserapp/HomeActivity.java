package co.alexwilkinson.bgguserapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * the main splash screen for where users can be directed
 */
public class HomeActivity extends HeaderActivity implements View.OnClickListener{
    Button buSearchUser, buUserHome, buCreateUser;
    TextView tvUser;
    UserRef userRef;

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



        if(DBManager.databaseExists() ==true){
            buCreateUser.setVisibility(View.GONE);
        }

        String userData = userRef.loadData();
        if(!userData.contains("No user created")) {
            String[]dataArray = userData.split("\n");
            tvUser = (TextView)findViewById(R.id.tvUser);

            tvUser.setText("Welcome back "+dataArray[0] + " you currently have " +dataArray[1]
                    + " games in your library"
            );
        }
    }

    @Override
    public void onClick(View view){
        int value = view.getId();
        if(value == R.id.buSearchUser){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

        }
        else if(value == R.id.buUserArea){

        }
        else if(value == R.id.buCreateUser){

        }
    }
}
