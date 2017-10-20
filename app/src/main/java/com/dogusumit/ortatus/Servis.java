package com.dogusumit.ortatus;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;


public class Servis extends AccessibilityService {

    WindowManager windowManager;
    LinearLayout linearLayout;
    ImageButton back, home, recent;
    WindowManager.LayoutParams params;
    boolean isEnabled = false;
    boolean isAdded = false;
    SharedPreferences settings;
    private final static int ATOMIC_ID = new AtomicInteger(0).incrementAndGet();

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        try {
            String s = intent.getAction();
            if (s!=null && s.equals("guncelle") && isEnabled)
                konumAyarla();
        } catch (Exception e){
            Log.d(e.getLocalizedMessage(),e.getLocalizedMessage());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onServiceConnected() {
        try {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            linearLayout = (LinearLayout) inflater.inflate(R.layout.servis_layout, null);
            isEnabled = true;
            settings = getApplicationContext().getSharedPreferences("com.dogusumit.ortatus", 0);

            back = (ImageButton) linearLayout.findViewById(R.id.back);
            home = (ImageButton) linearLayout.findViewById(R.id.home);
            recent = (ImageButton) linearLayout.findViewById(R.id.recent);


            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    } catch (Exception e) {
                        toastla(e.getMessage());
                    }
                }
            });

            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                    } catch (Exception e) {
                        toastla(e.getMessage());
                    }
                }
            });

            recent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                    } catch (Exception e) {
                        toastla(e.getMessage());
                    }
                }


            });


            konumAyarla();

        } catch (Exception e) {
            toastla(e.getMessage());
        }
        super.onServiceConnected();
    }

    void toastla(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    void konumAyarla() {
        try {
            if (isAdded)
                windowManager.removeViewImmediate(linearLayout);

            int konum = settings.getInt("Konumu", 0);
            int boyut = settings.getInt("Boyut", 10);
            int seffaflik = settings.getInt("Seffaflik",0);
            boolean uzunBas = settings.getBoolean("UzunBas", false);
            boolean uzunBas2 = settings.getBoolean("UzunBas2", false);
            boolean geriTusu = settings.getBoolean("GeriTusu", false);
            boolean yeniIkon = settings.getBoolean("YeniIkon", false);

            if (yeniIkon) {
                back.setImageResource(R.mipmap.ic_ucgen);
                home.setImageResource(R.mipmap.ic_yuvarlak);
                recent.setImageResource(R.mipmap.ic_kare);
            } else {
                back.setImageResource(R.mipmap.ic_back);
                home.setImageResource(R.mipmap.ic_home);
                recent.setImageResource(R.mipmap.ic_recent);
            }

            ImageButton sol,sag;
            if (geriTusu) {
                sol = back;
                sag = recent;
            } else {
                sol = recent;
                sag = back;
            }

            if (uzunBas) {
                sol.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("Konumu", 2).apply();
                        konumAyarla();
                        return true;
                    }
                });
                home.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        SharedPreferences.Editor editor = settings.edit();
                        if (settings.getInt("Konumu", 0) == 0)
                            editor.putInt("Konumu", 3).apply();
                        else
                            editor.putInt("Konumu", 0).apply();
                        konumAyarla();
                        return true;
                    }
                });
                sag.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("Konumu", 1).apply();
                        konumAyarla();
                        return true;
                    }
                });
            } else if(uzunBas2) {
                sol.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        gizle();
                        return true;
                    }
                });
                home.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        gizle();
                        return true;
                    }
                });
                sag.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        gizle();
                        return true;
                    }
                });
            } else {
                sol.setOnLongClickListener(null);
                home.setOnLongClickListener(null);
                sag.setOnLongClickListener(null);
            }

            linearLayout.removeAllViews();
            linearLayout.addView(sol);
            linearLayout.addView(home);
            linearLayout.addView(sag);

            switch (konum) {
                case 0:
                    params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT, boyut,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.BOTTOM;
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    break;
                case 1:
                    params = new WindowManager.LayoutParams(
                            boyut, WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.END;
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    break;
                case 2:
                    params = new WindowManager.LayoutParams(
                            boyut, WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.START;
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    break;
                case 3:
                    params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.MATCH_PARENT, boyut,
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = Gravity.TOP;
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    break;
            }

            linearLayout.getBackground().setAlpha(255-seffaflik);
            windowManager.addView(linearLayout, params);
            isAdded = true;
        } catch (Exception e) { toastla(e.getMessage()); }
    }

    void gizle() {
        try {
            if (isAdded)
                windowManager.removeViewImmediate(linearLayout);
            isAdded = false;
            Intent notificationIntent = new Intent(this, Servis.class);
            notificationIntent.setAction("guncelle");
            PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.str14))
                    .setContentIntent(pendingIntent).build();
            notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(ATOMIC_ID, notification);
        } catch (Exception e) {
            toastla(e.getLocalizedMessage());
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {  }
    @Override
    public void onInterrupt() {
        try {
            if (isAdded) {
                windowManager.removeViewImmediate(linearLayout);
                isAdded = false;
                isEnabled = false;
            }
        } catch (Exception e) {
            toastla(e.getLocalizedMessage());
        }
    }
    @Override
    public void onDestroy() {
        try {
            if (isAdded) {
                windowManager.removeViewImmediate(linearLayout);
                isAdded = false;
                isEnabled = false;
            }
        } catch (Exception e) {
            toastla(e.getLocalizedMessage());
        }
        super.onDestroy();
    }
}