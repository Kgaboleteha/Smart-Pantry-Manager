# Smart Pantry Manager

## Overview
Smart Pantry Manager is a native Android application built in Java designed to eliminate food waste by tracking ingredients users already have at home. The application uses a strict-matching algorithm to recommend only recipes that can be prepared immediately without requiring additional grocery purchases.

## Technical Architecture
- **Language**: Java
- **Target SDK**: Android 8.0 (API 26) / Target API 34+
- **Database**: SQLite (via SQLiteOpenHelper)
  - *Rationale*: Chosen for robust local persistence, zero dependency on external network services, rapid offline queries, and strict compliance with the mobile persistence module curriculum.
- **UI Architecture**: RecyclerView with custom Adapters, ConstraintLayout, and Material Design components.

## Core Features
1. **Pantry Inventory CRUD**: Add, view, edit, and delete pantry items with quantity, unit, and optional expiry tracking.
2. **Strict Recipe Matching**: Only suggests recipes where 100% of required ingredients exist in the user's pantry in sufficient quantities.
3. **Pre-seeded Recipes**: Seeded SQLite database containing standard recipes ready on first launch.
4. **Offline Resilience**: Full offline data persistence across app restarts.

## Setup & Installation
1. Clone the repository:
   \\\ash
   git clone https://github.com/Kgaboleteha/Smart-Pantry-Manager.git
   \\\
2. Open the project in Android Studio.
3. Allow Gradle to sync dependencies.
4. Select an Android Virtual Device (AVD) running API 26 or higher and click **Run (Shift + F10)**.
