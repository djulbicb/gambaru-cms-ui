package com.example.gambarucmsui.ports.interfaces.team;

import com.example.gambarucmsui.database.entity.TeamEntity;

import java.util.List;
import java.util.Optional;

public interface TeamLoadPort {
    List<TeamEntity> findAllActive();
    TeamEntity findByName(String teamName);
    Optional<TeamEntity> findById(Long teamId);
}
