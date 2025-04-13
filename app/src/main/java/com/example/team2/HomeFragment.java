package com.example.team2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private TextView noResultsText;
    private GiftAdapter adapter;
    private List<Gift> giftList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating HomeFragment view");
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        noResultsText = view.findViewById(R.id.no_results_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        giftList = new ArrayList<>();
        adapter = new GiftAdapter(giftList, false, true); // isCartMode=false, isHomeMode=true
        recyclerView.setAdapter(adapter);

        GiftData.getInstance().setOnGiftsLoadedListener(new GiftData.OnGiftsLoadedListener() {
            @Override
            public void onGiftsLoaded() {
                Log.d(TAG, "Gifts loaded: " + GiftData.getInstance().getGifts().size());
                giftList.clear();
                giftList.addAll(GiftData.getInstance().getGifts());
                adapter.notifyDataSetChanged();
                updateNoResultsVisibility();
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Error loading gifts: " + message);
            }
        });

        GiftData.getInstance().loadGifts();
        return view;
    }

    public void filterGifts(String query) {
        Log.d(TAG, "Filtering gifts with query: " + query);
        List<Gift> filteredList = new ArrayList<>();
        query = query.trim().toLowerCase();
        for (Gift gift : GiftData.getInstance().getGifts()) {
            if (gift.getName().toLowerCase().contains(query)) {
                filteredList.add(gift);
            }
        }
        giftList.clear();
        giftList.addAll(filteredList);
        adapter.notifyDataSetChanged();
        updateNoResultsVisibility();
    }

    private void updateNoResultsVisibility() {
        noResultsText.setVisibility(giftList.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(giftList.isEmpty() ? View.GONE : View.VISIBLE);
    }
}