package co.alexwilkinson.bgguserapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * the main splash screen for where users can be directed
 */
public class HomeActivity extends HeaderActivity implements View.OnClickListener{
    Button buSearchUser, buUserHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buSearchUser = (Button)findViewById(R.id.buSearchUser);
        buSearchUser.setOnClickListener(this);

        buUserHome = (Button)findViewById(R.id.buUserArea);
        buUserHome.setOnClickListener(this);
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
    }
}
