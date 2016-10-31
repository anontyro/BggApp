package co.alexwilkinson.bgguserapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import java.sql.*;

/**
 * Created by Alex on 21/10/2016.
 */

public class DBManager {
    private SQLiteDatabase sqlDB;
    public static final String dbName="BggApp";
    public static final int dbVersion = 1;
    public static String mainUser = "";

    //all items for the users table
    public static final String tableUsers = "BggUsers";
    //--------------------------------------------------
    public static final String colUsername = "Username"; //primary key
    public static final String colTotalGames = "TotalGames"; //int
    public static final String colPrimaryUser = "PrimaryUser"; //boolean

    //all data for the game list table
    public static final String tableGames = "GameList";
    //--------------------------------------------------
    public static final String colForUsername = "Username"; // foreign key
    public static final String colOwned = "Owned"; // boolean
    public static final String colTitle = "GameTitle";
    public static final String colDuration = "Length"; //int
    public static final String colPlayerCount = "MaxPlayers"; //int
    public static final String colImage = "ImageURL";
    public static final String colWantToPlay = "WantToPlay"; //boolean
    public static final String colWishlist = "Wishlist"; //boolean
    public static final String colModified = "LastModified"; //String/ date time
    public static final String colBggPage = "BggLink"; //String link to game page
    public static final String colID = "ID"; // prim key auto increment

    private final String buildUserTable =
            "CREATE TABLE IF NOT EXISTS " +tableUsers
            +"("+ colUsername +" TEXT PRIMARY KEY, "+colTotalGames+" INT, "+ colPrimaryUser +" BOOLEAN );"
            ;

    public DBManager(Context context, String mainUser){
        this.mainUser = mainUser;
        DatabaseHelper db = new DatabaseHelper(context);
        sqlDB = db.getWritableDatabase();

    }

    public long insertUser(ContentValues values){
        long id = sqlDB.insert(tableUsers,"",values);

        return id;
    }
    public long insertGame(ContentValues values){
        long id = sqlDB.insert(tableGames,"",values);

        return id;
    }

    //query the games table
    public Cursor query(String[]projection, String selection, String[]selecArgs, String sortOrder){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableGames);

        Cursor cursor = qb.query(sqlDB,projection,selection,selecArgs,null,null,sortOrder);

        return cursor;
    }




    public static class DatabaseHelper extends SQLiteOpenHelper{
        private Context context;

        public DatabaseHelper(Context context){
            super(context,dbName,null,dbVersion);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }




}
