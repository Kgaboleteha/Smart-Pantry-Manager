# Smart Pantry Manager

A native Android application built in Java designed to cut food waste by tracking ingredients users actually have in their pantry and suggesting recipes they can cook immediately using strictly those items.

---

## Technical Architecture & Database Justification
- **Platform / IDE**: Android Studio
- **Language**: Java (Android SDK)
- **Architecture Pattern**: Model-View-Adapter with SQLite persistence
- **Database Engine**: SQLite via `SQLiteOpenHelper`
  - *Technical Justification*: A food waste management app requires reliable, instantaneous offline access without network latency or reliance on external cloud services. Local SQLite provides robust relational integrity between recipes and their ingredients, executes queries in zero milliseconds, and guarantees that user pantry data persists across application restarts and device reboots.

---

## Core Features & Functionality
1. **Pantry Management (Full CRUD)**:
   - Create new pantry items with custom quantity, measurement units, and optional expiry dates.
   - Read and browse pantry items dynamically in a custom `RecyclerView`.
   - Update existing ingredient quantities or details.
   - Delete items with confirmation dialogs.
2. **Strict Recipe Matching Rule (Core Logic)**:
   - Recommends recipes only if 100% of required ingredients exist in the user's pantry in at least the required quantity.
   - Robust ingredient normalization handling case-insensitivity, whitespace, and common plural/singular forms (e.g., *tomato* vs *tomatoes*, *egg* vs *eggs*).
   - Dynamic UI feedback when zero recipes match the pantry.
3. **Pre-Seeded Recipe Catalog**:
   - Seeded SQLite database populated on first launch with 16 distinct household recipes.
4. **Recipe Detail View**:
   - Explicit Android `Intent` transitions passing recipe names, complete ingredient breakdowns, and step-by-step cooking instructions.

---

## Setup & Run Instructions
1. Clone this repository:
   ```bash
   git clone [https://github.com/Kgaboleteha/Smart-Pantry-Manager.git](https://github.com/Kgaboleteha/Smart-Pantry-Manager.git)
