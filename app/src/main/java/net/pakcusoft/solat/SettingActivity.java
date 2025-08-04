package net.pakcusoft.solat;

import static net.pakcusoft.solat.AlarmReceiver.CHANNEL_REMINDER_ID;
import static net.pakcusoft.solat.MainActivity.DEFAULT_PRAYER_TIME;
import static net.pakcusoft.solat.MainActivity.DEFAULT_STATE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_STATE_SELECTED;
import static net.pakcusoft.solat.MainActivity.DEFAULT_ZONE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_ZONE_SELECTED;
import static net.pakcusoft.solat.MainActivity.GLOBAL;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import net.pakcusoft.solat.data.Zone;
import net.pakcusoft.solat.databinding.ActivitySettingBinding;

import java.util.List;
import java.util.Random;

public class SettingActivity extends AppCompatActivity {

    public static final String SETTING_REMINDER_AZAN = "rem_azan";
    public static final String SETTING_REMINDER_EARLY = "rem_early";

    private String state;
    private String zone;

    private ArrayAdapter<String> negeriAdapter;
    private ArrayAdapter<Zone> zonAdapter;
    private Spinner spinnerNegeri;
    private Spinner spinnerZon;
    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tetapan");
        }

        spinnerNegeri = binding.spinnerNegeri;
        spinnerZon = binding.spinnerZon;
        SharedPreferences sharedPref = getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
        state = sharedPref.getString(DEFAULT_STATE, DEFAULT_STATE_SELECTED);
        zone = sharedPref.getString(DEFAULT_ZONE, DEFAULT_ZONE_SELECTED);
        boolean remAzan = sharedPref.getBoolean(SETTING_REMINDER_AZAN, true);
        boolean remEarly = sharedPref.getBoolean(SETTING_REMINDER_EARLY, true);
        binding.switchAzan.setChecked(remAzan);
        binding.switchAzan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(SETTING_REMINDER_AZAN, isChecked);
                editor.apply();
            }
        });
        binding.switchReminder.setChecked(remEarly);
        binding.switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(SETTING_REMINDER_EARLY, isChecked);
                editor.apply();
            }
        });
        binding.btnNotifyTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testReminder();
                view.setEnabled(false);
                view.postDelayed(() -> view.setEnabled(true), 60000);
            }
        });
        setupNegeri();
        setupZon();
    }

    private void testReminder() {
        Context ctx = this;
        Intent newIntent = new Intent(ctx, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, newIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_REMINDER_ID);
        builder = builder.setSmallIcon(R.drawable.ic_stat_jom_solat_notification)
                .setContentTitle("Ayuh Solat")
                .setContentText("Solat itu mencegah kemungkaran")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(new Random().nextInt(Integer.MAX_VALUE), builder.build());
            Log.d("XXX", "Sending test notification");
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage("Please enable notification").setTitle("Sila aktifkan pemberitahuan");
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            Log.d("XXX", "No permission granted");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNegeri() {
        negeriAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, Constant.negeris);
        negeriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNegeri.setAdapter(negeriAdapter);
        for (int i = 0; i < Constant.negeris.size(); i++) {
            if (state.equalsIgnoreCase(Constant.negeris.get(i))) {
                spinnerNegeri.setSelection(i);
                break;
            }
        }
        spinnerNegeri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPref = getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                String _state = (String) negeriAdapter.getItem(position);
                if (!_state.equalsIgnoreCase(state)) {
                    state = _state;
                    editor.putString(DEFAULT_STATE, _state);
                    editor.putString(DEFAULT_PRAYER_TIME, null);
                    editor.apply();
                    setupZon();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupZon() {
        List<Zone> zones = Constant.zones.get(state);
        zonAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, zones);
        zonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZon.setAdapter(zonAdapter);
        for (int i = 0; i < zones.size(); i++) {
            if (zone.equalsIgnoreCase(zones.get(i).getName())) {
                spinnerZon.setSelection(i);
                break;
            }
        }
        spinnerZon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPref = getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Zone _zone = zonAdapter.getItem(position);
                if (!_zone.getName().equalsIgnoreCase(zone)) {
                    zone = _zone.getName();
                    editor.putString(DEFAULT_ZONE, _zone.getName());
                    editor.putString(DEFAULT_PRAYER_TIME, null);
                    editor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}