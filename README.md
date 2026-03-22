# Bitsongs Android

Android port of the Bitsongs music player. Streams real music from the internet with search, recommendations, and background playback.

## Features

- Search any song or artist (iTunes API)
- Trending charts on launch  
- Real audio streaming via YouTube
- Full playback controls
- Album artwork with dynamic theming
- Background playback
- Lock screen controls
- Queue management
- Lyrics support
- Haptic feedback

## Project Structure

```
BitsongsAndroid/
├── app/src/main/java/com/jayesh/cmd/bitsongs/
│   ├── models/          # Data models
│   ├── viewmodels/      # ViewModels
│   ├── views/           # Compose UI
│   ├── services/        # Network, player services
│   └── utils/           # Utilities
├── app/src/main/res/    # Resources
└── app/build.gradle     # Dependencies
```

## Setup

### Backend Server

The backend is a Python FastAPI server that handles:
- iTunes API search
- YouTube audio streaming
- Lyrics fetching
- Recommendations

```bash
cd Server
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python app.py
```

### Android App

1. Open `BitsongsAndroid` in Android Studio
2. Update the server URL in `NetworkService.kt`:
   - Emulator: `http://10.0.2.2:499`
   - Physical device: `http://YOUR_COMPUTER_IP:499`
3. Build and run

## Architecture

### System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Android App (Kotlin/Compose)             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  UI Layer (Compose)                                 │  │
│  │  • MusicPlayerScreen.kt                             │  │
│  │  • SearchScreen.kt                                  │  │
│  │  • SongItem.kt                                      │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  ViewModel Layer                                    │  │
│  │  • MusicPlayerViewModel.kt                          │  │
│  │  • Handles state, playback logic, API calls         │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Service Layer                                      │  │
│  │  • NetworkService.kt (Retrofit)                     │  │
│  │  • MusicPlayerService.kt (ExoPlayer + Foreground)   │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Model Layer                                        │  │
│  │  • Song.kt                                          │  │
│  │  • StreamInfo.kt                                    │  │
│  │  • LyricsResponse.kt                                │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼ HTTP/JSON
┌─────────────────────────────────────────────────────────────┐
│                FastAPI Backend Server (Python)              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  API Layer (app.py)                                 │  │
│  │  • /api/mobile/search                               │  │
│  │  • /api/mobile/play                                 │  │
│  │  • /api/mobile/lyrics                               │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  External Services                                  │  │
│  │  • iTunes API (search/metadata)                     │  │
│  │  • YouTube (yt-dlp for audio streaming)             │  │
│  │  • LRCLIB (lyrics)                                  │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Recommendation Engine                              │  │
│  │  • behavior.py (user listening patterns)            │  │
│  │  • content.py (song similarity)                     │  │
│  │  • storage.py (tally_counter.json)                  │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Cache System                                       │  │
│  │  • song_cache/ (audio files)                        │  │
│  │  • Auto-clears at 600MB                             │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Android App Architecture

**UI Layer (Compose)**
- `MusicPlayerScreen`: Main player interface with controls, progress, artwork
- `SearchScreen`: Search functionality with debounced API calls  
- `SongItem`: Reusable component for song lists
- Uses Material 3 theming with dynamic colors from album artwork

**ViewModel Layer**
- `MusicPlayerViewModel`: Central state management
- Handles playback logic, API communication, user preferences
- Manages song queue, recommendations, lyrics
- Persists recent songs and last played track using SharedPreferences

**Service Layer**
- `NetworkService`: Retrofit-based API client for backend communication
- `MusicPlayerService`: Foreground service with ExoPlayer for audio playback
- Background playback with lock screen controls via MediaSession
- Notification with playback controls

**Model Layer**
- `Song`: Core data model matching iOS version
- `StreamInfo`: Contains audio stream URL and cache status
- `LyricsResponse`: Lyrics data with sync status
- `Recommendations`: Behavior-based and content-based song suggestions

### Backend Architecture

**API Routes (`app.py`)**
- `/api/mobile/search`: Queries iTunes API, returns formatted song data
- `/api/mobile/play`: Gets YouTube stream URL, tracks song transitions for recommendations
- `/api/mobile/lyrics`: Fetches lyrics from LRCLIB API
- `/api/mobile/recommend`: Combines behavior and content recommendations
- `/api/mobile/up_next`: Queue suggestions based on current song

**Recommendation System**
- **Behavior-based**: Tracks user listening patterns in `tally_counter.json`
  - Records which songs users play after each other
  - Auto-cleans data (max 50 songs, 3 transitions per song, 7-day decay)
- **Content-based**: Uses song metadata from `songs.json`
  - Matches by genre, artist, similar characteristics
- **Up Next**: Combines both strategies for queue suggestions

**Audio Streaming**
- Uses `yt-dlp` to extract audio from YouTube
- Streams directly or serves cached files
- Caches audio in `song_cache/` directory
- Auto-clears cache when exceeding 600MB

**Data Flow**
1. User searches → iTunes API → Formatted song list → Android UI
2. User plays song → YouTube audio extraction → Stream URL → ExoPlayer
3. Playback triggers → Transition tracking → Recommendation updates
4. Background playback → Foreground service → MediaSession → Lock screen controls

### Key Technical Decisions

**Why ExoPlayer?**
- Android's recommended media player
- Supports streaming, background playback, lock screen controls
- Better battery optimization than custom solutions

**Why Foreground Service?**
- Required for reliable background playback on Android 8+
- Provides persistent notification with playback controls
- Ensures audio continues when app is in background

**Why Retrofit?**
- Standard HTTP client for Android
- Type-safe API calls with Kotlin coroutines support
- Built-in JSON parsing with Gson

**Why Compose?**
- Modern Android UI toolkit
- Declarative, reactive programming model
- Better performance than XML-based layouts
- Seamless integration with ViewModel

## API Endpoints

| Endpoint | Description |
|----------|-------------|
| `/api/mobile/health` | Server health check |
| `/api/mobile/search?q=` | Search songs |
| `/api/mobile/chart` | Trending songs |
| `/api/mobile/play` | Get stream URL |
| `/api/mobile/lyrics` | Get lyrics |
| `/api/mobile/recommend` | Get recommendations |
| `/api/mobile/up_next` | Up Next suggestions |

## Notes

- Requires ffmpeg installed on the server machine
- Server caches audio files (clears at 600MB)
- Recommendations are behavior-based and content-based
- Lyrics from LRCLIB API

## License

Personal project.