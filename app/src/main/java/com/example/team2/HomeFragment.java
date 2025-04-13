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
    private RecyclerView categoriesRecycler;
    private GiftAdapter adapter;
    private CategoryAdapter categoryAdapter;
    private List<Gift> allGifts = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GiftAdapter(allGifts, false);
        recyclerView.setAdapter(adapter);

        categoriesRecycler = view.findViewById(R.id.categories_recycler);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        populateCategories();
        categoryAdapter = new CategoryAdapter(categories);
        categoriesRecycler.setAdapter(categoryAdapter);
        Log.d(TAG, "Categories RecyclerView set up with " + categories.size() + " categories");

        GiftData.getInstance().setOnGiftsLoadedListener(this);
        return view;
    }

    private void populateCategories() {
        categories.add(new Category("Birthdays", R.color.rose_quartz));
        categories.add(new Category("Anniversaries", R.color.soft_cherry));
        categories.add(new Category("Holidays", R.color.crimson_glow));
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
        Log.d(TAG, "Filtered gifts count: " + filteredGifts.size());
        adapter = new GiftAdapter(filteredGifts, false);
        recyclerView.setAdapter(adapter);
    }
}