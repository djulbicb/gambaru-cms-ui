package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.repo.*;
import com.example.gambarucmsui.ui.ToastView;
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

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;

import static com.example.gambarucmsui.common.LayoutUtil.stretchInsideAnchorPance;

public class GambaruSwitchController {

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

    @FXML
    private void initialize() throws IOException {
        HashMap<Class, Repository> repositoryMap = loadEntityManagementSystem();

        panelAttendanceController = new PanelAttendanceController(repositoryMap);
        FXMLLoader attendanceLoader = new FXMLLoader(getClass().getResource("panel-attendance.fxml"));
        attendanceLoader.setController(panelAttendanceController);
        panelAttendance = attendanceLoader.load();

        panelMembershipController = new PanelMembershipController(repositoryMap);
        FXMLLoader membershipLoader = new FXMLLoader(getClass().getResource("panel-membership.fxml"));
        membershipLoader.setController(panelMembershipController);
        panelMembership = membershipLoader.load();

        panelStatisticsController = new PanelStatisticsController(repositoryMap);
        FXMLLoader statisticsLoader = new FXMLLoader(getClass().getResource("panel-statistics.fxml"));
        statisticsLoader.setController(panelStatisticsController);
        panelStatistics = statisticsLoader.load();

        panelAdminController = new PanelAdminController(repositoryMap);
        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("panel-admin.fxml"));
        settingsLoader.setController(panelAdminController);
        panelSettings = settingsLoader.load();

        panelBarcodeController = new PanelBarcodeController(repositoryMap);
        FXMLLoader barcodeLoader = new FXMLLoader(getClass().getResource("panel-barcode.fxml"));
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

    HashMap<Class, Repository> loadEntityManagementSystem() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("gambaru-entity-manager");
            EntityManager entityManager = emf.createEntityManager();

            HashMap<Class, Repository> repos = new HashMap<>();
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
        panelAttendanceController.viewStitched();
    }

    private void switchToMembership() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelMembership);
        currentSelected = panelMembership;
        stretchInsideAnchorPance(panelMembership);
        panelMembershipController.viewStitched();
    }
    private void switchToStatistic() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelStatistics);
        currentSelected = panelStatistics;
        stretchInsideAnchorPance(panelStatistics);
        panelStatisticsController.viewStitched();
    }
    private void switchToAdmin() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelSettings);
        currentSelected = panelSettings;
        stretchInsideAnchorPance(panelSettings);
        panelAdminController.viewStitched();
    }
    private void switchToBarcode() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelBarcode);
        currentSelected = panelBarcode;
        stretchInsideAnchorPance(panelBarcode);
        panelBarcodeController.viewStitched();
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

    public void onBarcodeScanned(Long barcodeId) {
        if (currentSelected == panelAttendance) {
            panelAttendanceController.onBarcodeRead(barcodeId);
        } else if (currentSelected == panelMembership) {
            panelMembershipController.onBarcodeRead(barcodeId);
        }
    }
}