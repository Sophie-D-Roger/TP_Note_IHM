package sample;

import javafx.application.Application;  
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import sample.Controller;

public class Main extends Application {
	
	
	
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Graphical editor.fxml"));
        primaryStage.setTitle("Graphical editor");
        primaryStage.setScene(new Scene(root, 700, 495));
        primaryStage.show();
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}