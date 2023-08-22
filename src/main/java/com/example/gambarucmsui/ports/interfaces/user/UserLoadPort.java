package com.example.gambarucmsui.ports.interfaces.user;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;

import java.util.List;
import java.util.Optional;

public interface UserLoadPort {
    Optional<PersonEntity> loadUserByUserId(Long userId);
    Optional<PersonEntity> findUserByBarcodeId(Long barcodeId);
    List<PersonEntity> findAll();
    List<PersonEntity> findAll(int page, int pageSize, String sortColumn, String teamName, String firstName, String lastName, String barcode, boolean isOnlyActive);
    List<BarcodeEntity> findActiveUsersByTeamId(Long teamId);
}
