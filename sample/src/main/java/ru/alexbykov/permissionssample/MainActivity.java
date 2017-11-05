package ru.alexbykov.permissionssample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.alexbykov.permissionssample.fragments.ChooseFragment;

public class MainActivity extends AppCompatActivity {


    private static final int LAYOUT = R.layout.activity_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        startFragment(ChooseFragment.newInstance(), false);
    }


    public void startFragment(Fragment fragment, boolean addToBackStack) {

        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.lt_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getTag());
        }
        transaction.commit();
    }

}
