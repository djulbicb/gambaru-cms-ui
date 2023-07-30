package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.*;
import com.example.gambarucmsui.common.DelayedKeyListener;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.admin.TeamDetail;
import com.example.gambarucmsui.ui.dto.admin.subtables.AttendanceDetail;
import com.example.gambarucmsui.ui.dto.admin.subtables.BarcodeDetail;
import com.example.gambarucmsui.ui.dto.admin.subtables.MembershipDetail;
import com.example.gambarucmsui.ui.dto.admin.UserAdminDetail;
import com.example.gambarucmsui.ui.form.*;
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

import static com.example.gambarucmsui.common.LayoutUtil.formatPagination;
import static com.example.gambarucmsui.common.LayoutUtil.stretchColumnsToEqualSize;
import static com.example.gambarucmsui.util.FormatUtil.*;

public class PanelAdminController implements PanelHeader{
    private final Stage primaryStage;
    ;
    // PAGINATION
    private int currentPage = 1;
    private static final int PAGE_SIZE = 50;
    // REPOSITORY
    private final UserRepository userRepo;
    private final UserAttendanceRepository attendanceRepo;
    private final UserMembershipRepository membershipRepo;
    private final TeamRepository teamRepo;
    private final BarcodeRepository barcodeRepo;

    // TAB USER
    /////////////////////////////
    @FXML private TableView<UserAdminDetail> tableUsers;
    @FXML private Label paginationLabel;
    @FXML private TextField txtSearchFirstName;
    @FXML private TextField txtSearchLastName;
    @FXML private TextField txtSearchBarcode;
    @FXML private ComboBox<String> cmbSearchTeam;
    @FXML private CheckBox checkSearchOnlyActive;

    // USER DETAILS
    @FXML
    private TableView<AttendanceDetail> tableUserAttendance;
    @FXML private TableView<MembershipDetail> tableUserMembership;
    @FXML private TableView<BarcodeDetail> tableUserBarcode;

    // TEAM TAB
    /////////////////////////////
    @FXML private TableView<TeamDetail> tableTeam;
    @FXML private TableView<?> tableTeamUsers;

    public PanelAdminController(Stage primaryStage, HashMap<Class, Object> repositoryMap) {
        this.primaryStage = primaryStage;
        this.userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        this.attendanceRepo = (UserAttendanceRepository) repositoryMap.get(UserAttendanceRepository.class);
        this.membershipRepo = (UserMembershipRepository) repositoryMap.get(UserMembershipRepository.class);
        this.teamRepo = (TeamRepository) repositoryMap.get(TeamRepository.class);
        this.barcodeRepo = (BarcodeRepository) repositoryMap.get(BarcodeRepository.class);
    }

    @FXML
    public void initialize() {
        configureTabUsers();
        configureTabTeam();
    }

