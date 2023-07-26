package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.*;
import com.example.gambarucmsui.ui.PromptView;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.Attendance;
import com.example.gambarucmsui.ui.dto.Membership;
import com.example.gambarucmsui.ui.dto.TeamDetail;
import com.example.gambarucmsui.ui.dto.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.common.LayoutUtil.formatPagination;
import static com.example.gambarucmsui.util.FormatUtil.isDecimal;

public class PanelAdminController implements PanelHeader{
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
    @FXML TableView<User> tableUsers;
    @FXML Label paginationLabel;
    @FXML TextField txtSearchBarcodeId;
    @FXML TextField txtSearchFirstName;
    @FXML TextField txtSearchLastName;

    // USER DETAILS
    @FXML
    private TableView<Attendance> tableUserAttendance;
    @FXML private TableView<Membership> tableUserMembership;
    @FXML private TextField txtUserBarcodeId;
    @FXML private TextField txtUserFirstName;
    @FXML private TextField txtUserLastName;
    @FXML private TextField txtUserPhone;
    @FXML private ComboBox<String> comboUserGender;


    // TEAM TAB
    /////////////////////////////
    @FXML private TableView<TeamDetail> tableTeam;
    @FXML private TableView<?> tableTeamCoach;
    @FXML private TextField txtTeamMembershipFee;
    @FXML private TextField txtTeamName;
    @FXML private TextField txtTeamId;


