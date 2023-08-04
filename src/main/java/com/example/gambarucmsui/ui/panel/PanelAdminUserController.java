package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.database.repo.*;
import com.example.gambarucmsui.common.DelayedKeyListener;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.Response;
import com.example.gambarucmsui.ports.user.*;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.admin.TeamDetail;
import com.example.gambarucmsui.ui.dto.admin.subtables.AttendanceDetail;
import com.example.gambarucmsui.ui.dto.admin.subtables.BarcodeDetail;
import com.example.gambarucmsui.ui.dto.admin.subtables.MembershipDetail;
import com.example.gambarucmsui.ui.dto.admin.UserAdminDetail;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.util.LayoutUtil.formatPagination;
import static com.example.gambarucmsui.util.LayoutUtil.stretchColumnsToEqualSize;
import static com.example.gambarucmsui.util.FormatUtil.*;
import static com.example.gambarucmsui.util.PathUtil.*;

public class PanelAdminUserController implements PanelHeader{
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
    private final UserAddToTeamPort userAddToTeamPort;

    // TAB USER
    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////
    @FXML private TableView<UserAdminDetail> tableUsers;
    @FXML private Label paginationLabel;
    @FXML private TextField txtSearchFirstName;
    @FXML private TextField txtSearchLastName;
    @FXML private TextField txtSearchBarcode;
    @FXML private ComboBox<String> cmbSearchTeam;
    @FXML private CheckBox checkSearchOnlyActive;

    // PAGINATION
    private int currentPage = 1;
    private static final int PAGE_SIZE = 50;
    @FXML
    protected void goNextPage() {
        currentPage++;
        paginationLabel.setText(formatPagination(currentPage));
        loadTableUser();
    }
    @FXML
    protected void goPrevPage() {
        if (currentPage <= 1) {
            return;
        }
        currentPage--;
        paginationLabel.setText(formatPagination(currentPage));
        loadTableUser();
    }

