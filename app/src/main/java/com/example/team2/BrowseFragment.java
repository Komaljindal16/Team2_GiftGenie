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

public class BrowseFragment extends Fragment implements GiftData.OnGiftsLoadedListener {
    private static final String TAG = "BrowseFragment";
    private RecyclerView recyclerView;
    private TextView noResultsText;
    private GiftAdapter adapter;
    private List<Gift> allGifts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse, container, false);
        recyclerView = view.findViewById(R.id.browse_recycler_view);
        noResultsText = view.findViewById(R.id.no_results_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GiftAdapter(allGifts, false, false);  // isCartMode=false, isHomeMode=false
        recyclerView.setAdapter(adapter);

        GiftData.getInstance().setOnGiftsLoadedListener(this);
        return view;
    }

    @Override
    public void onGiftsLoaded() {
        Log.d(TAG, "Gifts loaded in BrowseFragment");
        allGifts.clear();
        allGifts.addAll(GiftData.getInstance().getGifts());
        filterGifts("");  // Show all gifts initially
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
        query = query.trim().toLowerCase();

        if (query.isEmpty()) {
            filteredGifts.addAll(allGifts);
        } else {
            for (Gift gift : allGifts) {
                if (gift.getName().toLowerCase().contains(query)) {
                    filteredGifts.add(gift);
                }
            }
        }

        adapter = new GiftAdapter(filteredGifts, false, false);  // isCartMode=false, isHomeMode=false
        recyclerView.setAdapter(adapter);
        noResultsText.setVisibility(filteredGifts.isEmpty() && !query.isEmpty() ? View.VISIBLE : View.GONE);
    }
}