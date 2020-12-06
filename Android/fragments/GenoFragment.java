package uz.programmer.rahmat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import uz.programmer.rahmat.R;

public class GenoFragment extends Fragment {

    ImageView saxovat_img;

    public GenoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_geno, container, false);

         saxovat_img = view.findViewById(R.id.saxovat_img);
        Glide.with(this)
                .load(R.drawable.saxovat)
                .into(saxovat_img);

        return view;
    }
}