package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserMembershipPaymentEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.BarcodeRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserMembershipRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserRepository;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.UserDetail;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.common.LayoutUtil.formatPagination;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class PanelMembershipController implements PanelHeader {
    private final UserMembershipRepository repository;
    private final UserRepository userRepo;
    private final UserMembershipRepository membershipRepo;
    private final BarcodeRepository barcodeRepo;
    // PAGINATION
    private LocalDate paginationDate = LocalDate.now();
    private static final int PAGE_SIZE = 50;
    // FXML TABLE
    @FXML
    TableView<UserDetail> table;
    @FXML
    Label paginationLabel;
    ObservableList<UserDetail> tableItems;

    public PanelMembershipController(Stage primaryStage, HashMap<Class, Repository> repositoryMap) {
        repository = (UserMembershipRepository) repositoryMap.get(UserMembershipPaymentEntity.class);
        userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        membershipRepo = (UserMembershipRepository) repositoryMap.get(UserMembershipRepository.class);
        barcodeRepo = (BarcodeRepository) repositoryMap.get(BarcodeRepository.class);
    }

    @FXML
    public void initialize() {
        System.out.println("Membership loaded");

        // Create columns
        TableColumn<UserDetail, String> idColumn = new TableColumn<>("Id");
        TableColumn<UserDetail, String> firstNameColumn = new TableColumn<>("Ime");
        TableColumn<UserDetail, Integer> lastNameColumn = new TableColumn<>("Prezime");
        TableColumn<UserDetail, Integer> genderNameColumn = new TableColumn<>("Pol");
        TableColumn<UserDetail, Integer> teamColumn = new TableColumn<>("Tim");
//        TableColumn<User, Integer> lastAttendanceColumn = new TableColumn<>("lastAttendanceTimestamp");
//        TableColumn<User, Integer> lastMembershipPaymentColumn = new TableColumn<>("lastMembershipPaymentTimestamp");
        TableColumn<UserDetail, Integer> createdAt = new TableColumn<>("createdAt");

        // Define how data should be displayed in columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("barcodeId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        genderNameColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("team"));
//        lastAttendanceColumn.setCellValueFactory(new PropertyValueFactory<>("lastAttendanceTimestamp"));
//        lastMembershipPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("lastMembershipPaymentTimestamp"));
        createdAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        table.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, genderNameColumn, teamColumn, createdAt);
        tableItems = table.getItems();

        updatePagination(LocalDate.now());
    }

    @Override
    public void viewSwitched() {
        listPageForDate();
        System.out.println("Panel membership");
    }

    private void listPageForDate() {
        List<UserDetail> collect = userRepo.findAllForMembershipDate(paginationDate, "lastMembershipPaymentTimestamp")
                .stream().map(o -> UserDetail.fromEntity(o)).collect(Collectors.toList());
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
    public void onBarcodeRead(String barcodeIdStr) {
        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        Optional<UserEntity> userOpt = userRepo.findUserByBarcodeId(barcodeId);
        if (userOpt.isPresent()) {
            BarcodeEntity barcode = barcodeRepo.findById(barcodeId);

            System.out.println("Adding attendance " + barcodeId);


            UserEntity en = userOpt.get();
//            en.setLastAttendanceTimestamp(LocalDateTime.now());

            userRepo.save(en);
            membershipRepo.save(new UserMembershipPaymentEntity(barcode, barcode.getTeam().getMembershipPayment()));

            listPageForDate();

            ToastView.showModal(String.format("Prisutnost registrovana za %s %s", en.getFirstName(), en.getLastName()));
        }
    }
}
