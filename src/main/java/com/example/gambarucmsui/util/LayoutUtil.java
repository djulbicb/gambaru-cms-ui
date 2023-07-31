package com.example.gambarucmsui.util;

import com.example.gambarucmsui.util.FormatUtil;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javax.swing.plaf.SpinnerUI;
import java.time.LocalDate;

import static com.example.gambarucmsui.util.FormatUtil.isLong;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

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
        return txt.getText();
    }
}

