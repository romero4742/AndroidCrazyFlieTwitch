package se.bitcraze.CS408;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import se.bitcraze.crazyfliecontrol.prefs.PreferencesActivity;
import se.bitcraze.crazyfliecontrol2.R;

/**
 * Wrapper class, embeds a webview for the twitch player
 */
public class TwitchView extends WebView {

    private final String API_BASE_URL = "https://api.twitch.tv/kraken/streams/";
    private final String BASE_URL = "http://www.twitch.tv/";
    private final String EMBED = "/embed";
    private final String DEFAULT_CHANNEL = "monstercat";
    private final String TAG = "TwitchView";

    private String channelName;

    //default size of twitch player
    private final float DEFAULT_WIDTH_PERCENT = 0.70f;
    private float width_percent;

    public TwitchView(Context context) {
        super(context);
        initTwitchView();
    }

    public TwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTwitchView();
    }

    public TwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTwitchView();
    }

    private void initTwitchView(){
        width_percent = DEFAULT_WIDTH_PERCENT;

        WebSettings mWebSettings = getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    public void setPlayerWidth(float percent){
        if(percent < 0 || percent > 1){
            Log.e("MyTag","Invalid percent set to player " + percent);
            return;
        }
        this.width_percent = percent;
    }

    public float getPlayerWidth(){
        return width_percent;
    }

    public void setChannelName(String name){
        this.channelName = name;
    }

    public String getChannelName(){
        return channelName;
    }

    public void start(){
        String tempUrl = BASE_URL;
        if(channelName != null && channelName.length() > 0){
            tempUrl += channelName + EMBED;
        } else {
            tempUrl += DEFAULT_CHANNEL + EMBED;
        }
        loadUrl(tempUrl);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * returns the size based on percentage
     * @param percent
     * @return size in pixels
     */
    public double widthPercentage(float percent){
        double screenWidth = PreferencesActivity.getScreenWidthIndp(getContext());

        if(percent < 0 || percent > 1){
            return dpToPixels(screenWidth);
        }
        return percent * dpToPixels(screenWidth);
    }

    private double dpToPixels(double dp){
        final float scale = this.getResources().getDisplayMetrics().density;
        return dp*scale + 0.5f;
    }

    /**
     * receives root view and attaches itself to the dynamic container
     * @param view
     */
    public void displayTwitchView(View view){
        double pixels = widthPercentage(width_percent);
        FrameLayout layout = (FrameLayout)view.findViewById(R.id.dynamicContainer);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int)pixels, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);
        layout.addView(this);
    }

    /**
     * root view is needed to find view instance
     * @param view
     */
    public void removeTwitchView(View view){
        stopLoading();
        destroy();
        ViewGroup parent = (ViewGroup)getParent();
        parent.removeView(this);
        FrameLayout layout = (FrameLayout)view.findViewById(R.id.dynamicContainer);
        layout.removeView(this);
  }

}