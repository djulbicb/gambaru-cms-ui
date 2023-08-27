package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.barcode.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.gambarucmsui.common.Messages.*;
import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.isLong;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class BarcodeService implements BarcodeLoadPort, BarcodeFetchOrGeneratePort, BarcodeUpdatePort, BarcodePurgePort, BarcodeStatusChangePort {

    private final BarcodeRepository barcodeRepo;

    public BarcodeService(BarcodeRepository barcodeRepo) {
        this.barcodeRepo = barcodeRepo;
    }

    @Override
    public ValidatorResponse validateBarcodeActive(String barcodeIdStr) {
        Map<String, String> errors = new HashMap<>();

        if (barcodeIdStr == null ) {
            errors.put(BARCODE_ID, Messages.BARCODE_IS_NULL);
            return new ValidatorResponse(errors);
        }

        if (!isLong(barcodeIdStr)) {
            if (barcodeIdStr.isBlank()) {
                errors.put(BARCODE_ID, "");
            } else {
                errors.put(BARCODE_ID, BARCODE_WRONG_FORMAT);
            }
            return new ValidatorResponse(errors);
        }

        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> barcodeEntityOptional = barcodeRepo.findById(barcodeId);
        if (barcodeEntityOptional.isEmpty()) {
            errors.put(BARCODE_ID, BARCODE_NOT_REGISTERED);
            return new ValidatorResponse(errors);
        }
        BarcodeEntity b = barcodeEntityOptional.get();
        if (b.getStatus() == BarcodeEntity.Status.DELETED)  {
            errors.put(BARCODE_ID, BARCODE_IS_DELETED);
            return new ValidatorResponse(errors);
        }
        if (b.getStatus() == BarcodeEntity.Status.NOT_USED)  {
            errors.put(BARCODE_ID, BARCODE_IS_NOT_ASSIGNED);
            return new ValidatorResponse(errors);
        }
        if (b.getStatus() == BarcodeEntity.Status.DEACTIVATED)  {
            errors.put(BARCODE_ID, BARCODE_IS_DEACTIVATED);
            return new ValidatorResponse(errors);
        }

        PersonEntity person = b.getPerson();
        return new ValidatorResponse(Messages.BARCODE_IS_VALID(person.getFirstName(), person.getLastName()));

    }

    @Override
    public Optional<BarcodeEntity> findById(Long barcodeId) {
        return barcodeRepo.findById(barcodeId);
    }

    @Override
    public List<BarcodeEntity> findByIds(List<Long> collect) {
        return barcodeRepo.findByIds("barcodeId", collect);
    }

    @Override
    public List<BarcodeEntity> findAllByStatus(BarcodeEntity.Status status) {
        return barcodeRepo.findAllByStatus(status);
    }

    @Override
    public List<BarcodeEntity> findByTeam(Long teamId) {
        return barcodeRepo.findByTeam(teamId);
    }

    @Override
    public List<BarcodeEntity> fetchOrGenerateBarcodes(int count, BarcodeEntity.Status status) {
        return barcodeRepo.fetchOrGenerateBarcodes(count, status);
    }

    @Override
    public List<BarcodeEntity> generateNewBarcodes(int generateCount) {
        return barcodeRepo.generateNewBarcodes(generateCount);
    }

    @Override
    public BarcodeEntity fetchOneOrGenerate(BarcodeEntity.Status status) {
        return barcodeRepo.fetchOneOrGenerate(status);
    }

    @Override
    public Optional<BarcodeEntity> updateBarcode(Long barcodeId, BarcodeEntity.Status status) {
        Optional<BarcodeEntity> byId = findById(barcodeId);

        if (byId.isEmpty()) {
            return Optional.empty();
        }
        BarcodeEntity barcodeEntity = byId.get();

        if (barcodeEntity.getStatus() == BarcodeEntity.Status.DELETED) {
            return Optional.empty();
        }

        barcodeEntity.setStatus(status);
        barcodeRepo.update(barcodeEntity);
        return Optional.of(barcodeEntity);
    }

    @Override
    public void purge() {
        barcodeRepo.deleteAll();
    }

    public ValidatorResponse deactivateBarcode(Long barcodeId) {
        HashMap<String, String> errors = new HashMap<>();
        Optional<BarcodeEntity> byId = barcodeRepo.findById(barcodeId);
        if (byId.isEmpty()) {
            errors.put(BarcodeEntity.BARCODE_ID, "Barkod ne postoji u sistemu");
            return new ValidatorResponse(errors);
        }

        BarcodeEntity barcode = byId.get();
        if (barcode.getStatus() == BarcodeEntity.Status.DELETED) {
            errors.put(BarcodeEntity.BARCODE_ID, "Barkod pripada timu koji je obrisan. Nije ga moguce deaktivirati.");
            return new ValidatorResponse(errors);
        }

        if (barcode.getStatus() == BarcodeEntity.Status.DEACTIVATED) {
            errors.put(BarcodeEntity.BARCODE_ID, "Barkod je već deaktiviran.");
            return new ValidatorResponse(errors);
        }

        barcode.setStatus(BarcodeEntity.Status.DEACTIVATED);
        barcodeRepo.update(barcode);

        return new ValidatorResponse("Barkod je uspešno deaktiviran.");
    }

    @Override
    public ValidatorResponse activateBarcode(Long barcodeId) {
        HashMap<String, String> errors = new HashMap<>();
        Optional<BarcodeEntity> byId = barcodeRepo.findById(barcodeId);
        if (byId.isEmpty()) {
            errors.put(BarcodeEntity.BARCODE_ID, "Barkod ne postoji u sistemu");
            return new ValidatorResponse(errors);
        }

        BarcodeEntity barcode = byId.get();
        if (barcode.getStatus() == BarcodeEntity.Status.DELETED) {
            errors.put(BarcodeEntity.BARCODE_ID, "Barkod pripada timu koji je obrisan. Nije ga moguce deaktivirati.");
            return new ValidatorResponse(errors);
        }

        if (barcode.getStatus() == BarcodeEntity.Status.ASSIGNED) {
            errors.put(BarcodeEntity.BARCODE_ID, "Barkod je već aktiviran.");
            return new ValidatorResponse(errors);
        }

        barcode.setStatus(BarcodeEntity.Status.ASSIGNED);
        barcodeRepo.update(barcode);

        return new ValidatorResponse("Barkod je uspešno aktiviran.");
    }
}
