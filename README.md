# 🚗 RideShare — Android Ride Sharing Application

A **full-featured Android ride-sharing prototype** built with Jetpack Compose, Firebase Authentication, Google Maps, and a clean MVVM + Clean Architecture. Riders can sign in with Google, book rides, track drivers in real-time, and view trip summaries. Drivers have their own dedicated login and dashboard.

---

## 📱 Screenshots & Features

| Screen | Description |
|---|---|
| **Splash / Onboarding** | Animated splash with brand logo, rider & driver entry points |
| **Login / Signup** | Dark-themed auth screens with Google Sign-In + email/password |
| **Home Map** | Live Google Map with nearby drivers, destination picker, and search |
| **Ride Selection** | Choose ride type (Go / Comfort / XL / Green), live fare estimate |
| **Driver Tracking** | Real-time driver movement → pickup → ride → destination |
| **Trip Summary** | Star rating, fare receipt, payment method, "Book again" |
| **Ride History** | Searchable past trips stored locally in Room |
| **Saved Locations** | Save Home, Work, and custom favourite places |
| **Payment** | Cash, UPI, and Card payment flows |
| **Profile** | User info, logout |
| **Driver Dashboard** | Driver status, vehicle info, ride queue |

---

## ✨ Key Features

- 🔑 **Google Sign-In** via Firebase Authentication + Credential Manager API
- 📧 **Email / Password** login with SHA-256 hashed passwords stored in Room
- 🗺️ **Live Google Maps** with tap-to-pin destination and nearby driver markers
- 🚘 **Two-phase ride flow**: Driver arrives at pickup → Rider taps Start → Driver heads to destination
- ⭐ **Post-trip rating** with animated star selector and fare receipt
- ❌ **Cancel ride** before start with driver position reset
- 💾 **Offline-first**: All rides, users, drivers, and saved locations stored in Room DB
- 🔄 **Session persistence** via DataStore — stays logged in across app restarts
- 🧭 **Type-safe Navigation** with Compose Navigation
- 💉 **Dependency Injection** with Hilt
- 🎨 **Premium dark UI** — custom dark palette, green accent, glassmorphism cards

---

## 🏗️ Architecture

```
RideSharingApplication/
├── app/src/main/java/com/example/ridesharingapplication/
│   ├── data/
│   │   ├── local/
│   │   │   ├── dao/           # Room DAOs (UserDao, RideDao, DriverDao, SavedLocationDao)
│   │   │   ├── datastore/     # SessionManager (DataStore Preferences)
│   │   │   ├── entity/        # Room entities (UserEntity, RideEntity, DriverEntity, SavedLocationEntity)
│   │   │   └── RideShareDatabase.kt
│   │   └── repository/        # AuthRepositoryImpl, RideRepositoryImpl
│   ├── di/
│   │   └── AppModule.kt       # Hilt modules (Database, Repositories)
│   ├── domain/
│   │   ├── model/             # AuthResult, DriverProfile, RideType
│   │   ├── repository/        # AuthRepository, RideRepository (interfaces)
│   │   └── usecase/
│   ├── presentation/
│   │   ├── components/        # GlassCard, AppTextField, PrimaryAction, ShimmerBar
│   │   ├── navigation/        # AppNavGraph, AppRoutes
│   │   ├── screens/           # AuthScreens.kt, RideScreens.kt
│   │   └── viewmodel/         # AuthViewModel, RideViewModel
│   └── utils/                 # PasswordHasher
└── app/
    └── google-services.json   # Firebase config (already included)
```

