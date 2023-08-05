package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BarcodeRepository extends Repository<BarcodeEntity> {
    public BarcodeRepository(EntityManager entityManager) {
        super(entityManager, BarcodeEntity.class);
    }

    public BarcodeEntity fetchOneOrGenerate(BarcodeEntity.Status status) {
        return fetchOrGenerateBarcodes(1, BarcodeEntity.Status.NOT_USED).get(0);
    }

    public List<BarcodeEntity> fetchOrGenerateBarcodes(int count, BarcodeEntity.Status status) {
        List<BarcodeEntity> barcodes = findBarcodesByStatus(count, status);

        int availableCount = barcodes.size();

        if (availableCount == count) {
            return barcodes;
        }

        int generateCount = count - availableCount;
       return generateNewBarcodes(generateCount);
    }

    public List<BarcodeEntity> generateNewBarcodes(int generateCount) {
        List<BarcodeEntity> toBeSaved = new ArrayList<>();
        for (int i = 0; i < generateCount; i++) {
            BarcodeEntity barcode = new BarcodeEntity();
            barcode.setStatus( BarcodeEntity.Status.NOT_USED);
            BarcodeEntity save = save(barcode);
            toBeSaved.add(save);
        }
        return saveAll(toBeSaved);
    }

    public List<BarcodeEntity> findBarcodesByStatus(int count, BarcodeEntity.Status status) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BarcodeEntity> query = criteriaBuilder.createQuery(BarcodeEntity.class);
        Root<BarcodeEntity> root = query.from(BarcodeEntity.class);

        query.select(root)
                .where(criteriaBuilder.equal(root.get("status"), status))
                .orderBy(criteriaBuilder.asc(root.get("barcodeId")));

        TypedQuery<BarcodeEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(count);

        return typedQuery.getResultList();
    }

    public void updateOne(BarcodeEntity barcode) {
        save(barcode);
    }

    public void updateBarcodeWithUserAndTeam(BarcodeEntity barcode, UserEntity user, TeamEntity team) {
        barcode.setStatus(BarcodeEntity.Status.ASSIGNED);
        barcode.setTeam(team);
        barcode.setUser(user);

        save(barcode);
    }

    public void saveMultiple(List<BarcodeEntity> barcodes) {
        saveAll(barcodes);
    }

    public List<BarcodeEntity> findByTeam(Long teamId) {
        String jpql = "SELECT b FROM BarcodeEntity b WHERE b.team.teamId = :teamId";
        TypedQuery<BarcodeEntity> query = entityManager.createQuery(jpql, BarcodeEntity.class);
        query.setParameter("teamId", teamId);
        return query.getResultList();
    }

    public List<BarcodeEntity> findAllByStatus(BarcodeEntity.Status status) {
        String jpql = "SELECT b FROM BarcodeEntity b WHERE b.status = :status";
        TypedQuery<BarcodeEntity> query = entityManager.createQuery(jpql, BarcodeEntity.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
}
