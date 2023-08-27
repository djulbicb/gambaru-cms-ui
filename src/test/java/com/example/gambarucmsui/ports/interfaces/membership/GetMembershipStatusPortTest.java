package com.example.gambarucmsui.ports.interfaces.membership;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.ui.dto.admin.SubscriptStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.example.gambarucmsui.TestData.*;
import static com.example.gambarucmsui.common.Messages.MEMBERSHIP_IS_PAYED;
import static com.example.gambarucmsui.common.Messages.MEMBERSHIP_NOT_PAYED;
import static org.junit.jupiter.api.Assertions.*;

class GetMembershipStatusPortTest extends H2DatabaseConfig {
    @Test
    public void shouldReturnGreen() throws IOException {
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("First", "Last", "Team", FEBRUARY_START_DATE, FEBRUARY_END_DATE);

        SubscriptStatus status = barcode.getSubscription().getStatus(FEBRUARY_MID_DATE);
        assertTrue(status.isGreen());
        assertEquals(MEMBERSHIP_IS_PAYED, status.getMessage());
    }

    @Test
    public void shouldReturnRed() throws IOException {
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("First", "Last", "Team", FEBRUARY_START_DATE, FEBRUARY_END_DATE);

        SubscriptStatus status = barcode.getSubscription().getStatus(JANUARY_END_DATE);
        assertTrue(status.isRed());
        assertEquals(MEMBERSHIP_NOT_PAYED, status.getMessage());

        SubscriptStatus status1 = barcode.getSubscription().getStatus(MARCH_START_DATE);
        assertTrue(status1.isRed());
        assertEquals(MEMBERSHIP_NOT_PAYED, status1.getMessage());
    }

    @Test
    public void shouldReturnOrange() throws IOException {
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("First", "Last", "Team", FEBRUARY_START_DATE, FEBRUARY_END_DATE);

        SubscriptStatus status = barcode.getSubscription().getStatus(FEBRUARY_END_DATE.minusDays(5));
        assertTrue(status.isGreen());

        SubscriptStatus status1 = barcode.getSubscription().getStatus(FEBRUARY_END_DATE.minusDays(4));
        assertTrue(status1.isOrange());
        assertEquals("Članarina ističe za 4 dana.", status1.getMessage());

        SubscriptStatus status2 = barcode.getSubscription().getStatus(FEBRUARY_END_DATE.minusDays(3));
        assertTrue(status2.isOrange());
        assertEquals("Članarina ističe za 3 dana.", status2.getMessage());

        SubscriptStatus status3 = barcode.getSubscription().getStatus(FEBRUARY_END_DATE.minusDays(2));
        assertTrue(status3.isOrange());
        assertEquals("Članarina ističe za 2 dana.", status3.getMessage());

        SubscriptStatus status4 = barcode.getSubscription().getStatus(FEBRUARY_END_DATE.minusDays(1));
        assertTrue(status4.isOrange());
        assertEquals("Članarina ističe za 1 dan.", status4.getMessage());

        SubscriptStatus status5 = barcode.getSubscription().getStatus(FEBRUARY_END_DATE.minusDays(0));
        assertTrue(status5.isOrange());
        assertEquals("Članarina ističe za 0 dana.", status5.getMessage());

        SubscriptStatus status6 = barcode.getSubscription().getStatus(FEBRUARY_END_DATE.plusDays(1));
        assertTrue(status6.isRed());
        assertEquals(MEMBERSHIP_NOT_PAYED, status6.getMessage());
    }
}