package com.example.kirill.lab1;


import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;
import com.github.danielnilsson9.colorpickerview.preference.ColorPreference;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements ColorPickerDialogFragment.ColorPickerDialogListener {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final int PREFERENCE_DIALOG_ID = 1;
    private static final int DIALOG_ID = 0;

    private GeneralPreferenceFragment preferenceFragment;

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

//            if (preference instanceof ListPreference) {
//                // For list preferences, look up the correct display value in
//                // the preference's 'entries' list.
//                ListPreference listPreference = (ListPreference) preference;
//                int index = listPreference.findIndexOfValue(stringValue);
//
//                // Set the summary to reflect the new value.
//                preference.setSummary(
//                        index >= 0
//                                ? listPreference.getEntries()[index]
//                                : null);
//
//            }  else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
//            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();

        preferenceFragment = new GeneralPreferenceFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, preferenceFragment).commit();


    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment implements ColorPickerDialogFragment.ColorPickerDialogListener , DatePickerDialog.OnDateSetListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            Preference btnDateFilter = (Preference) findPreference("btnDateFilter");
            btnDateFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDateDialog();
                    return true;
                }
            });



            ColorPreference pref = (ColorPreference) findPreference("color");
            pref.setOnShowDialogListener(new ColorPreference.OnShowDialogListener() {

                @Override
                public void onShowColorPickerDialog(String title, int currentColor) {

                    // Preference was clicked, we need to show the dialog.
                    ColorPickerDialogFragment dialog = ColorPickerDialogFragment
                            .newInstance(PREFERENCE_DIALOG_ID, "Color Picker", null, currentColor, false);


                    // PLEASE READ!
                    // Show the dialog, the result from the dialog
                    // will end up in the parent activity since
                    // there really isn't any good way for fragments
                    // to communicate with each other. The recommended
                    // ways is for them to communicate through their
                    // host activity, thats what we will do.
                    // In our case, we must then make sure that MainActivity
                    // implements ColorPickerDialogListener because that
                    // is expected by ColorPickerDialogFragment.
                    //
                    // We also make this fragment implement ColorPickerDialogListener
                    // and when we receive the result in the activity's
                    // ColorPickerDialogListener when just forward them
                    // to this fragment instead.
                    dialog.show(getFragmentManager(), "pre_dialog");
                }
            });

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("btnDateFilter"));
        }

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            Log.i("dasd", "year " + i + " month " + i2 + " day " + i3);
            SharedPreferences.Editor editor = findPreference("btnDateFilter").getEditor();

            editor.putString("btnDateFilter", i3+"."+(i2+1)+"."+i);
            editor.apply();
            bindPreferenceSummaryToValue(findPreference("btnDateFilter"));
        }

        private void showDateDialog() {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(getContext(), this, year, month, day).show();
//            SharedPreferences.Editor editor = findPreference("btnDateFilter").getEditor();
//
//            editor.putString("btnDateFilter", day+"."+month+"."+year);
//            editor.apply();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }


        @Override
        public void onColorSelected(int dialogId, int color) {
            switch (dialogId) {
                case PREFERENCE_DIALOG_ID:
                    // We have our result from the dialog, save it!
                    ColorPreference pref = (ColorPreference) findPreference("color");
                    pref.saveValue(color);
                    break;
            }
        }

        @Override
        public void onDialogDismissed(int dialogId) {
            // Nothing to do.
        }
    }




    @Override
    public void onDialogDismissed(int dialogId) {

        switch(dialogId) {
            case PREFERENCE_DIALOG_ID:
                // We got result back from preference picker dialog in
                // ExamplePreferenceFragment. We forward it to the
                // fragment handling that particular preference.

                ((ColorPickerDialogFragment.ColorPickerDialogListener)preferenceFragment)
                        .onDialogDismissed(dialogId);

                break;
        }
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch(dialogId) {
            case PREFERENCE_DIALOG_ID:
                // We got result back from preference picker dialog in
                // ExamplePreferenceFragment. We forward it to the
                // fragment handling that particular preference.

                ((ColorPickerDialogFragment.ColorPickerDialogListener)preferenceFragment)
                        .onColorSelected(dialogId, color);

                break;
            case DIALOG_ID:
                // We got result from the other dialog, the one that is
                // shown when clicking on the icon in the action bar.

                Toast.makeText(SettingsActivity.this, "Selected Color: " + colorToHexString(color), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static String colorToHexString(int color) {
        return String.format("#%06X", 0xFFFFFFFF & color);
    }
}
