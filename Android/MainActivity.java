package uz.programmer.rahmat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import uz.programmer.rahmat.structures.PreferencePackageHolder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login();

        finish();
    }

    private void login() {
        final SharedPreferences preferences = getSharedPreferences(PreferencePackageHolder.getUserPreference(), Context.MODE_PRIVATE);

        if(!preferences.contains("logged_in") || !preferences.getBoolean("logged_in", false)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(MainActivity.this, AppActivity.class);
            startActivity(intent);
        }

    }
}