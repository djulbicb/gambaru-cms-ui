package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.common.DelayedKeyListener;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceLoadForUserPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeStatusChangePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeUpdatePort;
import com.example.gambarucmsui.ports.interfaces.team.TeamLoadPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
import com.example.gambarucmsui.ports.interfaces.team.TeamUpdatePort;
import com.example.gambarucmsui.ports.interfaces.user.*;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.admin.subtables.AttendanceDetail;
import com.example.gambarucmsui.ui.dto.admin.subtables.BarcodeDetail;
import com.example.gambarucmsui.ui.dto.admin.UserAdminDetail;
import com.example.gambarucmsui.ui.form.*;
import com.example.gambarucmsui.util.LayoutUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.util.FormatUtil.*;
import static com.example.gambarucmsui.util.LayoutUtil.*;
import static com.example.gambarucmsui.util.PathUtil.*;

public class PanelAdminUserController implements PanelHeader {
    private final Stage primaryStage;
    private final UserSavePort userSavePort;
    private final UserUpdatePort userUpdatePort;
    private final TeamSavePort teamSavePort;
    private final TeamUpdatePort teamUpdatePort;
    private final UserAddToTeamPort userAddToTeamPort;
    private final UserLoadPort loadUserPort;
    private final TeamLoadPort teamLoadPort;
    private final IsUserAlreadyInThisTeamPort isUserAlreadyInThisTeamPort;
    private final BarcodeLoadPort barcodeLoadPort;
    private final AttendanceLoadForUserPort loadAttendance;
    private final BarcodeUpdatePort barcodeUpdatePort;
    private final BarcodeStatusChangePort barcodeStatusChange;
    private final UserPictureLoad userPictureLoad;

    // FXML
    ///////////////////////////////////////////////////
    @FXML
    private TableView<?> tableTeamUsers;
    @FXML
    private TableView<UserAdminDetail> tableUsers;
    @FXML
    private Label paginationLabel;
    @FXML
    private TextField txtSearchFirstName;
    @FXML
    private TextField txtSearchLastName;
    @FXML
    private TextField txtSearchBarcode;
    @FXML
    private ComboBox<String> cmbSearchTeam;
    @FXML
    private CheckBox checkSearchOnlyActive;
    // FXML - USER DETAILS
    ///////////////////////////////////////////////////
    @FXML private Label lblUserDetailsEmpty;
    @FXML private Label lblUserDetailsFirstName;
    @FXML private Label lblUserDetailsLastName;
    @FXML private Label lblUserDetailsPhone;
    @FXML private Pane paneUserDetailsPicture;
    @FXML private VBox paneUserDetails;
    @FXML private TableView<AttendanceDetail> tableUserAttendance;
    @FXML private TableView<BarcodeDetail> tableUserBarcode;

    @Override
    public void initialize() {
        configureTabUsers();
    }

    @Override
    public void viewSwitched() {
        System.out.println("Switched to panel Admin.");
        loadTableUser();
        loadTeamsToUserComboBox();
    }

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

    private void loadTeamsToUserComboBox() {
        cmbSearchTeam.getItems().clear();
        cmbSearchTeam.getItems().add(null);
        for (TeamEntity team : teamLoadPort.findAllActive()) {
            cmbSearchTeam.getItems().add(team.getName());
        }
    }

