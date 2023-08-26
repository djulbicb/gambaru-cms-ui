package com.example.gambarucmsui;

import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ui.panel.*;
import com.example.gambarucmsui.util.PathUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.gambarucmsui.util.LayoutUtil.stretchInsideAnchorPance;

public class GambaruSwitchController implements FxmlViewHandler {
    private final Stage primaryStage;

    // FXML
    ////////////////////////////////////////

    @FXML
    AnchorPane panelContent;
    // TOGGLE BUTTONS
    @FXML
    ToggleGroup headerToggleBtns;
    @FXML
    ToggleButton headerBtnAttendance;
    @FXML
    ToggleButton headerBtnMembership;
    @FXML
    ToggleButton headerBtnStatistics;
    @FXML
    ToggleButton headerBtnAdminUser;
    @FXML
    ToggleButton headerBtnAdminTeam;
    @FXML
    ToggleButton headerBtnBarcode;

    // PANELS AND CONTROLLERS
    ////////////////////////////////////////
    private Pane currentSelected;
    private Pane panelAttendance;
    private PanelAttendanceController panelAttendanceController;
    private Pane panelAdminUser;
    private PanelAdminUserController panelAdminUserController;
    private Pane panelAdminTeam;
    private PanelAdminTeamController panelAdminTeamController;
    private Pane panelBarcode;
    private PanelBarcodeController panelBarcodeController;

    public GambaruSwitchController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() throws IOException {
        loadEntityManagementSystem();

        panelAttendanceController = new PanelAttendanceController(primaryStage);
        panelAttendance = loadFxml(PathUtil.PANEL_ATTENDANCE, panelAttendanceController);

        panelAdminUserController = new PanelAdminUserController(primaryStage);
        panelAdminUser = loadFxml(PathUtil.PANEL_ADMIN_USER, panelAdminUserController);

        panelAdminTeamController = new PanelAdminTeamController(primaryStage);
        panelAdminTeam = loadFxml(PathUtil.PANEL_ADMIN_TEAM, panelAdminTeamController);

        panelBarcodeController = new PanelBarcodeController(primaryStage);
        panelBarcode = loadFxml(PathUtil.PANEL_BARCODE, panelBarcodeController);

        setPanelHeaderToggleButtonsToIgnoreAction();
        switchToAttendance();
    }


    private void setPanelHeaderToggleButtonsToIgnoreAction() {
        // Barcode scanning sometimes forces view switching.
        // Because barcode scanner inputs ENTER at the end,
        // and if toggle button view was "in-focus" its going to trigger toggle
        for (Toggle toggle : headerToggleBtns.getToggles()) {
            ToggleButton button = (ToggleButton) toggle;
            button.setOnAction(actionEvent -> {
                toggle.setSelected(true);
            });
        }
    }

    void loadEntityManagementSystem() {
        System.out.println("Entity Managment starting.");
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("gambaru-entity-manager");
            EntityManager entityManager = emf.createEntityManager();

            Container.initBeans(entityManager);

            System.out.println("Entity Managment started.");
        } catch (Exception e) {
            System.out.println(String.format("There was an error starting entity system", e));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hello!");
            alert.setHeaderText(null);
            alert.setContentText("Baza nije startovana");
            alert.showAndWait();
            throw e;
        }

    }

    ;

    private void switchToAttendance() {
        panelContent.getChildren().setAll(panelAttendance);
        currentSelected = panelAttendance;
        stretchInsideAnchorPance(panelAttendance);
        panelAttendanceController.viewSwitched();
    }

    private void switchToAdminUser() {
        panelContent.getChildren().setAll(panelAdminUser);
        currentSelected = panelAdminUser;
        stretchInsideAnchorPance(panelAdminUser);
        panelAdminUserController.viewSwitched();
    }

    private void switchToAdminTeam() {
        panelContent.getChildren().setAll(panelAdminTeam);
        currentSelected = panelAdminTeam;
        stretchInsideAnchorPance(panelAdminTeam);
        panelAdminTeamController.viewSwitched();
    }

    private void switchToBarcode() {
        panelContent.getChildren().setAll(panelBarcode);
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
        } else if (selectedButton.equals(headerBtnAdminUser)) {
            switchToAdminUser();
        } else if (selectedButton.equals(headerBtnAdminTeam)) {
            switchToAdminTeam();
        } else if (selectedButton.equals(headerBtnBarcode)) {
            switchToBarcode();
        }
    }

    public void onBarcodeScanned(String barcodeId) {
        if (currentSelected == panelAttendance) {
            panelAttendanceController.onBarcodeRead(barcodeId);
        }
    }
}