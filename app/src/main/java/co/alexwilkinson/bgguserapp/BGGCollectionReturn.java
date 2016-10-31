package co.alexwilkinson.bgguserapp;

import android.app.Activity;
import android.provider.DocumentsContract;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Alex on 26/10/2016.
 */

public class BGGCollectionReturn{

    String bgg = "https://www.boardgamegeek.com/xmlapi/";
    String collection = "collection/";
    String username = "";
    URL url;
    String rawXML = "";

    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> released = new ArrayList<>();
    ArrayList<String> image = new ArrayList<>();

    public BGGCollectionReturn(String username){
        this.username = username;
    }

    public ArrayList<BoardgameListItem> getUserCollection (){
        ArrayList<BoardgameListItem> bgList = new ArrayList<>();

        try{
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            xmlFactory.setNamespaceAware(false);

            XmlPullParser xpp = xmlFactory.newPullParser();

            xpp.setInput(getInputStream(url), "UTF_8");

            rawXML = xpp.getText();

            int eventType = xpp.getEventType();
            while( eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG){

                    if(xpp.getName().equalsIgnoreCase("name")){
                        name.add(xpp.nextText());
                    }
                    else if(xpp.getName().equalsIgnoreCase("yearpublished")){
                        released.add(xpp.nextText());
                    }
                    else if(xpp.getName().equalsIgnoreCase("thumbnail")){
                        image.add(xpp.nextText());
                    }
                }
                eventType = xpp.next();
            }

        }
        catch(Exception ex){
            ex.getStackTrace();
        }
        return bgList;
    }

    public InputStream getInputStream(URL url){
        try{
            return url.openConnection().getInputStream();

        }
        catch(Exception ex){
            ex.getStackTrace();
            return null;
        }
    }

    public ArrayList<String> output(){
        return name;
    }


}
