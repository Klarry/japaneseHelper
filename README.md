# Image Search

Japanese Helper shows a picture next to every JLPT vocabulary card so learners get a visual hint alongside the word, its reading, and its meaning. The picture is fetched from our own backend rather than bundled with the app.

## Overview

For each random word the app receives, it asks a backend service for a matching image and displays it in the vocabulary card once it arrives. The Android app never talks to an image-search or LLM provider directly — it only calls our backend, which returns the picture as raw JPEG bytes.

## Architecture

```
Android
  → Image Search Backend (FastAPI, VPS)
      → Gemini + Google Image Search tool
      → JPEG bytes
  → Android (ResponseBody)
  → ImageWithProgress / RandomWordCard
```

1. Android gets a random JLPT word from `VocabRepository`.
2. It sends the word's `meaning` as the search query to our backend.
3. The backend uses Gemini with a Google Image Search tool to find a matching image and returns it as raw JPEG bytes.
4. Android reads the bytes and renders them in the existing card UI.

## Request / Response

```
POST /image-search
Content-Type: application/json

{
  "query": "book, volume, this, present"
}
```

```
Response:
Content-Type: image/jpeg

<raw JPEG bytes>
```

## Android implementation

- **API**: `ImageSearchApi` (`data/remote/api/ImageSearchApi.kt`) — a Retrofit interface with a single `@POST("image-search")` method that takes an `ImageSearchRequestDto` (`data/remote/dto/ImageSearchRequestDto.kt`) and returns a raw `okhttp3.ResponseBody` (no JSON converter is applied to the response, since it's binary).
- **Networking**: `ImageSearchNetworkModule` (`di/ImageSearchNetworkModule.kt`) provides a dedicated Retrofit/OkHttp client pointed at the backend's base URL, with connect/read/write/call timeouts sized for the backend's search latency.
- **Repository**: `ImageSearchRepositoryImpl` (`data/repository/ImageSearchRepositoryImpl.kt`), behind the `ImageSearchRepository` interface (`domain/repository/ImageSearchRepository.kt`), calls the API and reads the response body's bytes on `Dispatchers.IO`, wrapping them in the domain model `SearchResult` (`domain/model/SearchResult.kt`).
- **ViewModel**: `VocabViewModel.getPictureData()` (`presentation/viewmodel/VocabViewModel.kt`) uses the current word's `meaning` as the query, calls the repository, and publishes the result as `PictureState` (`presentation/viewmodel/screendata/PictureState.kt`): `PictureLoading`, `PictureSuccess(imageBytes)`, `PictureError`, or `PictureLimitExceeded`.
- **UI**: `RandomWordCard` (`presentation/screens/homeScreen/components/RandomWordCard.kt`) observes `homeState` and passes the current `PictureState` to `ImageWithProgress` (`presentation/screens/homeScreen/components/ImageWithProgress.kt`), which loads `PictureSuccess.imageBytes` directly as a Coil `ImageRequest` model and shows a progress indicator or a placeholder for the other states.

## Backend

The backend is a FastAPI service running on our own VPS (`http://89.167.34.196:8000/`). It exposes `POST /image-search`, accepts `{"query": "..."}`, and returns the resulting image as raw JPEG bytes with `Content-Type: image/jpeg`. Beyond that contract, its internal implementation isn't part of this Android repository and isn't described further here.

## Logging / Demo

A temporary debug log (tag `ImageSearch`) traces each request for demo purposes:

```
ImageSearch  ─────────────────────────────
→ Searching image for: 本
→ Query: "book, volume, this, present"
→ POST /image-search
← 200 OK
← image/jpeg • 645 KB
──────────────────────────────────────────
```

It shows the Android → backend → image response round trip (word, query, endpoint, response code, content type, and human-readable image size) without ever logging the raw JPEG bytes or the `ResponseBody` itself.

## Demo

1. Open the app.
2. Get a JLPT word.
3. Show the image search happening.
4. Show the Logcat trace.
5. Show the resulting image in the card.

## Security

The Gemini API key lives on the backend only. The Android client never holds or sends any Gemini credentials — it only calls our own `/image-search` endpoint.

## Result

The app fetches a matching image for each JLPT word from our own backend, which handles the actual image search, and displays it in the existing card UI without any change to the app's design or architecture.