    private void configureTabUsers() {
        // Load teams to comboBox
        loadTeamsToUserComboBox();

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



        // Stretch columns based on table width
//        for (TableColumn<UserAdminDetail, ?> column : tableUsers.getColumns()) {
//            if (column.getId().equals("userIdColumn")) {
//                column.prefWidthProperty().bind(tableUsers.widthProperty().divide(tableUsers.getColumns().size() * 2));
//            } else if ( column.getId().equals("genderColumn")) {
//                column.prefWidthProperty().bind(tableUsers.widthProperty().divide(tableUsers.getColumns().size() * 4));
//            } else if ( column.getId().equals("barcodeTeamColumn")) {
//                column.prefWidthProperty().bind(tableUsers.widthProperty().divide(tableUsers.getColumns().size() / 2));
//            } else {
//                column.prefWidthProperty().bind(tableUsers.widthProperty().divide(tableUsers.getColumns().size()));
//            }
//        }
        stretchColumnsToEqualSize(tableUserAttendance);
        stretchColumnsToEqualSize(tableUserMembership);
        stretchColumnsToEqualSize(tableUserBarcode);
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
            barcodeDetails.add(new BarcodeDetail(formatBarcode(barcode.getBarcodeId()), barcode.getStatus(), barcode.getTeam().getName()));
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
                            System.out.println("sss");
                            if (selectedItem != null) {
                                Optional<BarcodeEntity> byId = barcodeRepo.findById(parseBarcodeStr(selectedItem.getBarcode()));
                                if (byId.isPresent()) {
                                    BarcodeEntity barcode = byId.get();
                                    barcode.setStatus(BarcodeEntity.Status.ASSIGNED);
                                    barcodeRepo.updateOne(barcode);

                                    loadTableUserBarcode(barcode.getUser());
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
                                    barcode.setStatus(BarcodeEntity.Status.DEACTIVATED);
                                    barcodeRepo.updateOne(barcode);

                                    loadTableUserBarcode(barcode.getUser());
                                }

                            }

                        });
                    }
                    setGraphic(pane);
                }
            }
        };
    }

    private void loadTeamsToUserComboBox() {
        cmbSearchTeam.getItems().clear();
        cmbSearchTeam.getItems().add(null);
        for (TeamEntity team : teamRepo.findAll()) {
            cmbSearchTeam.getItems().add(team.getName());
        }
    }

    @Override
    public void viewSwitched() {
        System.out.println("Panel admin");
        loadTableUser();
        loadTableTeam();
        loadTeamsToUserComboBox();
    }

    private String getOr(TextField txt, String opt) {
        if (txt == null) {
            return opt;
        }
        return txt.getText();
    }

    // USERS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    public void textSearchFieldChanged() {
        loadTableUser();
    }
    private void loadTableUser() {
        List<UserAdminDetail> collect = userRepo.findAll(currentPage, PAGE_SIZE, "createdAt",
                        getOr(cmbSearchTeam, ""),
                        getOr(txtSearchFirstName, ""),
                        getOr(txtSearchLastName, ""),
                        getNumericOr(txtSearchBarcode, ""),
                        checkSearchOnlyActive.isSelected())
                .stream().map(o -> UserAdminDetail.fromEntityToFull(o)
                ).collect(Collectors.toList());

        tableUsers.getItems().setAll(collect);
    }

    private String getNumericOr(TextField txtSearchBarcode, String opt) {
        if (txtSearchBarcode == null) {
            return opt;
        }
        String value = txtSearchBarcode.getText();
        if (isLong(value)) {
            return parseBarcodeStr(value).toString();
        }
        return opt;
    }

    private String getOr(ComboBox<String> combo, String opt) {
        if (combo.getSelectionModel() == null || combo.getSelectionModel().getSelectedItem() == null) {
            return opt;
        }
        return combo.getSelectionModel().getSelectedItem();
    }

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


    @FXML
    public void formUserAdd() throws IOException {
        FormUserAddController controller = new FormUserAddController(teamRepo);

        Pane root = loadFxml("form-user-add.fxml", controller);
        Stage dialogStage = createStage("Kreiraj novog korisnika", root, primaryStage);
        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            FormUserAddController.Data data = controller.getData();

            UserEntity saved = userRepo.saveOne(data.getFirstName(), data.getLastName(), data.getGender(), data.getPhone(), LocalDateTime.now());

            ToastView.showModal(String.format("Korisnik %s %s je dodat.", saved.getFirstName(), saved.getLastName()));
            loadTableUser();
        }
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
        FormUserUpdateController controller = new FormUserUpdateController(teamRepo, new FormUserUpdateController.Data(user.getFirstName(), user.getLastName(), user.getPhone(), user.getGender()));
        Pane root = loadFxml("form-user-add.fxml", controller);
        Stage window = createStage("Izmeni korisnika", root, primaryStage);

        window.showAndWait();

        if (controller.isFormReady()) {
            FormUserUpdateController.Data data = controller.getData();
            user.setFirstName(data.getFirstName());
            user.setLastName(data.getLastName());
            user.setGender(data.getGender());
            user.setPhone(data.getPhone());
            userRepo.updateOne(user);
        }

        loadTableUser();
    }
    @FXML
    public void formUserDelete() {

    }

    @FXML
    void formUserAddUserToTeam(MouseEvent event) throws IOException {
        UserAdminDetail selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj korisnika u tabeli pa klini.");
            return;
        }

        FormUserAddUserToTeamController controller = new FormUserAddUserToTeamController(teamRepo, new FormUserAddUserToTeamController.Data(selectedItem.getFirstName(), selectedItem.getLastName(), "", ""));
        Pane root = loadFxml("form-user-add-user-to-team.fxml", controller);

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

            Optional<BarcodeEntity> barcodeOpt = barcodeRepo.findById(parseBarcodeStr(data.getBarcode()));
            Optional<UserEntity> userOpt = userRepo.findById(selectedItem.getUserId());
            TeamEntity team = teamRepo.findByName(data.getTeamName());

            if (barcodeOpt.isEmpty() || userOpt.isEmpty()) {
                return;
            }
            barcodeRepo.updateBarcodeWithUserAndTeam(barcodeOpt.get(), userOpt.get(), team);
        }
        loadTableUser();
    }

    @FXML
    void formUserRemoveUserFromTeam(MouseEvent event) throws IOException {
        UserAdminDetail selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj korisnika u tabeli pa klini.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("form-user-remove-user-from-team.fxml"));
        FormUserRemoveUserFromTeamController controller = new FormUserRemoveUserFromTeamController(teamRepo, new FormUserRemoveUserFromTeamController.Data(selectedItem.getUserId(), selectedItem.getFirstName(), selectedItem.getLastName(), "", ""));

        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

        // Create the modal dialog
        Stage dialogStage = createStage("Ukloni korisnika iz tima", root, primaryStage);
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
//            FormUserRemoveUserFromTeamController.Data data = controller.getData();
//
//            Optional<BarcodeEntity> barcodeOpt = barcodeRepo.findById(parseBarcodeStr(data.getBarcode()));
//
//            if (barcodeOpt.isEmpty()) {
//                return;
//            }
//
//            BarcodeEntity barcode = barcodeOpt.get();
//
//            barcode.setStatus(BarcodeEntity.Status.DEACTIVATED);
//            barcode.setTeam(null);
//
//            barcodeRepo.updateOne(barcode);
        }
        loadTableUser();
    }

    @FXML
    void formUserDelete(MouseEvent event) {

    }


    // TEAM
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
        List<TeamDetail> teams = teamRepo.findAll().stream().map(en -> new TeamDetail(en.getTeamId(), en.getName(), en.getMembershipPayment())).collect(Collectors.toList());
        tableTeam.getItems().setAll(teams);
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
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to continue?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.getDialogPane().getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                teamRepo.delete(team);
                ToastView.showModal("Tim je obrisan");
            }
        });

        loadTableTeam();
    }

    @FXML
    public void addTeamForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("form-team-add.fxml"));
        FormTeamAddController controller = new FormTeamAddController(teamRepo);

        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

        // Create the modal dialog
        Stage dialogStage = createStage("Kreiraj novi tim", root, primaryStage);

        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            FormTeamAddController.Data data = controller.getData();


            TeamEntity team = teamRepo.saveNewTeam(data.getTeamName(), data.getMembershipPaymentFee());
            loadTableTeam();

            ToastView.showModal(String.format("Tim %s je dodat.", team.getName()));
        }
    }

    @FXML
    public void updateTeamForm() throws IOException {
        TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni na izmeni.");
            return;
        }

        FormTeamUpdateController.Data dto = new FormTeamUpdateController.Data(selectedItem.getTeamId(), selectedItem.getFee(), selectedItem.getName());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("form-team-update.fxml"));
        FormTeamUpdateController controller = new FormTeamUpdateController(dto, teamRepo);
        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

        // Create the modal dialog
        Stage dialogStage = createStage("Kreiraj novi tim", root, primaryStage);

        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            FormTeamUpdateController.Data data = controller.getData();
            Optional<TeamEntity> en = teamRepo.findById(data.getTeamId());
            if (en.isEmpty()) {
                return;
            }
            TeamEntity team = en.get();
            team.setName(data.getTeamName());
            team.setMembershipPayment(data.getMembershipPaymentFee());
            teamRepo.updateOne(team);
            loadTableTeam();

            ToastView.showModal("Team je izmenjen");
        }
    }

    @FXML
    private void onUserTabSwitch() {
        loadTableUser();
    }
    @FXML
    private void onTeamTabSwitch() {
        loadTableTeam();
    }
    @FXML
    void onCmbSearchTeamChange(ActionEvent event) {
        loadTableUser();
    }

}
