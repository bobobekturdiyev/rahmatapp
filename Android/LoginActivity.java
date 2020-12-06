package uz.programmer.rahmat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uz.programmer.rahmat.interfaces.Api;
import uz.programmer.rahmat.model.User;
import uz.programmer.rahmat.responses.ResponseLogin;
import uz.programmer.rahmat.structures.PreferencePackageHolder;

public class LoginActivity extends AppCompatActivity {

    TextView sign_up, forgot_password;
    Button sign_in_btn;
    EditText email_input, password_input;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sign_up = findViewById(R.id.sign_up);
        forgot_password = findViewById(R.id.forgot_password);
        sign_in_btn = findViewById(R.id.sign_in);
        loadingBar = new ProgressDialog(this);
        email_input = findViewById(R.id.email);
        password_input = findViewById(R.id.password);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
    }

    private void userLogin(){
        final String email = email_input.getText().toString().trim();
        final String password = password_input.getText().toString().trim();

        if(email.isEmpty()){
            email_input.setError("Email kiritilishi shart");
            email_input.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_input.setError("Noto'g'ri email kiritildi");
            email_input.requestFocus();
            return;
        }

        if(password.isEmpty()){
            password_input.setError("Parol kiritilishi shart");
            password_input.requestFocus();
            return;
        }

        if(password.length() < 8){
            password_input.setError("Parol minimum 8 ta belgidan iborat bo'lishi kerak");
            password_input.requestFocus();
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


        // Get User Device ID

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);

        api.userLogin(userData).enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                ResponseLogin responseLogin = response.body();
                loadingBar.dismiss();
                if(response.isSuccessful()){

                    assert responseLogin != null;
                    if(responseLogin.getSuccess() == 1){
                        assert response.body() != null;
                        User user = response.body().getUser();

                        if(user.getIs_active() == 0){
                            Toasty.error(LoginActivity.this, "Akkountingiz faollashmagan. Iltimos, oldin faollashtiring!", Toasty.LENGTH_LONG).show();
                            return;
                        }
                        else {// if account is active

                            SharedPreferences preferences = getSharedPreferences(PreferencePackageHolder.getUserPreference(), Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = preferences.edit();

                            editor.putString(PreferencePackageHolder.getEmailKey(), email);
                            editor.putString(PreferencePackageHolder.getFullNameKey(), user.getFull_name());
                            editor.putString(PreferencePackageHolder.getUsernameKey(), user.getUsername());
                            editor.putString(PreferencePackageHolder.getPasswordKey(), password);
                            editor.putString(PreferencePackageHolder.getEncryptedPasswordKey(), user.getPassword());
                            editor.putString(PreferencePackageHolder.getStatusKey(), user.getStatus());
                            editor.putInt(PreferencePackageHolder.getUserIDKey(), user.getId());

                            editor.putBoolean(PreferencePackageHolder.getLoggedInKey(), true);
                            editor.apply();

                            Toasty.success(LoginActivity.this, responseLogin.getMessage(), Toasty.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), AppActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else
                    {
                        Toasty.error(LoginActivity.this, responseLogin.getMessage(), Toasty.LENGTH_LONG).show();
                    }

                }
                else{
                    Toasty.error(LoginActivity.this, Objects.requireNonNull(responseLogin).getMessage(), Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                loadingBar.dismiss();

                if (t instanceof IOException){
                    Toasty.error(LoginActivity.this, "Qurilmangizda internet holati yaxshi emas!", Toasty.LENGTH_SHORT).show();
                }
                else
                {
                    Toasty.error(LoginActivity.this, "Xatolik sodir bo'ldi (Kod: 2)", Toasty.LENGTH_SHORT).show();
                }
            }
        });

    }



}