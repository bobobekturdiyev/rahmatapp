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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import uz.programmer.rahmat.R;
import uz.programmer.rahmat.adapters.PostsAdapter;
import uz.programmer.rahmat.model.Post;
import uz.programmer.rahmat.structures.PreferencePackageHolder;

public class MyPostsFragment extends Fragment implements ProfileFragment.postListener {

    RecyclerView recyclerView;
    TextView no_post_text;
    ImageView empty_box;
    SharedPreferences preferences;
    public MyPostsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        no_post_text = view.findViewById(R.id.no_post_text);
        empty_box = view.findViewById(R.id.empty_box);
        preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(PreferencePackageHolder.getUserPreference(), Context.MODE_PRIVATE);
        return view;
    }



    @Override
    public void postLive(List<Post> postList) {
        if(postList != null && postList.size() > 0) {

            PostsAdapter adapter = new PostsAdapter(getActivity(), postList, preferences.getInt(PreferencePackageHolder.getUserIDKey(), 0));
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setVisibility(View.VISIBLE);
            empty_box.setVisibility(View.GONE);
            no_post_text.setVisibility(View.GONE);
        }else
        {
            empty_box.setVisibility(View.VISIBLE);
            no_post_text.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }



}