package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserAttendanceEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserMembershipPaymentEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

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

    // FIELDS
    ////////////////////////////////////////
    private AnchorPane currentSelected;
    private AnchorPane panelAttendance;
    private AnchorPane panelMembership;

    @FXML
    private void initialize() throws IOException {
        HashMap<Class, Repository> repositoryMap = loadEntityManagementSystem();

        FXMLLoader attendanceLoader = new FXMLLoader(getClass().getResource("panel-attendance.fxml"));
        attendanceLoader.setController(new PanelAttendanceController(repositoryMap));
        panelAttendance = attendanceLoader.load();

        FXMLLoader membershipLoader = new FXMLLoader(getClass().getResource("panel-membership.fxml"));
        membershipLoader.setController(new PanelMembershipController());
        panelMembership = membershipLoader.load();

        switchToAttendance();
    }

    HashMap<Class, Repository> loadEntityManagementSystem() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gambaru-entity-manager");
        EntityManager entityManager = emf.createEntityManager();

        Repository<BarcodeEntity> repoBarcode = new Repository<>(entityManager, BarcodeEntity.class);
        Repository<UserEntity> repoUser = new UserRepository(entityManager);
        Repository<UserAttendanceEntity> repoAttendance = new Repository<>(entityManager, UserAttendanceEntity.class);
        Repository<UserMembershipPaymentEntity> repoMembership = new Repository<>(entityManager, UserMembershipPaymentEntity.class);
        HashMap<Class, Repository> repos = new HashMap<>();
        repos.put(UserEntity.class, repoUser);

        return repos;
    };

    private void switchToAttendance() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelAttendance);
        currentSelected = panelAttendance;
        stretchInsideAnchorPance(panelAttendance);
    }

    private void switchToMembership() {
        panelContent.getChildren().clear();
        panelContent.getChildren().add(panelMembership);
        currentSelected = panelAttendance;
        stretchInsideAnchorPance(panelAttendance);
    }


    @FXML
    private void switchView() throws IOException {
        ToggleButton selectedButton = (ToggleButton) headerToggleBtns.getSelectedToggle();
        if (selectedButton.equals(headerBtnAttendance)) {
            switchToAttendance();
        } else if (selectedButton.equals(headerBtnMembership)) {
            switchToMembership();
        }
    }

//    private final Repository<UserEntity> userRepo;
//    private ObservableList<User> items;


//    private final int PAGE_SIZE = 50;
//    private int CURRENT_PAGE = 1;
//
//    @FXML
//    protected void testMembership() {
//    }
//
//    @FXML
//    protected void testAttendance() {
//    }
//


//    public void onBarcodeScanned(Long barcodeId) {
//        System.out.println(barcodeId);
//        UserEntity byId = userRepo.findById(barcodeId);
//        System.out.println(byId.getFirstName());
//        byId.setLastAttendanceTimestamp(LocalDateTime.now());
//        userRepo.save(byId);
//        listPage();
//    }
//


}