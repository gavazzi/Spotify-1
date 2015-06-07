package nz.co.zzi.spotify;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by joao.gavazzi on 7/06/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        final FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(containerId, fragment, fragment.getClass().getName());
        if(addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }

        fragmentTransaction.commit();
    }

}
