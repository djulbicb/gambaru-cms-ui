package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.database.repo.*;
import com.example.gambarucmsui.common.DelayedKeyListener;
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

public class PanelAdminController implements PanelHeader{
    private final Stage primaryStage;
    private final UserRepository userRepo;
    private final UserAttendanceRepository attendanceRepo;
    private final UserMembershipRepository membershipRepo;
    private final TeamRepository teamRepo;
    private final BarcodeRepository barcodeRepo;

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

    @FXML
    private void onUserTabSwitch() {
        loadTeamsToUserComboBox();
        loadTableUser();
    }
    @FXML
    private void onTeamTabSwitch() {
        loadTableTeam();
    }

    @Override
    public void viewSwitched() {
        System.out.println("Switched to panel Admin.");
    }

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

    // USER DETAILS
    @FXML
    private TableView<AttendanceDetail> tableUserAttendance;
    @FXML private TableView<MembershipDetail> tableUserMembership;
    @FXML private TableView<BarcodeDetail> tableUserBarcode;

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

    private void loadTeamsToUserComboBox() {
        cmbSearchTeam.getItems().clear();
        cmbSearchTeam.getItems().add(null);
        for (TeamEntity team : teamRepo.findAllActive()) {
            cmbSearchTeam.getItems().add(team.getName());
        }
    }


    // TEAM TAB
    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////
    @FXML private TableView<TeamDetail> tableTeam;
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


        stretchColumnsToEqualSize(tableUserAttendance);
        stretchColumnsToEqualSize(tableUserMembership);
        stretchColumnsToEqualSize(tableUserBarcode);
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

            UserEntity saved = userRepo.saveOne(data.getFirstName(), data.getLastName(), data.getGender(), data.getPhone(), LocalDateTime.now());

            ToastView.showModal(String.format("Korisnik %s %s je dodat.", saved.getFirstName(), saved.getLastName()));
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
        FormUserUpdateController controller = new FormUserUpdateController(teamRepo, new FormUserUpdateController.Data(user.getFirstName(), user.getLastName(), user.getPhone(), user.getGender()));
        Pane root = loadFxml(FORM_USER_ADD, controller);
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
    void formUserAddUserToTeam(MouseEvent event) throws IOException {
        UserAdminDetail selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj korisnika u tabeli pa klini.");
            return;
        }

        FormUserAddUserToTeamController controller = new FormUserAddUserToTeamController(teamRepo, new FormUserAddUserToTeamController.Data(selectedItem.getFirstName(), selectedItem.getLastName(), "", ""));
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

            Optional<BarcodeEntity> barcodeOpt = barcodeRepo.findById(parseBarcodeStr(data.getBarcode()));
            Optional<UserEntity> userOpt = userRepo.findById(selectedItem.getUserId());
            if (barcodeOpt.isEmpty() || userOpt.isEmpty()) {
                return;
            }
            BarcodeEntity barcode = barcodeOpt.get();
            if (barcode.getStatus() != BarcodeEntity.Status.NOT_USED) {
                ToastView.showModal("Taj barkod je već u upotrebi. Koristi drugi.");
                return;
            }

            UserEntity user = userOpt.get();
            TeamEntity team = teamRepo.findByName(data.getTeamName());
            barcodeRepo.updateBarcodeWithUserAndTeam(barcode, user, team);

            user.getBarcodes().add(barcode);
            userRepo.saveOne(user);

        }
        loadTableUser();
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
            TeamEntity team = teamRepo.saveNewTeam(data.getTeamName(), data.getMembershipPaymentFee());
            ToastView.showModal(String.format("Tim %s je dodat.", team.getName()));
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

        FormTeamUpdateController.Data dto = new FormTeamUpdateController.Data(selectedItem.getTeamId(), selectedItem.getFee(), selectedItem.getName());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FORM_TEAM_UPDATE));
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
