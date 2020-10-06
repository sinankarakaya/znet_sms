package com.sinan.sms.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sinan.sms.MainActivity;
import com.sinan.sms.R;

import java.util.List;

public class MainFragment extends Fragment {

    private View parentView;
    private TextView deviceKey;
    private Button serviceButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_main, container, false);
        setUpViews();
        return parentView;
    }

    private void setUpViews() {
        final MainActivity parentActivity = (MainActivity) getActivity();
        checkPermission(parentActivity);

        String device_unique_id = this.getDeviceUniqueID(parentActivity);
        deviceKey = (TextView)parentView.findViewById(R.id.deviceId);
        deviceKey.setText(device_unique_id);
        MainActivity.deviceID = device_unique_id;

        LinearLayout deviceIDBox = (LinearLayout) parentView.findViewById(R.id.deviceIDBox);
        deviceIDBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyClipboard(parentActivity);
            }
        });

    }

    public String getDeviceUniqueID(Activity activity){
        String device_unique_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }

    public void checkPermission(final Activity activity){
        Dexter.withContext(activity)
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(!report.areAllPermissionsGranted()){
                    showPermissionAlert(activity);
                }
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

    }

    public void showPermissionAlert(final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Uyarı");
        builder.setMessage("Uygulamanın çalışabilmesi için izinlerin verilmesi gerekmektedir.");
        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkPermission(activity);
            }
        });
        builder.show();
    }

    public void copyClipboard(final Activity activity){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(getDeviceUniqueID(activity));
            Toast.makeText(activity,"Cihaz ID'si panoya kopyalandı.",Toast.LENGTH_LONG).show();
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", getDeviceUniqueID(activity));
            clipboard.setPrimaryClip(clip);
            Toast.makeText(activity,"Cihaz ID'si panoya kopyalandı.",Toast.LENGTH_LONG).show();
        }
    }
}
