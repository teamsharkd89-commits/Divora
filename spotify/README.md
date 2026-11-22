# spotify/codes.json

Divora's "Sign in to Spotify" reads a listener's liked songs and playlists the way Spotify's web player does. Each of
those reads is named by a code, and Spotify changes a code every few months. When it does, that read stops working.

The current codes are built into Divora. This file is the spare: when Spotify refuses a built-in code, Divora looks here
(at most once every six hours) for a newer one, so a change can be fixed by editing this file instead of waiting for a
new release.

- It is data only: a read's name and its 64-character code. Divora runs nothing from it.
- Divora takes only the four reads it already knows (`profileAttributes`, `libraryV3`, `fetchLibraryTracks`,
  `fetchPlaylist`) and ignores anything else, so this file can never make Divora change something on Spotify.
- `SpotifyCodesFileTest` keeps it in step with the app.
