package uz.programmer.rahmat.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uz.programmer.rahmat.EditProfileActivity;
import uz.programmer.rahmat.LoginActivity;
import uz.programmer.rahmat.MainActivity;
import uz.programmer.rahmat.R;
import uz.programmer.rahmat.adapters.ViewPagerAdapter;
import uz.programmer.rahmat.interfaces.Api;
import uz.programmer.rahmat.model.Post;
import uz.programmer.rahmat.model.User;
import uz.programmer.rahmat.responses.ResponseProfileData;
import uz.programmer.rahmat.structures.PreferencePackageHolder;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment{

    ViewPager viewPager;
    TabLayout tabLayout;
    CircleImageView profile_image;
    TextView username, like_count, follower_count, following_count, full_name, bio;
    SharedPreferences preferences;
    List<Post> postList;
    postListener postListener;
    Toolbar toolbar;
    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
         preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(PreferencePackageHolder.getUserPreference(), Context.MODE_PRIVATE);

        postList = new ArrayList<>();


        MyPostsFragment myPostsFragment = new MyPostsFragment();
        postListener = (postListener) myPostsFragment;

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.AddFragment(myPostsFragment, "Postlarim");
        viewPagerAdapter.AddFragment(new SavedPostsFragment(), "Saqlanganlar");

        toolbar = view.findViewById(R.id.toolbar);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        profile_image = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        like_count = view.findViewById(R.id.like_count);
        follower_count = view.findViewById(R.id.follower_count);
        following_count = view.findViewById(R.id.following_count);
        full_name = view.findViewById(R.id.full_name);
        bio = view.findViewById(R.id.bio);



        setHasOptionsMenu(true);

        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setDisplayShowTitleEnabled(false);



        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        loadUserInnerData();

        getUserProfileData();

        return view;
    }

    private void getUserProfileData() {

        int user_id = preferences.getInt(PreferencePackageHolder.getUserIDKey(), 0);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        api.getUserProfileData(user_id).enqueue(new Callback<ResponseProfileData>() {
            @Override
            public void onResponse(Call<ResponseProfileData> call, Response<ResponseProfileData> response) {
                if(response.isSuccessful()){

                    User user = response.body().getUser();

                    username.setText(user.getUsername());
                    follower_count.setText(String.valueOf(user.getFollowers_count()));
                    following_count.setText(String.valueOf(user.getFollowing_count()));
                    like_count.setText(String.valueOf(user.getLikes_count()));
                    full_name.setText(user.getFull_name());

                    postList = response.body().getPostList();

                    postListener.postLive(postList);

                    if(user.getBio()!=null) {
                        bio.setText(user.getBio());
                        bio.setVisibility(View.VISIBLE);
                    }
                    else {
                        bio.setVisibility(View.GONE);
                    }
                    Activity activity = getActivity();

                    if(user.getPhoto() != null){
                        if(activity != null) {
                            Glide.with(activity)
                                    .load(user.getPhoto())
                                    .into(profile_image);
                        }
                    }
                    else{
                        if(activity != null) {
                            Glide.with(activity)
                                    .load(R.drawable.unknown_user)
                                    .into(profile_image);
                        }
                    }

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(PreferencePackageHolder.getFollowerCountKey(), user.getFollowers_count());
                    editor.putInt(PreferencePackageHolder.getFollowingCountKey(), user.getFollowing_count());
                    editor.putInt(PreferencePackageHolder.getLikesCountKey(), user.getLikes_count());
                    editor.putString(PreferencePackageHolder.getUsernameKey(), user.getUsername());
                    editor.putString(PreferencePackageHolder.getUserBioKey(), user.getBio());
                    editor.putString(PreferencePackageHolder.getFullNameKey(), user.getFull_name());
                    editor.putString(PreferencePackageHolder.getUserPhotoUrlKey(), user.getPhoto());

                    editor.apply();

                }else{
                    Toasty.error(Objects.requireNonNull(getActivity()), response.message(), Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseProfileData> call, Throwable t) {
                if (t instanceof IOException){
                    Toasty.error(Objects.requireNonNull(getActivity()), "Qurilmangizda internet holati yaxshi emas!", Toasty.LENGTH_SHORT).show();
                }
                else
                {
                    Toasty.error(Objects.requireNonNull(getActivity()), "Xatolik sodir bo'ldi (Kod: 2)", Toasty.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadUserInnerData(){

        username.setText(preferences.getString(PreferencePackageHolder.getUsernameKey(), "-"));
        follower_count.setText(String.valueOf(preferences.getInt(PreferencePackageHolder.getFollowerCountKey(), 0)));
        following_count.setText(String.valueOf(preferences.getInt(PreferencePackageHolder.getFollowingCountKey(), 0)));
        like_count.setText(String.valueOf(preferences.getInt(PreferencePackageHolder.getLikesCountKey(), 0)));
        full_name.setText(preferences.getString(PreferencePackageHolder.getFullNameKey(), "-"));

        String user_bio = preferences.getString(PreferencePackageHolder.getUserBioKey(), "-");
        bio.setText(user_bio);
        if(user_bio != null && user_bio.length() > 1){
            bio.setVisibility(View.VISIBLE);
        }
    }


    public interface postListener{
        public void postLive(List<Post> postList);
    }

    public void setPostListener(postListener postListener){
        this.postListener = postListener;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivityForResult(intent, 111);
                break;

            case R.id.logout:
                logout();
                break;

        }
        return true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK){
            getUserProfileData();
        }
    }

    private void logout() {
        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(PreferencePackageHolder.getUserPreference(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


        editor.putBoolean(PreferencePackageHolder.getLoggedInKey(), false);

        editor.remove(PreferencePackageHolder.getUserBioKey());
        editor.remove(PreferencePackageHolder.getUsernameKey());
        editor.remove(PreferencePackageHolder.getFullNameKey());
        editor.remove(PreferencePackageHolder.getFollowingCountKey());
        editor.remove(PreferencePackageHolder.getFollowerCountKey());
        editor.remove(PreferencePackageHolder.getLikesCountKey());

        editor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        getActivity().finish();

    }
}