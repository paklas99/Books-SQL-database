module se.kth.anderslm.booksdb {
    requires javafx.controls;
    requires javafx.base;

    opens se.kth.jarwalli.booksdb to javafx.base;
    opens se.kth.jarwalli.booksdb.model to javafx.base; // open model package for reflection from PropertyValuesFactory (sigh ...)
    exports se.kth.jarwalli.booksdb;

    requires java.sql;
}