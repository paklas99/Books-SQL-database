package se.kth.jarwalli.booksdb.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginDialog {

    private Stage stage;
    private Button okButton;
    private TextField username;
    private TextField password;




    public LoginDialog(Controller controller) {
        initLoginDialog(controller);
    }

    private void initLoginDialog(Controller controller){
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        GridPane gridPane = new GridPane();
        Scene scene = new Scene(gridPane);
        stage.setTitle("Update Book");
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        okButton = new Button("OK");
        TextField storyTextField = new TextField();
    }
}
