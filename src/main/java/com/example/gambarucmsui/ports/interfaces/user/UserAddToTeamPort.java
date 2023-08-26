package com.example.gambarucmsui.ports.interfaces.user;

import com.example.gambarucmsui.ports.ValidatorResponse;

import java.time.LocalDate;

public interface UserAddToTeamPort {
    ValidatorResponse verifyAddUserToPort(Long userId, String barcodeId, String teamName, boolean isFreeOfCharge, LocalDate start, LocalDate end);
    ValidatorResponse verifyAndAddUserToPort(Long userId, String barcodeId, String teamName, boolean isFreeOfCharge, LocalDate start, LocalDate end);
}
