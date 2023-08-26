package com.example.gambarucmsui.ports.interfaces.user;

import com.example.gambarucmsui.ports.ValidatorResponse;

import java.time.LocalDate;

public interface UserAddToTeamPort {
    void addUserToTeam(Long userId, Long barcodeId, String teamName, boolean freeOfCharge, boolean payNextMonth);
    void addUserToTeam(Long userId, Long barcodeId, Long teamId, boolean isFreeOfCharge, LocalDate start, LocalDate end);
    ValidatorResponse verifyAddUserToPort(Long userId, String barcodeId, String teamName);
}
