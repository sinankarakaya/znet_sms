package com.sinan.sms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.sinan.sms.fragment.MainFragment;
import com.sinan.sms.service.ReceiveSmsControl;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;
    public static String deviceID ="";
    public static final String host = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        this.startService(new Intent(this, ReceiveSmsControl.class));
        changeFragment(new MainFragment());
    }

    @Override
    protected void onResume() {
        Log.d("activity", "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void changeFragment(Fragment targetFragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss();
    }
}