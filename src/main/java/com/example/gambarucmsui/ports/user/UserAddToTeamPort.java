package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.ports.ValidatorResponse;

public interface UserAddToTeamPort {
    void addUserToPort(Long userId, Long barcodeId, String teamName);
    ValidatorResponse verifyAddUserToPort(Long userId, String barcodeId, String teamName);
}
