# AndroidMusicApp-
Developping application project

Subjects N°5 : MusicApp


Arthur MIMOUNI, mail : arthur.mimouni@gmail.com


--------------------------------------------------------------------------------
DESCRIPTION 

The application is a music application in which we can listen to the music located in the internal memory of our phone. It is also possible to create several playlists.

---------------------------------------------------------------------------------
ACTIVITES

The app contains three activities:

1) ListMusicActivity (main) -> Lists all the music of the phone's internal memory.

2) MusicPlayerActivity -> Activity in which we listen the music. You can adjust the volume, the music search bar. You can also skip to the previous or next music.

3) PlaylistActivity -> Creation of playlists and addition of music to them.

---------------------------------------------------------------------------------
INTENTS

1) ListMusicActivity to MusicPlayerActivity: We store in the intent the sound we want to listen, the position of the sound in the listView
and the ArrayList of sounds of the internal memory or of the playlist.

2) MusicPlayerActivity to PlaylistActivity: We store in the intent the sound we listen to and the starting activity in which we click on the playlist logo (1).

Example for (1) : From MusicPlayerActivity to PlaylistActivity, the starting activity is "MusicPlayerActivity" while from ListMusicActivity to PlaylistActivity it will be "ListMusicActivity".

3) ListMusicActivity to PlaylistActivity: We store in the intent the starting activity in which we click on the playlist logo.

Note : When you click on the "Playlist" logo in the ListMusicActivity, you arrive in the PlaylistActivity in which you can choose / create your playlist
and activate the musics of these. When you click on the "Playlist" logo in the MusicPlayerActivity, you arrive in the PlaylistActivity
in which you can only save the music in one of the playlists (or create one).

----------------------------------------------------------------------------------
THREAD

A Thread is used to manage the progress of the music search bar. We calculate the progress in a new thread and we display the result graphically in
the Thread UI.

-----------------------------------------------------------------------------------
SENSOR

The sensor light is used to manage the music. When the music is finished, to move on to the next one there must be a change of light in the room.
