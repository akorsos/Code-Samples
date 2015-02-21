package edu.uchicago.akorsos;

import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

class Metadata extends AbstractMusic {

    public Metadata(Music music) {
        super(music);
    }

    @Override
    protected Node initView() {
        final Label title = createLabel("title");
        final Label artist = createLabel("artist");
        final Label album = createLabel("album");
        final Label year = createLabel("year");
        final ImageView albumCover = createAlbumCover();
        final ImageView waveform = createWaveform();

        title.textProperty().bind(music.titleProperty());
        artist.textProperty().bind(music.artistProperty());
        album.textProperty().bind(music.albumProperty());
        year.textProperty().bind(music.yearProperty());
        albumCover.imageProperty().bind(music.albumCoverProperty());
        waveform.imageProperty().bind(music.waveformProperty());

        final GridPane gp = new GridPane();
        gp.setPadding(new Insets(10));
        gp.setHgap(20);
        gp.add(albumCover, 0, 0, 1, GridPane.REMAINING);
        gp.add(waveform, 0, 0, 2, GridPane.REMAINING);
        gp.add(title, 1, 0);
        gp.add(artist, 1, 1);
        gp.add(album, 1, 2);
        gp.add(year, 1, 3);

        final ColumnConstraints c0 = new ColumnConstraints();
        final ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.ALWAYS);
        gp.getColumnConstraints().addAll(c0, c1);

        final RowConstraints r0 = new RowConstraints();
        r0.setValignment(VPos.TOP);
        gp.getRowConstraints().addAll(r0, r0, r0, r0);

        return gp;
    }

    private Label createLabel(String id) {
        return LabelBuilder.create().id(id).build();
    }

    private ImageView createAlbumCover() {
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.2);

        final ImageView albumCover = new ImageView();
        albumCover.setFitWidth(240);
        albumCover.setPreserveRatio(true);
        albumCover.setSmooth(true);
        albumCover.setEffect(reflection);

        return albumCover;
    }

    private ImageView createWaveform() {
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.2);

        final ImageView waveform = new ImageView();
        waveform.setFitWidth(240);
        waveform.setPreserveRatio(true);
        waveform.setSmooth(true);
        waveform.setEffect(reflection);

        return waveform;
    }
}