    public PanelAdminController(HashMap<Class, Repository> repositoryMap) {
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
        TableColumn<Attendance, String> attendanceColumn = new TableColumn<>("Dolaznost (prethodnih 100)");
        attendanceColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        tableUserAttendance.getColumns().add(attendanceColumn);
        tableUserAttendance.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Membership, String> membershipColumn = new TableColumn<>("Placanje (prethodnih 100)");
        membershipColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        tableUserMembership.getColumns().add(membershipColumn);
        tableUserMembership.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configureTabUsers() {
        tableUsers.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && !row.isEmpty()) {
                    User rowData = row.getItem();
                    // Perform actions with the clicked row data
                    System.out.println("Clicked row: " + rowData);

                    txtUserBarcodeId.setDisable(true);
                    txtUserBarcodeId.setText(rowData.getBarcodeId().toString());
                    txtUserFirstName.setText(rowData.getFirstName());
                    txtUserLastName.setText(rowData.getLastName());
                    txtUserPhone.setText(rowData.getPhone());

                    List<Attendance> userAttendanceEntities = attendanceRepo.fetchLastNEntriesForUserAttendance(rowData.getBarcodeId(), 100).stream().map(e->new Attendance(e.getTimestamp())).collect(Collectors.toList());
                    List<Membership> userMembershipPaymentEntities = membershipRepo.fetchLastNEntriesForUserAttendance(rowData.getBarcodeId(), 100).stream().map(e->new Membership(e.getTimestamp())).collect(Collectors.toList());;

                    tableUserAttendance.getItems().clear();
                    tableUserMembership.getItems().clear();
                    tableUserAttendance.getItems().addAll(userAttendanceEntities);
                    tableUserMembership.getItems().addAll(userMembershipPaymentEntities);
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
    public void viewStitched() {
        System.out.println("Panel admin");
        listTableUsers();
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
        listTableUsers();
    }
    private void listTableUsers() {
        List<User> collect = userRepo.findAll(currentPage, PAGE_SIZE, "createdAt",
                getOr(txtSearchBarcodeId, ""),
                getOr(txtSearchFirstName, ""), getOr(txtSearchLastName, ""))
                .stream().map(o ->
                new User(o.getBarcode().getBarcodeId(), o.getFirstName(), o.getLastName(), "phone", o.getGender(), o.getTeam(), o.getCreatedAt(), o.getLastAttendanceTimestamp(), o.getLastMembershipPaymentTimestamp())).collect(Collectors.toList());
        tableUsers.getItems().setAll(collect);
    }

    @FXML
    protected void goNextPage() {
        currentPage++;
        paginationLabel.setText(formatPagination(currentPage));
        listTableUsers();
    }
    @FXML
    protected void goPrevPage() {
        if (currentPage <= 1) {
            return;
        }
        currentPage--;
        paginationLabel.setText(formatPagination(currentPage));
        listTableUsers();
    }


    @FXML
    public void btnUserResetForm() {
        txtUserBarcodeId.setDisable(false);
        txtUserBarcodeId.setText("");
        txtUserFirstName.setText("");
        txtUserLastName.setText("");
        txtUserPhone.setText("");
    }
    @FXML
    public void btnUserSaveOrUpdate() {
        String barcodeId = getOr(txtUserBarcodeId.getText(), "");
        String firstName = getOr(txtUserFirstName.getText(), "");
        String lastName = getOr(txtUserLastName.getText(), "");
//        String gender = comboUserGender.getSelectionModel().getSelectedItem();
        String phone = getOr(txtUserPhone.getAlignment(), "");

        if (firstName.isBlank()) {
            ToastView.showModal("Upisi ime");
            return;
        }
        if (lastName.isBlank()) {
            ToastView.showModal("Upisi prezime");
            return;
        }

        UserEntity en = new UserEntity();
        en.setFirstName(firstName);
        en.setLastName(lastName);
        en.setPhone(phone);
        en.setCreatedAt(LocalDateTime.now());
        en.setGender(UserEntity.Gender.MALE);

        if (barcodeId.isBlank()) {
            en.setBarcode(new BarcodeEntity(BarcodeEntity.Status.NOT_USED));
            userRepo.save(en);
            listTableUsers();
            return;
        }

        BarcodeEntity byId = barcodeRepo.findById(Long.parseLong(barcodeId));
        en.setBarcode(byId);
        userRepo.save(en);
        listTableUsers();
        return;
    }
    @FXML
    public void btnUserDelete() {

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
                txtTeamId.setText(selectedItem.getTeamId().toString());
                txtTeamName.setText(selectedItem.getName());
                txtTeamMembershipFee.setText(getOr(selectedItem.getFee(), ""));
            });
            return row;
        });

    }

    void loadTableTeam() {
        List<TeamDetail> teams = teamRepo.findAll().stream().map(en -> new TeamDetail(en.getTeamId(), en.getName(), en.getMembershipPayment())).collect(Collectors.toList());
        tableTeam.getItems().setAll(teams);
    }

    @FXML
    void btnTeamClearForm() {
        txtTeamId.setText("");
        txtTeamName.setText("");
        txtTeamMembershipFee.setText("");
        tableTeamCoach.getItems().clear();

        loadTableTeam();
    }
    @FXML
    void btnTeamSaveOrUpdate(MouseEvent event) {
        // Validation
        if (txtTeamName.getText() == null || txtTeamName.getText().isBlank()) {
            ToastView.showModal("Napisi ime tima");
            return;
        }
        if (txtTeamName.getText() == null || !isDecimal(txtTeamMembershipFee.getText())) {
            ToastView.showModal("Napisi cenu clanarine");
            return;
        }

        Long teamId = null;
        if (!txtTeamId.getText().isBlank()) {
            teamId = Long.parseLong(txtTeamId.getText());
        }
        String teamName = txtTeamName.getText();
        BigDecimal teamFee = BigDecimal.valueOf(Double.parseDouble(txtTeamMembershipFee.getText()));

        // Update
        if (teamId != null) {
            TeamEntity byId = teamRepo.findById(teamId);
            byId.setName(teamName);
            byId.setMembershipPayment(teamFee);
            teamRepo.save(byId);

            loadTableTeam();
            ToastView.showModal("Izmene su sacuvane");
            return;
        }

        // Create
        TeamEntity entity = new TeamEntity(teamName, teamFee);
        teamRepo.save(entity);
        ToastView.showModal("Novi tim je kreiran");

        loadTableTeam();
    }

    @FXML
    void btnTeamDelete() {
        if (txtTeamId.getText() == null || txtTeamId.getText().isBlank()) {
            ToastView.showModal("Selektuj tim za brisanje");
            return;
        }

        Long teamId = Long.parseLong(txtTeamId.getText());
        TeamEntity byId = teamRepo.findById(teamId);
        PromptView.showConfirmation("Brisanje tima", "Da li zelis da obrises tim " + byId.getName(), new Runnable() {
            @Override
            public void run() {
                teamRepo.delete(byId);
                ToastView.showModal("Tim je obrisan");
            }
        });

        btnTeamClearForm();
        loadTableTeam();
    }
}
