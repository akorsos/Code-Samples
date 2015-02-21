package edu.uchicago.ak;

import com.google.gson.Gson;
import edu.uchicago.ak.HttpDownUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import static javafx.concurrent.Worker.State.CANCELLED;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.READY;
import static javafx.concurrent.Worker.State.RUNNING;
import static javafx.concurrent.Worker.State.SCHEDULED;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Controller implements Initializable {
    
    @FXML
    private TextField txtSearch;
    
    @FXML
    private TextField savePath;
    
    @FXML
    private Button btnGo;
    
    @FXML
    private CheckBox searchBox1;
    
    @FXML
    private CheckBox searchBox2;
    
    @FXML
    private ListView<String> lstView;
    @FXML
    private Label lblStatus;
    
    @FXML
    private Label lblTitle;
    
    @FXML
    private TableView<DownTask> table;
    
    private String strDirSave;
    
    @FXML
    private Button btnSelect;
    
    int relevanceInt = 0;
    
    @FXML
    private void btnSelect_go(ActionEvent event) {
        
        System.out.println("Go pressed");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        
        directoryChooser.setTitle("This is my file ch");
        
        //Show open file dialog
        File file = directoryChooser.showDialog(null);
        
        if (file != null) {
            
            btnSelect.setText(file.getPath());
            strDirSave = file.getAbsolutePath();
            
        }
        
    }
    
    public class GetReviewsTask extends Task<ObservableList<String>> {
        
        private String convertSpacesToPluses(String strOrig) {
            return strOrig.trim().replace(" ", "+");
        }
        
        @Override
        protected ObservableList<String> call() throws Exception {
            
            ObservableList<String> sales = FXCollections.observableArrayList();
            updateMessage("Searching for PDFs about " + txtSearch.getText());
            
            Document doc;
            ArrayList<String> strResults;
            strResults = new ArrayList<String>();
            
            if (searchBox1.isSelected()) {
                System.out.println("searchBox1 Selected");
                String sourceAbbrv = "ggl";
                String strUrl = "https://www.google.com/search?q=";
                strUrl += convertSpacesToPluses(txtSearch.getText());
                strUrl += "+filetype:pdf&num=50";
                
                //this just simulates some work, but since it  and should be in background thread
                try {
                    doc = Jsoup.connect(strUrl).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
                    Elements hrefs = doc.select("a[href]");
                    for (Element href : hrefs) {
                        
                        String strRef = href.attr("abs:href");
                        if (strRef.contains(".pdf") && strRef.contains("https://www.google.com/url?q=")) {
                            
                            String strPdfToDownload = strRef.substring(strRef.indexOf("https://www.google.com/url?q=") + 29, strRef.indexOf(".pdf") + 4);
                            // sales.add(strPdfToDownload);
                            strResults.add(sourceAbbrv + " " + strPdfToDownload);
                            
                        }
                        
                    }
                    if (strResults == null || strResults.size() == 0) {
                        updateMessage("No data found for that search term...try again");
                    } else {
                        updateMessage("pdfs found");
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (searchBox2.isSelected()) {
                System.out.println("searchBox2 Selected");
                String sourceAbbrv = "bing";
                
                String strUrl = "http://www.bing.com/search?q=";
                strUrl += convertSpacesToPluses(txtSearch.getText());
                strUrl += "+filetype:pdf&count=50";
                
                System.out.println("STRURL: " + strUrl);
                
                try {
                    doc = Jsoup.connect(strUrl).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
                    Elements hrefs = doc.select("a[href]");
                    for (Element href : hrefs) {
                        
                        String strRef = href.attr("abs:href");
                        System.out.println(strRef);
                        if (strRef.endsWith(".pdf")) {
                            
                            //String strPdfToDownload = strRef.substring(strRef.indexOf("http://www.bing.com/search?q=") + 24, strRef.indexOf(".pdf") + 4);
                            strResults.add(sourceAbbrv + " " + strRef);
                            System.out.println("STRPDFTODL: " + strRef);
                            
                        }
                        
                    }
                    if (strResults == null || strResults.size() == 0) {
                        updateMessage("No data found for that search term...try again");
                    } else {
                        updateMessage("pdfs found");
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
            
            return FXCollections.observableArrayList(strResults);
            
        }
    }
    
    public void initialize(URL location, ResourceBundle resources) {
        
        // table = new TableView<DownTask>();
        TableColumn<Controller.DownTask, String> statusCol = new TableColumn("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<Controller.DownTask, String>(
                                                                                            "message"));
        statusCol.setPrefWidth(100);
        
        TableColumn<Controller.DownTask, Double> progressCol = new TableColumn("Progress");
        progressCol.setCellValueFactory(new PropertyValueFactory<Controller.DownTask, Double>(
                                                                                              "progress"));
        
        progressCol.setPrefWidth(125);
        
        //this is the most important call
        progressCol
        .setCellFactory(ProgressBarTableCell.<Controller.DownTask>forTableColumn());
        
        TableColumn<Controller.DownTask, String> fileCol = new TableColumn("File");
        fileCol.setCellValueFactory(new PropertyValueFactory<Controller.DownTask, String>(
                                                                                          "title"));
        fileCol.setPrefWidth(375);
        
        //add the cols
        table.getColumns().addAll(statusCol, progressCol, fileCol);
        
        btnGo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                
                //Sets the paths to save and makes a new folder if necessary
                strDirSave = savePath.getText() + "//" + txtSearch.getText();
                System.out.println("STRDIRSAVE: " + strDirSave);
                
                if (strDirSave == null) {
                    System.err.println("You need to set an output dir!");
                    return;
                }
                
                File theDir = new File(savePath.getText() + "//" + txtSearch.getText());
                System.out.println("THEDIR: " + theDir);
                
                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    boolean result = false;
                    
                    try {
                        theDir.mkdir();
                        result = true;
                    } catch (SecurityException se) {
                        //handle it
                    }
                    if (result) {
                        System.out.println("DIR created");
                    }
                }
                
                //you can instantiate a new Task each time if you want to "re-use" it, but once this intance is cancelled, you can't restart it again.
                final Task<ObservableList<String>> getReviewsTask = new GetReviewsTask();
                
                table.getItems().clear();
                lblStatus.textProperty().bind(getReviewsTask.messageProperty());
                btnGo.disableProperty().bind(getReviewsTask.runningProperty());
                lstView.itemsProperty().bind(getReviewsTask.valueProperty());
                
                getReviewsTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        
                        ObservableList<String> observableList = getReviewsTask.getValue();
                        if (observableList == null || observableList.size() == 0) {
                            return;
                        }
                        
                        //add Tasks to the table
                        for (String str : getReviewsTask.getValue()) {
                            String[] sa = str.split(" ");
                            table.getItems().add(new DownTask(sa[1], strDirSave, sa[0]));
                        }
                        
                        //fire up executor-service with limted number of threads
                        ExecutorService executor = Executors.newFixedThreadPool(3, new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                Thread t = new Thread(r);
                                t.setDaemon(true);
                                return t;
                            }
                        });
                        
                        for (Controller.DownTask pbarTask : table.getItems()) {
                            executor.execute(pbarTask);
                        }
                        
                    }
                });
                
                new Thread(getReviewsTask).start();
                
            }
        });
        
    }
    
    class DownTask extends Task<Void> {
        
        private String pdfFrom, pdfTo, source;
        
        public DownTask(String pdfFrom, String pdfTo, String source) {
            this.source = source;
            this.pdfFrom = pdfFrom;
            this.pdfTo = pdfTo;
            
        }
        
        @Override
        protected Void call() throws Exception {
            relevanceInt++;
            String relevanceString = String.format("%04d", relevanceInt);
            FileOutputStream outputStream = null;
            
            //System.out.println("RELEVANCEINT: " + relevanceInt);
            //System.out.println("RELEVANCESTRING: " + relevanceString);
            HttpDownUtil util = new HttpDownUtil();
            try {
                
                this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
                this.updateMessage("Queued...");
                
                //Download function called here
                util.downloadFile(this.pdfFrom, relevanceString, source);
                
                InputStream inputStream = util.getInputStream();
                // opens an output stream to save into file
                outputStream = new FileOutputStream(pdfTo + "/" + util.getFileName());
                this.updateTitle(pdfTo + "/" + util.getFileName());
                
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                long totalBytesRead = 0;
                int percentCompleted = 0;
                long fileSize = util.getContentLength();
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    percentCompleted = (int) (totalBytesRead * 100 / fileSize);
                    updateProgress(percentCompleted, 100);
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                this.cancel(true);
                table.getItems().remove(this);
                
            } finally {
                updateMessage("Finished");
                outputStream.close();
                util.disconnect();
            }
            
            return null;
        }
        
    }
    
    public String getJSON(String url, int timeout) {
        try {
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();
            
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }
            
        } catch (MalformedURLException ex) {
            // Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            // Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
