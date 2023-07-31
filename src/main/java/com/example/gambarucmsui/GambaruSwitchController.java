package com.example.gambarucmsui;

import com.example.gambarucmsui.database.repo.*;
import com.example.gambarucmsui.ui.panel.*;
import com.example.gambarucmsui.util.PathUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

import static com.example.gambarucmsui.util.LayoutUtil.stretchInsideAnchorPance;

public class GambaruSwitchController {
    private final Stage primaryStage;

    // FXML
    ////////////////////////////////////////

    @FXML AnchorPane panelContent;
    // TOGGLE BUTTONS
    @FXML ToggleGroup headerToggleBtns;
    @FXML ToggleButton headerBtnAttendance;
    @FXML ToggleButton headerBtnMembership;
    @FXML ToggleButton headerBtnStatistics;
    @FXML ToggleButton headerBtnSettings;
    @FXML ToggleButton headerBtnBarcode;

    // PANELS AND CONTROLLERS
    ////////////////////////////////////////
    private AnchorPane currentSelected;
    private AnchorPane panelAttendance;
    private PanelAttendanceController panelAttendanceController;
    private AnchorPane panelMembership;
    private PanelMembershipController panelMembershipController;
    private AnchorPane panelStatistics;
    private PanelStatisticsController panelStatisticsController;
    private AnchorPane panelSettings;
    private PanelAdminController panelAdminController;
    private AnchorPane panelBarcode;
    private PanelBarcodeController panelBarcodeController;

    public GambaruSwitchController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() throws IOException {
        HashMap<Class, Object> repositoryMap = loadEntityManagementSystem();

        panelAttendanceController = new PanelAttendanceController(primaryStage, repositoryMap);
        FXMLLoader attendanceLoader = new FXMLLoader(getClass().getResource(PathUtil.PANEL_ATTENDANCE));
        attendanceLoader.setController(panelAttendanceController);
        panelAttendance = attendanceLoader.load();

        panelMembershipController = new PanelMembershipController(primaryStage, repositoryMap);
        FXMLLoader membershipLoader = new FXMLLoader(getClass().getResource(PathUtil.PANEL_MEMBERSHIP));
        membershipLoader.setController(panelMembershipController);
        panelMembership = membershipLoader.load();

        panelStatisticsController = new PanelStatisticsController(primaryStage, repositoryMap);
        FXMLLoader statisticsLoader = new FXMLLoader(getClass().getResource(PathUtil.PANEL_STATISTICS));
        statisticsLoader.setController(panelStatisticsController);
        panelStatistics = statisticsLoader.load();

        panelAdminController = new PanelAdminController(primaryStage, repositoryMap);
        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource(PathUtil.PANEL_ADMIN));
        settingsLoader.setController(panelAdminController);
        panelSettings = settingsLoader.load();

        panelBarcodeController = new PanelBarcodeController(primaryStage, repositoryMap);
        FXMLLoader barcodeLoader = new FXMLLoader(getClass().getResource(PathUtil.PANEL_BARCODE));
        barcodeLoader.setController(panelBarcodeController);
        panelBarcode = barcodeLoader.load();

        setHeaderToggleButtonToIgnoreAction();
        switchToAttendance();
    }

    private void setHeaderToggleButtonToIgnoreAction() {
        for (Toggle toggle : headerToggleBtns.getToggles()) {
            ToggleButton button = (ToggleButton) toggle;
            button.setOnAction(actionEvent -> {
                System.out.println("Some action");
                toggle.setSelected(true);
            });
        }
    }

    HashMap<Class, Object> loadEntityManagementSystem() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("gambaru-entity-manager");
            EntityManager entityManager = emf.createEntityManager();

            HashMap<Class, Object> repos = new HashMap<>();
            repos.put(UserRepository.class, new UserRepository(entityManager));
            repos.put(BarcodeRepository.class, new BarcodeRepository(entityManager));
            repos.put(UserAttendanceRepository.class, new UserAttendanceRepository(entityManager));
            repos.put(UserMembershipRepository.class, new UserMembershipRepository(entityManager));
            repos.put(TeamRepository.class, new TeamRepository(entityManager));

            return repos;

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hello!");
            alert.setHeaderText(null);
            alert.setContentText("Baza nije startovana");
            alert.showAndWait();

            throw e;
        }
    };

    private void switchToAttendance() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelAttendance);
        currentSelected = panelAttendance;
        stretchInsideAnchorPance(panelAttendance);
        panelAttendanceController.viewSwitched();
    }

    private void switchToMembership() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelMembership);
        currentSelected = panelMembership;
        stretchInsideAnchorPance(panelMembership);
        panelMembershipController.viewSwitched();
    }
    private void switchToStatistic() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelStatistics);
        currentSelected = panelStatistics;
        stretchInsideAnchorPance(panelStatistics);
        panelStatisticsController.viewSwitched();
    }
    private void switchToAdmin() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelSettings);
        currentSelected = panelSettings;
        stretchInsideAnchorPance(panelSettings);
        panelAdminController.viewSwitched();
    }
    private void switchToBarcode() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelBarcode);
        currentSelected = panelBarcode;
        stretchInsideAnchorPance(panelBarcode);
        panelBarcodeController.viewSwitched();
    }

    @FXML
    private void switchView() throws IOException {
        if (headerToggleBtns == null || headerToggleBtns.getSelectedToggle() == null) {
            return;
        }
        ToggleButton selectedButton = (ToggleButton) headerToggleBtns.getSelectedToggle();
        if (selectedButton.equals(headerBtnAttendance)) {
            switchToAttendance();
        } else if (selectedButton.equals(headerBtnMembership)) {
            switchToMembership();
        } else if (selectedButton.equals(headerBtnStatistics)) {
            switchToStatistic();
        } else if (selectedButton.equals(headerBtnSettings)) {
            switchToAdmin();
        } else if (selectedButton.equals(headerBtnBarcode)) {
            switchToBarcode();
        }
    }

    public void onBarcodeScanned(String barcodeId) {
        if (currentSelected == panelAttendance) {
            panelAttendanceController.onBarcodeRead(barcodeId);
        } else if (currentSelected == panelMembership) {
            panelMembershipController.onBarcodeRead(barcodeId);
        }
    }
}