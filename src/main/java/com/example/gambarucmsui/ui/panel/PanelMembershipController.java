package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.membership.AddUserMembership;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.membership.IsMembershipPayed;
import com.example.gambarucmsui.ports.interfaces.membership.LoadMembership;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.core.UserDetail;
import com.example.gambarucmsui.ui.form.FormBarcodeGetMembership;
import com.example.gambarucmsui.util.FormatUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.util.FormatUtil.formatBarcode;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.LayoutUtil.*;
import static com.example.gambarucmsui.util.PathUtil.FORM_BARCODE_GET;

public class PanelMembershipController implements PanelHeader {
    //  FXML
    //////////////////////////////////////////////////////////////////
    @FXML
    TableView<UserDetail> table;
    @FXML
    Label paginationLabel;
    private final Stage primaryStage;
    //  INIT
    //////////////////////////////////////////////////////////////////
    private final AddUserMembership addUserMembership;
    private final LoadMembership loadMembership;
    private final BarcodeLoadPort barcodeLoad;
    private final IsMembershipPayed isMembershipPayed;


    public PanelMembershipController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        addUserMembership = Container.getBean(AddUserMembership.class);
        loadMembership = Container.getBean(LoadMembership.class);
        barcodeLoad = Container.getBean(BarcodeLoadPort.class);
        isMembershipPayed = Container.getBean(IsMembershipPayed.class);
    }

    @FXML
    public void initialize() {
        System.out.println("Membership loaded");
        updatePagination(LocalDate.now());

        stretchColumnsToEqualSize(table);
    }

    @Override
    public void viewSwitched() {
        System.out.println("Switched to panel Membership.");
        listPageForDate();
    }

    //  PAGINATION
    //////////////////////////////////////////////////////////////////
    private LocalDate paginationDate = LocalDate.now();
    private static final int PAGE_SIZE = 50;

    @FXML
    protected void goNextPage() {
        updatePagination(paginationDate.plusMonths(1));
        listPageForDate();
    }
    @FXML
    protected void goPrevPage() {
        updatePagination(paginationDate.minusMonths(1));
        listPageForDate();
    }

    @FXML
    protected void goToToday() {
        updatePagination(LocalDate.now());
        listPageForDate();
    }
    private void updatePagination(LocalDate localDate) {
        paginationDate = localDate;
        paginationLabel.setText(formatPaginationMonth(paginationDate));
    }

    //  CORE
    //////////////////////////////////////////////////////////////////

    private void listPageForDate() {
        List<UserDetail> collect = loadMembership.findAllMembershipsForMonthAndYear(paginationDate.getMonthValue(), paginationDate.getYear())
                .stream().map(o -> UserDetail.fromEntityToFull(o.getBarcode(), FormatUtil.toMonthYeah(o.getTimestamp()))).collect(Collectors.toList());
        table.getItems().setAll(collect);
    }
    public void onBarcodeRead(String barcodeIdStr) {
        ValidatorResponse res = addUserMembership.validateAndAddMembership(barcodeIdStr, LocalDateTime.now());
        ToastView.showModal(res.getMessage());
        listPageForDate();
    }

    @FXML
    void onAddMembershipManually() throws IOException {
        FormBarcodeGetMembership controller = new FormBarcodeGetMembership(LocalDateTime.now());
        Pane root = loadFxml(FORM_BARCODE_GET, controller);
        createStage("Dodaj korisnika u tim", root, primaryStage).showAndWait();
        listPageForDate();
    }
}

