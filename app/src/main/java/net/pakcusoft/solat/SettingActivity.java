package net.pakcusoft.solat;

import static net.pakcusoft.solat.MainActivity.DEFAULT_PRAYER_TIME;
import static net.pakcusoft.solat.MainActivity.DEFAULT_STATE;
import static net.pakcusoft.solat.MainActivity.DEFAULT_ZONE;
import static net.pakcusoft.solat.MainActivity.GLOBAL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.pakcusoft.solat.data.DataZone;
import net.pakcusoft.solat.databinding.ActivitySettingBinding;
import net.pakcusoft.solat.service.MainService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {

    public static final String ZONE_LIST = "def_zone_list";
    public static final String SETTING_REMINDER_AZAN = "rem_azan";
    public static final String SETTING_REMINDER_EARLY = "rem_early";

    private String state;
    private String zone;

    private ArrayAdapter<CharSequence> negeriAdapter;
    private ArrayAdapter<String> zonAdapter;
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
        state = sharedPref.getString(DEFAULT_STATE, "selangor");
        zone = sharedPref.getString(DEFAULT_ZONE, "petaling");
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
        String zoneList = sharedPref.getString(ZONE_LIST, null);
        if (zoneList == null) {
            getZoneList();
        } else {
            setupZon(zoneList);
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

    private void getZoneList() {
        Call<DataZone> dataZoneCall = MainService.getService().getZones(state);
        dataZoneCall.enqueue(new Callback<DataZone>() {
            @Override
            public void onResponse(Call<DataZone> call, Response<DataZone> response) {
                if (response.isSuccessful()) {
                    DataZone dataZone = response.body();
                    String _zoneList = TextUtils.join(",", dataZone.data.negeri.zon).toUpperCase();
                    SharedPreferences sharedPref = getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(ZONE_LIST, _zoneList);
                    editor.apply();
                    setupZon(_zoneList);
                }
            }

            @Override
            public void onFailure(Call<DataZone> call, Throwable t) {
                Toast.makeText(SettingActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupNegeri() {
        negeriAdapter = ArrayAdapter.createFromResource(this, R.array.negeri, R.layout.spinner_item);
        negeriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNegeri.setAdapter(negeriAdapter);
        String[] states = getResources().getStringArray(R.array.negeri);
        for (int i = 0; i < states.length; i++) {
            if (state.equalsIgnoreCase(states[i])) {
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
                    getZoneList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupZon(String zoneList) {
        String[] zones = zoneList.split(",");
        zonAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, zones);
        zonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZon.setAdapter(zonAdapter);
        for (int i = 0; i < zones.length; i++) {
            if (zone.equalsIgnoreCase(zones[i])) {
                spinnerZon.setSelection(i);
                break;
            }
        }
        spinnerZon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPref = getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                String _zone = zonAdapter.getItem(position);
                if (!_zone.equalsIgnoreCase(zone)) {
                    zone = _zone;
                    editor.putString(DEFAULT_ZONE, _zone);
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