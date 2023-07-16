package com.example.gambarucmsui.common;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class LayoutUtil {
    public static void stretchInsideAnchorPance(Pane pane) {
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
}