    private void configureTabUsers() {
        loadTeamsToUserComboBox();

        // Add on row click listener
        tableUsers.setRowFactory(tv -> {
            TableRow<UserAdminDetail> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && !row.isEmpty()) {
                    UserAdminDetail rowData = row.getItem();
                    Optional<PersonEntity> userOpt = loadUserPort.loadUserByUserId(rowData.getUserId());
                    if (userOpt.isPresent()) {
                        PersonEntity user = userOpt.get();
                        loadTableUserDetails(user);
                        loadTableUserAttendance(user);
                        loadTableUserBarcode(user);
                    }
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
        stretchColumnsToEqualSize(tableUserBarcode);
    }

    public PanelAdminUserController(Stage primaryStage) {
        this.primaryStage = primaryStage;

        loadUserPort = Container.getBean(UserLoadPort.class);
        userSavePort = Container.getBean(UserSavePort.class);
        userUpdatePort = Container.getBean(UserUpdatePort.class);
        teamSavePort = Container.getBean(TeamSavePort.class);
        teamUpdatePort = Container.getBean(TeamUpdatePort.class);
        userAddToTeamPort = Container.getBean(UserAddToTeamPort.class);
        teamLoadPort = Container.getBean(TeamLoadPort.class);
        isUserAlreadyInThisTeamPort = Container.getBean(IsUserAlreadyInThisTeamPort.class);
        barcodeLoadPort = Container.getBean(BarcodeLoadPort.class);
        loadAttendance = Container.getBean(AttendanceLoadForUserPort.class);
        barcodeUpdatePort = Container.getBean(BarcodeUpdatePort.class);
        barcodeStatusChange = Container.getBean(BarcodeStatusChangePort.class);
        userPictureLoad = Container.getBean(UserPictureLoad.class);
    }

    private void hideUserDetailsPane() {
        paneUserDetails.setOpacity(0);
        lblUserDetailsEmpty.setOpacity(1);
    }
    private void showUserDetailsPane() {
        paneUserDetails.setOpacity(1);
        lblUserDetailsEmpty.setOpacity(0);
    }
    private void loadTableUserDetails(PersonEntity user) {
        showUserDetailsPane();
        lblUserDetailsFirstName.setText(user.getFirstName());
        lblUserDetailsLastName.setText(user.getLastName());
        lblUserDetailsPhone.setText(user.getPhone());

        ImageView imageView = userPictureLoad.loadUserPictureByUserId(user.getPersonId(), 200, 150);
        paneUserDetailsPicture.getChildren().setAll(imageView);
        stretchInsideAnchorPance(imageView);

    }
    private void loadTableUserAttendance(PersonEntity user) {
        List<AttendanceDetail> userAttendanceEntities = loadAttendance.fetchLastNEntriesForUserAttendance(user.getBarcodes(), 100).stream().map(e -> new AttendanceDetail(e.getBarcode(), e.getTimestamp(), e.getBarcode().getTeam())).collect(Collectors.toList());
        tableUserAttendance.getItems().setAll(userAttendanceEntities);
    }

    private void loadTableUserBarcode(PersonEntity user) {
        List<BarcodeDetail> barcodeDetails = new ArrayList<>();
        for (BarcodeEntity barcode : user.getBarcodes()) {
            TeamEntity team = barcode.getTeam();
            barcodeDetails.add(new BarcodeDetail(formatBarcode(barcode.getBarcodeId()), barcode.getStatus(), team.getName()));
        }
        tableUserBarcode.getItems().setAll(barcodeDetails);
    }

    private TableColumn<BarcodeDetail, String> buildUserBarcodeButtonColumn() {
        TableColumn<BarcodeDetail, String> activateColumn = new TableColumn<>("Deaktivacija");
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
                            if (selectedItem != null) {

                                ValidatorResponse res = barcodeStatusChange.activateBarcode(parseBarcodeStr(selectedItem.getBarcode()));

                                if (res.hasErrors()) {
                                    ToastView.showModal(res.getErrorOrEmpty(BarcodeEntity.BARCODE_ID));
                                    return;
                                }

                                ToastView.showModal(res.getMessage());
                                loadTableUser();
                            }
                        });
                        deactivateBtn.setOnMouseClicked(event -> {
                            BarcodeDetail selectedItem = getTableView().getItems().get(getIndex());
                            if (selectedItem != null) {

                                ValidatorResponse res = barcodeStatusChange.deactivateBarcode(parseBarcodeStr(selectedItem.getBarcode()));

                                if (res.hasErrors()) {
                                    ToastView.showModal(res.getErrorOrEmpty(BarcodeEntity.BARCODE_ID));
                                    return;
                                }

                                ToastView.showModal(res.getMessage());
                                loadTableUser();
                            }

                        });
                    }
                    setGraphic(pane);
                }
            }
        };
    }

    @FXML
    public void textSearchFieldChanged() {
        loadTableUser();
    }

    private void loadTableUser() {
        List<UserAdminDetail> collect = loadUserPort.findAll(
                        currentPage, PAGE_SIZE, "createdAt",
                        LayoutUtil.getOr(cmbSearchTeam, ""),
                        LayoutUtil.getOr(txtSearchFirstName, ""),
                        LayoutUtil.getOr(txtSearchLastName, ""),
                        LayoutUtil.getNumericOr(txtSearchBarcode, ""),
                        checkSearchOnlyActive.isSelected())
                .stream().map(o -> UserAdminDetail.fromEntityToFull(o)
                ).collect(Collectors.toList());

        tableUsers.getItems().setAll(collect);
        hideUserDetailsPane();
    }

    @FXML
    void onCmbSearchTeamChange(ActionEvent event) {
        loadTableUser();
    }

    @FXML
    public void formUserAdd() throws IOException {
        Pane root = loadFxml(FORM_USER_ADD, new FormUserAddController());
        createStage("Kreiraj novog korisnika", root, primaryStage).showAndWait();
        loadTableUser();
    }

    @FXML
    public void formUserUpdate() throws IOException {
        UserAdminDetail selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj korisnika u tabeli pa klini.");
            return;
        }
        Pane root = loadFxml(FORM_USER_ADD, new FormUserUpdateController(selectedItem.getUserId()));
        createStage("Izmeni korisnika", root, primaryStage).showAndWait();

        loadTableUser();
    }

    @FXML
    void formUserAddUserToTeam(MouseEvent event) throws IOException {
        UserAdminDetail selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj korisnika u tabeli pa klini.");
            return;
        }

        FormUserAddUserToTeamController controller = new FormUserAddUserToTeamController(selectedItem.getUserId());
        Pane root = loadFxml(FORM_USER_ADD_USER_TO_TEAM, controller);

        Stage dialogStage = createStage("Dodaj korisnika u tim", root, primaryStage);
        dialogStage.addEventFilter(KeyEvent.KEY_PRESSED, new DelayedKeyListener() {
            @Override
            public void onFinish(String word) {
                if (isBarcodeString(word)) {
                    controller.onBarcodeScanned(cleanBarcodeStr(word));
                }
            }
        });
        dialogStage.showAndWait();
        loadTableUser();
    }
}
