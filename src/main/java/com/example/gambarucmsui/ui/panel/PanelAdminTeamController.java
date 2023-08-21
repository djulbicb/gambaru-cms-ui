package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeUpdatePort;
import com.example.gambarucmsui.ports.interfaces.membership.GetMembershipStatusPort;
import com.example.gambarucmsui.ports.interfaces.team.*;
import com.example.gambarucmsui.ports.interfaces.user.UserLoadPort;
import com.example.gambarucmsui.ports.interfaces.user.UserSavePort;
import com.example.gambarucmsui.ports.interfaces.user.UserUpdatePort;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.admin.TeamDetail;
import com.example.gambarucmsui.ui.dto.core.UserDetail;
import com.example.gambarucmsui.ui.form.*;
import com.example.gambarucmsui.util.FormatUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
    private final TeamSavePort teamSavePort;
    private final TeamLoadPort teamLoadPort;
    private final UserLoadPort userLoadPort;
    private final TeamIfExists teamIfExists;
    private final TeamUpdatePort teamUpdatePort;
    private final BarcodeLoadPort barcodeLoadPort;
    private final BarcodeUpdatePort barcodeUpdatePort;
    private final GetMembershipStatusPort getMembershipStatusPort;

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
        getMembershipStatusPort = Container.getBean(GetMembershipStatusPort.class);
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
                TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    List<UserDetail> collect = userLoadPort.findUsersByTeamId(selectedItem.getTeamId()).stream().map(o ->
                            UserDetail.fromEntityToFull(o, String.format("%s %s", getEmoji(o.getLastMembershipPaymentTimestamp()), FormatUtil.toDateTimeFormat(o.getLastMembershipPaymentTimestamp())))).collect(Collectors.toList());
                    tableUser.getItems().setAll(collect);
                }
            });
            return row;
        });

        stretchColumnsToEqualSize(tableTeam);
        stretchColumnsToEqualSize(tableUser);
    }

    public static final String GREEN_CHECKMARK = "\u2714\uFE0F";
    public static final String ORANGE_EXCLAMATION = "\u2757\uFE0F";
    public static final String RED_X = "\u274C\uFE0F";

    private String getEmoji(LocalDateTime lastMembershipPaymentTimestamp) {
        if (lastMembershipPaymentTimestamp == null) {
            return RED_X;
        }

        GetMembershipStatusPort.State.Color color = getMembershipStatusPort.getLastMembershipForUser(lastMembershipPaymentTimestamp, LocalDateTime.now()).getColor();
        if (color == GetMembershipStatusPort.State.Color.GREEN) {
            return GREEN_CHECKMARK;
        }
        if (color == GetMembershipStatusPort.State.Color.ORANGE) {
            return ORANGE_EXCLAMATION;
        }
        return RED_X;
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
}
