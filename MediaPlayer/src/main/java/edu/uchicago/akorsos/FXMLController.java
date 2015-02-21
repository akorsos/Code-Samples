package edu.uchicago.akorsos;

import edu.uchicago.akorsos.Music;
import java.io.BufferedReader;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.swing.JFileChooser;

class FXMLController extends AbstractMusic {

    private StatusListener statusListener;
    private CurrentTimeListener currentTimeListener;

    private Node controlPanel;
    private Label statusLabel;
    private Label currentTimeLabel;
    private Label totalDurationLabel;
    private Slider volumeSlider;
    private Slider positionSlider;

    public FXMLController(Music music) {
        super(music);

        music.mediaPlayerProperty().addListener(new MediaPlayerListener());

        statusListener = new StatusListener();
        currentTimeListener = new CurrentTimeListener();
    }

    @Override
    protected Node initView() {
        final Button openButton = createOpenButton();
        controlPanel = createControlPanel();
        volumeSlider = createSlider("volumeSlider");
        statusLabel = createLabel("Buffering", "statusDisplay");
        positionSlider = createSlider("positionSlider");
        totalDurationLabel = createLabel("00:00", "mediaText");
        currentTimeLabel = createLabel("00:00", "mediaText");

        positionSlider.valueChangingProperty().addListener(new PositionListener());

        final GridPane gp = new GridPane();
        gp.setHgap(1);
        gp.setVgap(1);
        gp.setPadding(new Insets(10));

        final ColumnConstraints buttonCol = new ColumnConstraints(100);
        final ColumnConstraints spacerCol = new ColumnConstraints(40, 80, 80);
        final ColumnConstraints middleCol = new ColumnConstraints();
        middleCol.setHgrow(Priority.ALWAYS);

        gp.getColumnConstraints().addAll(buttonCol, spacerCol, middleCol,
                spacerCol, buttonCol);

        GridPane.setValignment(openButton, VPos.BOTTOM);
        GridPane.setValignment(volumeSlider, VPos.TOP);
        GridPane.setHalignment(statusLabel, HPos.RIGHT);
        GridPane.setValignment(statusLabel, VPos.TOP);
        GridPane.setHalignment(currentTimeLabel, HPos.RIGHT);

        gp.add(openButton, 0, 0, 1, 3);
        gp.add(volumeSlider, 1, 1);
        gp.add(controlPanel, 2, 0, 1, 2);
        gp.add(statusLabel, 3, 1);
        gp.add(currentTimeLabel, 1, 2);
        gp.add(positionSlider, 2, 2);
        gp.add(totalDurationLabel, 3, 2);

        return gp;
    }

    private Button createOpenButton() {
        final Button openButton = new Button();
        openButton.setId("openButton");
        openButton.setText("Open File");
        openButton.setOnAction(new OpenHandler());
        openButton.setPrefWidth(100);
        openButton.setPrefHeight(30);
        return openButton;
    }

    private Node createControlPanel() {
        final HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setFillHeight(false);

        final Button playPauseButton = createPlayPauseButton();

        hbox.getChildren().addAll(playPauseButton);
        return hbox;
    }

    private Button createPlayPauseButton() {
        final Button playPauseButton = new Button(null);
        playPauseButton.setId("playPauseButton");
        playPauseButton.setText("Play/Pause");
        playPauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                final MediaPlayer mediaPlayer = music.getMediaPlayer();
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.play();
                }
            }
        });
        return playPauseButton;
    }

    private Slider createSlider(String id) {
        final Slider slider = new Slider(0.0, 1.0, 0.1);
        slider.setId(id);
        slider.setValue(0);
        return slider;
    }

    private Label createLabel(String text, String styleClass) {
        final Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private void addListenersAndBindings(final MediaPlayer mp) {
        mp.statusProperty().addListener(statusListener);
        mp.currentTimeProperty().addListener(currentTimeListener);
        mp.totalDurationProperty().addListener(new TotalDurationListener());

        volumeSlider.valueProperty().bindBidirectional(mp.volumeProperty());
    }

    private void removeListenersAndBindings(MediaPlayer mp) {
        volumeSlider.valueProperty().unbind();
        mp.statusProperty().removeListener(statusListener);
        mp.currentTimeProperty().removeListener(currentTimeListener);
    }

    private void seekAndUpdatePosition(Duration duration) {
        final MediaPlayer mediaPlayer = music.getMediaPlayer();

        if (mediaPlayer.getStatus() == Status.STOPPED) {
            mediaPlayer.pause();
        }

        mediaPlayer.seek(duration);

        if (mediaPlayer.getStatus() != Status.PLAYING) {
            updatePositionSlider(duration);
        }
    }

    private String formatDuration(Duration duration) {
        double millis = duration.toMillis();
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) (millis / (1000 * 60));
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void updateStatus(Status newStatus) {
        if (newStatus == Status.UNKNOWN || newStatus == null) {
            controlPanel.setDisable(true);
            positionSlider.setDisable(true);
        } else {
            controlPanel.setDisable(false);
            positionSlider.setDisable(false);

            statusLabel.setText(newStatus.toString());
        }
    }

    private void updatePositionSlider(Duration currentTime) {
        if (positionSlider.isValueChanging()) {
            return;
        }

        final MediaPlayer mediaPlayer = music.getMediaPlayer();
        final Duration total = mediaPlayer.getTotalDuration();

        if (total == null || currentTime == null) {
            positionSlider.setValue(0);
        } else {
            positionSlider.setValue(currentTime.toMillis() / total.toMillis());
        }
    }

    private class MediaPlayerListener implements ChangeListener<MediaPlayer> {

        @Override
        public void changed(ObservableValue<? extends MediaPlayer> observable,
                MediaPlayer oldValue, MediaPlayer newValue) {
            if (oldValue != null) {
                removeListenersAndBindings(oldValue);
            }
            addListenersAndBindings(newValue);
        }
    }

    private class OpenHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            FileChooser fc = new FileChooser();
            fc.setTitle("Pick a Sound File");
            File song = fc.showOpenDialog(viewNode.getScene().getWindow());
            if (song != null) {
                music.setURL(song.toURI().toString());
                music.getMediaPlayer().play();
            }
        }
    }

    private class StatusListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    updateStatus(music.getMediaPlayer().getStatus());
                }
            });
        }
    }

    private class CurrentTimeListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    final MediaPlayer mediaPlayer = music.getMediaPlayer();
                    final Duration currentTime = mediaPlayer.getCurrentTime();
                    currentTimeLabel.setText(formatDuration(currentTime));
                    updatePositionSlider(currentTime);
                }
            });
        }
    }

    private class TotalDurationListener implements InvalidationListener {

        @Override
        public void invalidated(Observable observable) {
            final MediaPlayer mediaPlayer = music.getMediaPlayer();
            final Duration totalDuration = mediaPlayer.getTotalDuration();
            totalDurationLabel.setText(formatDuration(totalDuration));
        }
    }

    private class PositionListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable,
                Boolean oldValue, Boolean newValue) {
            if (oldValue && !newValue) {
                double pos = positionSlider.getValue();
                final MediaPlayer mediaPlayer = music.getMediaPlayer();
                final Duration seekTo = mediaPlayer.getTotalDuration().multiply(pos);
                seekAndUpdatePosition(seekTo);
            }
        }
    }
}
