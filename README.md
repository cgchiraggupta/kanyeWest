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

- **Kotlin/Compose** for UI
- **ExoPlayer** for audio streaming  
- **Retrofit** for network calls
- **ViewModel** for state management
- **Foreground Service** for background playback
- **MediaSession** for lock screen controls

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