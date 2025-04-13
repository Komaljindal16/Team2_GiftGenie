package com.example.team2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements GiftData.OnGiftsLoadedListener {
    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private GiftAdapter adapter;
    private List<Gift> allGifts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GiftAdapter(allGifts, false);
        recyclerView.setAdapter(adapter);

        GiftData.getInstance().setOnGiftsLoadedListener(this);
        return view;
    }

    @Override
    public void onGiftsLoaded() {
        Log.d(TAG, "Gifts loaded in HomeFragment");
        allGifts.clear();
        allGifts.addAll(GiftData.getInstance().getGifts());
        adapter.notifyDataSetChanged();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateCartBadge();
        }
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "Error loading gifts: " + message);
    }

    public void filterGifts(String query) {
        Log.d(TAG, "Filtering gifts with query: " + query);
        List<Gift> filteredGifts = new ArrayList<>();
        for (Gift gift : allGifts) {
            if (gift.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredGifts.add(gift);
            }
        }
        adapter = new GiftAdapter(filteredGifts, false);
        recyclerView.setAdapter(adapter);
    }
}