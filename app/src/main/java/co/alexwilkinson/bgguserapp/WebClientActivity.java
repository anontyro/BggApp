package co.alexwilkinson.bgguserapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebClientActivity extends AppCompatActivity {
    WebView wvBrowser;
    String url ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_client);

        Bundle b = getIntent().getExtras();
        url = "http://boardgamegeek.com/boardgame/" + b.getString("boardgame");

        System.out.println(url);

        wvBrowser = (WebView)findViewById(R.id.wvBrowser);
//        this.getActionBar().setTitle("Boardgames");

        wvBrowser.setWebViewClient(new myWebView());
        wvBrowser.getSettings().setJavaScriptEnabled(true);
        wvBrowser.getSettings().setDomStorageEnabled(true);

        wvBrowser.loadUrl(url);

    }




    private class myWebView extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url);
            return true;
        }
    }
}
