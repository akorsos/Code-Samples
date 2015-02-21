package edu.uchicago.akorsos;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

final class Music {

    private final StringProperty album = new SimpleStringProperty(this, "album");
    private final StringProperty artist = new SimpleStringProperty(this, "artist");
    private final StringProperty title = new SimpleStringProperty(this, "title");
    private final StringProperty year = new SimpleStringProperty(this, "year");

    private final ObjectProperty<Image> albumCover
            = new SimpleObjectProperty<Image>(this, "albumCover");

    private final ObjectProperty<Image> waveform
            = new SimpleObjectProperty<Image>(this, "waveform");

    private final ReadOnlyObjectWrapper<MediaPlayer> mediaPlayer
            = new ReadOnlyObjectWrapper<MediaPlayer>(this, "mediaPlayer");

    public Music() {
        resetProperties();
    }

    public void setURL(String url) {
        if (mediaPlayer.get() != null) {
            mediaPlayer.get().stop();
        }

        initializeMedia(url);
    }

    public String getAlbum() {
        return album.get();
    }

    public void setAlbum(String value) {
        album.set(value);
    }

    public StringProperty albumProperty() {
        return album;
    }

    public String getArtist() {
        return artist.get();
    }

    public void setArtist(String value) {
        artist.set(value);
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String value) {
        title.set(value);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getYear() {
        return year.get();
    }

    public void setYear(String value) {
        year.set(value);
    }

    public StringProperty yearProperty() {
        return year;
    }

    public Image getAlbumCover() {
        return albumCover.get();
    }

    public void setAlbumCover(Image value) {
        albumCover.set(value);
    }

    public ObjectProperty<Image> albumCoverProperty() {
        return albumCover;
    }

    public ObjectProperty<Image> waveformProperty() {
        return waveform;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer.get();
    }

    public ReadOnlyObjectProperty<MediaPlayer> mediaPlayerProperty() {
        return mediaPlayer.getReadOnlyProperty();
    }

    private void resetProperties() {
        setArtist("");
        setAlbum("");
        setTitle("");
        setYear("");
    }

    private void initializeMedia(String url) {
        resetProperties();

        try {          
            
            final Media media = new Media(url);
            media.getMetadata().addListener(new MapChangeListener<String, Object>() {
                @Override
                public void onChanged(Change<? extends String, ? extends Object> ch) {
                    if (ch.wasAdded()) {
                        handleMetadata(ch.getKey(), ch.getValueAdded());
                    }
                }
            });           

            mediaPlayer.setValue(new MediaPlayer(media));
            mediaPlayer.get().setOnError(new Runnable() {
                @Override
                public void run() {
                    String errorMessage = mediaPlayer.get().getError().getMessage();
                    System.out.println("MediaPlayer Error: " + errorMessage);
                }
            });
        } catch (RuntimeException re) {
            System.out.println("Caught Exception: " + re.getMessage());
        }
    }

    private void handleMetadata(String key, Object value) {
        if (key.equals("album")) {
            setAlbum(value.toString());
        } else if (key.equals("artist")) {
            setArtist(value.toString());
        }
        if (key.equals("title")) {
            setTitle(value.toString());
        }
        if (key.equals("year")) {
            setYear(value.toString());
        }
        if (key.equals("image")) {
            setAlbumCover((Image) value);
        }
        
        
        
    }
}
