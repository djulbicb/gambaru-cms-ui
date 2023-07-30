package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.*;
import com.example.gambarucmsui.common.DelayedKeyListener;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.*;
import com.example.gambarucmsui.ui.form.*;
import com.example.gambarucmsui.util.FormatUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
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

import static com.example.gambarucmsui.common.LayoutUtil.formatPagination;
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
    @FXML TableView<UserAdminDetail> tableUsers;
    @FXML Label paginationLabel;
    @FXML TextField txtSearchTeamName;
    @FXML TextField txtSearchFirstName;
    @FXML TextField txtSearchLastName;

    // USER DETAILS
    @FXML
    private TableView<AttendanceDetail> tableUserAttendance;
    @FXML private TableView<MembershipDetail> tableUserMembership;

    // TEAM TAB
    /////////////////////////////
    @FXML private TableView<TeamDetail> tableTeam;
    @FXML private TableView<?> tableTeamUsers;

    public PanelAdminController(Stage primaryStage, HashMap<Class, Repository> repositoryMap) {
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

        // user details
//        TableColumn<AttendanceDetail, String> attendanceColumn = new TableColumn<>("Dolaznost (prethodnih 100)");
//        attendanceColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
//        tableUserAttendance.getColumns().add(attendanceColumn);
//        tableUserAttendance.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//
//        TableColumn<MembershipDetail, String> membershipColumn = new TableColumn<>("Placanje (prethodnih 100)");
//        membershipColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
//        tableUserMembership.getColumns().add(membershipColumn);
//        tableUserMembership.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configureTabUsers() {
        tableUsers.setRowFactory(tv -> {
            TableRow<UserAdminDetail> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && !row.isEmpty()) {
                    UserAdminDetail rowData = row.getItem();
                    Optional<UserEntity> userOpt = userRepo.findById(rowData.getUserId());
                    if (userOpt.isEmpty()) {
                        return;
                    }

                    List<AttendanceDetail> userAttendanceEntities = attendanceRepo.fetchLastNEntriesForUserAttendance(userOpt.get().getBarcodes(), 100).stream().map(e->new AttendanceDetail(e.getBarcode(), e.getTimestamp(), e.getBarcode().getTeam())).collect(Collectors.toList());
                    List<MembershipDetail> userMembershipPaymentEntities = membershipRepo.fetchLastNEntriesForUserAttendance(userOpt.get().getBarcodes(), 100).stream().map(e->new MembershipDetail(e.getBarcode(), e.getTimestamp(), e.getBarcode().getTeam())).collect(Collectors.toList());;
                    tableUserAttendance.getItems().setAll(userAttendanceEntities);
                    tableUserMembership.getItems().setAll(userMembershipPaymentEntities);
                }
            });
            return row;
        });

        for (TableColumn<UserAdminDetail, ?> column : tableUsers.getColumns()) {
            if (column.getId().equals("userIdColumn")) {
                column.prefWidthProperty().bind(tableUsers.widthProperty().divide(tableUsers.getColumns().size() * 2));
            } else if ( column.getId().equals("genderColumn")) {
                column.prefWidthProperty().bind(tableUsers.widthProperty().divide(tableUsers.getColumns().size() * 4));
            } else if ( column.getId().equals("barcodeTeamColumn")) {
                column.prefWidthProperty().bind(tableUsers.widthProperty().divide(tableUsers.getColumns().size() / 2));
            } else {
                column.prefWidthProperty().bind(tableUsers.widthProperty().divide(tableUsers.getColumns().size()));
            }
        }
        for (TableColumn<AttendanceDetail, ?> column : tableUserAttendance.getColumns()) {
            column.prefWidthProperty().bind(tableUserAttendance.widthProperty().divide(tableUserAttendance.getColumns().size()));
        }
        for (TableColumn<MembershipDetail, ?> column : tableUserMembership.getColumns()) {
            column.prefWidthProperty().bind(tableUserMembership.widthProperty().divide(tableUserMembership.getColumns().size()));
        }

    }

    @Override
    public void viewSwitched() {
        System.out.println("Panel admin");
        loadTableUser();
        loadTableTeam();
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
                getOr(txtSearchTeamName, ""),
                getOr(txtSearchFirstName, ""), getOr(txtSearchLastName, ""))
                .stream().map(o -> UserAdminDetail.fromEntityToFull(o)
                ).collect(Collectors.toList());

        tableUsers.getItems().setAll(collect);
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

                System.out.println("clicked");
                TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    System.out.println("NULL");
                }
//                txtTeamId.setText(selectedItem.getTeamId().toString());
//                txtTeamName.setText(selectedItem.getName());
//                txtTeamMembershipFee.setText(getOr(selectedItem.getFee(), ""));
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

}
