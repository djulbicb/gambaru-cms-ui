package com.example.gambarucmsui.ports.interfaces.user;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserAddToTeamPortTest extends H2DatabaseConfig {
    @Test
    public void shouldAssignUserToTeam() throws IOException {
        BarcodeEntity barcode = barcodeFetchOrGenerate.fetchOneOrGenerate(BarcodeEntity.Status.NOT_USED);
        PersonEntity user = userSavePort.save("Bo", "Lowe", PersonEntity.Gender.MALE, "123", null);
        TeamEntity team = teamSavePort.save("Lowe", BigDecimal.valueOf(123));

        userAddToTeam.addUserToPort(user.getPersonId(), barcode.getBarcodeId(), team.getName(), false, true);

        BarcodeEntity after = barcodeLoad.findById(barcode.getBarcodeId()).get();

        assertTrue(after.isAssigned());
        assertEquals(after.getPerson().getPersonId(), user.getPersonId());
        assertEquals(after.getTeam().getTeamId(), team.getTeamId());
    }






}