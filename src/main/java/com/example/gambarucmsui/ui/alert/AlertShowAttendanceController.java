package com.example.gambarucmsui.ui.alert;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.util.DataUtil;
import com.example.gambarucmsui.util.FormatUtil;
import com.example.gambarucmsui.util.PathUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class AlertShowAttendanceController implements Initializable {

    private final BarcodeEntity barcode;
    private final LocalDate paginationDate;
    @FXML private Pane root;
    @FXML private Label lblFirstName;
    @FXML private Label lblLastMembershipPayment;
    @FXML private Label lblLastName;
    @FXML private Label lblNotification;
    @FXML private Label lblTeamName;
    @FXML private StackPane paneNotification;
    @FXML private AnchorPane panePicture;


    public AlertShowAttendanceController(BarcodeEntity barcode, LocalDate paginationDate) {
       this.barcode = barcode;
       this.paginationDate = paginationDate;




//

//        box.getChildren().add(imageView);
//
//        VBox vBox = new VBox();
//        vBox.getChildren().add(createLabel("Ime: " + user.getFirstName()));
//        vBox.getChildren().add(createLabel("Prezime: " + user.getFirstName()));
//
//        box.getChildren().add(vBox);
//
//        showModal(box, 2000, 100);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PersonEntity person = barcode.getPerson();

        lblFirstName.setText(person.getFirstName());
        lblLastName.setText(person.getLastName());
        lblTeamName.setText(barcode.getTeam().getName());
        lblLastMembershipPayment.setText(getPayment(barcode.getLastMembershipPaymentTimestamp()));

        setNotification(barcode.getLastMembershipPaymentTimestamp());

        Image userPicture;
        if (person.getPicture() != null && person.getPicture().getPictureData() != null) {
            userPicture = new Image(new ByteArrayInputStream(person.getPicture().getPictureData()));
        } else {
            userPicture = DataUtil.loadImageFromResources(PathUtil.USER_NOT_FOUND);
        }
        ImageView imageView = new ImageView(userPicture);
//        double aspectRatio = userPicture.getWidth() / userPicture.getHeight();
//        double desiredHeight = 400;
//        double desiredWidth = desiredHeight * aspectRatio;
//        imageView.setFitHeight(desiredHeight);
//        imageView.setFitWidth(desiredWidth);
        imageView.setFitHeight(400);
        imageView.setFitWidth(300);

        panePicture.getChildren().add(imageView);

    }

    private void setNotification(LocalDateTime lastMembershipPaymentTimestamp) {
        if (lastMembershipPaymentTimestamp == null) {
            setNotificationRed();
            return;
        }
        LocalDate lastPayed = lastMembershipPaymentTimestamp.toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(lastPayed, paginationDate);

        if (daysBetween < 25) {
            setNotificationGreen();
        } else if (daysBetween < 30) {
            setNotificationOrange(30 - daysBetween);
        } else {
            setNotificationRed();
        }
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
        lblNotification.setText("Članarina je plaćena.");
    }
    private void setNotificationOrange(long days) {
        paneNotification.setStyle("-fx-background-radius: 10; -fx-background-color: orange; -fx-padding: 20px;");
        lblNotification.setText(String.format("Članarinu treba platiti. Broj dana pre uplate je %s.", days ));
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
        lblNotification.setText("Članarina nije plaćena");
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
            return "Nije plaćeno";
        }
        return FormatUtil.toDateFormat(barcode.getLastMembershipPaymentTimestamp());
    }
}

