package bebo.comeandtaste;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import java.util.InputMismatchException;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, android.support.v7.preference.Preference.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_recipes);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            android.support.v7.preference.Preference p = getPreferenceScreen().getPreference(i);
            String value = sharedPreferences.getString(p.getKey(), "");
            setpreferencesummary(p, value);

        }
        android.support.v7.preference.Preference preference = findPreference(getString(R.string.edit_key));
        preference.setOnPreferenceChangeListener(this);

        android.support.v7.preference.Preference preference1 = findPreference(getString(R.string.ed_page_key));
        preference1.setOnPreferenceChangeListener(this);

    }

    public void setpreferencesummary(android.support.v7.preference.Preference preference, String value) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefindex = listPreference.findIndexOfValue(value);
            if (prefindex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefindex]);
            }
        } else if (preference instanceof EditTextPreference) {
            if (value.equals("")) {
                preference.setSummary(getString(R.string.summary_empty));
            } else {
                preference.setSummary(value);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        android.support.v7.preference.Preference preference = findPreference(s);
        if (null != preference) {
            String value = sharedPreferences.getString(preference.getKey(), "");
            setpreferencesummary(preference, value);
        }
    }


    @Override
    public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object o) {
        if (preference.getKey().equals(getString(R.string.edit_key))) {
            try {
                String edvalue = o.toString();
                String a[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ";", ",", "!", ")", "(", "@", "%", "$", "*", "+", "-", "_", "#", "."};
                for (int i = 0; i < a.length; i++) {
                    if (edvalue.contains(a[i])) {

                        String message = getString(R.string.invalid_ingredient);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }

            } catch (InputMismatchException i) {
                String message = getString(R.string.invalid_ingredient);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (preference.getKey().equals(getString(R.string.ed_page_key))) {
            try {
                int pageValue = Integer.parseInt(o.toString());
                if (pageValue < 1 || pageValue > 3500) {
                    Toast.makeText(getActivity(), getString(R.string.number_input), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (NumberFormatException n) {
                Toast.makeText(getActivity(), getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        return true;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
