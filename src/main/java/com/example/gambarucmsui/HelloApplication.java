package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserAttendanceEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserMembershipPaymentEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import com.example.gambarucmsui.repo.Repository;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;



public class HelloApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gambaru-entity-manager");
        EntityManager entityManager = emf.createEntityManager();

        Repository<BarcodeEntity> repoBarcode = new Repository<>(entityManager, BarcodeEntity.class);
        Repository<UserEntity> repoUser = new Repository<>(entityManager, UserEntity.class);
        Repository<UserAttendanceEntity> repoAttendance = new Repository<>(entityManager, UserAttendanceEntity.class);
        Repository<UserMembershipPaymentEntity> repoMembership = new Repository<>(entityManager, UserMembershipPaymentEntity.class);
        HashMap<Class, Repository> repos = new HashMap<>();
        repos.put(UserEntity.class, repoUser);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        HelloController controller = new HelloController(repos);
        loader.setController(controller);

        AnchorPane root = loader.load();
        Scene scene = new Scene(root, 1000, 700);

        stage.setTitle("Hello!");
        stage.setScene(scene);






        stage.show();
    }


    

    public static void main(String[] args) {
        launch();
    }
}