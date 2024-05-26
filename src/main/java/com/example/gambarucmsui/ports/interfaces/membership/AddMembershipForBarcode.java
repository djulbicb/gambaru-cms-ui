package com.example.gambarucmsui.ports.interfaces.membership;

import com.example.gambarucmsui.database.entity.PersonMembershipEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AddMembershipForBarcode {
    Optional<PersonMembershipEntity> addMembership(Long barcodeId, LocalDateTime timestamp, int fee);
}
