package com.example.gambarucmsui.ports.user;

public interface IsUserAlreadyInThisTeamPort {
    boolean isUserAlreadyInThisTeam(Long userId, Long teamId);
    boolean isUserAlreadyInThisTeam(Long userId, String teamId);
}
