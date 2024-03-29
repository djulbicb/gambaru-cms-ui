package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.PersonPictureEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.TeamLogoEntity;
import com.example.gambarucmsui.ports.impl.ImageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.example.gambarucmsui.util.ImageUtil.resizeAndOptimizeImage;

public class TeamLogoRepository extends Repository<TeamLogoEntity> {
    public TeamLogoRepository(EntityManager entityManager) {
        super(entityManager, TeamLogoEntity.class);
    }

    public Optional<TeamLogoEntity> findByTeamId(Long teamId) {
        List<TeamLogoEntity> results = entityManager.createQuery(
                        "SELECT p FROM TeamLogoEntity p " +
                                "WHERE p.teamId = :teamId", TeamLogoEntity.class)
                .setParameter("teamId", teamId)
                .setMaxResults(1)
                .getResultList();

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void saveOrUpdatePictureData(Long teamId, byte[] pictureData) throws IOException {
        // Check if a TeamLogoEntity already exists for the teamId
        Optional<TeamLogoEntity> existingTeamLogo = findByTeamId(teamId);

        resizeAndOptimizeImage(pictureData, 400, 400);

        if (existingTeamLogo.isPresent()) {
            // Update the existing TeamLogoEntity with new pictureData
            TeamLogoEntity teamLogoEntity = existingTeamLogo.get();
            teamLogoEntity.setPictureData(pictureData);
            update(teamLogoEntity);
        } else {
            // Create a new TeamLogoEntity and persist it
            TeamLogoEntity newTeamLogo = new TeamLogoEntity(pictureData, teamId);
            newTeamLogo.setPictureData(pictureData);
            save(newTeamLogo);
        }
    }
}
