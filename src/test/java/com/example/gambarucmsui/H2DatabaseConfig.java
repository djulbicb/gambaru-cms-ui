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
import com.example.gambarucmsui.ports.interfaces.membership.AddUserMembership;
import com.example.gambarucmsui.ports.interfaces.membership.GetMembershipStatusPort;
import com.example.gambarucmsui.ports.interfaces.membership.MembershipPurgePort;
import com.example.gambarucmsui.ports.interfaces.team.*;
import com.example.gambarucmsui.ports.interfaces.user.PersonPictureBarcodePurgePort;
import com.example.gambarucmsui.ports.interfaces.user.UserAddToTeamPort;
import com.example.gambarucmsui.ports.interfaces.user.UserPurgePort;
import com.example.gambarucmsui.ports.interfaces.user.UserSavePort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Table;
import org.h2.engine.User;
import org.h2.tools.Server;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    protected UserAddToTeamPort userAddToTeam;
    protected BarcodeFetchOrGeneratePort barcodeFetchOrGenerate;
    protected UserSavePort userSave;
    protected TeamSavePort teamSave;
    protected BarcodeLoadPort barcodeLoad;
    protected TeamLoadPort teamLoad;
    protected TeamDeletePort teamDeletePort;
    protected BarcodeStatusChangePort barcodeStatusChangePort;
    protected AddUserMembership addUserMembership;
    protected TeamIfExists teamIfExists;
    protected GetMembershipStatusPort getMembershipStatusPortTest;

    @BeforeEach
    public void set() {
        attendanceLoadForUserPort = Container.getBean(AttendanceLoadForUserPort.class);
        attendanceAddForUserPort = Container.getBean(AttendanceAddForUserPort.class);
        addUserMembership = Container.getBean(AddUserMembership.class);
        barcodeFetchOrGenerate = Container.getBean(BarcodeFetchOrGeneratePort.class);
        barcodeStatusChangePort = Container.getBean(BarcodeStatusChangePort.class);
        userAddToTeam = Container.getBean(UserAddToTeamPort.class);
        userSave = Container.getBean(UserSavePort.class);
        teamSave = Container.getBean(TeamSavePort.class);
        barcodeLoad = Container.getBean(BarcodeLoadPort.class);
        teamLoad = Container.getBean(TeamLoadPort.class);
        teamDeletePort = Container.getBean(TeamDeletePort.class);
        teamIfExists = Container.getBean(TeamIfExists.class);
        getMembershipStatusPortTest = Container.getBean(GetMembershipStatusPort.class);
    }

    @AfterEach
    public void purge() {
        Container.getBean(AttendancePurgePort.class).purge();
        Container.getBean(MembershipPurgePort.class).purge();
        Container.getBean(BarcodePurgePort.class).purge();
        Container.getBean(TeamPurgePort.class).purge();
        Container.getBean(UserPurgePort.class).purge();
        Container.getBean(PersonPictureBarcodePurgePort.class).purge();
    }

    @AfterAll
    public static void tearDownAll() throws SQLException {
        entityManager.clear();
        connection.close();
        server.stop();
    }

    @NotNull
    protected BarcodeEntity scenario_AssignPersonToTeamAndReturnAssignedBarcode() throws IOException {
        BarcodeEntity barcode = barcodeFetchOrGenerate.fetchOneOrGenerate(BarcodeEntity.Status.NOT_USED);
        PersonEntity user = userSave.save("Bo", "Lowe", PersonEntity.Gender.MALE, "123", null);
        TeamEntity team = teamSave.save("Lowe", BigDecimal.valueOf(123));
        userAddToTeam.addUserToPort(user.getPersonId(), barcode.getBarcodeId(), team.getName());
        return barcode;
    }
}
