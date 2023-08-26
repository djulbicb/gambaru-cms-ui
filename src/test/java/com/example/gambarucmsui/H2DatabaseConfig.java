package com.example.gambarucmsui;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceAddForUserPort;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceLoadForUserPort;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendancePurgePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeFetchOrGeneratePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodePurgePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeStatusChangePort;
import com.example.gambarucmsui.ports.interfaces.subscription.AddSubscriptionPort;
import com.example.gambarucmsui.ports.interfaces.subscription.SubscriptionLoadPort;
import com.example.gambarucmsui.ports.interfaces.subscription.SubscriptionPurgePort;
import com.example.gambarucmsui.ports.interfaces.team.*;
import com.example.gambarucmsui.ports.interfaces.user.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.h2.tools.Server;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class H2DatabaseConfig {
    private static Server server;
    private static Connection connection;
    protected static EntityManagerFactory entityManagerFactory;
    protected static EntityManager entityManager;

    static {
        try {
            server = Server.createTcpServer().start();
            connection = DriverManager.getConnection("jdbc:h2:mem:test;MODE=MYSQL", "gambaru", "password");

            entityManagerFactory = Persistence.createEntityManagerFactory("gambaru-entity-manager-test");
            entityManager = entityManagerFactory.createEntityManager();
            Container.initBeans(entityManager);
        } catch (Exception e) {

        }
    }

    protected AttendanceLoadForUserPort attendanceLoadForUserPort;
    protected AttendanceAddForUserPort attendanceAddForUserPort;
    protected BarcodeFetchOrGeneratePort barcodeFetchOrGenerate;
    protected BarcodeLoadPort barcodeLoad;
    protected BarcodeStatusChangePort barcodeStatusChangePort;
    // USER
    /////////////////////////////////////////////////////////////////
    protected UserSavePort userSavePort;
    protected UserLoadPort userLoadPort;
    protected UserPictureLoad userPictureLoadPort;
    protected UserUpdatePort userUpdatePort;
    protected UserAddToTeamPort userAddToTeam;
    // TEAM
    /////////////////////////////////////////////////////////////////
    protected TeamLoadPort teamLoad;
    protected TeamSavePort teamSavePort;
    protected TeamDeletePort teamDeletePort;
    protected TeamIfExists teamIfExists;
    protected TeamUpdatePort teamUpdatePort;
    private AddSubscriptionPort addSubscriptionPort;
    private SubscriptionLoadPort subscriptionLoadPort;

    @BeforeEach
    public void set() {
        barcodeFetchOrGenerate = Container.getBean(BarcodeFetchOrGeneratePort.class);
        barcodeStatusChangePort = Container.getBean(BarcodeStatusChangePort.class);
        barcodeLoad = Container.getBean(BarcodeLoadPort.class);

        userSavePort = Container.getBean(UserSavePort.class);
        userLoadPort = Container.getBean(UserLoadPort.class);
        userUpdatePort = Container.getBean(UserUpdatePort.class);
        userPictureLoadPort = Container.getBean(UserPictureLoad.class);
        userAddToTeam = Container.getBean(UserAddToTeamPort.class);

        teamSavePort = Container.getBean(TeamSavePort.class);
        teamLoad = Container.getBean(TeamLoadPort.class);
        teamUpdatePort = Container.getBean(TeamUpdatePort.class);
        teamDeletePort = Container.getBean(TeamDeletePort.class);
        teamIfExists = Container.getBean(TeamIfExists.class);

        attendanceLoadForUserPort = Container.getBean(AttendanceLoadForUserPort.class);
        attendanceAddForUserPort = Container.getBean(AttendanceAddForUserPort.class);

        addSubscriptionPort = Container.getBean(AddSubscriptionPort.class);
        subscriptionLoadPort = Container.getBean(SubscriptionLoadPort.class);


    }

    @AfterEach
    public void purge() {
        Container.getBean(PersonPictureBarcodePurgePort.class).purge();
        Container.getBean(SubscriptionPurgePort.class).purge();
        Container.getBean(AttendancePurgePort.class).purge();
        Container.getBean(BarcodePurgePort.class).purge();
        Container.getBean(TeamPurgePort.class).purge();
        Container.getBean(UserPurgePort.class).purge();
    }

    @AfterAll
    public static void tearDownAll() throws SQLException {
        entityManager.clear();
        connection.close();
        server.stop();
    }

    @NotNull
    protected BarcodeEntity scenario_AssignPersonToTeamAndReturnAssignedBarcode(String firstName, String lastName, String teamName) throws IOException {
        BarcodeEntity barcode = barcodeFetchOrGenerate.fetchOneOrGenerate(BarcodeEntity.Status.NOT_USED);
        PersonEntity user = userSavePort.save(firstName, lastName, PersonEntity.Gender.MALE, "123", null);
        TeamEntity team = teamSavePort.save(teamName, BigDecimal.valueOf(123));
        userAddToTeam.addUserToPort(user.getPersonId(), barcode.getBarcodeId(), team.getName(), false, true);
        return barcode;
    }

    public class ScenarioUser {
        private String firstName;
        private String lastName;
        private String phone;
        private PersonEntity.Gender gender = PersonEntity.Gender.MALE;

        public ScenarioUser(String firstName, String lastName, String phone) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getPhone() {
            return phone;
        }
    }
    @NotNull
    protected List<BarcodeEntity> scenario_AssignMultiplePersonsToTeamAndReturnBarcodes(List<ScenarioUser> usersToAdd, String teamName) throws IOException {
        List<BarcodeEntity> barcodes = barcodeFetchOrGenerate.fetchOrGenerateBarcodes(usersToAdd.size(), BarcodeEntity.Status.NOT_USED);
        TeamEntity team = teamSavePort.save(teamName, BigDecimal.valueOf(123));

        for (int i = 0; i < usersToAdd.size(); i++) {
            ScenarioUser u = usersToAdd.get(i);
            PersonEntity user = userSavePort.save(u.getFirstName(), u.getLastName(), PersonEntity.Gender.MALE, u.getPhone(), null);
            userAddToTeam.addUserToPort(user.getPersonId(), barcodes.get(i).getBarcodeId(), team.getName(), false, true);
        }

        return barcodes;
    }
}
