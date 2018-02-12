package youtubedownloader;  
import java.io.BufferedInputStream;  
import java.io.BufferedReader;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.net.HttpURLConnection;  
import java.net.MalformedURLException;  
import java.net.URL;  
import java.net.URLDecoder;  
import javafx.application.Application;  
import javafx.concurrent.Service;  
import javafx.concurrent.Task;  
import javafx.concurrent.WorkerStateEvent;  
import javafx.event.ActionEvent;  
import javafx.scene.Group;  
import javafx.scene.Scene;  
import javafx.scene.control.Alert;  
import javafx.scene.control.Alert.AlertType;  
import javafx.scene.control.Button;  
import javafx.scene.control.Label;  
import javafx.scene.control.ProgressBar;  
import javafx.scene.control.TextField;  
import javafx.scene.text.Font;  
import javafx.stage.Stage;  
/** 
 * 
 * @author Passionate Coder 
 */  
public class YoutubeDownloader extends Application {  
    /*java fx UI element used in the javafx application*/  
    private Button downloadBtn = new Button();  
    private TextField youtubeUrlField = new TextField();  
    private TextField fileName = new TextField();  
    private Label label1 = new Label();  
    private Label label2 = new Label();  
    private Label about = new Label("Created and Developed by Deepank Pant");  
    private ProgressBar pbar = new ProgressBar(0);  
    private URL downloadURL;  
    @Override  
    public void start(Stage primaryStage) {  
        /*Font used in the project*/  
        Font poorRichard = new Font("Poor Richard", 16);  
        Font poorRichard2 = new Font("Poor Richard", 13);  
        /*setting for the UI element*/  
        downloadBtn.setText("Download");  
        downloadBtn.setFont(poorRichard);  
        downloadBtn.setLayoutX(10);  
        downloadBtn.setLayoutY(200);  
        youtubeUrlField.setFont(poorRichard);  
        youtubeUrlField.setLayoutX(10);  
        youtubeUrlField.setLayoutY(80);  
        youtubeUrlField.setPrefColumnCount(23);  
        fileName.setFont(poorRichard);  
        fileName.setLayoutX(10);  
        fileName.setLayoutY(150);  
        fileName.setPrefColumnCount(23);  
        label1.setFont(poorRichard);  
        label1.setText("Youtube URL");  
        label1.setLayoutX(10);  
        label1.setLayoutY(50);  
        label2.setFont(poorRichard);  
        label2.setText("File Name (Optional)");  
        label2.setLayoutX(10);  
        label2.setLayoutY(120);  
        pbar.setVisible(false);  
        pbar.setPrefWidth(350);  
        pbar.setLayoutX(100);  
        pbar.setLayoutY(200);  
        about.setFont(poorRichard2);  
        about.setLayoutX(20);  
        about.setLayoutY(260);  
        /*Event is triggered when we press the download button*/  
        downloadBtn.setOnAction((ActionEvent event) - > {  
            sendHTTPRequest.restart();  
        });  
        /*Event is triggered when the sendHTTP request service completed successfully*/  
        sendHTTPRequest.setOnSucceeded((WorkerStateEvent we) - > {  
            try {  
                downloadURL = new URL(getURLS(sendHTTPRequest.getValue()));  
                pbar.progressProperty().unbind();  
                pbar.setProgress(0);  
                pbar.progressProperty().bind(VideoDownload.progressProperty());  
                pbar.setVisible(true);  
                /*if everything goes right then it will start a new service to download the video*/  
                VideoDownload.restart();  
            } catch (MalformedURLException ex) {  
                Alert msg = new Alert(AlertType.INFORMATION);  
                msg.setTitle("Message from Youtube Downloader");  
                msg.setContentText("Invalid Url");  
                msg.showAndWait();  
            }  
        });  
        /*Event is fired when videDownload service is completed successfully*/  
        VideoDownload.setOnSucceeded((WorkerStateEvent we) - > {  
            boolean val = VideoDownload.getValue();  
            System.out.println(val);  
            if (val) {  
                Alert msg = new Alert(AlertType.INFORMATION);  
                msg.setTitle("Message from Youtube Downloader");  
                msg.setContentText("Download complete");  
                msg.showAndWait();  
            } else {  
                Alert msg = new Alert(AlertType.INFORMATION);  
                msg.setTitle("Message from Youtube Downloader");  
                msg.setContentText("Download Failed");  
                msg.showAndWait();  
            }  
            pbar.setVisible(false);  
        });  
        Group root = new Group();  
        root.getChildren().add(downloadBtn);  
        root.getChildren().add(youtubeUrlField);  
        root.getChildren().add(fileName);  
        root.getChildren().add(label1);  
        root.getChildren().add(label2);  
        root.getChildren().add(pbar);  
        root.getChildren().add(about);  
        Scene scene = new Scene(root, 500, 280);  
        primaryStage.setTitle("Youtube Downloader");  
        primaryStage.setScene(scene);  
        primaryStage.setResizable(false);  
        primaryStage.show();  
    }  
    /*Method to extract the video id from the url. 
    if the url does not contain 'v=' parameter 
    then it will not work. It will accept only 
    standard url*/  
    private String getVideoID(String url) {  
        int index = url.indexOf("v=");  
        String;  
        index += 2;  
        for (int i = index; i < url.length(); i++) id += url.charAt(i);  
        return id;  
    }  
    /*This service send the HTTP Request to the youtube server. In response the youtube server 
    sends the video information. This information contains the url in the encoded format. This 
    method decode the url return it as a StringBuilder Object*/  
    final private Service < StringBuilder > sendHTTPRequest = new Service < StringBuilder > () {  
        @Override  
        protected Task < StringBuilder > createTask() {  
            return new Task < StringBuilder > () {  
                @Override  
                protected StringBuilder call() {  
                    String response;  
                    StringBuilder res = new StringBuilder();  
                    StringBuilder refinedres = new StringBuilder();  
                    try {  
                        URL url = new URL("https://www.youtube.com/get_video_info?&video_id=" + getVideoID(youtubeUrlField.getText()));  
                        System.out.println(url.toString());  
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
                        conn.setRequestMethod("GET");  
                        System.out.println(conn.getResponseMessage());  
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
                        while ((response = in .readLine()) != null) res.append(response);  
                        refinedres.append(URLDecoder.decode(URLDecoder.decode(res.toString(), "UTF-8"), "UTF-8")); in .close();  
                        return refinedres;  
                    } catch (MalformedURLException ex) {} catch (IOException ex) {}  
                    return null;  
                }  
            };  
        }  
    };  
    /*This service will download the videos using the URL*/  
    Service < Boolean > VideoDownload = new Service < Boolean > () {  
        @Override  
        protected Task < Boolean > createTask() {  
            return new Task < Boolean > () {  
                @Override  
                protected Boolean call() throws Exception {  
                    long length;  
                    boolean completed = false;  
                    int count = 0;  
                    try (BufferedInputStream bis = new BufferedInputStream(downloadURL.openStream()); FileOutputStream fos = new FileOutputStream(fileName.getText().length() == 0 ? "video.mp4" : fileName.getText().concat(".mp4"))) {  
                        length = downloadURL.openConnection().getContentLength();  
                        int i = 0;  
                        final byte[] data = new byte[1024];  
                        while ((count = bis.read(data)) != -1) {  
                            i += count;  
                            fos.write(data, 0, count);  
                            updateProgress(i, length);  
                        }  
                        completed = true;  
                    } catch (IOException ex) {}  
                    return completed;  
                }  
            };  
        }  
    };  
    /*This methid receives refined response as a paramter and extract the url from the 
    response which will be used to download the video from the youtube*/  
    private String getURLS(StringBuilder response) {  
        StringBuilder temp1 = new StringBuilder();  
        String[] temp2, temp3, temp4;  
        try {  
            int index = response.indexOf("url_encoded_fmt_stream_map");  
            for (int i = index; i < response.length(); i++) {  
                temp1.append(response.charAt(i));  
            }  
            temp2 = temp1.toString().split("&url=");  
            if (temp2.length > 0) {  
                temp3 = temp2[1].split(";");  
                if (temp3.length > 0) {  
                    temp4 = temp3[0].split(",");  
                    if (temp4.length > 0) return temp4[0];  
                    else return temp3[0];  
                } else return temp2[1];  
            }  
        } catch (Exception e) {  
            Alert msg = new Alert(AlertType.INFORMATION);  
            msg.setTitle("Message form youtube Downloader");  
            msg.setContentText("Error in downloading");  
            msg.showAndWait();  
        }  
        return null;  
    }  
    /** 
     * @param args the command line arguments 
     */  
    public static void main(String[] args) {  
        launch(args);  
    }  
}  
