package uz.programmer.rahmat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

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
import uz.programmer.rahmat.responses.DefaultResponse;

public class RegisterActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView sign_in;

    Button sign_up;
    EditText name_input, username_input, email_input, password_input;
    private ProgressDialog loadingBar;
    ImageView login_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.toolbar);
        name_input = findViewById(R.id.name);
        username_input = findViewById(R.id.username);
        email_input = findViewById(R.id.email);
        password_input = findViewById(R.id.password);
        sign_up = findViewById(R.id.sign_up_button);

        loadingBar = new ProgressDialog(this);
        login_image = findViewById(R.id.login_image);


        sign_in = findViewById(R.id.sign_in);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginScreen();
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void openLoginScreen(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void register(){
        String name = name_input.getText().toString().trim();
        String username = username_input.getText().toString().trim();
        String email = email_input.getText().toString().trim();
        String password = password_input.getText().toString().trim();

        if(name.isEmpty()){
            name_input.setError("Ism kiritilishi shart");
            name_input.requestFocus();
            return;
        }

        if(name.matches(".*\\d.*")){
            name_input.setError("Ismda raqam ishtirok etmaydi");
            name_input.requestFocus();
            return;
        }

        if(username.isEmpty()){
            username_input.setError("Username kiritilishi shart");
            username_input.requestFocus();
            return;
        }

        if(!username.matches("^[^0-9][a-z0-9_.]+$")){
            username_input.setError("Username faqat kichkina harf, raqam, nuqta va ostki chizidan iborat bo'lishi mumkin");
            username_input.requestFocus();
            return;
        }


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

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("full_name", name);
        userData.put("username", username);

        api.userRegister(userData).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                loadingBar.dismiss();

                if(response.isSuccessful()){
                    DefaultResponse defaultResponse = response.body();
                    if(Objects.requireNonNull(defaultResponse).getSuccess() == 1){
                        View parentLayout = findViewById(android.R.id.content);

                        Snackbar snackbar = Snackbar.make(parentLayout, defaultResponse.getMessage(), Snackbar.LENGTH_INDEFINITE)
                                .setAction("Tizimga kirish", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openLoginScreen();
                                    }
                                })
                                .setActionTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null))
                                ;


                        View snackbarView = snackbar.getView();
                        TextView tv= (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                        tv.setMaxLines(3);
                        snackbar.show();
                    }
                    else if(Objects.requireNonNull(defaultResponse).getError() == 1)
                    {
                        Toasty.error(RegisterActivity.this, defaultResponse.getMessage(), Toasty.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                loadingBar.dismiss();

                if (t instanceof IOException){
                    Toasty.error(RegisterActivity.this, "Qurilmangizda internet holati yaxshi emas!", Toasty.LENGTH_SHORT).show();
                }
                else
                {
                    Toasty.error(RegisterActivity.this, "Xatolik sodir bo'ldi (Kod: 2)", Toasty.LENGTH_SHORT).show();
                }

            }
        });

    }

}