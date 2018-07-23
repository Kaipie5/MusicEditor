/*
 * CS 300-A, 2017S
 */
package tunecomposer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This JavaFX app lets the user play scales.
 * @author Janet Davis 
 * @author SOLUTION - PROJECT 1
 * @since January 26, 2017
 */
public class TuneComposer extends Application {

    /**
     * Construct the scene and start the application.
     * @param primaryStage the stage for the main window
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {  
        primaryStage.setTitle("Flim Flam Jam");    
        ImageView splash = new ImageView(new Image("/logo.png"));
        Pane splashPane = new Pane(splash);
        primaryStage.setScene(new Scene(splashPane));
        primaryStage.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        Parent root = FXMLLoader.load(getClass().getResource("TuneComposer.fxml")); 
        delay.setOnFinished(e -> primaryStage.setScene(new Scene(root)));
        delay.play();
        primaryStage.setOnCloseRequest(e -> System.exit(0));        
    }

    /**
     * Launch the application.
     * @param args the command line arguments are ignored
     */
    public static void main(String[] args) {
        launch(args);
    }
}
