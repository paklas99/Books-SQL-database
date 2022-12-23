package se.kth.jarwalli.booksdb;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import se.kth.jarwalli.booksdb.model.BooksDbException;
import se.kth.jarwalli.booksdb.model.BooksDbImplMongo;
import se.kth.jarwalli.booksdb.model.BooksDbImplSQL;
import se.kth.jarwalli.booksdb.view.BooksPane;

import java.sql.SQLException;

/**
 * Application start up.
 *
 * @author anderslm@kth.se
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws SQLException, ClassNotFoundException, BooksDbException {

        BooksDbImplMongo booksDb = new BooksDbImplMongo(); // model
        // TODO delete this connection härnere !!!!!!!!
        booksDb.connect("LibTest");

        // Don't forget to connect to the db, somewhere...


        BooksPane root = new BooksPane(booksDb);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Books Database Client");
        // add an exit handler to the stage (X) ?
        primaryStage.setOnCloseRequest(event -> {
            try {
                booksDb.disconnect();
            } catch (Exception e) {}
        });
            primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
