package co.alexwilkinson.bgguserapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

/**
 * the root Activity that will determain consistent style and menu across all of the other
 * activies in the app
 */
public class HeaderActivity extends AppCompatActivity {
    UserRef userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header);

        userRef = new UserRef(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        //used for the search bar in menubar
//        SearchView sv = (SearchView) menu.findItem(R.id.searchbar).getActionView();
//        SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        sv.setSearchableInfo(sm.getSearchableInfo(getComponentName()));
//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //when enter a search query
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getApplicationContext(),query,Toast.LENGTH_LONG).show();
//                return false;
//            }
//
//            //writing text into the searchbar
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.muhome:
                Intent home = new Intent(this,HomeActivity.class);
                startActivity(home);
                return true;
            case R.id.muuserarea:
                Toast.makeText(getApplicationContext(), "userarea clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.musearchuser:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
