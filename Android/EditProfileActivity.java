package uz.programmer.rahmat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uz.programmer.rahmat.interfaces.Api;
import uz.programmer.rahmat.responses.DefaultResponse;
import uz.programmer.rahmat.structures.PreferencePackageHolder;

public class EditProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText full_name, username, bio;
    Button update_profile;
    private ProgressDialog loadingBar;
    CircleImageView profile_image;
    final int IMAGE_REQUEST_CODE = 226;
    private Uri file_path;
    Bitmap bitmap;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        toolbar = findViewById(R.id.toolbar);
        full_name = findViewById(R.id.full_name);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        update_profile = findViewById(R.id.update_profile);
        profile_image = findViewById(R.id.profile_image);
        loadingBar = new ProgressDialog(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = getSharedPreferences(PreferencePackageHolder.getUserPreference(), MODE_PRIVATE);

        full_name.setText(preferences.getString(PreferencePackageHolder.getFullNameKey(), ""));
        username.setText(preferences.getString(PreferencePackageHolder.getUsernameKey(), ""));
        bio.setText(preferences.getString(PreferencePackageHolder.getUserBioKey(), ""));

        String photo_url = preferences.getString(PreferencePackageHolder.getUserPhotoUrlKey(), "");

        if(photo_url != null && !photo_url.equals("")){
            Glide.with(EditProfileActivity.this)
                    .load(photo_url)
                    .into(profile_image);
        }

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Rasm tanlang"), IMAGE_REQUEST_CODE);
            }
        });

    }

    private void update() {
        if(full_name.getText().toString().isEmpty()){
            full_name.setError("Ism-familiya kiritilishi shart");
            full_name.requestFocus();
            return;
        }

        if(full_name.getText().toString().matches(".*\\d.*")){
            full_name.setError("Ismda raqam ishtirok etmaydi");
            full_name.requestFocus();
            return;
        }

        if(username.getText().toString().isEmpty()){
            username.setError("Username kiritilishi shart");
            username.requestFocus();
            return;
        }

        if(!username.getText().toString().matches("^[^0-9][a-z0-9_.]+$")){
            username.setError("Username faqat kichkina harf, raqam, nuqta va ostki chizidan iborat bo'lishi mumkin");
            username.requestFocus();
            return;
        }


        loadingBar.setTitle("Tizimga kirish");
        loadingBar.setMessage("Ma'lumotlar tekshirilgunicha biroz kuting!");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);



        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String encoded_image = null;
        if(bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);

            byte[] imageInByte = byteArrayOutputStream.toByteArray();

            encoded_image = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        }

        int user_id = preferences.getInt(PreferencePackageHolder.getUserIDKey(), 0);

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("full_name", full_name.getText().toString());
        userData.put("username", username.getText().toString());
        userData.put("bio", bio.getText().toString());
        userData.put("user_id",user_id);

        userData.put("profile_photo", encoded_image);

        api.updateProfile(userData).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                loadingBar.dismiss();
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().getSuccess() == 1) {
                        Toasty.success(EditProfileActivity.this, response.body().getMessage(), Toasty.LENGTH_LONG).show();
                        Intent data = new Intent();
                        String text = response.body().getMessage();
                        data.setData(Uri.parse(text));
                        setResult(RESULT_OK, data);
                        finish();
                    }
                    else if(response.body().getError() == 1)
                    {
                        Toasty.error(EditProfileActivity.this, response.body().getMessage(), Toasty.LENGTH_LONG).show();
                    }
                }
                else{
                    Toasty.error(EditProfileActivity.this, response.message(), Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                loadingBar.dismiss();


                if (t instanceof IOException){
                    Toasty.error(EditProfileActivity.this, "Qurilmangizda internet holati yaxshi emas!", Toasty.LENGTH_SHORT).show();
                }
                else
                {
                    Toasty.error(EditProfileActivity.this, "Xatolik sodir bo'ldi (Kod: 2)", Toasty.LENGTH_SHORT).show();
                }
            }
        });




    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE &&
                resultCode == RESULT_OK && data != null && data.getData() != null) {
            file_path = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file_path);
                profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}