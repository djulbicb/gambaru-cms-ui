package com.example.gambarucmsui.ui.alert;

import com.example.gambarucmsui.common.model.Color;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.user.UserPictureLoad;
import com.example.gambarucmsui.ui.dto.admin.SubscriptStatus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AlertShowAttendanceController implements Initializable {

    private final BarcodeEntity barcode;
    private final LocalDate forDate;
    private final UserPictureLoad userPictureLoad;
    @FXML private Pane root;
    @FXML private Label lblFirstName;
    @FXML private Label lblLastMembershipPayment;
    @FXML private Label lblLastName;
    @FXML private Label lblNotification;
    @FXML private Label lblTeamName;
    @FXML private StackPane paneNotification;
    @FXML private AnchorPane panePicture;


    public AlertShowAttendanceController(BarcodeEntity barcode, LocalDate forDate) {
       this.barcode = barcode;
       this.forDate = forDate;
       this.userPictureLoad = Container.getBean(UserPictureLoad.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PersonEntity person = barcode.getPerson();

        lblFirstName.setText(person.getFirstName());
        lblLastName.setText(person.getLastName());
        lblTeamName.setText(barcode.getTeam().getName());
        panePicture.getChildren().add(userPictureLoad.loadUserPictureByUserId(person.getPersonId(), 400, 300));

        SubscriptStatus status = barcode.getSubscription().getStatus(forDate);

        if (status.getColor() == Color.GREEN) {
            setNotificationGreen(status.getMessage());
        } else if (status.getColor() == Color.ORANGE) {
            setNotificationOrange(status.getMessage());
        } else {
            setNotificationRed(status.getMessage());
        }
    }

    private void setNotificationGreen(String message) {
        lblNotification.setText(message);
        paneNotification.setStyle("-fx-background-radius: 10; -fx-background-color: green; -fx-padding: 20px;");
        root.setStyle(
                """
                    -fx-background-color: #34495e;
                    -fx-border-color: green;
                    -fx-border-width: 7px;
                """
        );
    }
    private void setNotificationOrange(String message) {
        lblNotification.setText(message);
        paneNotification.setStyle("-fx-background-radius: 10; -fx-background-color: orange; -fx-padding: 20px;");
        root.setStyle(
                """
                    -fx-background-color: #34495e;
                    -fx-border-color: orange;
                    -fx-border-width: 7px;
                """
        );
    }
    private void setNotificationRed(String message) {
        lblNotification.setText(message);
        paneNotification.setStyle("-fx-background-radius: 10; -fx-background-color: red; -fx-padding: 20px;");
        root.setStyle(
                """
                    -fx-background-color: #34495e;
                    -fx-border-color: red;
                    -fx-border-width: 7px;
                """
        );
    }


    @FXML
    private void onClose() {
        root.getScene().getWindow().hide();
    }
}

