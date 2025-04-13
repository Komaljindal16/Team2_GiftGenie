#How to Install App in Android Device?
Step 1. In Android Studio, go to Build -> Build Bundle/APK -> Build APK
Step 2. A message will appear and Click locate to view the file.
Step 3. Insatll the APK file in your Android device.
Step 4. If it doesnot work, make sure developer mode is turned on in settings and enable apps from unknown resources.


#logic to UpdateCart
public void updateCartBadge() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        int cartSize = 0;
        for (Gift gift : GiftData.getInstance().getCart()) {
            cartSize += gift.getQuantity();
        }
        Log.d(TAG, "Updating badge with cart quantity: " + cartSize);
        bottomNavigationView.getOrCreateBadge(R.id.nav_cart).setNumber(cartSize);
        bottomNavigationView.getOrCreateBadge(R.id.nav_cart).setVisible(cartSize > 0);
    }
