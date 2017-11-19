package ru.alexbykov.permissionssample.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.alexbykov.permissionssample.activities.MainActivity;
import ru.alexbykov.permissionssample.R;
import ru.alexbykov.permissionssample.activities.LocationSampleActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseFragment extends Fragment {


    private static final int LAYOUT = R.layout.fragment_choose;

    private View view;

    public static Fragment newInstance() {
        return new ChooseFragment();
    }


    public ChooseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(LAYOUT, container, false);
        setupUX();
        return view;
    }

    private void setupUX() {
        view.findViewById(R.id.btn_activity_sample).setOnClickListener(v -> onClickActivitySample());
        view.findViewById(R.id.btn_fragment_sample).setOnClickListener(v -> onClickFragmentSample());
    }

    private void onClickFragmentSample() {
        ((MainActivity)getActivity()).startFragment(LocationSampleFragment.newInstance(),true);
    }

    private void onClickActivitySample() {
        startActivity(new Intent(getContext(), LocationSampleActivity.class));
    }

}
