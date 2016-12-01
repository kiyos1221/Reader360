package com.a360ground.epubreader360;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.a360ground.epubreader360.EpubManipulation.EpubReader;
import com.a360ground.epubreader360.View.ExtendedWebview;
import com.a360ground.epubreader360.View.StyleableTextView;
import com.a360ground.epubreader360.utils.GeneralUtills;
import com.a360ground.epubreader360.utils.PreferenceUtill;
import com.a360ground.epubreader360.utils.ShareUtill;
import com.a360ground.epubreader360.utils.TAGS;
import com.a360ground.epubreader360.utils.TinyDB;
import com.a360ground.epubreader360.utils.quickaction.ActionItem;
import com.a360ground.epubreader360.utils.quickaction.QuickAction;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bossturban.webviewmarker.TextSelectionSupport;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class ReaderActivity extends AppCompatActivity {
    ExtendedWebview webView;
    int position;
    float brightness;
    Button day, night, sepia;
    Boolean wasClicked = false;
    ImageView bookmark,brightnessLow,brightnessHigh;
    AHBottomNavigation bottomNavigation;
    StyleableTextView ebrima, jiret, wookianos, nyala;
    ImageButton minimizeFont, maximizeFont;
    AppCompatSeekBar brightnessSeekBar;
    BottomSheetBehavior bottomSheetBehavior;
    private int pos, fontCounter = 0;
    WindowManager.LayoutParams params;
    private String bookname;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        params = getWindow().getAttributes();
        params.screenBrightness = Float.parseFloat(PreferenceUtill.readFromPreferences(this, TAGS.BRIGHTNESS_SETTING, String.valueOf(0.5f)));
        getWindow().setAttributes(params);
        setContentView(R.layout.activity_reader);
        Intent intent = getIntent();
        bookname =  intent.getStringExtra(TAGS.FILE_PATH);
        Log.d("Bookpath",bookname);
        brightnessSeekBar = (AppCompatSeekBar) findViewById(R.id.brightness);
        night = (Button) findViewById(R.id.nightButton);
        sepia = (Button) findViewById(R.id.sepiaButton);
        minimizeFont = (ImageButton) findViewById(R.id.minimizeFont);
        maximizeFont = (ImageButton) findViewById(R.id.maximizeFont);
        day = (Button) findViewById(R.id.dayButton);
        ebrima = (StyleableTextView) findViewById(R.id.btn_font_ebrima);
        bookmark = (ImageView) findViewById(R.id.bookmark);
        brightnessHigh = (ImageView) findViewById(R.id.brightness_high);
        brightnessLow = (ImageView) findViewById(R.id.brightness_low);
        bookmark.bringToFront();
        jiret = (StyleableTextView) findViewById(R.id.btn_font_jiret);
        wookianos = (StyleableTextView) findViewById(R.id.btn_font_wookianos);
        nyala = (StyleableTextView) findViewById(R.id.btn_font_nyala);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("", R.drawable.font, R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("", R.drawable.share_arrow, R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("", R.drawable.list, R.color.white);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        int brightnessValue = (int) (Float.parseFloat(PreferenceUtill.readFromPreferences(this, TAGS.BRIGHTNESS_SETTING, String.valueOf(0.5))) * 10);
        brightnessSeekBar.setProgress(brightnessValue);
        position = Integer.parseInt(PreferenceUtill.readFromPreferences(this, TAGS.PAGE_POSITION, String.valueOf(0)));
        final View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        EpubReader epubReader = new EpubReader(new File(bookname), ReaderActivity.this);
        if(epubReader.unPackEpub()){
            Log.d("Epub Extracting","Extracted Successfully");
        }
        else{
            Log.d("Epub Extracting","is not Extracted Successfully");
        }
        try {
            TinyDB tinyDB = new TinyDB(ReaderActivity.this);
            ArrayList<String> chapters = new ArrayList<>();
            for(int i=0;i<epubReader.getChapters().size();i++)
                 chapters.add(epubReader.getTableOfContent().getChapterList().get(i).getTitle());
                tinyDB.putListString(TAGS.CHAPTER_LIST,chapters);

        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        webView = (ExtendedWebview) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if(TextUtils.isDigitsOnly(message)){
                    position = Integer.parseInt(message);
                }
                if(message.contains("highlight")){
                    String highlight = message.substring(9,message.length());
                    Toast.makeText(ReaderActivity.this, "The highlighted text is "+highlight, Toast.LENGTH_SHORT).show();
                    PreferenceUtill.saveToPreferences(ReaderActivity.this,TAGS.HIGHLIGHT_PREFERENCE_SAMPLE,highlight);
                }
                else{
                    try{
                    Log.d("Message from JS",message);
                    List<String> rect = Arrays.asList(message.split(","));
                    double left = Double.parseDouble(rect.get(0));
                    double top = Double.parseDouble(rect.get(1));
                    double width = Double.parseDouble(rect.get(2));
                    double height = Double.parseDouble(rect.get(3));
                    showTextSelectionMenu((int) (GeneralUtills.convertDpToPixel((float) left,
                            ReaderActivity.this)),
                            (int) (GeneralUtills.convertDpToPixel((float) top,
                                    ReaderActivity.this)),
                            (int) (GeneralUtills.convertDpToPixel((float) width,
                                    ReaderActivity.this)),
                            (int) (GeneralUtills.convertDpToPixel((float) height,
                                    ReaderActivity.this)));
                }catch (Exception e){e.printStackTrace();}}
                result.confirm();
                return true;
            }
        });
        TextSelectionSupport textSelectionSupport = TextSelectionSupport.support(ReaderActivity.this,webView);
        textSelectionSupport.setSelectionListener(new TextSelectionSupport.SelectionListener() {
            @Override
            public void startSelection() {

            }

            @Override
            public void selectionChanged(String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:alert(getRectForSelectedText())");
                    }
                });

            }

            @Override
            public void endSelection() {

            }
        });

        try {
            webView.loadDataWithBaseURL(epubReader.getBaseURL(), epubReader.getReader(), "text/html", "UTF-8", null);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                params.screenBrightness = i / 10f;
                Log.d("Brightness ", String.valueOf(i) + " , " + i / 10f);
                getWindow().setAttributes(params);
                brightness = i / 10f;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        brightnessHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brightnessSeekBar.setProgress(brightnessSeekBar.getProgress()+1);
            }
        });
        brightnessLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brightnessSeekBar.setProgress(brightnessSeekBar.getProgress()-1);
            }
        });
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceUtill.saveToPreferences(ReaderActivity.this, TAGS.READING_MODE, "day");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("javascript:document.getElementsByTagName('Html')[0].setAttribute('class', 'day');", null);
                } else
                    webView.loadUrl("javascript:document.getElementsByTagName('Html')[0].setAttribute('class', 'day');");
            }
        });
        night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    PreferenceUtill.saveToPreferences(ReaderActivity.this, TAGS.READING_MODE, "black");
                    webView.evaluateJavascript("javascript:document.getElementsByTagName('Html')[0].setAttribute('class', 'black');", null);
                } else
                    webView.loadUrl("javascript:document.getElementsByTagName('Html')[0].setAttribute('class', 'black');");

            }
        });
        sepia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceUtill.saveToPreferences(ReaderActivity.this, TAGS.READING_MODE, "sepia");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("javascript:document.getElementsByTagName('Html')[0].setAttribute('class', 'sepia');", null);
                } else
                    webView.loadUrl("javascript:document.getElementsByTagName('Html')[0].setAttribute('class', 'sepia');");

            }
        });
        maximizeFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fontCounter++;
                Log.d("maximize", "font counter " + fontCounter);
                if (fontCounter <= 5) {
                    maximizeFont.setEnabled(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webView.evaluateJavascript("javascript:maximizeFontSize();", null);
                    } else
                        webView.loadUrl("javascript:maximizeFontSize();");
                } else {
                    maximizeFont.setEnabled(false);
                }
            }
        });
        minimizeFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fontCounter--;
                Log.d("maximize", "font counter " + fontCounter);
                if (fontCounter <= 5 && fontCounter >= 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webView.evaluateJavascript("javascript:minimizeFontSize();", null);
                    } else
                        webView.loadUrl("javascript:document.getElementsByTagName('Html')[0].setAttribute('class', 'sepia');");
                } else {
                    maximizeFont.setEnabled(false);
                }
            }
        });
        ebrima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceUtill.saveToPreferences(ReaderActivity.this, TAGS.FONT_FAMILY, "Ebrima");
                ebrima.setTextColor(getResources().getColor(R.color.mainColor));
                jiret.setTextColor(getResources().getColor(R.color.gray_text));
                wookianos.setTextColor(getResources().getColor(R.color.gray_text));
                nyala.setTextColor(getResources().getColor(R.color.gray_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("javascript:setFont('Ebrima');", null);
                } else
                    webView.loadUrl("javascript:setFont('Ebrima');");

            }
        });
        jiret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceUtill.saveToPreferences(ReaderActivity.this, TAGS.FONT_FAMILY, "Jiret");
                ebrima.setTextColor(getResources().getColor(R.color.gray_text));
                jiret.setTextColor(getResources().getColor(R.color.mainColor));
                wookianos.setTextColor(getResources().getColor(R.color.gray_text));
                nyala.setTextColor(getResources().getColor(R.color.gray_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("javascript:setFont('Jiret');", null);
                } else
                    webView.loadUrl("javascript:setFont('Jiret');");
            }
        });
        wookianos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceUtill.saveToPreferences(ReaderActivity.this, TAGS.FONT_FAMILY, "Wookianos");
                ebrima.setTextColor(getResources().getColor(R.color.gray_text));
                jiret.setTextColor(getResources().getColor(R.color.gray_text));
                wookianos.setTextColor(getResources().getColor(R.color.mainColor));
                nyala.setTextColor(getResources().getColor(R.color.gray_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("javascript:setFont('Wookianos');", null);
                } else
                    webView.loadUrl("javascript:setFont('Wookianos');");
            }
        });
        nyala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceUtill.saveToPreferences(ReaderActivity.this, TAGS.FONT_FAMILY, "nyala");
                ebrima.setTextColor(getResources().getColor(R.color.gray_text));
                jiret.setTextColor(getResources().getColor(R.color.gray_text));
                wookianos.setTextColor(getResources().getColor(R.color.gray_text));
                nyala.setTextColor(getResources().getColor(R.color.mainColor));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("javascript:setFont('Nyala');", null);
                } else
                    webView.loadUrl("javascript:setFont('Nyala');");
            }
        });
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                bottomNavigation.hideBottomNavigation(true);
                if (position == 0)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if (position == 1)
                    ShareUtill.share("I am using Lomi and it is awesome go and download it", ReaderActivity.this);
                if (position == 2){
                    PreferenceUtill.saveToPreferences(ReaderActivity.this, TAGS.BRIGHTNESS_SETTING, String.valueOf(brightness));
                    Intent intent1 = new Intent(ReaderActivity.this, ContentHighlight.class);
                    intent1.putExtra(TAGS.BOOKNAME,bookname);
                    startActivityForResult(intent1,5);}
                return false;
            }
        });
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!wasClicked){
                    bookmark.setBackgroundResource(R.drawable.bookmark_active);
                    wasClicked = true;
                    Toast.makeText(ReaderActivity.this, "This Page is Bookmarked", Toast.LENGTH_SHORT).show();
                }
                else{
                    bookmark.setBackgroundResource(R.drawable.bookmark_inactive);
                    wasClicked = false;
                    Toast.makeText(ReaderActivity.this, "This Page is Unbookmarked", Toast.LENGTH_SHORT).show();
                }
            }
        });
        webView.setOnTouchListener(new View.OnTouchListener() {
            private float mDownPosX;
            private float mDownPosY;
            private float mUpPosX;
            private float mUpPosY;
            private float MOVE_THRESHOLD_DP;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MOVE_THRESHOLD_DP = 20.0F * getResources().getDisplayMetrics().density;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                            this.mDownPosX = event.getX();
                            this.mDownPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                            this.mUpPosX = event.getX();
                            this.mUpPosY = event.getY();
                        if ((Math.abs(mUpPosX - this.mDownPosX) < MOVE_THRESHOLD_DP) && (Math.abs(mUpPosY - this.mDownPosY) < MOVE_THRESHOLD_DP)) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            if(bottomNavigation.isHidden())
                                bottomNavigation.restoreBottomNavigation(true);
                            else
                                bottomNavigation.hideBottomNavigation(true);
                        }

                        break;
                }
                return false;
            }
        });
    }
    public class WebClient extends WebViewClient {
        ProgressDialog progressDialog;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressDialog= ProgressDialog.show(ReaderActivity.this,"በማዘጋጀት ላይ","እባክዎ ጥቂት ይጠብቁ");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String mode = PreferenceUtill.readFromPreferences(ReaderActivity.this,TAGS.READING_MODE,"day");
            String fontName = PreferenceUtill.readFromPreferences(ReaderActivity.this,TAGS.FONT_FAMILY,"Ebrima");
            String high = PreferenceUtill.readFromPreferences(ReaderActivity.this,TAGS.HIGHLIGHT_PREFERENCE_SAMPLE,"");
            Log.d("Highlight Preference",high);
            if(fontName.equals("Ebrima"))
                ebrima.setTextColor(getResources().getColor(R.color.mainColor));
            if(fontName.equals("jiret"))
                jiret.setTextColor(getResources().getColor(R.color.mainColor));
            if(fontName.equals("wookianos"))
                wookianos.setTextColor(getResources().getColor(R.color.mainColor));
            if(fontName.equals("nyala"))
                nyala.setTextColor(getResources().getColor(R.color.mainColor));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.evaluateJavascript("javascript:console.log(this.index);",null);
                view.evaluateJavascript("javascript:setFont('" + fontName + "')",null);
                view.evaluateJavascript("javascript:highlight("+high+")",null);
                view.evaluateJavascript("javascript:document.getElementsByTagName('Html')[0].setAttribute('class', '"+mode+"');",null);
                view.evaluateJavascript("javascript:pager.navigate(" + position + ")", null);
            } else
                view.loadUrl("javascript:pager.navigate(" + position + ")");
            progressDialog.dismiss();
        }

    }


    @Override
    public void onBackPressed() {
        PreferenceUtill.saveToPreferences(this, TAGS.PAGE_POSITION, String.valueOf(position));
        if (brightness != 0)
            PreferenceUtill.saveToPreferences(this, TAGS.BRIGHTNESS_SETTING, String.valueOf(brightness));
        super.onBackPressed();
    }

   /* @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
           webView.evaluateJavascript("javascript:pager.init();",null);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
           webView.evaluateJavascript("javascript:pager.init();",null);
        }
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
        if(requestCode==5){
            webView.evaluateJavascript("javascript:pager.navigateChapter(" + data.getIntExtra("MESSAGE",0) + ")", null);
        }}catch(Exception e){}
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(ReaderActivity.this, ContentHighlight.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    public void showTextSelectionMenu(int x, int y, final int width, final int height) {
        final ViewGroup root =
                (ViewGroup) this.getWindow()
                        .getDecorView().findViewById(android.R.id.content);
        final View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        view.setBackgroundColor(Color.TRANSPARENT);

        root.addView(view);

        view.setX(x);
        view.setY(y);
        final QuickAction quickAction =
                new QuickAction(this, QuickAction.HORIZONTAL);
        quickAction.addActionItem(new ActionItem(1,
                getString(R.string.highlight)));
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                quickAction.dismiss();
                root.removeView(view);
                onTextSelectionActionItemClicked(actionId, view, width, height);
            }
        });
        quickAction.show(view, width, height);
    }
    private void onTextSelectionActionItemClicked(int actionId, View view, int width, int height) {
        if (actionId == 1) {
            webView.loadUrl("javascript:alert(getHighlightString())");
        }
    }
    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        return this.dummyActionMode();
    }
    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return this.dummyActionMode();
    }

    public ActionMode dummyActionMode() {
        return new ActionMode() {
            @Override
            public void setTitle(CharSequence title) {
            }
            @Override
            public void setTitle(int resId) {
            }

            @Override
            public void setSubtitle(CharSequence subtitle) {
            }

            @Override
            public void setSubtitle(int resId) {
            }

            @Override
            public void setCustomView(View view) {
            }

            @Override
            public void invalidate() {
            }

            @Override
            public void finish() {
            }

            @Override
            public Menu getMenu() {
                return null;
            }

            @Override
            public CharSequence getTitle() {
                return null;
            }

            @Override
            public CharSequence getSubtitle() {
                return null;
            }

            @Override
            public View getCustomView() {
                return null;
            }

            @Override
            public MenuInflater getMenuInflater() {
                return null;
            }
        };
    }


}
