
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	// key:m = "my" for privaate
	private Pane mPane1, mPane2;
	private VBox mVBox1, mVBox2;
	private TextField mTextField;
	private Button mButton;
	private ListView<String> listView;
	private Text mText;
	private EventHandler<ActionEvent> takeStats;
	private int portNum;
	private ObservableList<String> mData;
	@SuppressWarnings("unused")
	private Server serverConnection;

	
	
	// lets create a start screen
	public Scene startServerScene() {
		
		// Assign VBOX  to the working main scene
		mTextField = new TextField("5000");
		
		mButton = new Button("Press To Turn on the Server");
		
		mButton.setOnAction(takeStats);
		
		// assigning into the Vbox
		mVBox1 = new VBox(30, mTextField, mButton);
		
		mVBox1.setLayoutX(235);
		mVBox1.setLayoutY(210);
		
		mVBox1.setAlignment(Pos.BASELINE_CENTER);
		
		// Add the mainPnae to hold vbox 
		mPane1 = new Pane(mVBox1);
		// setting up background
		Image image = new Image("src/main/java/resources/mini.jpg");
		BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
		Background background = new Background(backgroundImage);
		mPane1.setBackground(background);
		return new Scene(mPane1,500,500);
	}
	
	public Scene secondaryServerScene() {
		// taking the stats 
		mText = new Text("Game Stats");
		
		mText.setFont(Font.font("TimesRoman", FontWeight.BOLD, FontPosture.REGULAR, 20));
		mText.setFill(Color.BLUE);
		
		listView = new ListView<>();
		
		mData = FXCollections.observableArrayList (" Game starts with 2 players");

		
		listView.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");
		listView.getItems().addAll(mData);
		
		mVBox2 = new VBox(50, mText, listView);
		mVBox2.setLayoutX(180);
		mVBox2.setLayoutY(50);
		mVBox2.setAlignment(Pos.CENTER);
		// insert into the pane
		mPane2 = new Pane(mVBox2);
		
		Image image = new Image("src/main/java/resources/back1.png");
		BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
		Background background = new Background(backgroundImage);
		mPane2.setBackground(background);
		return new Scene(mPane2, 500,500);
	}
	
	public boolean isValidPort(String port) {
		for(char x : port.toCharArray()) {
			if(!Character.isDigit(x)) {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI For Morra Game");
		
		
		// create a  Hashmap to facilitate the scenes 
		
		HashMap<String, Scene> getSceneMap = new HashMap<String, Scene>();
		getSceneMap.put("Insert Second Scene", secondaryServerScene());
		
		takeStats = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if(isValidPort(mTextField.getText())) {
					portNum = Integer.parseInt(mTextField.getText());
					primaryStage.setScene(getSceneMap.get("secondScene"));

					serverConnection = new Server(qdata->{
						Platform.runLater(()-> {
							listView.getItems().clear();
							listView.getItems().add(qdata.toString());
						});
					},portNum);
				}
					
				else {
					mTextField.setText("Port Number is Invalid/Wrong");
				}
			}
		};
		
		getSceneMap.put("Background", startServerScene());
		primaryStage.setScene(getSceneMap.get("Background"));
		primaryStage.show();
		
	}


}
