package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.UserAttendanceEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserMembershipPaymentEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserAttendanceRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserMembershipRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserRepository;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.common.LayoutUtil.formatPagination;

public class PanelAdminController {
    // PAGINATION
    private int currentPage = 1;
    private static final int PAGE_SIZE = 50;
    // FIELDS
    private final HashMap<Class, Repository> repositoryMap;
    private final UserRepository userRepo;
    private final UserAttendanceRepository attendanceRepo;
    private final UserMembershipRepository membershipRepo;

    // FXML TABLE
    @FXML
    TableView<User> table;
    @FXML
    Label paginationLabel;
    ObservableList<User> tableItems;
    @FXML
    TextField txtSearchBarcodeId;
    @FXML
    TextField txtSearchFirstName;
    @FXML
    TextField txtSearchLastName;

    // USER DETAILS
    @FXML
    private TableView<Attendance> tableUserAttendance;
    @FXML
    private TableView<Membership> tableUserMembership;
    @FXML
    private TextField txtBarcodeId;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    public PanelAdminController(HashMap<Class, Repository> repositoryMap) {
        this.repositoryMap = repositoryMap;
        this.userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        this.attendanceRepo = (UserAttendanceRepository) repositoryMap.get(UserAttendanceRepository.class);
        this.membershipRepo = (UserMembershipRepository) repositoryMap.get(UserMembershipRepository.class);
    }

    @FXML
    private void initialize() {
        System.out.println("PanelAdminController");

        // Create columns
        TableColumn<User, String> idColumn = new TableColumn<>("Id");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("Ime");
        TableColumn<User, Integer> lastNameColumn = new TableColumn<>("Prezime");
        TableColumn<User, Integer> genderNameColumn = new TableColumn<>("Pol");
        TableColumn<User, Integer> teamColumn = new TableColumn<>("Tim");
        TableColumn<User, Integer> lastAttendanceColumn = new TableColumn<>("lastAttendanceTimestamp");
        TableColumn<User, Integer> lastMembershipPaymentColumn = new TableColumn<>("lastMembershipPaymentTimestamp");
        TableColumn<User, Integer> createdAt = new TableColumn<>("createdAt");
//        TableColumn<User, Button> openColumn = new TableColumn<>("Open");

        // Define how data should be displayed in columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("barcodeId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        genderNameColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("team"));
        lastAttendanceColumn.setCellValueFactory(new PropertyValueFactory<>("lastAttendanceTimestamp"));
        lastMembershipPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("lastMembershipPaymentTimestamp"));
        createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

//        openColumn.setCellFactory(param -> new TableCell<>() {
//            private final Button button = new Button("Click");
//
//            {
//                button.setOnAction(event -> {
//                    User person = getTableRow().getItem();
//                    if (person != null) {
//                        System.out.println("Clicked on " + person.getFirstName());
//                    }
//                });
//            }
//
//            @Override
//            protected void updateItem(Button item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty) {
//                    setGraphic(null);
//                } else {
//                    setGraphic(button);
//                }
//            }
//        });

        table.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, genderNameColumn, teamColumn, lastAttendanceColumn, lastMembershipPaymentColumn, createdAt);
        tableItems = table.getItems();

        table.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && !row.isEmpty()) {
                    User rowData = row.getItem();
                    // Perform actions with the clicked row data
                    System.out.println("Clicked row: " + rowData);

                    txtBarcodeId.setText(rowData.getBarcodeId().toString());
                    txtFirstName.setText(rowData.getFirstName());
                    txtLastName.setText(rowData.getLastName());

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

       listPage();



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

    private String getOr(TextField txt, String opt) {
        if (txt == null) {
            return opt;
        }
        return txt.getText();
    }

    @FXML
    public void textSearchFieldChanged() {
        listPage();
    }
    private void listPage() {
        List<User> collect = userRepo.findAll(currentPage, PAGE_SIZE, "createdAt",
                getOr(txtSearchBarcodeId, ""),
                getOr(txtSearchFirstName, ""), getOr(txtSearchLastName, ""))
                .stream().map(o ->
                new User(o.getBarcode().getBarcodeId(), o.getFirstName(), o.getLastName(), o.getGender(), o.getTeam(), o.getCreatedAt(), o.getLastAttendanceTimestamp(), o.getLastMembershipPaymentTimestamp())).collect(Collectors.toList());
        tableItems.setAll(collect);
    }

    @FXML
    protected void goNextPage() {
        currentPage++;
        paginationLabel.setText(formatPagination(currentPage));
        listPage();
    }
    @FXML
    protected void goPrevPage() {
        if (currentPage <= 1) {
            return;
        }
        currentPage--;
        paginationLabel.setText(formatPagination(currentPage));
        listPage();

    }

}
