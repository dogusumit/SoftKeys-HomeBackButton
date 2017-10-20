package com.dogusumit.ortatus;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            final Button btn1 = (Button) findViewById(R.id.buton1);
            final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
            final SeekBar seekbar1 = (SeekBar) findViewById(R.id.seekbar1);
            final LinearLayout linlayout1 = (LinearLayout) findViewById(R.id.linlayout1);
            final CheckBox checkbox1 = (CheckBox) findViewById(R.id.checkbox1);
            final CheckBox checkbox2 = (CheckBox) findViewById(R.id.checkbox2);
            final CheckBox checkbox3 = (CheckBox) findViewById(R.id.checkbox3);
            final CheckBox checkbox4 = (CheckBox) findViewById(R.id.checkbox4);
            final SeekBar seekbar2 = (SeekBar) findViewById(R.id.seekbar2);

            int max;
            if (Resources.getSystem().getDisplayMetrics().widthPixels <
                    Resources.getSystem().getDisplayMetrics().heightPixels)
                max = Resources.getSystem().getDisplayMetrics().widthPixels / 4;
            else
                max = Resources.getSystem().getDisplayMetrics().heightPixels / 4;
            seekbar1.setMax(max);

            SharedPreferences settings = getApplicationContext().getSharedPreferences("com.dogusumit.ortatus", 0);
            final SharedPreferences.Editor editor = settings.edit();
            boolean uzunBas = settings.getBoolean("UzunBas", false);
            boolean uzunBas2 = settings.getBoolean("UzunBas2", false);
            boolean geriTusu = settings.getBoolean("GeriTusu", false);
            boolean yeniIkon = settings.getBoolean("YeniIkon", false);
            int konum = settings.getInt("Konumu", 0);
            int boyut = settings.getInt("Boyut", 10);
            int seffaflik = settings.getInt("Seffaflik", 0);
            checkbox3.setChecked(uzunBas);
            checkbox4.setChecked(uzunBas2);
            spinner1.setSelection(konum);


            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                }
            });

            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    editor.putInt("Konumu", position).apply();
                    if (isAccessibilityEnabled() && izinleriKontrolEt())
                        servisGuncelle();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor.putBoolean("YeniIkon", isChecked).apply();
                    if (isAccessibilityEnabled() && izinleriKontrolEt())
                        servisGuncelle();
                    if (isChecked) {
                        ((ImageButton) findViewById(R.id.resim_back)).setImageResource(R.mipmap.ic_ucgen);
                        ((ImageButton) findViewById(R.id.resim_home)).setImageResource(R.mipmap.ic_yuvarlak);
                        ((ImageButton) findViewById(R.id.resim_recent)).setImageResource(R.mipmap.ic_kare);
                    } else {
                        ((ImageButton) findViewById(R.id.resim_back)).setImageResource(R.mipmap.ic_back);
                        ((ImageButton) findViewById(R.id.resim_home)).setImageResource(R.mipmap.ic_home);
                        ((ImageButton) findViewById(R.id.resim_recent)).setImageResource(R.mipmap.ic_recent);
                    }
                }
            });
            if (yeniIkon)
                checkbox1.setChecked(true);

            checkbox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        checkbox4.setChecked(false);
                    editor.putBoolean("UzunBas", isChecked).apply();
                    if (isAccessibilityEnabled() && izinleriKontrolEt())
                        servisGuncelle();
                }
            });

            checkbox4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                    checkbox3.setChecked(false);
                    editor.putBoolean("UzunBas2", isChecked).apply();
                    if (isAccessibilityEnabled() && izinleriKontrolEt())
                        servisGuncelle();
                }
            });

            checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor.putBoolean("GeriTusu", isChecked).apply();
                    if (isAccessibilityEnabled() && izinleriKontrolEt())
                        servisGuncelle();

                    ArrayList<View> views = new ArrayList<>();
                    for (int i = 0; i < linlayout1.getChildCount(); i++) {
                        views.add(linlayout1.getChildAt(i));
                    }
                    linlayout1.removeAllViews();
                    for (int i = views.size()-1; i >= 0; i--) {
                        linlayout1.addView(views.get(i));
                    }
                }
            });
            if (geriTusu)
                checkbox2.setChecked(true);

            seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress < 10) {
                        progress = 10;
                        seekbar1.setProgress(progress);
                    }
                    linlayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, progress));
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    editor.putInt("Boyut", seekBar.getProgress()).apply();
                    if (isAccessibilityEnabled() && izinleriKontrolEt())
                        servisGuncelle();}
            });
            seekbar1.setProgress(boyut);

            seekbar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    linlayout1.getBackground().setAlpha(255-progress);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    editor.putInt("Seffaflik", seekBar.getProgress()).apply();
                    if (isAccessibilityEnabled() && izinleriKontrolEt())
                        servisGuncelle();}
            });
            seekbar2.setProgress(seffaflik);


            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        } catch (Exception e) {
            toastla(e.getMessage());
        }
    }


    private void uygulamayiOyla() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
            } catch (Exception ane) {
                toastla(e.getMessage());
            }
        }
    }

    private void marketiAc() {
        try {
            Uri uri = Uri.parse("market://developer?id=" + getString(R.string.play_store_id));
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/developer?id=" + getString(R.string.play_store_id))));
            } catch (Exception ane) {
                toastla(e.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.oyla:
                uygulamayiOyla();
                return true;
            case R.id.market:
                marketiAc();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void toastla(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    boolean isAccessibilityEnabled(){
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE_NAME = getPackageName() + "/" + Servis.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            toastla(e.getMessage());
        }

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1){

            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE_NAME)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void servisGuncelle() {
        try {
            Intent intent = new Intent(getApplicationContext(), Servis.class);
            intent.setAction("guncelle");
            startService(intent);
            stopService(intent);
        } catch (Exception e) {
            toastla(e.getLocalizedMessage());
        }
    }

    boolean izinleriKontrolEt() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    return false;
                }
                return true;
            }
            else
                return true;
        } catch (Exception e) {
            toastla(e.getLocalizedMessage());
            return false;
        }
    }
}