# 📱 Zayka — Customer / User Application

Standalone Android food delivery application for customers.

## Features
- **Browse Menu**: Real-time categorized food menu (Biryani, Curries, Tandoor & Grill, Starters, Breads & Rice, Desserts).
- **Search & Filters**: Instant item search, veg/non-veg filter, bestsellers and ratings.
- **Customization**: Select portion sizes (Half / Full / Handi), spice levels (Mild, Medium, Spicy), and add-ons (Extra Raita, Salan, Boiled Egg).
- **Cart & Bill Breakdown**: Live subtotal, delivery fee, taxes, packaging, and coupon discounts (e.g., `ZAYKA100`, `FIRST50`).
- **Live GPS Tracking**: Real-time order progress stepper (`Placed` → `Confirmed` → `Preparing` → `Out for Delivery` → `Delivered`), animated rider delivery route map, ETA countdown, rider details and call option.
- **Order History**: Past orders with re-order capability and rating/feedback submission.
- **Profile & Addresses**: Saved delivery addresses (Home, Work, Other), light/dark mode switch, FAQ and support.
- **Authentication**: Google One-Tap Sign-In via Credential Manager, Phone OTP login, Email/Password, or Instant Guest Mode.

## Security & Isolation
- Completely free of restaurant admin routes, admin controls, price alteration, or kitchen sound alerts.
- Firestore Security Rules enforce that customers can only read and create their own orders and profile documents.

## Running the Standalone User App
- **Entry Point Activity**: `com.example.user.UserMainActivity`
- **Compose Root**: `com.example.user.ui.UserMainScreen`
- **Gradle Module**: `:app` (or `:user-app` in standalone multi-repo)
- **Home Screen Launcher Label**: `Zayka Delivery`