    @FXML private TableView<?> tableTeamUsers;
    private void configureTabUsers() {
        // Add on row click listener
        tableUsers.setRowFactory(tv -> {
            TableRow<UserAdminDetail> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && !row.isEmpty()) {
                    UserAdminDetail rowData = row.getItem();
                    Optional<UserEntity> userOpt = userRepo.findById(rowData.getUserId());
                    if (userOpt.isEmpty()) {
                        return;
                    }
                    UserEntity user = userOpt.get();

                    loadTableUserAttendance(user);
                    loadTableUserMembership(user);
                    loadTableUserBarcode(user);

                }
            });
            return row;
        });

        // Set up user table for barcodes
        // Create columns for barcode and team
        TableColumn<BarcodeDetail, String> barcodeColumn = new TableColumn<>("Barcode");
        barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        TableColumn<BarcodeDetail, String> teamColumn = new TableColumn<>("Team");
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("team"));
        TableColumn<BarcodeDetail, String> statusColumn = new TableColumn<>("Team");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("team"));
        TableColumn<BarcodeDetail, String> activateColumn = buildUserBarcodeButtonColumn();
        tableUserBarcode.getColumns().addAll(barcodeColumn, teamColumn, statusColumn, activateColumn);


        stretchColumnsToEqualSize(tableUsers);
        stretchColumnsToEqualSize(tableUserAttendance);
        stretchColumnsToEqualSize(tableUserMembership);
        stretchColumnsToEqualSize(tableUserBarcode);
    }

    @Override
    public void initialize() {
        configureTabUsers();
        loadTeamsToUserComboBox();
    }

    @Override
    public void viewSwitched() {
        System.out.println("Switched to panel Admin.");
        loadTableUser();
    }

    // USER DETAILS
    @FXML
    private TableView<AttendanceDetail> tableUserAttendance;
    @FXML private TableView<MembershipDetail> tableUserMembership;
    @FXML private TableView<BarcodeDetail> tableUserBarcode;

        public PanelAdminUserController(Stage primaryStage, HashMap<Class, Object> repositoryMap) {
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
            userAddToTeamPort = Container.getBean(UserAddToTeamPort.class);
        }

    private void loadTableUserMembership(UserEntity user) {
        List<MembershipDetail> userMembershipPaymentEntities = membershipRepo.fetchLastNEntriesForUserAttendance(user.getBarcodes(), 100).stream().map(e->new MembershipDetail(e.getBarcode(), e.getTimestamp(), e.getBarcode().getTeam())).collect(Collectors.toList());;
        tableUserMembership.getItems().setAll(userMembershipPaymentEntities);
    }

    private void loadTableUserAttendance(UserEntity user) {
        List<AttendanceDetail> userAttendanceEntities = attendanceRepo.fetchLastNEntriesForUserAttendance(user.getBarcodes(), 100).stream().map(e->new AttendanceDetail(e.getBarcode(), e.getTimestamp(), e.getBarcode().getTeam())).collect(Collectors.toList());
        tableUserAttendance.getItems().setAll(userAttendanceEntities);
    }

    private void loadTableUserBarcode(UserEntity user) {
        List<BarcodeDetail> barcodeDetails = new ArrayList<>();
        for (BarcodeEntity barcode : user.getBarcodes()) {
            TeamEntity team = barcode.getTeam();
            barcodeDetails.add(new BarcodeDetail(formatBarcode(barcode.getBarcodeId()), barcode.getStatus(), team.getName()));
        }
        tableUserBarcode.getItems().setAll(barcodeDetails);
    }
    private TableColumn<BarcodeDetail, String> buildUserBarcodeButtonColumn() {
        TableColumn<BarcodeDetail, String> activateColumn = new TableColumn<>("sss");
        activateColumn.setCellFactory(param -> createButtonTableCell());
        return activateColumn;
    }
    private TableCell<BarcodeDetail, String> createButtonTableCell() {
        return new TableCell<BarcodeDetail, String>() {
            private final Button activateBtn = new Button("Aktiviraj");
            private final Button deactivateBtn = new Button("Deaktiviraj");
            private HBox pane;
            @Override
            protected void updateItem(String s, boolean b) {
                super.updateItem(s, b);

                if (b) {
                    setGraphic(null);
                } else {
                    if (pane == null) {
                        pane = new HBox(activateBtn, deactivateBtn);
                        activateBtn.setOnMouseClicked(event -> {
                            BarcodeDetail selectedItem = getTableView().getItems().get(getIndex());
                            System.out.println("Akcije");
                            if (selectedItem != null) {
                                Optional<BarcodeEntity> byId = barcodeRepo.findById(parseBarcodeStr(selectedItem.getBarcode()));
                                if (byId.isPresent()) {
                                    BarcodeEntity barcode = byId.get();
                                    if (barcode.getTeam().getStatus() == TeamEntity.Status.DELETED) {
                                        ToastView.showModal("Tim je obrisan. Barkod se ne moze aktivirati.");
                                        return;
                                    }
                                    barcode.setStatus(BarcodeEntity.Status.ASSIGNED);
                                    barcodeRepo.updateOne(barcode);

                                    loadTableUserBarcode(barcode.getUser());
                                    loadTableUser();
                                }
                            }
                        });
                        deactivateBtn.setOnMouseClicked(event -> {
                            BarcodeDetail selectedItem = getTableView().getItems().get(getIndex());
                            System.out.println("qqq");
                            if (selectedItem != null) {
                                Optional<BarcodeEntity> byId = barcodeRepo.findById(parseBarcodeStr(selectedItem.getBarcode()));
                                if (byId.isPresent()) {
                                    BarcodeEntity barcode = byId.get();
                                    if (barcode.getTeam().getStatus() == TeamEntity.Status.DELETED) {
                                        ToastView.showModal("Tim je obrisan. Barkod se ne moze aktivirati.");
                                        return;
                                    }

                                    barcode.setStatus(BarcodeEntity.Status.DEACTIVATED);
                                    barcodeRepo.updateOne(barcode);

                                    loadTableUserBarcode(barcode.getUser());
                                    loadTableUser();
                                }

                            }

                        });
                    }
                    setGraphic(pane);
                }
            }
        };
    }

    // USERS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    public void textSearchFieldChanged() {
        loadTableUser();
    }
    private void loadTableUser() {
        List<UserAdminDetail> collect = userRepo.findAll(
                        currentPage, PAGE_SIZE, "createdAt",
                        LayoutUtil.getOr(cmbSearchTeam, ""),
                        LayoutUtil.getOr(txtSearchFirstName, ""),
                        LayoutUtil.getOr(txtSearchLastName, ""),
                        LayoutUtil.getNumericOr(txtSearchBarcode, ""),
                        checkSearchOnlyActive.isSelected())
                .stream().map(o -> UserAdminDetail.fromEntityToFull(o)
                ).collect(Collectors.toList());

        tableUsers.getItems().setAll(collect);
    }
    @FXML
    void onCmbSearchTeamChange(ActionEvent event) {
        loadTableUser();
    }

    @FXML
    public void formUserAdd() throws IOException {
        FormUserAddController controller = new FormUserAddController(teamRepo);

        Pane root = loadFxml(FORM_USER_ADD, controller);
        Stage dialogStage = createStage("Kreiraj novog korisnika", root, primaryStage);
        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            FormUserAddController.Data data = controller.getData();
            UserEntity user = userSavePort.save(data.getFirstName(), data.getLastName(), data.getGender(), data.getPhone(), LocalDateTime.now(), data.getPictureData());
            ToastView.showModal(String.format("Korisnik %s %s je dodat.", user.getFirstName(), user.getLastName()));
        }
        loadTableUser();
    }



    @FXML
    public void formUserUpdate() throws IOException {
        UserAdminDetail selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj korisnika u tabeli pa klini.");
            return;
        }
        Optional<UserEntity> userOpt = userRepo.findById(selectedItem.getUserId());
        if (userOpt.isEmpty()) {
            return;
        }

        UserEntity user = userOpt.get();
        byte[] pictureData = user.getPicture() == null ? null : user.getPicture().getPictureData();
        FormUserUpdateController controller = new FormUserUpdateController(teamRepo, new FormUserUpdateController.Data(user.getFirstName(), user.getLastName(), user.getPhone(), user.getGender(), pictureData));
        Pane root = loadFxml(FORM_USER_ADD, controller);
        Stage window = createStage("Izmeni korisnika", root, primaryStage);

        window.showAndWait();

        if (controller.isFormReady()) {
            FormUserUpdateController.Data data = controller.getData();
            userUpdatePort.update(user.getUserId(), data.getFirstName(), data.getLastName(), data.getGender(), data.getPhone(), data.getPictureData());
        }

        loadTableUser();
    }
    @FXML
    void formUserAddUserToTeam(MouseEvent event) throws IOException {
        UserAdminDetail selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj korisnika u tabeli pa klini.");
            return;
        }

        Long userId = Long.valueOf(selectedItem.getUserId());
        FormUserAddUserToTeamController controller = new FormUserAddUserToTeamController( new
                FormUserAddUserToTeamController.Data( userId, 0L, ""), teamRepo, barcodeRepo, userRepo);
        Pane root = loadFxml(FORM_USER_ADD_USER_TO_TEAM, controller);

        // Create the modal dialog
        Stage dialogStage = createStage("Dodaj korisnika u tim", root, primaryStage);
        dialogStage.addEventFilter(KeyEvent.KEY_PRESSED, new DelayedKeyListener()
        {
            @Override
            public void onFinish(String word) {
                if (isBarcode(word)) {
                    controller.onBarcodeScanned(cleanBarcodeStr(word));
                }
            }
        });

        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            FormUserAddUserToTeamController.Data data = controller.getData();
            userAddToTeamPort.addUserToPort(selectedItem.getUserId(), data.getBarcode(), data.getTeamName());
        }
        loadTableUser();
    }

    private void loadTeamsToUserComboBox() {
        cmbSearchTeam.getItems().clear();
        cmbSearchTeam.getItems().add(null);
        for (TeamEntity team : teamRepo.findAllActive()) {
            cmbSearchTeam.getItems().add(team.getName());
        }
    }

}
