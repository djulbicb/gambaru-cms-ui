package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.TeamEntity;

import java.util.List;
public interface TeamLoadPort {
    List<TeamEntity> findAllActive();
    TeamEntity findByName(String teamName);
}
