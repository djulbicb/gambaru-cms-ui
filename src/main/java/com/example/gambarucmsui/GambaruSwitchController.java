package com.example.gambarucmsui;

import com.example.gambarucmsui.database.repo.*;
import com.example.gambarucmsui.ports.impl.*;
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
import java.util.HashMap;

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
    private Pane panelMembership;
    private PanelMembershipController panelMembershipController;
    private Pane panelStatistics;
    private PanelStatisticsController panelStatisticsController;
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

        panelMembershipController = new PanelMembershipController(primaryStage);
        panelMembership = loadFxml(PathUtil.PANEL_MEMBERSHIP, panelMembershipController);

        panelStatisticsController = new PanelStatisticsController(primaryStage);
        panelStatistics = loadFxml(PathUtil.PANEL_STATISTICS, panelStatisticsController);

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

            UserRepository userRepository = new UserRepository(entityManager);
            BarcodeRepository barcodeRepository = new BarcodeRepository(entityManager);
            UserAttendanceRepository userAttendanceRepository = new UserAttendanceRepository(entityManager);
            UserMembershipRepository userMembershipRepository = new UserMembershipRepository(entityManager);
            TeamRepository teamRepository = new TeamRepository(entityManager);
            UserPictureRepository userPictureRepository = new UserPictureRepository(entityManager);

            Container.addBean(new ImageService());

            Container.addBean(new UserServiceSave(barcodeRepository, teamRepository, userRepository, userAttendanceRepository, userPictureRepository));
            Container.addBean(new TeamServiceSave(teamRepository));
            Container.addBean(new AttendanceService(barcodeRepository, userAttendanceRepository, userMembershipRepository));
            Container.addBean(new BarcodeService(barcodeRepository));
            Container.addBean(new StatisticsService(userAttendanceRepository, userMembershipRepository));
            Container.addBean(new MembershipService(barcodeRepository, userAttendanceRepository, userMembershipRepository));

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

    private void switchToMembership() {
        panelContent.getChildren().setAll(panelMembership);
        currentSelected = panelMembership;
        stretchInsideAnchorPance(panelMembership);
        panelMembershipController.viewSwitched();
    }

    private void switchToStatistic() {
        panelContent.getChildren().setAll(panelStatistics);
        currentSelected = panelStatistics;
        stretchInsideAnchorPance(panelStatistics);
        panelStatisticsController.viewSwitched();
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
        } else if (selectedButton.equals(headerBtnMembership)) {
            switchToMembership();
        } else if (selectedButton.equals(headerBtnStatistics)) {
            switchToStatistic();
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
        } else if (currentSelected == panelMembership) {
            panelMembershipController.onBarcodeRead(barcodeId);
        }
    }
}