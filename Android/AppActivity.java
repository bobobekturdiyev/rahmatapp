package uz.programmer.rahmat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import uz.programmer.rahmat.fragments.CreatePostFragment;
import uz.programmer.rahmat.fragments.GenoFragment;
import uz.programmer.rahmat.fragments.HomeFragment;
import uz.programmer.rahmat.fragments.ProfileFragment;
import uz.programmer.rahmat.fragments.SearchFragment;
import uz.programmer.rahmat.model.Post;

public class AppActivity extends AppCompatActivity implements ProfileFragment.postListener{

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        bottomMenu();
    }

    private void bottomMenu() {

        BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.nav_home:{
                        fragment = new HomeFragment();
                        break;
                    }
                    case R.id.nav_search:{
                        fragment = new SearchFragment();
                        break;
                    }

                    case R.id.nav_add:{
                        fragment = new CreatePostFragment();
                        break;
                    }

                    case R.id.nav_geno:{
                            fragment = new GenoFragment();
                        break;
                    }
                    case R.id.nav_profile:{
                        fragment = new ProfileFragment();
                        break;
                    }
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

                return true;
            }

        };
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    @Override
    public void postLive(List<Post> postList) {

    }
}