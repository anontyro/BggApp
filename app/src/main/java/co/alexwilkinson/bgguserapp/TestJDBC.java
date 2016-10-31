/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.alexwilkinson.bgguserapp;

import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * Abstract class to carry out the heavily lifting when connecting to a mysql
 * database contains all the logic for database integration
 *
 * @author Alex
 */
public class TestJDBC {

    private final String SQL_CON = "jdbc:mysql://";
    private final String SQL_LOCAL = "jdbc:mysql://localhost:3306/";
    private String table = "";
    private String url = "";
    protected String connection = "";
    private String username = "";
    private String dbPass = "";
    protected Connection mycon = null;
    protected Statement myst = null;
    protected ResultSet myrs = null;
    protected PreparedStatement pst = null;

    /**
     * Constructor that takes four variables to create the connection name and
     * save the username.
     *
     * @param url database address.
     * @param table which table you would like to connect to.
     * @param username username to log into the database.
     * @param dbPass the password required to log into the database.
     */
    public TestJDBC(String url, String table, String username, String dbPass) {
        connection = SQL_CON + url + table;
        this.username = username;
        this.table = table;
        this.dbPass = dbPass;
        this.url = url;

        if (dbPass.equals("")) {
            dbPass = userInput("Enter password: ");
        }

        try {
            testConnection(username, dbPass);
        } catch (SQLException e) {
            System.err.println("Unable to connect to database: \n" + e);
        }
    }

