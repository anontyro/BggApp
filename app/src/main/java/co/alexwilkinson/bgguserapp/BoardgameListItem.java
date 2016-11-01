package co.alexwilkinson.bgguserapp;

/**
 * Created by Alex on 26/10/2016.
 */

public class BoardgameListItem {
    public String title;
    public String description;
    public String image;
    public String gameID;

    BoardgameListItem(String title, String description, String image, String gameID){
        this.title = title;
        this.description = description;
        this.image = image;
        this.gameID = gameID;
    }
}
