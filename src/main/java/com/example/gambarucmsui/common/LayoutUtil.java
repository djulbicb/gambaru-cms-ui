package com.example.gambarucmsui.common;

import com.example.gambarucmsui.util.FormatUtil;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.time.LocalDate;

public class LayoutUtil {
    public static void stretchInsideAnchorPance(Node pane) {
        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setBottomAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);
    }

    public static String getOrElse(Object val, String alternative) {
        if (val == null) {
            return alternative;
        }
        return val.toString();
    }

    public static String formatPagination(int pageNumber) {
        return String.format("%02d", pageNumber);
    }
    public static String formatPagination(LocalDate date) {
        return date.toString();
    }
    public static String formatPaginationMonth(LocalDate date) {
        return FormatUtil.toMonthYeah(date.atStartOfDay());
    }
}

