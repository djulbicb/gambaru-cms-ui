package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.UserAttendanceEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserAttendanceRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserRepository;

import com.example.gambarucmsui.ui.ModalView;
import com.example.gambarucmsui.ui.ToastView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

            Label messageLabel = new Label("Attendance added for " + byId.getFirstName());
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
            ToastView.showModal(messageLabel, 500,500);
        }
    }

    @FXML
    public void test() {
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        Button saveButton = new Button("Save");
        Button closeButton = new Button("Close");

        ModalView<String> modalView = new ModalView<>(new AnchorPane(), result -> {
            System.out.println("Result: " + result);
        });

        saveButton.setOnAction(saveEvent -> {
            String fullName = firstNameField.getText() + " " + lastNameField.getText();
            modalView.closeModal(fullName);
        });
        closeButton.setOnAction(saveEvent -> {
            modalView.closeModal(null);
        });

        AnchorPane modalContent = new AnchorPane();
        modalContent.setPadding(new Insets(10));
        modalContent.getChildren().addAll(firstNameField, lastNameField, saveButton, closeButton);
        AnchorPane.setTopAnchor(firstNameField, 10.0);
        AnchorPane.setLeftAnchor(firstNameField, 10.0);
        AnchorPane.setTopAnchor(lastNameField, 40.0);
        AnchorPane.setLeftAnchor(lastNameField, 10.0);
        AnchorPane.setTopAnchor(saveButton, 80.0);
        AnchorPane.setLeftAnchor(saveButton, 10.0);

        modalView.setContent(modalContent);
        modalView.showAndWait();
    }
    @FXML
    public void testWORKING() {
        Stage modalStage = createModalStage();

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        Button saveButton = new Button("Save");
        saveButton.setOnAction(saveEvent -> {
            String fullName = firstNameField.getText() + " " + lastNameField.getText();
            closeAndReturnResult(modalStage, fullName);
        });

        AnchorPane modalContent = new AnchorPane();
        modalContent.setPadding(new Insets(10));
        modalContent.getChildren().addAll(firstNameField, lastNameField, saveButton);

        AnchorPane.setTopAnchor(firstNameField, 10.0);
        AnchorPane.setLeftAnchor(firstNameField, 10.0);
        AnchorPane.setTopAnchor(lastNameField, 40.0);
        AnchorPane.setLeftAnchor(lastNameField, 10.0);
        AnchorPane.setTopAnchor(saveButton, 80.0);
        AnchorPane.setLeftAnchor(saveButton, 10.0);

        modalStage.setScene(new Scene(modalContent));
        modalStage.showAndWait();
    }

    private Stage createModalStage() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initStyle(StageStyle.TRANSPARENT);
        modalStage.setResizable(false);
        modalStage.setAlwaysOnTop(true);
        return modalStage;
    }

    private void closeAndReturnResult(Stage modalStage, String result) {
        modalStage.close();
        System.out.println("Result: " + result);
    }


}
