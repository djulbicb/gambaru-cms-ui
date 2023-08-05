package com.example.gambarucmsui;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.ports.Container;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.metamodel.EntityType;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

public class H2Database {
    private static Server server;
    private static Connection connection;
    protected static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;
    @BeforeAll
    public static void setUps() throws SQLException {
        server = Server.createTcpServer().start();
        connection = DriverManager.getConnection("jdbc:h2:mem:test;MODE=MYSQL", "gambaru", "password");

        entityManagerFactory = Persistence.createEntityManagerFactory("gambaru-entity-manager-test");
        entityManager = entityManagerFactory.createEntityManager();
        Container.initBeans(entityManager);
    }
    @BeforeEach
    public void setUp() {
        entityManager.clear();
    }

    protected void delete(Class entity) {
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM " + entity.getSimpleName()).executeUpdate();
        entityManager.getTransaction().commit();
    }

    @AfterAll
    public static void tearDownAll() throws SQLException {
        connection.close();
        server.stop();
    }
}
