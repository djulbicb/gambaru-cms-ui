package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserAttendanceEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.BarcodeRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserAttendanceRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserRepository;

import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.UserDetail;
import com.example.gambarucmsui.ui.form.FormBarcodeGet;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.common.LayoutUtil.formatPagination;
import static com.example.gambarucmsui.util.FormatUtil.*;

public class PanelAttendanceController implements PanelHeader {

    ;
    // PAGINATION
    private LocalDate paginationDate = LocalDate.now();
    private static final int PAGE_SIZE = 50;
    // FIELDS
    private final HashMap<Class, Repository> repositoryMap;
    private final UserRepository userRepo;
    private final UserAttendanceRepository attendanceRepo;
    private final BarcodeRepository barcodeRepository;
    // FXML TABLE
    private final Stage primaryStage;
    @FXML
    TableView<UserDetail> table;
    @FXML
    Label paginationLabel;
    ObservableList<UserDetail> tableItems;


    public PanelAttendanceController(Stage primaryStage, HashMap<Class, Repository> repositoryMap) {
        this.primaryStage = primaryStage;
        this.repositoryMap = repositoryMap;
        this.userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        this.attendanceRepo = (UserAttendanceRepository) repositoryMap.get(UserAttendanceRepository.class);
        this.barcodeRepository = (BarcodeRepository) repositoryMap.get(BarcodeRepository.class);

    }

    @FXML
    public void initialize() {
        System.out.println("Attendance loaded");

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
        listPageForDate();
    }

    @Override
    public void viewSwitched() {
        System.out.println("Panel attendance");
    }

    private void listPageForDate() {
        List<UserDetail> collect = userRepo.findAllForAttendanceDate(paginationDate, "lastAttendanceTimestamp")
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
    @FXML
    protected void addAttendanceManually() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("form-barcode-get.fxml"));
        FormBarcodeGet controller = new FormBarcodeGet();
        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

        Stage dialogStage = createStage("Dodaj korisnika u tim", root, primaryStage);
        dialogStage.showAndWait();


    }
    private void updatePagination(LocalDate localDate) {
        paginationDate = localDate;
        paginationLabel.setText(formatPagination(paginationDate));
    }


    public void onBarcodeRead(String barcodeIdStr) {
        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        addAttendanceForBarcodeId(paginationDate, barcodeId);
    }

    private void addAttendanceForBarcodeId(LocalDate paginationDate, Long barcodeId) {
        Optional<UserEntity> userOpt = userRepo.findUserByBarcodeId(barcodeId);
        if (userOpt.isPresent()) {
            BarcodeEntity barcode = barcodeRepository.findById(barcodeId);

            System.out.println("Adding attendance " + barcodeId);


            UserEntity en = userOpt.get();
            en.setLastAttendanceTimestamp(paginationDate.atStartOfDay());

            userRepo.save(en);
            attendanceRepo.save(new UserAttendanceEntity(barcode));

            listPageForDate();

            ToastView.showModal(String.format("Prisutnost registrovana za %s %s", en.getFirstName(), en.getLastName()));
        }
    }
}
