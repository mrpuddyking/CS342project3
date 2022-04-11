import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class GUI_server extends Application {

	Button servercreating;
	TextField portnumber;
	BorderPane startPane;
	HBox buttonbox;
	VBox textnpress;
	Server myserver;
	HashMap<String, Scene> sceneMap;
	ListView<String> listItems;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Server Window.");

		// creating textfield for port number as well as the button to make sure its ok
		this.portnumber = new TextField();

		// creating button
		this.servercreating = new Button("Create Server");


		// placing the things in borderpane
		this.buttonbox = new HBox(10,servercreating);
		this.buttonbox.setAlignment(Pos.CENTER);
		this.textnpress = new VBox(10,portnumber);
		this.textnpress.setAlignment(Pos.TOP_CENTER);
		startPane = new BorderPane();
		startPane.getStyleClass().add("startPane"); // to set the name of start pane to be accessed in css
		startPane.setCenter(buttonbox);
		startPane.setBottom(textnpress);


		// event handlers for buttons
		this.servercreating.setOnAction(e->{
			int i = Integer.parseInt(portnumber.getText());
			System.out.println("Port number: "+ i);
			primaryStage.setScene(sceneMap.get("server"));
			myserver = new Server(i, data->{
				Platform.runLater(()->{
					listItems.getItems().add(data.toString());
				});
			});
		});

		listItems = new ListView<String>(); // initializing list items


		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("server", createServerGui());

		Scene scene = new Scene(startPane, 600,600);
		scene.getStylesheets().add("style.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public Scene createServerGui(){

		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(70));
		bp.setLeft(listItems);
		return new Scene(bp, 700, 700);
	}

}
