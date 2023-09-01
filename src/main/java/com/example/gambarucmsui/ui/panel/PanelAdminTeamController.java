package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceAddForUserPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeUpdatePort;
import com.example.gambarucmsui.ports.interfaces.subscription.AddSubscriptionPort;
import com.example.gambarucmsui.ports.interfaces.team.*;
import com.example.gambarucmsui.ports.interfaces.user.UserLoadPort;
import com.example.gambarucmsui.ports.interfaces.user.UserSavePort;
import com.example.gambarucmsui.ports.interfaces.user.UserUpdatePort;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.alert.AlertShowMembershipController;
import com.example.gambarucmsui.ui.dto.admin.SubscriptStatus;
import com.example.gambarucmsui.ui.dto.admin.TeamDetail;
import com.example.gambarucmsui.ui.dto.core.UserDetail;
import com.example.gambarucmsui.ui.form.*;
import com.example.gambarucmsui.util.FormatUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.ui.dto.admin.SubscriptStatus.GREEN_CHECKMARK;
import static com.example.gambarucmsui.ui.dto.admin.SubscriptStatus.ORANGE_EXCLAMATION;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.LayoutUtil.*;
import static com.example.gambarucmsui.util.PathUtil.*;

public class PanelAdminTeamController implements PanelHeader {
    @FXML
    private TableView<TeamDetail> tableTeam;
    @FXML
    private TableView<UserDetail> tableUser;

    private final Stage primaryStage;
    private final UserSavePort userSavePort;
    private final TeamDeletePort teamDeletePort;
    private final UserUpdatePort userUpdatePort;
    private final AddSubscriptionPort addSubscriptionPort;
    private final TeamSavePort teamSavePort;
    private final TeamLoadPort teamLoadPort;
    private final UserLoadPort userLoadPort;
    private final TeamIfExists teamIfExists;
    private final TeamUpdatePort teamUpdatePort;
    private final BarcodeLoadPort barcodeLoadPort;
    private final BarcodeUpdatePort barcodeUpdatePort;
    private final AttendanceAddForUserPort attendanceAddForUserPort;

