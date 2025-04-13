package com.example.team2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartFragment extends Fragment {
    private static final String TAG = "CartFragment";
    private RecyclerView recyclerView;
    private GiftAdapter adapter;
    private TextView emptyCartText;
    private Button checkoutButton;
    private SessionManager sessionManager;
    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d(TAG, "Login result: " + result.getResultCode());
            if (result.getResultCode() == requireActivity().RESULT_OK && sessionManager.isLoggedIn()) {
                proceedWithCheckout();
            } else {
                Toast.makeText(getContext(), "Login required to checkout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating CartFragment view");
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        emptyCartText = view.findViewById(R.id.empty_cart_text);
        checkoutButton = view.findViewById(R.id.checkout_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GiftAdapter(GiftData.getInstance().getCart(), true, false);
        recyclerView.setAdapter(adapter);

        checkoutButton.setOnClickListener(v -> {
            Log.d(TAG, "Checkout button clicked");
            if (GiftData.getInstance().getCart().isEmpty()) {
                Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!sessionManager.isLoggedIn()) {
                Log.d(TAG, "User not logged in, launching LoginActivity");
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                loginLauncher.launch(intent);
            } else {
                Log.d(TAG, "User logged in, proceeding with checkout");
                proceedWithCheckout();
            }
        });

        updateEmptyCartVisibility();
        return view;
    }

    private void proceedWithCheckout() {
        Log.d(TAG, "Completing checkout");
        GiftData.getInstance().getCart().clear();
        adapter.notifyDataSetChanged();
        updateEmptyCartVisibility();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateCartBadge();
        }
        Toast.makeText(getContext(), "Checkout successful! Thank you for your purchase.", Toast.LENGTH_LONG).show();
    }

    private void updateEmptyCartVisibility() {
        boolean isEmpty = GiftData.getInstance().getCart().isEmpty();
        emptyCartText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        checkoutButton.setEnabled(!isEmpty);
    }
}