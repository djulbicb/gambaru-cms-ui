package com.example.gambarucmsui.ports.interfaces.user;

import com.example.gambarucmsui.H2DatabaseConfig;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.util.FormatUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserLoadPortTest extends H2DatabaseConfig {
    @Test
    public void shouldFindAllUsers() throws IOException {
        // given
        List<ScenarioUser> usersToBeAdded = listOfUsers();

        // when
        List<PersonEntity> saved = scenario_AssignMultiplePersonsToTeamAndReturnBarcodes(usersToBeAdded, "Team").stream().map(b -> b.getPerson()).collect(Collectors.toList());
        // List is sorted by createdAt, so reversing the order of added items
        Collections.reverse(saved);
        List<PersonEntity> loaded = userLoadPort.findAll(1, 10, "createdAt", "", "", "", "", false);

        // then
        assertEquals(saved, loaded);
    }

    @Test
    public void shouldFindAllUsersAndIgnoreDeletedUsers() throws IOException {
        // given
        List<ScenarioUser> usersToBeAdded = listOfUsers();

        // when
        List<PersonEntity> saved = scenario_AssignMultiplePersonsToTeamAndReturnBarcodes(usersToBeAdded, "Team").stream().map(b -> b.getPerson()).collect(Collectors.toList());
        BarcodeEntity barcode = scenario_AssignPersonToTeamAndReturnAssignedBarcode("first", "last", "Delete");
        teamDeletePort.validateAndDeleteTeam(barcode.getTeam().getTeamId());

        // List is sorted by createdAt, so reversing the order of added items
        Collections.reverse(saved);
        List<PersonEntity> loaded = userLoadPort.findAll(1, 10, "createdAt", "", "", "", "", true);

        // then
        assertEquals(saved, loaded);
    }

    @Test
    public void shouldFilterUsersByProperties() throws IOException {
        // given
        List<ScenarioUser> usersToBeAdded = listOfUsers();

        // when - create users and teams
        scenario_AssignMultiplePersonsToTeamAndReturnBarcodes(usersToBeAdded, "Team").stream().map(b -> b.getPerson()).collect(Collectors.toList());
        scenario_AssignMultiplePersonsToTeamAndReturnBarcodes(usersToBeAdded, "Delete").stream().map(b -> b.getPerson()).collect(Collectors.toList());
        scenario_AssignPersonToTeamAndReturnAssignedBarcode("ignore", "ignore", "ignore");
        BarcodeEntity findThisOne = scenario_AssignPersonToTeamAndReturnAssignedBarcode("findThisOne", "findThisOne", "findThisOne");

        // when - delete one team
        TeamEntity teamToBeDelete = teamLoad.findByName("Delete");
        teamDeletePort.validateAndDeleteTeam(teamToBeDelete.getTeamId());

        // FILTER AND SHOW ONLY ACTIVE USERS
        // when - find filter only active users
        boolean searchForActiveUsers = true;
        List<PersonEntity> filterByFirstName = userLoadPort.findAll(1, 10, "createdAt", "", "fa", "", "", searchForActiveUsers);
        List<PersonEntity> filterByLastName = userLoadPort.findAll(1, 10, "createdAt", "", "", "la", "", searchForActiveUsers);
        List<PersonEntity> filterByTeamName = userLoadPort.findAll(1, 10, "createdAt", "Team", "", "", "", searchForActiveUsers);
        List<PersonEntity> filterByBarcodeBarcodeFullDigit = userLoadPort.findAll(1, 10, "createdAt", "", "", "", FormatUtil.formatBarcode(findThisOne.getBarcodeId()), searchForActiveUsers);
        List<PersonEntity> filterByBarcodeBarcodeSingleDigit = userLoadPort.findAll(1, 10, "createdAt", "", "", "", String.valueOf(findThisOne.getBarcodeId()), searchForActiveUsers);

        // then - check filtered active users
        assertEquals(1, filterByBarcodeBarcodeFullDigit.size());
        assertEquals(1, filterByBarcodeBarcodeSingleDigit.size());
        assertEquals(2, filterByFirstName.size());
        assertEquals(3, filterByLastName.size());
        assertEquals(10, filterByTeamName.size());

        // FILTER AND SHOW BOTH ACTIVE AND DEACTIVATED, IGNORE DELETED
        // when - find filter only active users
        searchForActiveUsers = false;
        filterByFirstName = userLoadPort.findAll(1, 10, "createdAt", "", "fa", "", "", searchForActiveUsers);
        filterByLastName = userLoadPort.findAll(1, 10, "createdAt", "", "", "la", "", searchForActiveUsers);

        // then - check filtered active users
        assertEquals(4, filterByFirstName.size());
        assertEquals(6, filterByLastName.size());
    }

    @Test
    public void shouldFindOnlyUsersWithActiveBarcodeAndIgnoreDeactivated() throws IOException {
        // given
        List<BarcodeEntity> barcodes = scenario_AssignMultiplePersonsToTeamAndReturnBarcodes(List.of(
                        new ScenarioUser("first", "first", "123"),
                        new ScenarioUser("second", "second", "234")),
                "Team");

        BarcodeEntity first = barcodes.get(0);
        BarcodeEntity second = barcodes.get(1);

        // when
        barcodeStatusChangePort.deactivateBarcode(first.getBarcodeId());
        List<BarcodeEntity> actual = userLoadPort.findActiveUsersByTeamId(first.getTeam().getTeamId());

        // then
        assertEquals(List.of(second), actual);
    }

    private List<ScenarioUser> listOfUsers() {
        return List.of(
                new ScenarioUser("fabc", "labc", "123"),
                new ScenarioUser("facd", "lacd", "125"),
                new ScenarioUser("fdef", "laef", "156"),
                new ScenarioUser("fefg", "lefg", "112"),
                new ScenarioUser("fhij", "lijk", "567"),
                new ScenarioUser("fkij", "lkij", "678"),
                new ScenarioUser("flmn", "lmnm", "789"),
                new ScenarioUser("fopq", "lopq", "890"),
                new ScenarioUser("frst", "lrst", "901"),
                new ScenarioUser("fuvt", "luvt", "012")
        );
    }

}