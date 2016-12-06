package co.alexwilkinson.bgguserapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Alex on 6/12/2016.
 */

public class FontManager {

    public static final String ROOT = "fonts/",
    FONTAWESOME = ROOT + "fontawesome_webfont.ttf";

    public static Typeface getTypeface(Context context, String font){
        return Typeface.createFromAsset(context.getAssets(),font);
    }
}
