package se.kth.jarwalli.booksdb.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import se.kth.jarwalli.booksdb.model.Book;

public class LoginDialog {

    private Stage stage;
    private Button okButton;
    private String username;
    private String password;




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
        TextField usernameTextField = new TextField();
        TextField passwordTextField = new TextField();

        //add nodes to gridPane
        gridPane.add(new Label("Username"), 0, 0);
        gridPane.add(usernameTextField, 0,1);
        gridPane.add(new Label("Password"), 0,2);
        gridPane.add(passwordTextField,0,3);
        gridPane.add((okButton),0, 4);

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                username = usernameTextField.getCharacters().toString();
                password = passwordTextField.getCharacters().toString();
                controller.handleLogin(username, password);
                stage.close();
            }
        });
        stage.setScene(scene);
    }
    public void showLoginDialog(){
        stage.showAndWait();
    }
}
