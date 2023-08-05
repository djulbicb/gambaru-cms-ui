package com.example.gambarucmsui.ports.interfaces.user;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeFetchOrGeneratePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserAddToTeamPortTest extends H2DatabaseConfig {

    private UserAddToTeamPort userAddToTeam;
    private BarcodeFetchOrGeneratePort barcodeFetchOrGenerate;
    private UserSavePort userSave;
    private TeamSavePort teamSave;
    private BarcodeLoadPort barcodeLoad;

    @BeforeEach
    public void set() {
        barcodeFetchOrGenerate = Container.getBean(BarcodeFetchOrGeneratePort.class);
        userAddToTeam = Container.getBean(UserAddToTeamPort.class);
        userSave = Container.getBean(UserSavePort.class);
        teamSave = Container.getBean(TeamSavePort.class);
        barcodeLoad = Container.getBean(BarcodeLoadPort.class);
    }

    @AfterEach()
    public void purge(){
        delete(TeamEntity.class);
        delete(UserEntity.class);
        delete(BarcodeEntity.class);
    }

    @Test
    public void shouldAssignUserToTeam() throws IOException {
        BarcodeEntity barcode = barcodeFetchOrGenerate.fetchOneOrGenerate(BarcodeEntity.Status.NOT_USED);
        UserEntity user = userSave.save("Bo", "Lowe", UserEntity.Gender.MALE, "123", null);
        TeamEntity team = teamSave.save("Lowe", BigDecimal.ONE);

        userAddToTeam.addUserToPort(user.getUserId(), barcode.getBarcodeId(), team.getName());

        BarcodeEntity after = barcodeLoad.findById(barcode.getBarcodeId()).get();
        assertTrue(after.isAssigned());
    }






}