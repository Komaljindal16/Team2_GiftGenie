package com.example.team2;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import android.view.ScaleGestureDetector;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.List;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {
    private static final String TAG = "GiftAdapter";
    private List<Gift> gifts;
    private boolean isCartMode;
    private boolean isHomeMode;

    public GiftAdapter(List<Gift> gifts, boolean isCartMode, boolean isHomeMode) {
        this.gifts = (gifts != null) ? gifts : new ArrayList<>();
        this.isCartMode = isCartMode;
        this.isHomeMode = isHomeMode;
        Log.d(TAG, "Adapter initialized with " + gifts.size() + " items, isCartMode: " + isCartMode + ", isHomeMode: " + isHomeMode);
    }

    @Override
    public GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating ViewHolder");
        int layoutId = isHomeMode ? R.layout.item_home_gift : R.layout.item_gift;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new GiftViewHolder(view, isHomeMode);
    }

    @Override
    public void onBindViewHolder(GiftViewHolder holder, int position) {
        Log.d(TAG, "Binding position " + position);
        Gift gift = gifts.get(position);
        holder.name.setText(gift.getName().substring(0, Math.min(gift.getName().length(), 20)));
        holder.price.setText(String.format("$%.2f", gift.getPrice()));
        String imageUrl = gift.getImageUrl();
        Log.d(TAG, "Gift: " + gift.getName() + ", URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_gift_flaticon)
                    .error(R.drawable.ic_gift_flaticon)
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Image loaded: " + imageUrl);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Image failed: " + imageUrl + ", " + e.getMessage());
                        }
                    });
        } else {
            Log.w(TAG, "No URL for " + gift.getName());
            holder.image.setImageResource(R.drawable.ic_gift_flaticon);
        }

        if (!isHomeMode) {
            final int finalCartQuantity;
            if (isCartMode) {
                finalCartQuantity = gift.getQuantity();
            } else {
                int cartQuantity = 0;
                for (Gift cartGift : GiftData.getInstance().getCart()) {
                    if (cartGift.getName().equals(gift.getName())) {
                        cartQuantity = cartGift.getQuantity();
                        break;
                    }
                }
                finalCartQuantity = cartQuantity;
            }
            holder.quantity.setText(String.valueOf(finalCartQuantity));
            holder.increaseButton.setText(isCartMode ? "Remove" : "+");
            holder.increaseButton.setOnClickListener(v -> {
                Log.d(TAG, "Increase button clicked for " + gift.getName());
                if (isCartMode) {
                    GiftData.getInstance().removeFromCart(gift);
                    int removedPosition = gifts.indexOf(gift);
                    if (removedPosition != -1) {
                        gifts.remove(removedPosition);
                        notifyItemRemoved(removedPosition);
                    }
                    Toast.makeText(holder.itemView.getContext(), "Removed " + gift.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    GiftData.getInstance().addToCart(new Gift(gift.getName(), gift.getPrice(), gift.getImageUrl(), 0));
                    Toast.makeText(holder.itemView.getContext(), "Added " + gift.getName(), Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                }
            });

            holder.decreaseButton.setOnClickListener(v -> {
                Log.d(TAG, "Decrease button clicked for " + gift.getName());
                if (finalCartQuantity > 0) {
                    GiftData.getInstance().removeFromCart(new Gift(gift.getName(), gift.getPrice(), gift.getImageUrl(), 0));
                    Toast.makeText(holder.itemView.getContext(), "Decreased " + gift.getName(), Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                }
            });
        }

        holder.image.setClickable(true);
        holder.image.setFocusable(true);
        holder.image.setOnClickListener(v -> {
            Log.d(TAG, "Image clicked for " + gift.getName());
            Dialog dialog = new Dialog(holder.itemView.getContext());
            dialog.setContentView(R.layout.dialog_zoom_image);
            ImageView zoomImage = dialog.findViewById(R.id.zoom_image);
            String zoomUrl = imageUrl;

            ZoomHandler zoomHandler = new ZoomHandler(zoomImage);
            zoomImage.setOnTouchListener(zoomHandler);

            if (zoomUrl != null) {
                Picasso.get()
                        .load(zoomUrl)
                        .into(zoomImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Zoom image loaded: " + zoomUrl);
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "Zoom image failed: " + zoomUrl + ", " + e.getMessage());
                                zoomImage.setImageResource(R.drawable.ic_gift_flaticon);
                            }
                        });
            } else {
                zoomImage.setImageResource(R.drawable.ic_gift_flaticon);
            }
            dialog.show();
            Log.d(TAG, "Zoom dialog shown with URL: " + zoomUrl);
        });
    }

    @Override
    public int getItemCount() {
        return gifts.size();
    }

    static class GiftViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, quantity;
        Button increaseButton, decreaseButton;

        GiftViewHolder(View itemView, boolean isHomeMode) {
            super(itemView);
            Log.d(TAG, "ViewHolder created, isHomeMode: " + isHomeMode);
            image = itemView.findViewById(R.id.gift_image);
            name = itemView.findViewById(R.id.gift_name);
            price = itemView.findViewById(R.id.gift_price);
            if (!isHomeMode) {
                quantity = itemView.findViewById(R.id.gift_quantity);
                increaseButton = itemView.findViewById(R.id.increase_quantity_button);
                decreaseButton = itemView.findViewById(R.id.decrease_quantity_button);
            }
        }
    }

    private static class ZoomHandler implements View.OnTouchListener {
        private final ImageView imageView;
        private final ScaleGestureDetector scaleGestureDetector;
        private float scaleFactor = 1.0f;
        private static final float MIN_SCALE = 1.0f;
        private static final float MAX_SCALE = 4.0f;

        ZoomHandler(ImageView imageView) {
            this.imageView = imageView;
            this.scaleGestureDetector = new ScaleGestureDetector(imageView.getContext(), new ScaleListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        }

        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));
                imageView.setScaleX(scaleFactor);
                imageView.setScaleY(scaleFactor);
                return true;
            }
        }
    }
}