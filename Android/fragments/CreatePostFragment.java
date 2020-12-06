package uz.programmer.rahmat.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uz.programmer.rahmat.LoginActivity;
import uz.programmer.rahmat.R;
import uz.programmer.rahmat.interfaces.Api;
import uz.programmer.rahmat.responses.DefaultResponse;
import uz.programmer.rahmat.structures.PreferencePackageHolder;

import static android.app.Activity.RESULT_OK;

public class CreatePostFragment extends Fragment {

    EditText post_text;
    SharedPreferences preferences;
    CardView add_image_button;
    final int IMAGE_REQUEST_CODE = 226;
    private Uri file_path;
    Bitmap bitmap;
    ImageView post_image;
    TextView add_image_text;
    private ProgressDialog loadingBar;
    Toolbar toolbar;
    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(PreferencePackageHolder.getAppPreference(), Context.MODE_PRIVATE);

        loadingBar = new ProgressDialog(getActivity());

        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        post_text = view.findViewById(R.id.post_text);
        add_image_button = view.findViewById(R.id.add_image_button);
        post_image = view.findViewById(R.id.post_image);
        add_image_text = view.findViewById(R.id.add_image_text);
        toolbar = view.findViewById(R.id.toolbar);

        String draft = preferences.getString(PreferencePackageHolder.getDraftPostText(), "");

        post_text.setText(draft);

        setHasOptionsMenu(true);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setDisplayShowTitleEnabled(false);
        add_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Rasm tanlang"), IMAGE_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IMAGE_REQUEST_CODE &&
            resultCode == RESULT_OK && data != null && data.getData() != null){
            file_path = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), file_path);
                post_image.setImageBitmap(bitmap);
                post_image.setVisibility(View.VISIBLE);
                add_image_text.setText("Rasmni o'zgartirish");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferencePackageHolder.getDraftPostText(), post_text.getText().toString().trim());
        editor.apply();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.create_post_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_post:
                preSend();
                break;

        }
        return true;

    }

    private void sendPost() {
        if(post_text.length() <= 0){
            post_text.setError("Matn yozilishi shart");
            post_text.requestFocus();
            return;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String encoded_image = null;
        if(bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);

            byte[] imageInByte = byteArrayOutputStream.toByteArray();

            encoded_image = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(PreferencePackageHolder.getUserPreference(), Context.MODE_PRIVATE);
        int user_id = sharedPreferences.getInt(PreferencePackageHolder.getUserIDKey(), 0);
        HashMap<String, Object> postData = new HashMap<>();
        postData.put("post_text", post_text.getText().toString());
        postData.put("user_id", user_id);
        postData.put("post_image", encoded_image);


        loadingBar.setTitle("Post yaratilayapti");
        loadingBar.setMessage("Ma'lumotlar tekshirilgunicha biroz kuting!");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        api.createPost(postData).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                loadingBar.dismiss();
                if(response.isSuccessful()){
                    DefaultResponse defaultResponse = response.body();

                    assert defaultResponse != null;
                    if(defaultResponse.getSuccess() == 1)
                    {

                        post_text.setText("");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(PreferencePackageHolder.getDraftPostText());
                        editor.apply();

                        assert getFragmentManager() != null;
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, new ProfileFragment(), "Profile");
                        ft.commit();
                        Toasty.success(Objects.requireNonNull(getActivity()), defaultResponse.getMessage(), Toasty.LENGTH_LONG).show();

                    }

                    if(defaultResponse.getError() == 1){
                        Toasty.error(Objects.requireNonNull(getActivity()), defaultResponse.getMessage(), Toasty.LENGTH_LONG).show();
                    }
                }
                else {
                    Toasty.error(Objects.requireNonNull(getActivity()), response.message(), Toasty.LENGTH_LONG).show();
                    }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                loadingBar.dismiss();

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

    private void preSend(){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                    {
                        dialog.dismiss();
                        sendPost();
                        break;
                    }

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setMessage("Haqiqatdan ham postni yuborasizmi?")
                .setCancelable(false)
                .setPositiveButton("Ha", dialogClickListener)
                .setNegativeButton("Yo'q", dialogClickListener).show();
    }
}