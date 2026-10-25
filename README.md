# CleanStreak — Daily Clean-Day Tracker

A minimal Android app for tracking the things you want to stay clean from.
Tap a color block each day you're clean — the block lights up, your streak grows.
Your habits live on your home screen too: the widget rows are tappable, so you
never even need to open the app.

Built for Samsung Galaxy S24 Ultra (Android 14+), works on any modern Android
device (minSdk 26 = Android 8.0+).

## What it does

- **Tap-to-light blocks** — one card per habit. Tap = today's block lights up (clean).
  Tap again = undo.
- **Streaks** — current streak, best streak and total clean days per habit.
  Today being "pending" (not yet tapped) never breaks a streak; only a missed
  past day does.
- **Home-screen widget** — 4x2 resizable widget showing every habit with its
  color. Tap a row right on the home screen to toggle today. Stays in perfect
  sync with the app (same database), and refreshes itself across midnight.
- **Private by design** — all data stays in a local Room database. No network
  permission, no accounts, no analytics, nothing leaves the phone.

## Getting the app on your phone

### Option A — let GitHub build it for you (recommended, no tools needed)

1. Create a GitHub repository and push this folder (or upload it) to it.
2. Open the repo on github.com → **Actions** tab → workflow **Build APK** → run it
   (it also runs automatically on every push to main/master).
3. When it finishes, download the **CleanStreak-debug-apk** artifact,
   unzip it to get `app-debug.apk`.
4. Copy `app-debug.apk` to your S24 Ultra (e.g. via Samsung Quick Share, or
   upload to Google Drive and download on the phone).
5. On the phone, open the APK (tap it in My Files / Downloads). Android will
   ask to allow installs from that source — allow it, then **Install**.

### Option B — build locally with Android Studio

1. Install Android Studio (Ladybug or newer recommended).
2. **File → Open** → select this `CleanStreak` folder. Let Gradle sync finish
   (it downloads the Android SDK and dependencies on first run).
3. Connect your S24 Ultra with USB debugging enabled (Settings → About phone →
   Software information → tap Build number 7×; then Settings → Developer
   options → USB debugging).
4. Press **Run** ▶ — the app installs and launches on the phone.

Using the command line instead? Run `gradle wrapper --gradle-version 8.9` once
to generate the `gradlew` scripts (the repo ships the wrapper *configuration*,
not the wrapper jar), then `./gradlew assembleDebug` — the APK lands in
`app/build/outputs/apk/debug/`.

### Add the widget (Samsung One UI)

1. Long-press an empty area of the home screen.
2. Tap **Widgets**.
3. Find **CleanStreak** → long-press the widget → **Add** (or drag it out).
4. The widget shows all habits; tap any row to mark today clean. Resize it
   long-press → drag the frame handles if you like.

## Project structure

```
CleanStreak/
├── app/src/main/java/com/cleanstreak/app/
│   ├── CleanStreakApplication.kt      # Shared repository owner + worker scheduling
│   ├── MainActivity.kt                # Compose host + Material You theming
│   ├── MainViewModel.kt               # Reactive state; syncs widget on change
│   ├── data/
│   │   ├── Habit.kt                   # Room entities, DAO, database
│   │   ├── HabitRepository.kt         # add/delete/toggle-today logic
│   │   ├── HabitWithStatus.kt         # UI-facing model (app + widget shared)
│   │   ├── StreakCalculator.kt        # Pure streak math
│   │   └── HabitColor.kt              # Shared palette
│   ├── ui/
│   │   ├── CleanStreakApp.kt          # Main screen, add/delete dialogs
│   │   └── HabitCard.kt               # Tap-to-light card
│   └── widget/
│       ├── CleanStreakWidget.kt       # Glance widget (tap-to-toggle rows)
│       ├── WidgetColors.kt            # Light/dark theme-aware widget colors
│       ├── ToggleAction.kt            # Widget tap handler
│       └── WidgetSyncWorker.kt        # Midnight refresh worker
├── app/src/main/res/                  # Strings, themes, widget metadata, icons
└── .github/workflows/build-apk.yml    # CI: builds the APK for you
```

## Tech stack

- **Kotlin 2.0**, **Jetpack Compose** (Material 3, dynamic color on Android 12+)
- **Room** for persistence, **KSP** for annotation processing
- **Glance 1.1** for the home-screen widget
- **WorkManager** for the midnight widget refresh
- AGP 8.6.1 / Gradle 8.9, minSdk 26, target/compile SDK 35

## Roadmap ideas (say the word)

- History calendar view (month grid of lit days)
- Reminder notifications ("haven't marked today yet")
- Export/import data (JSON backup)
- Reorder habits by drag & drop
- Play Store release signing + Play Store listing

## Notes

- Debug APKs are fine for personal use. For sharing beyond yourself, build a
  signed release (`gradle assembleRelease`) with your own keystore.
- The widget needs **Battery → Unrestricted** (or at least not Deep Sleep for
  CleanStreak) if you want the most reliable midnight refresh on One UI:
  Settings → Apps → CleanStreak → Battery → Unrestricted.
