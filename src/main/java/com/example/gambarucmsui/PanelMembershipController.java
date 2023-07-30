package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserMembershipPaymentEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.*;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.common.LayoutUtil.formatPagination;
import static com.example.gambarucmsui.common.LayoutUtil.formatPaginationMonth;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class PanelMembershipController implements PanelHeader {
    private final UserMembershipRepository repository;
    private final UserRepository userRepo;
    private final UserMembershipRepository membershipRepo;
    private final BarcodeRepository barcodeRepo;
    private final TeamRepository teamRepo;
    private final Stage primaryStage;
    // PAGINATION
    private LocalDate paginationDate = LocalDate.now();
    private static final int PAGE_SIZE = 50;
    // FXML TABLE
    @FXML
    TableView<UserDetail> table;
    @FXML
    Label paginationLabel;

    public PanelMembershipController(Stage primaryStage, HashMap<Class, Object> repositoryMap) {
        this.primaryStage = primaryStage;
        repository = (UserMembershipRepository) repositoryMap.get(UserMembershipPaymentEntity.class);
        userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        membershipRepo = (UserMembershipRepository) repositoryMap.get(UserMembershipRepository.class);
        barcodeRepo = (BarcodeRepository) repositoryMap.get(BarcodeRepository.class);
        teamRepo = (TeamRepository) repositoryMap.get(TeamRepository.class);

    }

    @FXML
    public void initialize() {
        System.out.println("Membership loaded");
        updatePagination(LocalDate.now());
    }

    @Override
    public void viewSwitched() {
        listPageForDate();
        System.out.println("Panel membership");
    }

    private void listPageForDate() {
        List<UserDetail> collect = barcodeRepo.findAllMembershipsForMonthAndYear(paginationDate.getMonthValue(), paginationDate.getYear())
                .stream().map(o -> UserDetail.fromEntityToFull(o.getBarcode(), FormatUtil.toMonthYeah(o.getTimestamp()))).collect(Collectors.toList());
        table.getItems().setAll(collect);
    }

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
    private void updatePagination(LocalDate localDate) {
        paginationDate = localDate;
        paginationLabel.setText(formatPaginationMonth(paginationDate));
    }
    public void onBarcodeRead(String barcodeIdStr) {
        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> optBarcode = barcodeRepo.findById(barcodeId);
        if (optBarcode.isPresent()) {
            BarcodeEntity barcode = optBarcode.get();
            int month = paginationDate.getMonthValue();
            int year = paginationDate.getYear();

            if (barcode.getStatus() != BarcodeEntity.Status.ASSIGNED) {
                ToastView.showModal("Barkod se trenutno ne koristi.");
                return;
            }
            if (barcodeRepo.isMembershipAlreadyPayedByBarcodeAndMonthAndYear(barcodeId, month, year)) {
                ToastView.showModal("Članarina za ovaj mesec je već plaćena.");
                return;
            }

            System.out.println("Adding membership payment " + barcodeId);
            UserEntity user = barcode.getUser();

            addAttendance(barcode, month, year, barcode.getTeam().getMembershipPayment());

            listPageForDate();

            ToastView.showModal(String.format("Članarina plaćena za %s %s.", user.getFirstName(), user.getLastName()));
        }
    }

    private void addAttendance(BarcodeEntity barcode, int month, int year, BigDecimal membershipPayment) {
        membershipRepo.saveNew(barcode, month, year, membershipPayment);
    }

    @FXML
    void onAddMembershipManually() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("form-barcode-get-membership.fxml"));
        FormBarcodeGetMembership controller = new FormBarcodeGetMembership(barcodeRepo, teamRepo);
        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

        Stage dialogStage = createStage("Dodaj korisnika u tim", root, primaryStage);
        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            Optional<BarcodeEntity> barcodeOpt = barcodeRepo.findById(controller.getBarcodeId());
            if (barcodeOpt.isPresent()) {
                BarcodeEntity barcode = barcodeOpt.get();
                int month = paginationDate.getMonthValue();
                int year = paginationDate.getYear();

                if (barcode.getStatus() != BarcodeEntity.Status.ASSIGNED) {
                    ToastView.showModal("Barkod se trenutno ne koristi.");
                    return;
                }
                if (barcodeRepo.isMembershipAlreadyPayedByBarcodeAndMonthAndYear(barcode.getBarcodeId(), month, year)) {
                    ToastView.showModal("Članarina za ovaj mesec je već plaćena.");
                    return;
                }

                addAttendance(barcode, month, year, barcode.getTeam().getMembershipPayment());
                listPageForDate();
            }
        }
    }
}
