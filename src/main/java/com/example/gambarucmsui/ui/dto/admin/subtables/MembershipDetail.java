package com.example.gambarucmsui.ui.dto.admin.subtables;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;

import java.time.LocalDateTime;

import static com.example.gambarucmsui.util.FormatUtil.formatBarcode;
import static com.example.gambarucmsui.util.FormatUtil.toDateTimeFormat;

public class MembershipDetail {
    private String timestamp;
    private String barcodeId;
    private String teamName;
    public MembershipDetail(BarcodeEntity barcodeId, LocalDateTime timestamp, TeamEntity team) {
        this.barcodeId = formatBarcode(barcodeId.getBarcodeId());
        this.timestamp = toDateTimeFormat(timestamp);
        this.teamName = team.getName();
    }
    public String getTimestamp() {
        return timestamp;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public String getTeamName() {
        return teamName;
    }
}
