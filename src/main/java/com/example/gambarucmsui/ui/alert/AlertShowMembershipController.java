package com.example.gambarucmsui.ui.alert;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.subscription.UpdateSubscriptionPort;
import com.example.gambarucmsui.ports.interfaces.user.UserPictureLoad;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class AlertShowMembershipController implements Initializable {

    private final BarcodeEntity barcode;
    private final LocalDateTime forDate;
    private final UserPictureLoad userPictureLoad;
    private final UpdateSubscriptionPort updateSubscriptionPort;
    @FXML private Pane root;
    @FXML private Pane paneMembership;
    @FXML private Label lblFirstName;
    @FXML private Label lblLastMembershipPayment;
    @FXML private Label lblLastName;
    @FXML private Label lblTeamName;
    @FXML private Label lblMembershipError;
    @FXML private CheckBox chkFreeOfCharge;
    @FXML private DatePicker datePickEnd;
    @FXML private DatePicker datePickStart;
    @FXML private AnchorPane panePicture;


    public AlertShowMembershipController(BarcodeEntity barcode, LocalDateTime forDate) {
       this.barcode = barcode;
       this.forDate = forDate;
       this.userPictureLoad = Container.getBean(UserPictureLoad.class);
       this.updateSubscriptionPort = Container.getBean(UpdateSubscriptionPort.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PersonEntity person = barcode.getPerson();

        lblFirstName.setText(person.getFirstName());
        lblLastName.setText(person.getLastName());
        lblTeamName.setText(barcode.getTeam().getName());

        panePicture.getChildren().add(userPictureLoad.loadUserPictureByUserId(person.getPersonId()));

        LocalDate start = barcode.getSubscription().getStartDate();
        LocalDate end = barcode.getSubscription().getEndDate();
        datePickStart.setValue(start);
        datePickEnd.setValue(end);

        changeDisableOfMembershipPane(barcode.getSubscription().isFreeOfCharge());
    }

    @FXML
    void setDateEndToNextMonth(MouseEvent event) {
        datePickEnd.setValue(getDateOrNow(datePickEnd).plusMonths(1));
    }

    @FXML
    void setDateEndToPrevMonth(MouseEvent event) {
        datePickEnd.setValue(getDateOrNow(datePickEnd).minusMonths(1));
    }

    private LocalDate getDateOrNow(DatePicker datePickEnd) {
        try {
            return datePickEnd.getValue();
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    @FXML
    void setDateEndToToday(MouseEvent event) {
        datePickEnd.setValue(LocalDate.now());
    }

    @FXML
    void setDateStartToToday(MouseEvent event) {
        datePickStart.setValue(LocalDate.now());
    }
    @FXML
    void setDateEndToCurrent(MouseEvent event) {
        datePickEnd.setValue(barcode.getSubscription().getEndDate());
    }

    @FXML
    void toggleOnFreeCharge(MouseEvent event) {
        changeDisableOfMembershipPane(chkFreeOfCharge.isSelected());
    }

    private void changeDisableOfMembershipPane(boolean isSelected) {
        chkFreeOfCharge.setSelected(isSelected);
        paneMembership.setDisable(isSelected);
    }

    @FXML
    void setDateStartToCurrent(MouseEvent event) {
        datePickStart.setValue(barcode.getSubscription().getStartDate());
    }
    @FXML
    void onDatePick(ActionEvent event) {
        lblMembershipError.setText("");
    }

    @FXML
    void updateMembership(MouseEvent event) {
        LocalDate start = datePickStart.getValue();
        LocalDate end = datePickEnd.getValue();

        if (start != null && end != null) {
            if (start.isAfter(end)) {
                lblMembershipError.setText("Početni datum ne može da bude posle krajnjeg.");
                return;
            }
        }

        updateSubscriptionPort.updateSubsscription(barcode.getBarcodeId(), chkFreeOfCharge.isSelected(), datePickStart.getValue(), datePickEnd.getValue());
        onClose();
    }
    @FXML
    private void onClose() {
        root.getScene().getWindow().hide();
    }
}

