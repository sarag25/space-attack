module com.project.spaceattack {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.project.spaceattack to javafx.fxml;
    exports com.project.spaceattack;
}