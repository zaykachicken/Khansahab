# 🏪 Zayka — Restaurant Partner / Admin Application

Standalone Android management and kitchen dashboard application for restaurant owners and staff.

## Features
- **Admin Authentication**: Secure login gatekeeping requiring `RESTAURANT_ADMIN` role credentials. Default credentials:
  - Email: `zaykachicken@gmail.com` | Password: `zayka1236`
  - Email: `restaurant@zayka.com` | Password: `admin123`
- **Continuous Order Receive Alert ⚠️ Sound**:
  - Automatically loops a high-visibility, synthesized dual-tone harmonic kitchen chime (`STREAM_MUSIC` + `ToneGenerator` + vibration pulse) whenever unaccepted orders (`status == PLACED`) are pending.
  - High-priority pulsing alert card with 1-click **Accept Order** and **Mute Alarm** actions.
  - Test Chime button and 1-tap **Simulate New Order** button for instant testing.
- **Order Lifecycle Management**:
  - Live filter tabs: `Live Orders` vs `Past Orders`.
  - Step-by-step kitchen status transition: `Placed` → `Confirmed` → `Preparing` → `Out for Delivery` → `Delivered`.
  - Reject order option with custom cancellation reasons.
- **Menu Management**:
  - Add new menu items (Name, category, price, veg/non-veg flag, description).
  - Edit existing items, modify prices, or delete items.
  - 1-tap **In-Stock / Out-of-Stock** toggle synced directly to user menu.
- **Deals & Coupons**:
  - Create promotional discount codes, discount percentage/amount, and minimum order requirements.
  - Enable/disable or delete coupons.
- **Restaurant Details & Help**:
  - Update official restaurant support phone number, email, address, and operating hours.
- **Sales Analytics**:
  - Real-time today's orders, revenue tracking, average order value, and top-selling dishes.

## Security & Isolation
- Access restricted exclusively to verified administrators.
- Prevents normal customer shopping flows, cart checkouts, or personal address management.
- Backend Firestore Security Rules guarantee only authorized admins can update order statuses and menu items.

## Running the Standalone Admin App
- **Entry Point Activity**: `com.example.admin.AdminMainActivity`
- **Compose Root**: `com.example.admin.ui.AdminMainScreen`
- **Gradle Module**: `:app` (or `:admin-app` in standalone multi-repo)
- **Home Screen Launcher Label**: `Zayka Admin`
