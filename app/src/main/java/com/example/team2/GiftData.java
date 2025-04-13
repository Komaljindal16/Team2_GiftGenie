package com.example.team2;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class GiftData {
    private static final String TAG = "GiftData";
    private static GiftData instance;
    private List<Gift> gifts = new ArrayList<>();
    private List<Gift> cart = new ArrayList<>();
    private OnGiftsLoadedListener listener;
    private Context context;

    // Retrofit interface for Fake Store API
    public interface FakeStoreApi {
        @GET("products")
        Call<List<FakeStoreProduct>> getProducts();
    }

    public static class FakeStoreProduct {
        private String title;
        private double price;
        private String image;

        public String getTitle() { return title; }
        public double getPrice() { return price; }
        public String getImage() { return image; }
    }

    private GiftData() {
        // Initialize without fetching to avoid immediate API call
    }

    public static GiftData getInstance() {
        if (instance == null) {
            instance = new GiftData();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOnGiftsLoadedListener(OnGiftsLoadedListener listener) {
        this.listener = listener;
        if (!gifts.isEmpty() && listener != null) {
            listener.onGiftsLoaded();
        }
    }

    public void loadGifts() {
        Log.d(TAG, "loadGifts called");
        fetchGifts();
    }

    private void fetchGifts() {
        Log.d(TAG, "Fetching gifts from Fake Store API");
        gifts.clear();

        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FakeStoreApi api = retrofit.create(FakeStoreApi.class);
        api.getProducts().enqueue(new Callback<List<FakeStoreProduct>>() {
            @Override
            public void onResponse(Call<List<FakeStoreProduct>> call, Response<List<FakeStoreProduct>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FakeStoreProduct> products = response.body();
                    for (int i = 0; i < Math.min(products.size(), 10); i++) {  // Limit to 10 items
                        FakeStoreProduct product = products.get(i);
                        String name = product.getTitle().substring(0, Math.min(product.getTitle().length(), 20));
                        double price = product.getPrice();
                        String imageUrl = product.getImage();
                        Log.d(TAG, "Gift added - Name: " + name + ", URL: " + imageUrl + ", Price: " + price);
                        gifts.add(new Gift(name, price, imageUrl, 0)); // Added quantity=0
                    }
                    Log.d(TAG, "Gifts loaded: " + gifts.size());
                    if (listener != null) {
                        listener.onGiftsLoaded();
                    }
                } else {
                    Log.e(TAG, "API failed: " + response.code());
                    if (listener != null) {
                        listener.onError("API error: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FakeStoreProduct>> call, Throwable t) {
                Log.e(TAG, "Network failure: " + t.getMessage());
                if (listener != null) {
                    listener.onError("Network error: " + t.getMessage());
                }
            }
        });
    }

    public List<Gift> getGifts() {
        return new ArrayList<>(gifts);
    }

    public List<Gift> getCart() {
        return new ArrayList<>(cart);
    }

    public void addToCart(Gift gift) {
        Log.d(TAG, "Adding to cart: " + gift.getName());
        Gift cartGift = null;
        for (Gift g : cart) {
            if (g.getName().equals(gift.getName())) {
                cartGift = g;
                break;
            }
        }
        if (cartGift != null) {
            cartGift.setQuantity(cartGift.getQuantity() + 1);
            Log.d(TAG, "Incremented quantity for " + cartGift.getName() + ": " + cartGift.getQuantity());
        } else {
            Gift newGift = new Gift(gift.getName(), gift.getPrice(), gift.getImageUrl(), 1);
            cart.add(newGift);
            Log.d(TAG, "Added new gift to cart: " + newGift.getName());
        }
        updateCartBadge();
    }

    public void removeFromCart(Gift gift) {
        Log.d(TAG, "Removing from cart: " + gift.getName());
        Gift cartGift = null;
        for (Gift g : cart) {
            if (g.getName().equals(gift.getName())) {
                cartGift = g;
                break;
            }
        }
        if (cartGift != null) {
            cartGift.setQuantity(cartGift.getQuantity() - 1);
            Log.d(TAG, "Decremented quantity for " + cartGift.getName() + ": " + cartGift.getQuantity());
            if (cartGift.getQuantity() <= 0) {
                cart.remove(cartGift);
                Log.d(TAG, "Removed gift from cart: " + cartGift.getName());
            }
        }
        updateCartBadge();
    }

    private void updateCartBadge() {
        Log.d(TAG, "Updating cart badge");
        if (context instanceof MainActivity) {
            ((MainActivity) context).updateCartBadge();
        } else {
            Log.e(TAG, "Context is not MainActivity: " + (context != null ? context.getClass().getSimpleName() : "null"));
        }
    }

    public interface OnGiftsLoadedListener {
        void onGiftsLoaded();
        void onError(String message);
    }
}
