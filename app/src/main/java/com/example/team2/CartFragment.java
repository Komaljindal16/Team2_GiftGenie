package com.example.team2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartFragment extends Fragment {
    private static final String TAG = "CartFragment";
    private RecyclerView recyclerView;
    private GiftAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GiftAdapter(GiftData.getInstance().getCart(), true);
        recyclerView.setAdapter(adapter);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateCartBadge();
        }
        return view;
    }
}