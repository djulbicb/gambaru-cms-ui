package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserAttendanceEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserMembershipPaymentEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import com.example.gambarucmsui.common.DelayedKeyListener;
import com.example.gambarucmsui.common.DelayedListener;
import com.example.gambarucmsui.repo.Repository;
import com.example.gambarucmsui.repo.UserRepository;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;



public class HelloApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gambaru-entity-manager");
        EntityManager entityManager = emf.createEntityManager();

        Repository<BarcodeEntity> repoBarcode = new Repository<>(entityManager, BarcodeEntity.class);
        Repository<UserEntity> repoUser = new UserRepository(entityManager);
        Repository<UserAttendanceEntity> repoAttendance = new Repository<>(entityManager, UserAttendanceEntity.class);
        Repository<UserMembershipPaymentEntity> repoMembership = new Repository<>(entityManager, UserMembershipPaymentEntity.class);
        HashMap<Class, Repository> repos = new HashMap<>();
        repos.put(UserEntity.class, repoUser);




        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        HelloController controller = new HelloController(repos);
        loader.setController(controller);

        AnchorPane root = loader.load();
        Scene scene = new Scene(root, 1000, 700);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, new DelayedKeyListener() {
            @Override
            public void onFinish(String word) {
                if (word == null || word.isBlank() ) {
                    return;
                }
                System.out.println("Input " + word.trim());
                controller.onBarcodeScanned(Long.parseLong(word.trim()));
            }
        });

        stage.setTitle("Hello!");
        stage.setScene(scene);


        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());



        stage.show();
    }


    

    public static void main(String[] args) {
        launch();
    }
}