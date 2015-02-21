package edu.uchicago.akorsos;

import static com.sun.javafx.robot.impl.FXRobotHelper.getChildren;
import edu.uchicago.akorsos.FXMLController;
import java.net.URL;
import javafx.application.Application;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

 public class MainApp extends Application {
  private final Music music;
  
  private Metadata metaData;
  private FXMLController fxmlc;
  
  public static void main(String[] args) {
    launch(args);
  }

  public MainApp() {
    music = new Music();
  }

  @Override
  public void start(Stage primaryStage) {
    metaData = new Metadata(music);
    fxmlc = new FXMLController(music);
    
    final BorderPane root = new BorderPane();
    root.setCenter(metaData.getViewNode());
    root.setBottom(fxmlc.getViewNode());
    
    final Scene scene = new Scene(root, 800, 400);
    initSceneDragAndDrop(scene);

    primaryStage.setScene(scene);
    primaryStage.setTitle("HonestY 0.8");
    primaryStage.show();
  }
  
  private void initSceneDragAndDrop(Scene scene) {
    scene.setOnDragOver(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles() || db.hasUrl()) {
          event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
      }
    });

    scene.setOnDragDropped(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        String url = null;
        
        if (db.hasFiles()) {
          url = db.getFiles().get(0).toURI().toString();
        } else if (db.hasUrl()) {
          url = db.getUrl();
        }
        
        if (url != null) {
          music.setURL(url);
          music.getMediaPlayer().play();
        }
        
        event.setDropCompleted(url != null);
        event.consume();
      }
    });
  }
}