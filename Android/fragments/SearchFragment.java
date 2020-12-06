package uz.programmer.rahmat.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uz.programmer.rahmat.LoginActivity;
import uz.programmer.rahmat.R;
import uz.programmer.rahmat.adapters.UsersAdapter;
import uz.programmer.rahmat.interfaces.Api;
import uz.programmer.rahmat.responses.ResponseUser;
import uz.programmer.rahmat.structures.PreferencePackageHolder;


public class SearchFragment extends Fragment {

    private int user_id;
    RecyclerView recyclerView;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(PreferencePackageHolder.getUserPreference(), Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerView);
        getUsers();
        return view;
    }

    private void getUsers(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        api.getUsers().enqueue(new Callback<ResponseUser>() {
            @Override
            public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                if(response.isSuccessful()){
                    ResponseUser responseUser = response.body();

                    UsersAdapter adapter = new UsersAdapter(getActivity(), Objects.requireNonNull(responseUser).getUserList(), user_id);

                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                }
                else
                {
                    Toasty.error(Objects.requireNonNull(getContext()), response.message(), Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUser> call, Throwable t) {
                if (t instanceof IOException){
                    Toasty.error(Objects.requireNonNull(getContext()), "Qurilmangizda internet holati yaxshi emas!", Toasty.LENGTH_SHORT).show();
                }
                else
                {
                    Toasty.error(Objects.requireNonNull(getContext()), "Xatolik sodir bo'ldi (Kod: 2)", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }
}