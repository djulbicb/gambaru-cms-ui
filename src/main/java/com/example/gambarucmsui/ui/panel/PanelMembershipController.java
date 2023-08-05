package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.ports.Container;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.LayoutUtil.*;
import static com.example.gambarucmsui.util.PathUtil.FORM_BARCODE_GET_MEMBERSHIP;

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
        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> optBarcode = barcodeLoad.findById(barcodeId);
        if (optBarcode.isPresent()) {
            BarcodeEntity barcode = optBarcode.get();
            int month = paginationDate.getMonthValue();
            int year = paginationDate.getYear();

            if (barcode.getStatus() != BarcodeEntity.Status.ASSIGNED) {
                ToastView.showModal("Barkod se trenutno ne koristi.");
                return;
            }
            if (isMembershipPayed.isMembershipPayedByBarcodeAndMonthAndYear(barcodeId, month, year)) {
                ToastView.showModal("Članarina za ovaj mesec je već plaćena.");
                return;
            }

            System.out.println("Adding membership payment " + barcodeId);
            UserEntity user = barcode.getUser();

            addUserMembership.addMembership(barcode.getBarcodeId(), month, year, barcode.getTeam().getMembershipPayment());

            listPageForDate();

            ToastView.showModal(String.format("Članarina plaćena za %s %s.", user.getFirstName(), user.getLastName()));
        } else {
            ToastView.showModal("Barkod ne postoji u sistemu.");
        }
    }

    @FXML
    void onAddMembershipManually() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FORM_BARCODE_GET_MEMBERSHIP));
        FormBarcodeGetMembership controller = new FormBarcodeGetMembership();
        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

        Stage dialogStage = createStage("Dodaj korisnika u tim", root, primaryStage);
        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            Optional<BarcodeEntity> barcodeOpt = barcodeLoad.findById(controller.getBarcodeId());
            if (barcodeOpt.isPresent()) {
                BarcodeEntity barcode = barcodeOpt.get();
                int month = paginationDate.getMonthValue();
                int year = paginationDate.getYear();

                if (barcode.getStatus() != BarcodeEntity.Status.ASSIGNED) {
                    ToastView.showModal("Barkod se trenutno ne koristi.");
                    return;
                }
                if (isMembershipPayed.isMembershipPayedByBarcodeAndMonthAndYear(barcode.getBarcodeId(), month, year)) {
                    ToastView.showModal("Članarina za ovaj mesec je već plaćena.");
                    return;
                }

                addUserMembership.addMembership(barcode.getBarcodeId(), month, year, barcode.getTeam().getMembershipPayment());
                listPageForDate();
            }
        }
    }
}
