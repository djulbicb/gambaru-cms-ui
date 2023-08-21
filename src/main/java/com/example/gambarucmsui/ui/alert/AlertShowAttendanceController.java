package com.example.gambarucmsui.ui.alert;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.membership.GetMembershipStatusPort;
import com.example.gambarucmsui.ports.interfaces.user.UserPictureLoad;
import com.example.gambarucmsui.util.FormatUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class AlertShowAttendanceController implements Initializable {

    private final BarcodeEntity barcode;
    private final LocalDateTime forDate;
    private final GetMembershipStatusPort getMembershipStatusPort;
    private final UserPictureLoad userPictureLoad;
    @FXML private Pane root;
    @FXML private Label lblFirstName;
    @FXML private Label lblLastMembershipPayment;
    @FXML private Label lblLastName;
    @FXML private Label lblNotification;
    @FXML private Label lblTeamName;
    @FXML private StackPane paneNotification;
    @FXML private AnchorPane panePicture;


    public AlertShowAttendanceController(BarcodeEntity barcode, LocalDateTime forDate) {
       this.barcode = barcode;
       this.forDate = forDate;
       this.getMembershipStatusPort = Container.getBean(GetMembershipStatusPort.class);
       this.userPictureLoad = Container.getBean(UserPictureLoad.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PersonEntity person = barcode.getPerson();

        lblFirstName.setText(person.getFirstName());
        lblLastName.setText(person.getLastName());
        lblTeamName.setText(barcode.getTeam().getName());
        lblLastMembershipPayment.setText(getPayment(barcode.getLastMembershipPaymentTimestamp()));

        GetMembershipStatusPort.State membershipState = getMembershipStatusPort.getLastMembershipForUser(barcode, forDate);
        panePicture.getChildren().add(userPictureLoad.loadUserPictureByUserId(person.getPersonId()));

        if (membershipState.getColor() == GetMembershipStatusPort.State.Color.ORANGE) {
            setNotificationOrange(membershipState.getDays());
        } else if (membershipState.getColor() == GetMembershipStatusPort.State.Color.GREEN) {
            setNotificationGreen();
        } else {
            setNotificationRed();
        }
        lblNotification.setText(membershipState.getMessage());

    }

    private void setNotificationGreen() {
        paneNotification.setStyle("-fx-background-radius: 10; -fx-background-color: green; -fx-padding: 20px;");
        root.setStyle(
                """
                    -fx-background-color: #34495e;
                    -fx-border-color: green;
                    -fx-border-width: 7px;
                """
        );
    }
    private void setNotificationOrange(long days) {
        paneNotification.setStyle("-fx-background-radius: 10; -fx-background-color: orange; -fx-padding: 20px;");
        root.setStyle(
                """
                    -fx-background-color: #34495e;
                    -fx-border-color: orange;
                    -fx-border-width: 7px;
                """
        );
    }
    private void setNotificationRed() {
        paneNotification.setStyle("-fx-background-radius: 10; -fx-background-color: red; -fx-padding: 20px;");
        root.setStyle(
                """
                    -fx-background-color: #34495e;
                    -fx-border-color: red;
                    -fx-border-width: 7px;
                """
        );
    }

    private String getPayment(LocalDateTime lastMembershipPaymentTimestamp) {
        if (lastMembershipPaymentTimestamp == null) {
            return "Nema uplata";
        }
        return FormatUtil.toDateFormat(barcode.getLastMembershipPaymentTimestamp());
    }
}

