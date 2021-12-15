package net.pakcusoft.solat;

import static net.pakcusoft.solat.MainActivity.DEFAULT_PRAYER_TIME;
import static net.pakcusoft.solat.MainActivity.DEFAULT_STATE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_STATE_SELECTED;
import static net.pakcusoft.solat.MainActivity.DEFAULT_ZONE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_ZONE_SELECTED;
import static net.pakcusoft.solat.MainActivity.GLOBAL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import net.pakcusoft.solat.data.Zone;
import net.pakcusoft.solat.databinding.ActivitySettingBinding;

import java.util.List;

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
        setupNegeri();
        setupZon();
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