### Pattern: MVVM + Clean Architecture
```
UI (Compose Screens)
    ↕  collectAsStateWithLifecycle()
ViewModel (state holders, business logic)
    ↕  suspend functions / Flow
Repository (interface)
    ↕
RepositoryImpl (Room + Firebase + DataStore)
```

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Kotlin | 2.0.21 |
| UI | Jetpack Compose + Material 3 | BOM 2024.09.00 |
| Architecture | MVVM + Clean Architecture | — |
| DI | Hilt | 2.57.2 |
| Navigation | Compose Navigation | 2.9.6 |
| Maps | Google Maps Compose | 4.4.1 |
| Location | Google Play Services Location | 21.3.0 |
| Auth | Firebase Authentication + Credential Manager | BOM 33.13.0 |
| Database | Room | 2.8.4 |
| Session | DataStore Preferences | 1.2.0 |
| Image Loading | Coil | 2.7.0 |
| Networking | Retrofit + OkHttp | 2.11.0 / 4.12.0 |
| Build | AGP | 8.13.2 |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio **Hedgehog** or newer
- JDK 11+
- A Google account with access to [Firebase Console](https://console.firebase.google.com)
- A Google Maps API Key from [Google Cloud Console](https://console.cloud.google.com)

---

### 1. Clone the repository

```bash
git clone https://github.com/your-username/RideSharingApplication.git
cd RideSharingApplication
```

---

### 2. Add your Google Maps API Key

In `gradle.properties` (project root), add:

```properties
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY_HERE
```

To get a key:
1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Enable **Maps SDK for Android**
3. Create an API key and restrict it to your package name: `com.example.ridesharingapplication`

---

### 3. Firebase Setup

The `google-services.json` is already present at `app/google-services.json`.

**Firebase Project:** `ridesharingapplication-ac9b0`
**Package Name:** `com.example.ridesharingapplication`

In [Firebase Console](https://console.firebase.google.com/project/ridesharingapplication-ac9b0):
- Go to **Authentication → Sign-in method**
- Enable **Google** provider
- Enable **Email/Password** provider (for driver login)

---

### 4. ⚠️ Add SHA Fingerprints (Required for Google Sign-In)

Google Sign-In **will not work** without adding your debug SHA-1 and SHA-256 fingerprints to Firebase. This is the most common setup step people miss.

#### Step 1 — Get your Debug SHA fingerprints

Open a terminal in the project root and run:

**On Windows (PowerShell):**
```powershell
.\gradlew signingReport
```

**On macOS/Linux:**
```bash
./gradlew signingReport
```

Look for the `debug` variant output. It will look like:

```
Variant: debug
Config: debug
Store: C:\Users\YourName\.android\debug.keystore
Alias: AndroidDebugKey

MD5:  AB:CD:EF:...
SHA1: 1F:0D:69:20:60:54:0B:8A:87:28:78:E5:18:44:6B:1F:3F:AC:F0:ED
SHA-256: A1:B2:C3:D4:...
```

#### Step 2 — Add SHA fingerprints to Firebase

1. Open [Firebase Console](https://console.firebase.google.com/project/ridesharingapplication-ac9b0/settings/general/android:com.example.ridesharingapplication)
2. Click on your Android app: `com.example.ridesharingapplication`
3. Scroll down to **SHA certificate fingerprints**
4. Click **Add fingerprint**
5. Paste the **SHA-1** value → Save
6. Click **Add fingerprint** again
7. Paste the **SHA-256** value → Save
8. Download the updated `google-services.json` and replace `app/google-services.json`

> **Note:** The current `google-services.json` already has SHA-1 `1f0d692060540b8a872878e518446b1f3facf0ed` registered. If your debug keystore produces a different SHA-1, add it alongside the existing one — Firebase supports multiple fingerprints.

#### Step 3 — Verify SHA-1 Matches

The SHA-1 in `google-services.json` under `certificate_hash` must match your debug keystore's SHA-1. If it doesn't match, you'll get a `ApiException: 10` or `GetCredentialException` error when tapping "Continue with Google".

---

### 5. Build & Run

```bash
# Clean build
./gradlew clean assembleDebug

# Or open in Android Studio and click Run ▶
```

Minimum SDK: **API 24 (Android 7.0)**
Target SDK: **API 36**

---

## 🗺️ User Flow

```
App Launch
    └── Splash Screen (900ms)
            ├── [Logged in] → Home Map
            └── [Not logged in] → Onboarding
                    ├── Rider Login / Signup (Google or Email)
                    └── Driver Login / Signup

Home Map
    └── Select destination (search or tap map)
            └── Ride Selection (pick type, see fare)
                    └── Confirm Ride
                            └── Driver Tracking Screen
                                    ├── Phase 1: DRIVER_TO_PICKUP
                                    │       └── [Cancel Ride ❌]
                                    ├── Phase 2: ARRIVED_PICKUP → [Start Ride →]
                                    ├── Phase 3: IN_PROGRESS
                                    └── Phase 4: COMPLETED → [End Ride & Rate ⭐]
                                                    └── Trip Summary Screen
                                                            └── [Back to Home / Book Again]
```

---

## 🔐 Authentication Details

| Method | How it works |
|---|---|
| **Google Sign-In** | Uses Android `CredentialManager` API → Firebase `signInWithCredential(GoogleAuthProvider)` → User upserted into Room for session |
| **Email/Password** | User stored in Room with SHA-256 hashed password, session saved in DataStore |
| **Session** | DataStore Preferences — persists `userId` across app restarts. `remember_me` flag controls session expiry |
| **Logout** | Clears Firebase session + DataStore |

---

## 🚘 Ride Phases

| Phase | Status | Polyline Target | UI Action |
|---|---|---|---|
| `IDLE` | No active ride | — | — |
| `DRIVER_TO_PICKUP` | Driver moving toward rider | → Pickup location | Cancel Ride button |
| `ARRIVED_PICKUP` | Driver at pickup | → Pickup location | **Start Ride →** |
| `IN_PROGRESS` | En route to destination | → Destination | — |
| `COMPLETED` | Arrived | — | **End Ride & Rate ⭐** |

---

## 📁 Database Schema

### `users`
| Column | Type | Notes |
|---|---|---|
| id | LONG PK | Auto-generated |
| username | TEXT | Unique |
| email | TEXT | Unique, lowercase |
| phone | TEXT | |
| passwordHash | TEXT | SHA-256 hashed |

### `rides`
| Column | Type | Notes |
|---|---|---|
| id | LONG PK | |
| userId | LONG | Foreign key |
| pickupLocation | TEXT | |
| destination | TEXT | |
| fare | DOUBLE | Calculated |
| status | TEXT | Accepted / Completed / Cancelled |
| rideDate | LONG | Timestamp |

### `drivers`
| Column | Type | Notes |
|---|---|---|
| id | LONG PK | |
| name | TEXT | |
| vehicle | TEXT | |
| plateNumber | TEXT | |
| latitude | DOUBLE | |
| longitude | DOUBLE | |
| rating | DOUBLE | |

### `saved_locations`
| Column | Type | Notes |
|---|---|---|
| id | LONG PK | |
| userId | LONG | |
| label | TEXT | e.g. "Home", "Work" |
| address | TEXT | |
| latitude / longitude | DOUBLE | |
| type | TEXT | Home / Work / Favorite |

---

## 📦 Project Dependencies Summary

```toml
# Core
androidx-core-ktx         = "1.18.0"
lifecycle                 = "2.10.0"
activity-compose          = "1.13.0"
compose-bom               = "2024.09.00"

# Navigation & DI
navigation-compose        = "2.9.6"
hilt                      = "2.57.2"
hilt-navigation-compose   = "1.3.0"

# Maps & Location
maps-compose              = "4.4.1"
play-services-maps        = "19.2.0"
play-services-location    = "21.3.0"
places                    = "5.0.0"

# Firebase
firebase-bom              = "33.13.0"   # firebase-auth included
credentials               = "1.5.0"     # CredentialManager
googleid                  = "1.1.1"     # GoogleIdTokenCredential

# Persistence
room                      = "2.8.4"
datastore                 = "1.2.0"

# Networking
retrofit                  = "2.11.0"
okhttp                    = "4.12.0"

# Image
coil                      = "2.7.0"
```

---

## 🧪 Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

This project is for educational and prototype purposes.

---

## 👤 Author

**Gowthami** — RideSharingApplication
Firebase Project: `ridesharingapplication-ac9b0`
