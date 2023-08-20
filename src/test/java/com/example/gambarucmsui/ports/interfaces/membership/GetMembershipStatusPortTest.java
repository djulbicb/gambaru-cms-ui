package com.example.gambarucmsui.ports.interfaces.membership;

import com.example.gambarucmsui.H2DatabaseConfig;
import org.junit.jupiter.api.Test;

import static com.example.gambarucmsui.TestData.*;
import static com.example.gambarucmsui.common.Messages.MEMBERSHIP_IS_PAYED;
import static com.example.gambarucmsui.common.Messages.MEMBERSHIP_NOT_PAYED;
import static org.junit.jupiter.api.Assertions.*;

class GetMembershipStatusPortTest extends H2DatabaseConfig {
    @Test
    public void shouldReturnGreen() {
        GetMembershipStatusPort.State state = getMembershipStatusPortTest.getLastMembershipForUser(FEBRUARY_START_DATE, FEBRUARY_MID_DATE);
        assertTrue(state.isGreen());
        assertEquals(MEMBERSHIP_IS_PAYED, state.getMessage());
    }
    @Test
    public void shouldReturnOrange() {
        GetMembershipStatusPort.State stateGreen = getMembershipStatusPortTest.getLastMembershipForUser(FEBRUARY_START_DATE, FEBRUARY_END_DATE.minusDays(5));
        assertTrue(stateGreen.isGreen());

        GetMembershipStatusPort.State state1 = getMembershipStatusPortTest.getLastMembershipForUser(FEBRUARY_START_DATE, FEBRUARY_END_DATE);
        assertTrue(state1.isOrange());
        assertEquals("Članarina uskoro ističe. Za 1 dan.", state1.getMessage());

        GetMembershipStatusPort.State state2 = getMembershipStatusPortTest.getLastMembershipForUser(FEBRUARY_START_DATE, FEBRUARY_END_DATE.minusDays(1));
        assertTrue(state2.isOrange());
        assertEquals("Članarina uskoro ističe. Za 2 dana.", state2.getMessage());

        GetMembershipStatusPort.State state3 = getMembershipStatusPortTest.getLastMembershipForUser(FEBRUARY_START_DATE, FEBRUARY_END_DATE.minusDays(2));
        assertTrue(state3.isOrange());
        assertEquals("Članarina uskoro ističe. Za 3 dana.", state3.getMessage());

        GetMembershipStatusPort.State state4 = getMembershipStatusPortTest.getLastMembershipForUser(FEBRUARY_START_DATE, FEBRUARY_END_DATE.minusDays(3));
        assertTrue(state4.isOrange());
        assertEquals("Članarina uskoro ističe. Za 4 dana.", state4.getMessage());

        GetMembershipStatusPort.State state5 = getMembershipStatusPortTest.getLastMembershipForUser(FEBRUARY_START_DATE, FEBRUARY_END_DATE.minusDays(4));
        assertTrue(state5.isOrange());
        assertEquals("Članarina uskoro ističe. Za 5 dana.", state5.getMessage());

        GetMembershipStatusPort.State stateRed = getMembershipStatusPortTest.getLastMembershipForUser(FEBRUARY_START_DATE, FEBRUARY_END_DATE.plusDays(1));
        assertTrue(stateRed.isRed());
    }

    @Test
    public void shouldReturnRed() {
        GetMembershipStatusPort.State state = getMembershipStatusPortTest.getLastMembershipForUser(FEBRUARY_START_DATE, MARCH_START_DATE);
        assertTrue(state.isRed());
        assertEquals(MEMBERSHIP_NOT_PAYED, state.getMessage());
    }
}