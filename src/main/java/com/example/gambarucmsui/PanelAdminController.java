package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.*;
import com.example.gambarucmsui.common.DelayedKeyListener;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.AttendanceDetail;
import com.example.gambarucmsui.ui.dto.MembershipDetail;
import com.example.gambarucmsui.ui.dto.TeamDetail;
import com.example.gambarucmsui.ui.dto.UserDetail;
import com.example.gambarucmsui.ui.form.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
    @FXML TableView<UserDetail> tableUsers;
    @FXML Label paginationLabel;
    @FXML TextField txtSearchBarcodeId;
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
        TableColumn<AttendanceDetail, String> attendanceColumn = new TableColumn<>("Dolaznost (prethodnih 100)");
        attendanceColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        tableUserAttendance.getColumns().add(attendanceColumn);
        tableUserAttendance.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<MembershipDetail, String> membershipColumn = new TableColumn<>("Placanje (prethodnih 100)");
        membershipColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        tableUserMembership.getColumns().add(membershipColumn);
        tableUserMembership.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configureTabUsers() {
        tableUsers.setRowFactory(tv -> {
            TableRow<UserDetail> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && !row.isEmpty()) {
                    UserDetail rowData = row.getItem();
                    // Perform actions with the clicked row data
                    System.out.println("Clicked row: " + rowData);

//                    txtUserBarcodeId.setDisable(true);
//                    txtUserBarcodeId.setText(rowData.getBarcodeId().toString());
//                    txtUserFirstName.setText(rowData.getFirstName());
//                    txtUserLastName.setText(rowData.getLastName());
//                    txtUserPhone.setText(rowData.getPhone());

//                    List<Attendance> userAttendanceEntities = attendanceRepo.fetchLastNEntriesForUserAttendance(rowData.getBarcodeId(), 100).stream().map(e->new Attendance(e.getTimestamp())).collect(Collectors.toList());
//                    List<Membership> userMembershipPaymentEntities = membershipRepo.fetchLastNEntriesForUserAttendance(rowData.getBarcodeId(), 100).stream().map(e->new Membership(e.getTimestamp())).collect(Collectors.toList());;

//                    tableUserAttendance.getItems().clear();
//                    tableUserMembership.getItems().clear();
//                    tableUserAttendance.getItems().addAll(userAttendanceEntities);
//                    tableUserMembership.getItems().addAll(userMembershipPaymentEntities);
                }
            });
            return row;
        });
    }

    private String getOr(Object fee, String opt) {
        if (fee == null) {
            return opt;
        }
        return fee.toString();
    }

    @Override
    public void viewSwitched() {
        System.out.println("Panel admin");
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
        List<UserDetail> collect = userRepo.findAll(currentPage, PAGE_SIZE, "createdAt",
                getOr(txtSearchBarcodeId, ""),
                getOr(txtSearchFirstName, ""), getOr(txtSearchLastName, ""))
                .stream().map(o -> UserDetail.fromEntity(o)
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("form-user-add.fxml"));
        FormUserAddController controller = new FormUserAddController(teamRepo);

        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

        // Create the modal dialog
        Stage dialogStage = createStage("Kreiraj novog korisnika", root, primaryStage);
        dialogStage.addEventFilter(KeyEvent.KEY_PRESSED, new DelayedKeyListener() {
            @Override
            public void onFinish(String word) {
                if (word == null || word.isBlank() ) {
                    return;
                }
                String numberOnly= word.trim().replaceAll("[^0-9]", "");
                if (numberOnly.length() < 10) {
                    return;
                }
                System.out.println("Input " + numberOnly);
                controller.onBarcodeScanned(numberOnly);
            }
        });

        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            FormUserAddController.Data data = controller.getData();

            BarcodeEntity barcode = barcodeRepo.findById(data.getBarcodeId());
            TeamEntity team = teamRepo.findByName(data.getTeamName());

            UserEntity en = new UserEntity(data.getFirstName(), data.getLastName(), data.getGender(), data.getPhone(), List.of(barcode), LocalDateTime.now());
            UserEntity saved = userRepo.save(en);

            barcode.setUser(saved);
            barcode.setTeam(team);
            barcode.setStatus(BarcodeEntity.Status.ASSIGNED);
            barcodeRepo.save(barcode);

            ToastView.showModal(String.format("Korisnik %s %s je dodat.", en.getFirstName(), en.getLastName()));

            loadTableUser();
        }
    }
    @FXML
    public void formUserUpdate() {

    }
    @FXML
    public void formUserDelete() {

    }

    @FXML
    void formUserAddUserToTeam(MouseEvent event) throws IOException {
        UserDetail selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj korisnika u tabeli pa klini.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("form-user-add-user-to-team.fxml"));
        FormUserAddUserToTeamController controller = new FormUserAddUserToTeamController(teamRepo, new FormUserAddUserToTeamController.Data(selectedItem.getFirstName(), selectedItem.getLastName(), "", ""));

        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

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

            BarcodeEntity barcode = barcodeRepo.findById(parseBarcodeStr(data.getBarcode()));
            TeamEntity team = teamRepo.findByName(data.getTeamName());
            UserEntity user = userRepo.findById(selectedItem.getUserId());

            barcode.setStatus(BarcodeEntity.Status.ASSIGNED);
            barcode.setTeam(team);
            barcode.setUser(user);

            barcodeRepo.save(barcode);
        }
        loadTableUser();
    }

    @FXML
    void formUserRemoveUserFromTeam(MouseEvent event) throws IOException {
        UserDetail selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj korisnika u tabeli pa klini.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("form-user-remove-user-from-team.fxml"));
        FormUserRemoveUserFromTeamController controller = new FormUserRemoveUserFromTeamController(teamRepo, new FormUserRemoveUserFromTeamController.Data(selectedItem.getUserId(), selectedItem.getFirstName(), selectedItem.getLastName(), selectedItem.getBarcodeId(), ""));

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
            FormUserRemoveUserFromTeamController.Data data = controller.getData();

            BarcodeEntity barcode = barcodeRepo.findById(parseBarcodeStr(data.getBarcode()));
            UserEntity user = userRepo.findById(selectedItem.getUserId());

            barcode.setStatus(BarcodeEntity.Status.DEACTIVATED);
            barcode.setTeam(null);

            barcodeRepo.save(barcode);
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

        TeamEntity byId = teamRepo.findById(selectedItem.getTeamId());

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
                teamRepo.delete(byId);
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
            TeamEntity en = new TeamEntity(data.getTeamName(), data.getMembershipPaymentFee());
            teamRepo.save(en);
            loadTableTeam();

            ToastView.showModal(String.format("Tim %s je dodat.", en.getName()));
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
            TeamEntity en = teamRepo.findById(data.getTeamId());
            en.setName(data.getTeamName());
            en.setMembershipPayment(data.getMembershipPaymentFee());
            teamRepo.save(en);
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
