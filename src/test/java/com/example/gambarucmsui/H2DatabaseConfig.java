package com.example.gambarucmsui;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendancePurgePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodePurgePort;
import com.example.gambarucmsui.ports.interfaces.membership.MembershipPurgePort;
import com.example.gambarucmsui.ports.interfaces.team.TeamPurgePort;
import com.example.gambarucmsui.ports.interfaces.user.PersonPictureBarcodePurgePort;
import com.example.gambarucmsui.ports.interfaces.user.UserPurgePort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Table;
import org.h2.engine.User;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.lang.annotation.Annotation;
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

    @AfterEach
    public void purge() {
        Container.getBean(BarcodePurgePort.class).purge();
        Container.getBean(TeamPurgePort.class).purge();
        Container.getBean(UserPurgePort.class).purge();
        Container.getBean(AttendancePurgePort.class).purge();
        Container.getBean(PersonPictureBarcodePurgePort.class).purge();
        Container.getBean(MembershipPurgePort.class).purge();
    }

    @AfterAll
    public static void tearDownAll() throws SQLException {
        entityManager.clear();
        connection.close();
        server.stop();
    }
}
