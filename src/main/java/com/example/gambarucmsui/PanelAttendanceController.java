package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.UserAttendanceEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserAttendanceRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserRepository;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.common.LayoutUtil.formatPagination;

public class PanelAttendanceController {
    ;
    // PAGINATION
    private int currentPage = 1;
    private static final int PAGE_SIZE = 50;
    // FIELDS
    private final HashMap<Class, Repository> repositoryMap;
    private final UserRepository userRepo;
    private final UserAttendanceRepository attendanceRepo;
    // FXML TABLE
    @FXML
    TableView<User> table;
    @FXML
    Label paginationLabel;
    ObservableList<User> tableItems;


    public PanelAttendanceController(HashMap<Class, Repository> repositoryMap) {
        this.repositoryMap = repositoryMap;
        this.userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        this.attendanceRepo = (UserAttendanceRepository) repositoryMap.get(UserAttendanceRepository.class);
    }

    @FXML
    private void initialize() {
        System.out.println("Attendance loaded");

        // Create columns
        TableColumn<User, String> idColumn = new TableColumn<>("Id");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("Ime");
        TableColumn<User, Integer> lastNameColumn = new TableColumn<>("Prezime");
        TableColumn<User, Integer> genderNameColumn = new TableColumn<>("Pol");
        TableColumn<User, Integer> teamColumn = new TableColumn<>("Tim");
        TableColumn<User, Integer> lastAttendanceColumn = new TableColumn<>("lastAttendanceTimestamp");
        TableColumn<User, Integer> lastMembershipPaymentColumn = new TableColumn<>("lastMembershipPaymentTimestamp");
        TableColumn<User, Integer> createdAt = new TableColumn<>("createdAt");

        // Define how data should be displayed in columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("barcodeId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        genderNameColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("team"));
        lastAttendanceColumn.setCellValueFactory(new PropertyValueFactory<>("lastAttendanceTimestamp"));
        lastMembershipPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("lastMembershipPaymentTimestamp"));
        createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        table.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, genderNameColumn, teamColumn, lastAttendanceColumn, lastMembershipPaymentColumn, createdAt);
        tableItems = table.getItems();

        listPage();
    }

    private void listPage() {
        List<User> collect = userRepo.findAll(currentPage, PAGE_SIZE, "lastAttendanceTimestamp").stream().map(o ->
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

    public void onBarcodeRead(Long barcodeId) {
        Optional<UserEntity> userByBarcodeId = userRepo.findUserByBarcodeId(barcodeId);
        if (userByBarcodeId.isPresent()) {
            System.out.println("Adding attendance " + barcodeId);

            UserEntity byId = userByBarcodeId.get();
            byId.setLastAttendanceTimestamp(LocalDateTime.now());

            userRepo.save(byId);
            attendanceRepo.save(new UserAttendanceEntity(byId.getBarcode()));

            listPage();
        }
    }
}
