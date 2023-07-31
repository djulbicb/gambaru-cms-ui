package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.BarcodeRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserAttendanceRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserRepository;

import com.example.gambarucmsui.common.LayoutUtil;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.core.UserDetail;
import com.example.gambarucmsui.ui.form.FormBarcodeGetAttendance;
import com.example.gambarucmsui.util.FormatUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.common.LayoutUtil.formatPagination;
import static com.example.gambarucmsui.util.FormatUtil.*;

public class PanelAttendanceController implements PanelHeader {

    ;
    // PAGINATION
    private LocalDate paginationDate = LocalDate.now();
    private static final int PAGE_SIZE = 50;
    // FIELDS
    private final UserRepository userRepo;
    private final UserAttendanceRepository attendanceRepo;
    private final BarcodeRepository barcodeRepository;
    // FXML TABLE
    private final Stage primaryStage;
    @FXML
    TableView<UserDetail> table;
    @FXML
    Label paginationLabel;

    public PanelAttendanceController(Stage primaryStage, HashMap<Class, Object> repositoryMap) {
        this.primaryStage = primaryStage;
        this.userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        this.attendanceRepo = (UserAttendanceRepository) repositoryMap.get(UserAttendanceRepository.class);
        this.barcodeRepository = (BarcodeRepository) repositoryMap.get(BarcodeRepository.class);

    }

    @FXML
    public void initialize() {
        System.out.println("Attendance loaded");

        LayoutUtil.stretchColumnsToEqualSize(table);
        updatePagination(LocalDate.now());
        listPageForDate();
    }

    @Override
    public void viewSwitched() {
        System.out.println("Panel attendance");
    }

    private void listPageForDate() {
        List<UserDetail> collect = barcodeRepository.findAllForAttendanceDate(paginationDate)
                .stream().map(o -> UserDetail.fromEntityToFull(o.getBarcode(), FormatUtil.toDateTimeFormat(o.getTimestamp()))).collect(Collectors.toList());
        table.getItems().setAll(collect);
    }

    @FXML
    protected void goNextPage() {
        updatePagination(paginationDate.plusDays(1));
        listPageForDate();
    }
    @FXML
    protected void goPrevPage() {
        updatePagination(paginationDate.minusDays(1));
        listPageForDate();
    }
    @FXML
    protected void addAttendanceManually() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/panel/form-barcode-get-attendance.fxml"));
        FormBarcodeGetAttendance controller = new FormBarcodeGetAttendance(barcodeRepository);
        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

        Stage dialogStage = createStage("Dodaj korisnika u tim", root, primaryStage);
        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            addAttendanceForBarcodeId(paginationDate, controller.getBarcodeId());
        }

    }
    private void updatePagination(LocalDate localDate) {
        paginationDate = localDate;
        paginationLabel.setText(formatPagination(paginationDate));
    }


    public void onBarcodeRead(String barcodeIdStr) {
        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        addAttendanceForBarcodeId(barcodeId);
    }
    private void addAttendanceForBarcodeId(Long barcodeId) {
        addAttendanceForBarcodeId(LocalDateTime.now(), barcodeId);
    }

    private void addAttendanceForBarcodeId(LocalDate paginationDate, Long barcodeId) {
        addAttendanceForBarcodeId(paginationDate.atStartOfDay(), barcodeId);
    }

    private void addAttendanceForBarcodeId(LocalDateTime paginationDate, Long barcodeId) {
        Optional<BarcodeEntity> optBarcode = barcodeRepository.findById(barcodeId);
        if (optBarcode.isPresent()) {
            BarcodeEntity barcode = optBarcode.get();

            if (barcode.getStatus() != BarcodeEntity.Status.ASSIGNED) {
                ToastView.showModal("Barkod se trenutno ne koristi.");
                return;
            }

            System.out.println("Adding attendance " + barcodeId);
            attendanceRepo.saveNew(barcode, LocalDateTime.now());


            listPageForDate();
            UserEntity user = barcode.getUser();
            ToastView.showModal(String.format("Prisutnost registrovana za %s %s", user.getFirstName(), user.getLastName()));
        }
    }
}
