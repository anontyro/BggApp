package co.alexwilkinson.bgguserapp.usersearch;

/**
 * Created by Alex on 26/10/2016.
 */

public class BoardgameListItem {
    public String title;
    public String description;
    public String image;
    public String gameID;
    public boolean owned;
    public boolean wantsToPlay;
    public boolean wishlist;

    BoardgameListItem(String title, String description, String gameID){
        this.title = title;
        this.description = description;
//        this.image = image;
        this.gameID = gameID;
    }
    BoardgameListItem(String title, String description, String gameID, String image){
        this.title = title;
        this.description = description;
        this.image = image;
        this.gameID = gameID;
    }

    BoardgameListItem(
            String title, String description, String gameID, String image,
            int owned, int wantsToPlay, int wishlist){

        this.title = title;
        this.description = description;
        this.image = image;
        this.gameID = gameID;
        addStatus(owned,wantsToPlay,wishlist);
    }

    public void addStatus(int own, int wPlay, int wishlist){
        addowned(own);
        addWillPlay(wPlay);
        addwishlist(wishlist);
    }

    public void addimage(String image){
        this.image = image;
    }

    public void addowned(int owned){
        if(owned ==0){
            this.owned = false;
        }
        else {
            this.owned = true;
        }
    }

    public void addWillPlay(int wPlay){
        if(wPlay ==0){
            this.wantsToPlay = false;
        }
        else {
            this.wantsToPlay = true;
        }
    }

    public void addwishlist (int wish){
        if(wish ==0){
            this.wishlist = false;
        }
        else {
            this.wishlist = true;
        }
    }









}
