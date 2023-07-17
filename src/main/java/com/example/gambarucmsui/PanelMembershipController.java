package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.UserMembershipPaymentEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserMembershipRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserRepository;
import com.example.gambarucmsui.ui.ToastView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.common.LayoutUtil.formatPagination;

public class PanelMembershipController {
    private final UserMembershipRepository repository;
    private final UserRepository userRepo;
    private final UserMembershipRepository membershipRepo;
    // PAGINATION
    private LocalDate paginationDate = LocalDate.now();
    private static final int PAGE_SIZE = 50;
    // FXML TABLE
    @FXML
    TableView<User> table;
    @FXML
    Label paginationLabel;
    ObservableList<User> tableItems;

    public PanelMembershipController(HashMap<Class, Repository> repositoryMap) {
        repository = (UserMembershipRepository) repositoryMap.get(UserMembershipPaymentEntity.class);
        userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        membershipRepo = (UserMembershipRepository) repositoryMap.get(UserMembershipRepository.class);
    }

    @FXML
    private void initialize() {
        System.out.println("Membership loaded");

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

        updatePagination(LocalDate.now());
        listPageForDate();
    }

    private void listPageForDate() {
        List<User> collect = userRepo.findAllForDate(paginationDate, "lastMembershipPaymentTimestamp").stream().map(o ->
                new User(o.getBarcode().getBarcodeId(), o.getFirstName(), o.getLastName(), o.getGender(), o.getTeam(), o.getCreatedAt(), o.getLastAttendanceTimestamp(), o.getLastMembershipPaymentTimestamp())).collect(Collectors.toList());
        tableItems.setAll(collect);
    }

    @FXML
    protected void goNextPage() {
        updatePagination(paginationDate.plusDays(1));
        listPageForDate();
    }
    @FXML
    protected void goPrevPage() {
        updatePagination(paginationDate.minusDays(1));
        listPageForDate();
    }
    private void updatePagination(LocalDate localDate) {
        paginationDate = localDate;
        paginationLabel.setText(formatPagination(paginationDate));
    }
    public void onBarcodeRead(Long barcodeId) {
        Optional<UserEntity> userByBarcodeId = userRepo.findUserByBarcodeId(barcodeId);
        if (userByBarcodeId.isPresent()) {
            System.out.println("Adding membership " + barcodeId);
            UserEntity byId = userByBarcodeId.get();
            byId.setLastMembershipPaymentTimestamp(LocalDateTime.now());
            userRepo.save(byId);

            membershipRepo.save(new UserMembershipPaymentEntity(byId.getBarcode()));
            listPageForDate();

            Label messageLabel = new Label("Membership added for " + byId.getFirstName());
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
            ToastView.showModal(messageLabel, 500,500);
        }
    }
}
