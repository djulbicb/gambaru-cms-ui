package com.example.gambarucmsui.ports.interfaces.user;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeFetchOrGeneratePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamLoadPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserAddToTeamPortTest extends H2DatabaseConfig {
    @Test
    public void shouldAssignUserToTeam() throws IOException {
        BarcodeEntity barcode = barcodeFetchOrGenerate.fetchOneOrGenerate(BarcodeEntity.Status.NOT_USED);
        PersonEntity user = userSave.save("Bo", "Lowe", PersonEntity.Gender.MALE, "123", null);
        TeamEntity team = teamSave.save("Lowe", BigDecimal.valueOf(123));

        userAddToTeam.addUserToPort(user.getPersonId(), barcode.getBarcodeId(), team.getName());

        BarcodeEntity after = barcodeLoad.findById(barcode.getBarcodeId()).get();

        System.out.println(after);
        assertTrue(after.isAssigned());
        assertEquals(after.getPerson().getPersonId(), user.getPersonId());
        assertEquals(after.getTeam().getTeamId(), team.getTeamId());
    }






}