package com.example.gambarucmsui.adapter.out.persistence.repo;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class TeamRepository extends Repository<TeamEntity> {
    public TeamRepository(EntityManager entityManager) {
        super(entityManager, TeamEntity.class);
    }

}
