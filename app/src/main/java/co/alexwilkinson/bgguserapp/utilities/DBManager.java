package co.alexwilkinson.bgguserapp.utilities;

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
    private static String mainUser = "";

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
    public static final String colReleased = "Yearpublished"; //String link to game page
    public static final String colBggPage = "BggLink"; //String link to game page
    public static final String colID = "GameID"; // String for the gameID

    public final static String buildUserTable =
            "CREATE TABLE IF NOT EXISTS " +tableUsers
            +"("
                    + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + colUsername +" TEXT, "
                    + colTotalGames+" INT, "
                    + colPrimaryUser +" BOOLEAN " +
                    ");"
            ;

    public final static String buildGameTable =
            "CREATE TABLE IF NOT EXISTS " +tableGames +getUser()+"("
                    + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + colForUsername + " TEXT NOT NULL, "
                    + colTitle + " TEXT, "
                    + colOwned + " BOOLEAN, "
                    + colDuration + " INT, "
                    + colPlayerCount + " INT, "
                    + colImage + " TEXT, "
                    + colWantToPlay + " BOOLEAN, "
                    + colWishlist + " BOOLEAN, "
                    + colModified + " TEXT, "
                    + colBggPage + " TEXT, "
                    + colID +" TEXT, "
                    + colReleased +" INT, "
                    + "FOREIGN KEY ("+colForUsername+") REFERENCES "+tableUsers +"(" +colUsername + ")" +
                    ");"
            ;

    public DBManager(Context context, String mainUser){
        this.mainUser = mainUser;
        DatabaseHelper db = new DatabaseHelper(context);
        sqlDB = db.getWritableDatabase();
    }

    //method that calls the database exists methods taking no parameters if looking for default
    public static boolean databaseExists(){
        return databaseExists("/data/data/co.alexwilkinson.bgguserapp/databases/BggApp");
    }
    //allows the user to add their own path if different
    public static boolean databaseExists(String dbPath){
        SQLiteDatabase dbTest = null;

        try{
            dbTest = SQLiteDatabase.openDatabase(dbPath,null,SQLiteDatabase.OPEN_READONLY);
            dbTest.close();
            System.out.println("Database does exists");
        }
        catch(Exception ex){
            System.out.println("Database does not exist");
        }
        return dbTest != null;
    }

    public long insertUser(ContentValues values){
        long id = sqlDB.insert(tableUsers,"",values);

        return id;
    }
    public long insertGame(ContentValues values){
        long id = sqlDB.insert(tableGames,"",values);

        return id;
    }
    public void queryGameTable(String query){
        sqlDB.execSQL(query);


    }

    /*
    query the games table

     */
    public Cursor queryGame(String[]columns, String where, String[]whereArgs, String sortOrder){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableGames);

        Cursor cursor = qb.query(sqlDB,columns,where,whereArgs,null,null,sortOrder);

        return cursor;
    }

    //query for user table
    public Cursor queryUser(String[]projection, String selection, String[]selectArgs, String sortOrder){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableUsers);

        Cursor cursor = qb.query(sqlDB,projection,selection,selectArgs,null,null,sortOrder);


        return cursor;
    }

    public static String getUser(){return mainUser.toLowerCase();}


    public static class DatabaseHelper extends SQLiteOpenHelper{
        private Context context;

        public DatabaseHelper(Context context){
            super(context,dbName,null,dbVersion);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(buildUserTable);
            db.execSQL(buildGameTable);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        }
    }




}
