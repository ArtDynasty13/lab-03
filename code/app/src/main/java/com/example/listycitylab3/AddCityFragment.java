package com.example.listycitylab3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddCityFragment extends DialogFragment {
    interface AddCityDialogListener {
        void addCity(City city);
    }
    private AddCityDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AddCityDialogListener) {
           listener = (AddCityDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddCityDialogListener");
        }
    }
    // more or less followed the lab hint here
    static AddCityFragment newInstance(City city) {
        Bundle args = new Bundle();
        args.putSerializable("city", city);
        AddCityFragment fragment = new AddCityFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        City city = null;
        if (getArguments() != null) { // if there is something in the bundle, let's deserialize
            city = (City) getArguments().getSerializable("city");
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_city, null);
        EditText editCityName = view.findViewById(R.id.edit_text_city_text);
        EditText editProvinceName = view.findViewById(R.id.edit_text_province_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // looks like we got a city. prefill the popup for the user so they know they are editing
        if (city != null) {
            editCityName.setText(city.getName());
            editProvinceName.setText(city.getProvince());
        }

        // lambda needs an effective final (probably to avoid side effects in fp style?). here I just make an explicit final.
        City finalCity = city;
        return builder
                .setView(view)
                .setTitle("Add/Edit a city")
                .setNegativeButton("Cancel", null)
                // changed this to OK instead of Add
                .setPositiveButton("OK", (dialog, which) -> {
                    String cityName = editCityName.getText().toString();
                    String provinceName = editProvinceName.getText().toString();
                    // if its null, we are making a new city
                    if (finalCity == null) {
                        listener.addCity(new City(cityName, provinceName));
                    } else { // let's edit the deserialized city
                        finalCity.setName(cityName);
                        finalCity.setProvince(provinceName);
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).refreshList();
                        }
                    }
                })
                .create();
    }
}