    public TestJDBC() {
        try {
            ArrayList<String> loginDetails = (ArrayList<String>) readFile("C:\\Users\\Alex\\Desktop\\db login.txt");

            this.username = loginDetails.get(0);
            this.dbPass = loginDetails.get(1);
            this.table = loginDetails.get(2);
            this.url = loginDetails.get(3);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        //this.username = LoginDetails.username;
        //this.table = LoginDetails.table;
        //this.dbPass = LoginDetails.password;
        //this.url = LoginDetails.database;
        connection = SQL_CON + url + table;
    }

    /**
     * method to test the connection to the database to ensure that one can be
     * made.
     *
     * @param username
     * @param password
     * @throws SQLException
     */
    public void testConnection(String username, String password) throws SQLException {
        String output = "";
        this.setUsername(username);

        try {
            mycon = DriverManager.getConnection(connection, username, password);
            if (mycon != null) {
                System.out.println("Connected to database: ");
            }
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            mycon.close();
        }
    }

    /**
     * no parameter method to test the database connection using the values
     * already assigned by the constructor
     *
     * @return String to inform of how the test went
     * @throws SQLException finally will throw so must be caught
     */
    public String testMyConnection() throws SQLException {
        try {
            mycon = DriverManager.getConnection(connection, getUsername(), getDbPass());
            if (mycon != null) {
                return "Connected to database";
            }
        } catch (SQLException e) {
            return e.getMessage();
        } finally {
            mycon.close();
        }
        return "Could not connect to database try again";

    }

    /**
     * method that will ask values to be added to the database
     *
     * @throws SQLException to close the connection this exception is thrown
     */
    public void addToDB() throws SQLException {
        String[] coloums;
        String[] values;
        int noCols = 0;
        String preparedUpdate = "INSERT INTO ";
        String coloumsToPlace = "";
        String valuesToPlace = "";

        //requests the table which will be written to from the user
        String table = userInput("Enter table to add to: ");
        try {
            //converts into a number
            noCols = Integer.parseInt(userInput("Number of colomns: "));
        } catch (Exception e) { //catches error if Integer cannot parse
            System.err.println(e);
        }
        //assign the size of the two arrays
        coloums = new String[noCols];
        values = new String[noCols];

        /*
         for loop used to generate all the coloums the user wants to add
         asking for the coloumn name (same as in database) followed by the requested
         value for that coloum, both put into respective arrays to be called later
         */
        for (int i = 0; i < noCols; i++) {
            String userColoum = userInput("What is the coloumn name: ");
            coloums[i] = userColoum;
            String userValue = userInput("What is the value for " + userColoum + ":");
            values[i] = userValue;
        }

        /*
         creates the two strings to be used for the update command and create the
         correct strings
         */
        for (String x : coloums) {
            coloumsToPlace += x + ",";
            valuesToPlace += "?,";
        }

        /*
         used to remove the last ',' in each of the strings to ensure that the
         sql command works correctly
         */
        coloumsToPlace = coloumsToPlace.substring(0, (coloumsToPlace.length() - 1));
        valuesToPlace = valuesToPlace.substring(0, (valuesToPlace.length() - 1));

        //creates the prepared string for sql
        preparedUpdate += table + "(" + coloumsToPlace + ")" + " VALUES"
                + "(" + valuesToPlace + ")";

        //requests the password from the user, used to login if none exists
        if (getDbPass().equals("")) {
            setDbPass(userInput("Enter password: "));
        }

        // if any of the arrays are not inialized will exit after this error
        if ((values == null) || (coloums == null)) {
            System.err.println("No data to add, quiting");
            System.exit(1);
        }

        /*
         main block used to add the information to the database
         */
        try {
            //used to initilise the connection to the database
            mycon = DriverManager.getConnection(connection, getUsername(), getDbPass());

            //set the prepareStatement using the values above
            pst = mycon.prepareStatement(preparedUpdate);

            // for loop to create all of the values from the prepared statment
            for (int i = 0; i < values.length; i++) {
                pst.setString(i + 1, values[i]);
            }
            //execute the update
            pst.executeUpdate();
            System.out.println("Database updated");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mycon.close();
        }

    }

    /**
     *
     * @param selectCol
     * @param table
     * @param colValu
     * @return
     * @throws SQLException
     */
    public String queryDB(String selectCol, String table, String colValu) throws SQLException {
        String output = "";
        if (getDbPass().equals("")) {
            setDbPass(userInput("Enter database password: "));
        }

        String queryDB = "SELECT " + selectCol + " FROM " + table + " WHERE " + selectCol + " =?";
        System.out.println(queryDB);

        try {
            mycon = DriverManager.getConnection(connection, getUsername(), getDbPass());

            pst = mycon.prepareStatement(queryDB);
            pst.setString(1, colValu);
            ResultSet rs = pst.executeQuery();

            output = rs.getString(selectCol);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mycon.close();
        }

        return output;
    }

    /**
     * A query to find the column names from the table and return the values as
     * a collection.
     *
     * @param table
     * @return
     * @throws SQLException
     */
    public Collection getColumnNames(String table) throws SQLException {
        if (getDbPass().equals("")) {
            setDbPass(userInput("Enter database password: "));
        }

        String queryColumn = "SELECT * FROM " + table;
        ArrayList<String> columnList = new ArrayList<>();

        try {
            mycon = DriverManager.getConnection(connection, getUsername(), getDbPass());

            pst = mycon.prepareStatement(queryColumn);
            myrs = pst.executeQuery();

            ResultSetMetaData rsmd = myrs.getMetaData();

            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columnList.add(rsmd.getColumnName(i));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mycon.close();
        }
        return columnList;
    }

    /**
     * getter to return connection String value.
     *
     * @return
     */
    public String getConnection() {

        return connection;
    }

    /**
     * setter to alter the connection String value.
     *
     * @param connection
     */
    public void setConnection(String connection) {
        this.connection = connection;
    }

    //utility method that will allow user input
    protected String userInput(String prompt) {
        String user = "";

        Scanner input = new Scanner(System.in);
        System.out.print(prompt);
        user = input.nextLine();

        return user;
    }

    public Collection getColVals(String col, String table) throws SQLException {
        ArrayList<String> colReturn = new ArrayList<>();

        String query = "SELECT " + col + " FROM " + table;

        String test = "";

        try {
            mycon = DriverManager.getConnection(connection, getUsername(), getDbPass());

            pst = mycon.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                colReturn.add(rs.getString(col));
                test += rs.getString(col);

            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            mycon.close();
        }

        return (Collection) colReturn;
    }

    public static Collection readFile(String filename) throws IOException {
        File inFile = new File(filename);
        BufferedReader bin = null;
        String fileReading = "";
        String[] readLines;
        ArrayList<String> outputCol = new ArrayList<>();

        try {
            FileReader fin = new FileReader(inFile);
            bin = new BufferedReader(fin);
            String line = bin.readLine();
            while (line != null) {
                fileReading += line + "\n";

                line = bin.readLine();
            }
            readLines = fileReading.split("\n");

            for (String x : readLines) {
                outputCol.add(x);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());

        } finally {
            if (bin != null) {
                bin.close();
            }
        }
        return outputCol;
    }

    public static void writeToFile(String filename, String[] output) {
        PrintWriter out = null;

        try {
            File outFile = new File(filename);

            FileWriter fout = new FileWriter(outFile);
            BufferedWriter bout = new BufferedWriter(fout);
            out = new PrintWriter(bout);

            for (String x : output) {
                out.println(x);
            }
            out.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static void appendToFile(String filename, String[] output){
        PrintWriter out = null;

        try{
            FileWriter fout = new FileWriter(filename, true);
            BufferedWriter bout = new BufferedWriter(fout);
            out = new PrintWriter(bout);

            for(String x : output){
                out.println(x);
            }
            out.close();
        }
        catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        finally{
            if(out !=null){
                out.close();
            }
        }
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the dbPass
     */
    public String getDbPass() {
        return dbPass;
    }

    /**
     * @param dbPass the dbPass to set
     */
    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

}
