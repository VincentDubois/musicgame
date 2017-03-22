package fr.univartois.iutlens.mmi.web2.musicgame;
        import android.content.Context;
        import android.content.res.AssetFileDescriptor;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.net.Uri;

public class AudioPlayer implements AudioManager.OnAudioFocusChangeListener {

    private static AudioPlayer audioPlayer; // On souhaite un seul audioPlayer pour l'application, quoi qu'il arrive

    private Context context;
    private MediaPlayer mediaPlayer;
    private Uri lastUri;
    private MediaPlayer.OnCompletionListener onCompletionlistener;

    public void setOnCompletionlistener(MediaPlayer.OnCompletionListener onCompletionlistener) {
        this.onCompletionlistener = onCompletionlistener;
        if (mediaPlayer != null) mediaPlayer.setOnCompletionListener(onCompletionlistener);
    }

    public static AudioPlayer get(Context context){
        if (audioPlayer == null) {
            audioPlayer = new AudioPlayer(context);
        }

        // On demande à gérer les évènements liés au son
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(audioPlayer, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            release();
            // could not get audio focus.
        }

        return audioPlayer;
    }

    public static void release(){
        if (audioPlayer == null) return;
        if (audioPlayer.mediaPlayer == null) return; // déjà libéré

        audioPlayer.mediaPlayer.release(); // libère le mediaplayer
        audioPlayer.mediaPlayer = null;  // Précise que le mediaplayer a été libéré
    }

    private AudioPlayer(Context context) {
        this.context = context;
    }

    public void loadById(Context context, int id){
        if (mediaPlayer != null) {
            // Si on joue déjà qq chose
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop(); // On abandonne le morceau en cours
            }
            mediaPlayer.release(); // On libère l'ancien
        }
        // On sélectionne le nouveau morceau
        mediaPlayer = MediaPlayer.create(context, id);
        if (onCompletionlistener != null)
            this.mediaPlayer.setOnCompletionListener(onCompletionlistener);

    }

    public void loadUri(Context context, Uri uri) {
        if (uri != null) {
            this.context = context;
            this.lastUri = uri;
        }
        load();
    }

    private void load() {
        if (lastUri == null) return;
        if (mediaPlayer != null) {
            // Si on joue déjà qq chose
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop(); // On abandonne le morceau en cours
            }
            mediaPlayer.release(); // On libère l'ancien
        }
        // On sélectionne le nouveau morceau
        mediaPlayer = MediaPlayer.create(context, lastUri);
        if (onCompletionlistener != null)
            this.mediaPlayer.setOnCompletionListener(onCompletionlistener);
    }

    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) load(); // Si le mediaPlayer n'est pas configuré, on le configure
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start(); // Sinon on reprend où on en était
                mediaPlayer.setVolume(1.0f, 1.0f); // On remet le volume à 100%
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer == null) break; // Déjà libéré, ne rien faire
                if (mediaPlayer.isPlaying()) mediaPlayer.stop(); // On arrête la musique en cours
                release();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer == null) break;
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer == null) break;
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    public void play() {
        if (mediaPlayer == null) load();
        if (mediaPlayer != null) mediaPlayer.start();
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer=null;
    }

    public void pause() {
        mediaPlayer.pause();
    }
}