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
 * Helper class used to allow the app to interface with the SQLite internal database
 */

public class DBManager {
    //declared variables
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
    public static final String colTitle = "GameTitle"; //String
    public static final String colDuration = "Length"; //int
    public static final String colPlayerCount = "MaxPlayers"; //int
    public static final String colImage = "ImageURL"; //String for url
    public static final String colWantToPlay = "WantToPlay"; //boolean
    public static final String colWishlist = "Wishlist"; //boolean
    public static final String colModified = "LastModified"; //String/ date time
    public static final String colReleased = "Yearpublished"; //String link to game page
    public static final String colBggPage = "BggLink"; //String link to game page
    public static final String colID = "GameID"; // String for the gameID

    //builder user table query
    public final static String buildUserTable =
            "CREATE TABLE IF NOT EXISTS " +tableUsers
            +"("
                    + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + colUsername +" TEXT, "
                    + colTotalGames+" INT, "
                    + colPrimaryUser +" BOOLEAN " +
                    ");"
            ;

    //build game table query
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

    /**
     * Constructor for the class that will take the application context and the mainuser creating
     * a writeable instance of the database.
     * @param context this or getApplicationContext()
     * @param mainUser the primary user of the database, the phone owner
     */
    public DBManager(Context context, String mainUser){
        this.mainUser = mainUser;
        DatabaseHelper db = new DatabaseHelper(context);
        sqlDB = db.getWritableDatabase();
    }


    /**
     * Simple method that called the databaseExists method to check if the database has yet been
     * created.
     * @return true if database has been created false if not
     */
    public static boolean databaseExists(){
        return databaseExists("/data/data/co.alexwilkinson.bgguserapp/databases/BggApp");
    }

    /**
     * Overide method that takes one parameter to check that the database exists.
     * @param dbPath the full location of the database
     * @return true if database exists false if not
     */
    public static boolean databaseExists(String dbPath){
        SQLiteDatabase dbTest = null;

        try{
            dbTest = SQLiteDatabase.openDatabase(dbPath,null,SQLiteDatabase.OPEN_READONLY);
            dbTest.close();
        }
        catch(Exception ex){
        }
        return dbTest != null;
    }

    /**
     * Method used to add a user into the database (UserTable) by constructing values to add.
     * @param values the arguments to add into the database
     * @return long id >0 inserted
     */
    public long insertUser(ContentValues values){
        long id = sqlDB.insert(tableUsers,"",values);

        return id;
    }

    /**
     * Method used to add a new game into the database (GameTable) by constructing the values to add.
     * @param values list of arguments to add to the database
     * @return long id > 0 instered
     */
    public long insertGame(ContentValues values){
        long id = sqlDB.insert(tableGames,"",values);
        return id;
    }


    public void removeGames(String username){
        sqlDB.execSQL("DELETE FROM "+tableGames + " WHERE "+colForUsername +" = "+"'" +username +"'"+ ";");
    }
    public void removeUser(String username){
        sqlDB.execSQL("DELETE FROM "+tableUsers + " WHERE "+colUsername +" = "+"'" +username +"'"+ ";");
    }

    /**
     * Open Method that allows for full String SQL queries to be used, returns void.
     * @param query full String SQL query
     */
    public void queryGameTable(String query){sqlDB.execSQL(query);}

    /**
     * Method that queries the Game table using verious parameters.
     * @param columns String array of colomns as named in the table
     * @param where String value of the selection statement after the SQL Where
     * @param whereArgs String array of the where arguments in order to fill in the ? ? ?
     * @param sortOrder String value of what to sort the returned values by
     * @return Database cursor returned to iterate through the values received
     */
    public Cursor queryGame(String[]columns, String where, String[]whereArgs, String sortOrder){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableGames);

        Cursor cursor = qb.query(sqlDB,columns,where,whereArgs,null,null,sortOrder);

        return cursor;
    }


    /**
     * Method that queries the User table using verious user defined parameters.
     * @param columns String array of the coloumn names
     * @param where String value of the where arguments
     * @param whereArgs String value of arguments to be used in the where arguments ? ? ?
     * @param sortOrder String value of the way to sort the returned values
     * @return Database curosr returned to iterate through the values recieved from the query
     */
    public Cursor queryUser(String[]columns, String where, String[]whereArgs, String sortOrder){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableUsers);

        Cursor cursor = qb.query(sqlDB,columns,where,whereArgs,null,null,sortOrder);


        return cursor;
    }

    /**
     * Getter method used to return the value of the main user for the DBManager in lowercase
     * @return String value of the user in lowercase.
     */
    public static String getUser(){return mainUser.toLowerCase();}


    /**
     * Inner helper class that is used to interface with the database and build or update the
     * tables within.
     */
    public static class DatabaseHelper extends SQLiteOpenHelper{
        private Context context;

        /**
         * Constructor that takes a single argument.
         * @param context this or getApplicationContext()
         */
        public DatabaseHelper(Context context){
            super(context,dbName,null,dbVersion);
            this.context = context;
        }

        /**
         * Override method call to create the database with default tables
         * @param db SQLiteDatabase parameter
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(buildUserTable);
            db.execSQL(buildGameTable);

        }

        /**
         * Override upgrade method that will update the database and version, currently only placeholder
         * @param db
         * @param i
         * @param i1
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        }
    }




}
