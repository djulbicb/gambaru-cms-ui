package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserMembershipPaymentEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.BarcodeRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserMembershipRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserRepository;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.UserDetail;
import com.example.gambarucmsui.util.FormatUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    // PAGINATION
    private LocalDate paginationDate = LocalDate.now();
    private static final int PAGE_SIZE = 50;
    // FXML TABLE
    @FXML
    TableView<UserDetail> table;
    @FXML
    Label paginationLabel;

    public PanelMembershipController(Stage primaryStage, HashMap<Class, Repository> repositoryMap) {
        repository = (UserMembershipRepository) repositoryMap.get(UserMembershipPaymentEntity.class);
        userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        membershipRepo = (UserMembershipRepository) repositoryMap.get(UserMembershipRepository.class);
        barcodeRepo = (BarcodeRepository) repositoryMap.get(BarcodeRepository.class);
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
                .stream().map(o -> UserDetail.fromEntity(o.getBarcode(), FormatUtil.toMonthYeah(o.getTimestamp()))).collect(Collectors.toList());
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
            LocalDateTime timestamp = LocalDateTime.now();

            if (barcode.getStatus() != BarcodeEntity.Status.ASSIGNED) {
                ToastView.showModal("Barkod se trenutno ne koristi.");
                return;
            }
            if (barcodeRepo.isMembershipAlreadyPayedByBarcodeAndMonthAndYear(barcodeId, month, year)) {
                ToastView.showModal("Članarina za ovaj mesec je već plaćena.");
                return;
            }

            UserEntity user = barcode.getUser();
            System.out.println("Adding attendance " + barcodeId);

            barcode.setLastMembershipPaymentTimestamp(LocalDateTime.now());

            membershipRepo.save(new UserMembershipPaymentEntity(barcode, month, year, timestamp, barcode.getTeam().getMembershipPayment()));

            listPageForDate();

            ToastView.showModal(String.format("Članarina plaćena za %s %s.", user.getFirstName(), user.getLastName()));
        }
    }
}
