package com.example.gambarucmsui.util;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.time.LocalDate;

import static com.example.gambarucmsui.ui.dto.admin.SubscriptStatus.GREEN_CHECKMARK;
import static com.example.gambarucmsui.ui.dto.admin.SubscriptStatus.ORANGE_EXCLAMATION;
import static com.example.gambarucmsui.util.FormatUtil.isLong;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class LayoutUtil {
    public static void stretchInsideAnchorPance(Node pane) {
        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setBottomAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> createBarcodeCellFactory() {
        return userDetailTableColumn -> new TableCell<S, T>() {
            @Override
            protected void updateItem(T object, boolean empty) {
                super.updateItem(object, empty);
                if (object == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    if (object.toString().startsWith(GREEN_CHECKMARK)) {
                        setStyle("-fx-background-color: rgba(22, 160, 133, 0.5);");
                    } else if (object.toString().startsWith(ORANGE_EXCLAMATION)) {
                        setStyle("-fx-background-color: rgba(230, 126, 34, 0.5);");
                    } else {
                        setStyle("-fx-background-color: rgba(192, 57, 43,0.5);");
                    }
                    setText(object.toString());
                }
            }
        };
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

    public static void stretchColumnsToEqualSize(TableView<?> table) {
        for (TableColumn<?, ?> column : table.getColumns()) {
            column.prefWidthProperty().bind(table.widthProperty().divide(table.getColumns().size()));
        }
    }

    public static String getNumericOr(TextField txtSearchBarcode, String opt) {
        if (txtSearchBarcode == null) {
            return opt;
        }
        String value = txtSearchBarcode.getText();
        if (isLong(value)) {
            return parseBarcodeStr(value).toString();
        }
        return opt;
    }

    public static String getOr(ComboBox<String> combo, String opt) {
        if (combo.getSelectionModel() == null || combo.getSelectionModel().getSelectedItem() == null) {
            return opt;
        }
        return combo.getSelectionModel().getSelectedItem();
    }

    public static String getOr(TextField txt, String opt) {
        if (txt == null) {
            return opt;
        }
        return txt.getText().trim();
    }


}

