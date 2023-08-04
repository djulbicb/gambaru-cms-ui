package com.example.gambarucmsui.ports.user;

public interface UserAddToTeamPort {
    void addUserToPort(Long userId, Long barcodeId, String teamName);
}