    public PanelAdminTeamController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        userSavePort = Container.getBean(UserSavePort.class);
        userUpdatePort = Container.getBean(UserUpdatePort.class);
        teamSavePort = Container.getBean(TeamSavePort.class);
        teamUpdatePort = Container.getBean(TeamUpdatePort.class);
        teamLoadPort = Container.getBean(TeamLoadPort.class);
        barcodeLoadPort = Container.getBean(BarcodeLoadPort.class);
        barcodeUpdatePort = Container.getBean(BarcodeUpdatePort.class);
        userLoadPort = Container.getBean(UserLoadPort.class);
        teamDeletePort = Container.getBean(TeamDeletePort.class);
        teamIfExists = Container.getBean(TeamIfExists.class);
        attendanceAddForUserPort = Container.getBean(AttendanceAddForUserPort.class);
        addSubscriptionPort = Container.getBean(AddSubscriptionPort.class);
    }

    @FXML
    public void initialize() {
        configureTabTeam();
    }

    @Override
    public void viewSwitched() {
        System.out.println("Switched to panel Admin.");
        loadTableTeam();
    }

    private void configureTabTeam() {
        tableTeam.setRowFactory(tv -> {
            TableRow<TeamDetail> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                TeamDetail team = row.getItem();
                if (team == null) {
                    return;
                }
                loadUserTeam();
            });
            return row;
        });

        tableUser.getColumns().get(0).setCellFactory(createBarcodeCellFactory());

        stretchColumnsToEqualSize(tableTeam);
        stretchColumnsToEqualSize(tableUser);
    }

    private void loadUserTeam() {
        TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            List<UserDetail> collect = userLoadPort.findActiveUsersByTeamId(selectedItem.getTeamId()).stream().map(o -> {
                SubscriptStatus status = o.getSubscription().getStatus(LocalDate.now());
                return UserDetail.fromEntityToFull(o, LocalDate.now());
            }).collect(Collectors.toList());
            tableUser.getItems().setAll(collect);
        }
    }


    void loadTableTeam() {
        List<TeamDetail> teams = teamLoadPort.findAllActive().stream().map(en ->
                new TeamDetail(en.getTeamId(), en.getName(), en.getMembershipPayment())).collect(Collectors.toList());
        tableTeam.getItems().setAll(teams);
        tableUser.getItems().clear();
    }

    @FXML
    public void addTeamForm() throws IOException {
        FormTeamAddController controller = new FormTeamAddController(teamIfExists, teamSavePort);

        Pane root = loadFxml(FORM_TEAM_ADD, controller);
        createStage("Kreiraj novi tim", root, primaryStage).showAndWait();

        loadTableTeam();
    }

    @FXML
    public void updateTeamForm() throws IOException {
        TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni na izmeni.");
            return;
        }

        FormTeamUpdateController controller = new FormTeamUpdateController(selectedItem.getTeamId(), selectedItem.getName(), selectedItem.getFee());
        createStage("Kreiraj novi tim", loadFxml(FORM_TEAM_UPDATE, controller), primaryStage).showAndWait();

        loadTableTeam();
    }

    private boolean promptUserToContinueDeletion(String teamName) {
        AtomicBoolean shouldContinue = new AtomicBoolean(false);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje tima");
        alert.setHeaderText(String.format("Da li želiš da obrišeš tim %s?", teamName));
        alert.setContentText("Svi barkodovi asocirani sa ovim timom ce biti trajno deaktivirani");

        ButtonType yesButton = new ButtonType("Obriši");
        ButtonType noButton = new ButtonType("Nemoj da brišeš");
        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.getDialogPane().getStylesheets().add(getClass().getResource(CSS).toExternalForm());
        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                shouldContinue.set(true);
            }
        });

        return shouldContinue.get();
    }

    @FXML
    void teamDeleteForm() {
        TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni na obrisi.");
            return;
        }


        if (promptUserToContinueDeletion(selectedItem.getName())) {
            ValidatorResponse res = teamDeletePort.validateAndDeleteTeam(selectedItem.getTeamId());
            if (res.hasErrors()) {
                ToastView.showModal(res.getErrorOrEmpty(TeamEntity.TEAM_ID));
                return;
            }

            ToastView.showModal(res.getMessage());
            loadTableTeam();
        }
    }

    @FXML
    void onAddAttendance(MouseEvent event) {
        UserDetail selectedItem = tableUser.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni.");
            return;
        }

        ValidatorResponse res = attendanceAddForUserPort.validateAndAddAttendance(parseBarcodeStr(selectedItem.getBarcodeId()), LocalDateTime.now());
        if (res.isOk()) {
            ToastView.showModal(Messages.ATTENDANCE_ADDED);
        } else {
            ToastView.showModal(res.getMessage());
        }
    }

    @FXML
    void onPayNextMonth(MouseEvent event) {
        UserDetail selectedItem = tableUser.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj red");
            return;
        }

        ValidatorResponse res = addSubscriptionPort.addNextMonthSubscription(selectedItem.getBarcodeId());
        if (res.isOk()) {
            ToastView.showModal(res.getMessage());
            loadUserTeam();
        } else {
            ToastView.showModal(Messages.ERROR_ADDING_SUBSCRIPTION);
        }
    }

    @FXML
    void onShowSubscriptionDetails(MouseEvent event) {
        UserDetail selectedItem = tableUser.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni.");
            return;
        }

        BarcodeEntity barcode = barcodeLoadPort.findById(parseBarcodeStr(selectedItem.getBarcodeId())).get();
        AlertShowMembershipController alertCtrl = new AlertShowMembershipController(barcode, LocalDateTime.now());
        Pane pane = loadFxml(ALERT_SHOW_MEMBERSHIP, alertCtrl);
        createStage("Članarina", pane, primaryStage).showAndWait();

        loadUserTeam();
    }
}
