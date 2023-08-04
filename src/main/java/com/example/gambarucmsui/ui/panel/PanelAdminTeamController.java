package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.common.DelayedKeyListener;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.database.repo.*;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.Response;
import com.example.gambarucmsui.ports.user.TeamSavePort;
import com.example.gambarucmsui.ports.user.TeamUpdatePort;
import com.example.gambarucmsui.ports.user.UserSavePort;
import com.example.gambarucmsui.ports.user.UserUpdatePort;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.admin.TeamDetail;
import com.example.gambarucmsui.ui.dto.admin.UserAdminDetail;
import com.example.gambarucmsui.ui.dto.admin.subtables.AttendanceDetail;
import com.example.gambarucmsui.ui.dto.admin.subtables.BarcodeDetail;
import com.example.gambarucmsui.ui.dto.admin.subtables.MembershipDetail;
import com.example.gambarucmsui.ui.form.*;
import com.example.gambarucmsui.util.LayoutUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.util.FormatUtil.cleanBarcodeStr;
import static com.example.gambarucmsui.util.FormatUtil.isBarcode;
import static com.example.gambarucmsui.util.LayoutUtil.formatPagination;
import static com.example.gambarucmsui.util.LayoutUtil.stretchColumnsToEqualSize;
import static com.example.gambarucmsui.util.PathUtil.*;

public class PanelAdminTeamController implements PanelHeader {
    @FXML
    private TableView<TeamDetail> tableTeam;

    private final Stage primaryStage;
    private final UserRepository userRepo;
    private final UserAttendanceRepository attendanceRepo;
    private final UserMembershipRepository membershipRepo;
    private final TeamRepository teamRepo;
    private final BarcodeRepository barcodeRepo;
    private final UserSavePort userSavePort;
    private final UserUpdatePort userUpdatePort;
    private final TeamSavePort teamSavePort;
    private final TeamUpdatePort teamUpdatePort;

    public PanelAdminTeamController(Stage primaryStage, HashMap<Class, Object> repositoryMap) {
        this.primaryStage = primaryStage;
        this.userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        this.attendanceRepo = (UserAttendanceRepository) repositoryMap.get(UserAttendanceRepository.class);
        this.membershipRepo = (UserMembershipRepository) repositoryMap.get(UserMembershipRepository.class);
        this.teamRepo = (TeamRepository) repositoryMap.get(TeamRepository.class);
        this.barcodeRepo = (BarcodeRepository) repositoryMap.get(BarcodeRepository.class);

        userSavePort = Container.getBean(UserSavePort.class);
        userUpdatePort = Container.getBean(UserUpdatePort.class);
        teamSavePort = Container.getBean(TeamSavePort.class);
        teamUpdatePort = Container.getBean(TeamUpdatePort.class);
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
                if (selectedItem == null) {

                }
            });
            return row;
        });
    }

    void loadTableTeam() {
        List<TeamDetail> teams = teamRepo.findAllActive().stream().map(en -> new TeamDetail(en.getTeamId(), en.getName(), en.getMembershipPayment())).collect(Collectors.toList());
        tableTeam.getItems().setAll(teams);
    }

    @FXML
    public void addTeamForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FORM_TEAM_ADD));
        FormTeamAddController controller = new FormTeamAddController(teamRepo);

        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

        // Create the modal dialog
        Stage dialogStage = createStage("Kreiraj novi tim", root, primaryStage);
        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            FormTeamAddController.Data data = controller.getData();
            Response<TeamEntity> save = teamSavePort.save(data.getTeamName(), data.getMembershipPaymentFee());
            ToastView.showModal(save.getMessage());
        }
        loadTableTeam();
    }

    @FXML
    public void updateTeamForm() throws IOException {
        TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni na izmeni.");
            return;
        }

        FormTeamUpdateController.Data inputDto = new FormTeamUpdateController.Data(selectedItem.getFee(), selectedItem.getName());
        FormTeamUpdateController controller = new FormTeamUpdateController(inputDto, teamRepo);
        Pane root = loadFxml(FORM_TEAM_UPDATE, controller);

        Stage dialogStage = createStage("Kreiraj novi tim", root, primaryStage);
        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            FormTeamUpdateController.Data data = controller.getData();
            Response<TeamEntity> teamEntityResponse = teamUpdatePort.updateTeam(selectedItem.getTeamId(), data.getTeamName(), data.getMembershipPaymentFee());
            loadTableTeam();
            ToastView.showModal(teamEntityResponse.getMessage());

        }
    }

    @FXML
    void teamDeleteForm() {
        TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni na obrisi.");
            return;
        }

        Optional<TeamEntity> teamOpt = teamRepo.findById(selectedItem.getTeamId());
        if (teamOpt.isEmpty()) {
            return;
        }

        TeamEntity team = teamOpt.get();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje tima");
        alert.setHeaderText(String.format("Da li želiš da obrišeš tim %s?", team.getName()));
        alert.setContentText("Svi barkodovi asocirani sa ovim timom ce biti trajno deaktivirani");

        ButtonType yesButton = new ButtonType("Obriši");
        ButtonType noButton = new ButtonType("Nemoj da brišeš");
        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.getDialogPane().getStylesheets().add(getClass().getResource(CSS).toExternalForm());
        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                List<BarcodeEntity> barcodes = barcodeRepo.findByTeam(team);
                for (BarcodeEntity barcode : barcodes) {
                    barcode.setStatus(BarcodeEntity.Status.DELETED);
                }
                barcodeRepo.saveMultiple(barcodes);
                team.setStatus(TeamEntity.Status.DELETED);
                team.setName(team.getName() + " (Obrisan)");
                teamRepo.updateOne(team);
                ToastView.showModal("Tim je obrisan");
                loadTableTeam();
            }
        });

        loadTableTeam();
    }
}
