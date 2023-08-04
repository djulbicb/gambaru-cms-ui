package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.database.repo.BarcodeRepository;
import com.example.gambarucmsui.database.repo.UserAttendanceRepository;
import com.example.gambarucmsui.database.repo.UserRepository;

import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.user.AddUserAttendance;
import com.example.gambarucmsui.util.LayoutUtil;
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

import static com.example.gambarucmsui.util.LayoutUtil.formatPagination;
import static com.example.gambarucmsui.util.FormatUtil.*;
import static com.example.gambarucmsui.util.LayoutUtil.stretchColumnsToEqualSize;
import static com.example.gambarucmsui.util.PathUtil.FORM_BARCODE_GET_ATTENDANCE;

public class PanelAttendanceController implements PanelHeader {

    private final AddUserAttendance addAttendance;
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

        addAttendance = Container.getBean(AddUserAttendance.class);

    }

    @FXML
    public void initialize() {
        System.out.println("Attendance loaded");

        LayoutUtil.stretchColumnsToEqualSize(table);
        updatePagination(LocalDate.now());
        listPageForDate();

        stretchColumnsToEqualSize(table);
    }

    @Override
    public void viewSwitched() {
        System.out.println("Switched to panel Attendance.");

    }

    private void listPageForDate() {
        List<UserDetail> collect = barcodeRepository.findAllForAttendanceDate(paginationDate)
                .stream().map(o -> UserDetail.fromEntityToFull(o.getBarcode(), FormatUtil.toDateTimeFormat(o.getTimestamp()))).collect(Collectors.toList());
        table.getItems().setAll(collect);
    }

    @FXML
    protected void goToToday() {
        updatePagination(LocalDate.now());
        listPageForDate();
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FORM_BARCODE_GET_ATTENDANCE));
        FormBarcodeGetAttendance controller = new FormBarcodeGetAttendance(barcodeRepository);
        fxmlLoader.setController(controller);
        VBox root = fxmlLoader.load();

        Stage dialogStage = createStage("Dodaj korisnika u tim", root, primaryStage);
        dialogStage.showAndWait();

        if (controller.isFormReady()) {
            addAttendance.addAttendance(controller.getBarcodeId(), getDateOfPaginationOrNow());
        }

    }
    private void updatePagination(LocalDate localDate) {
        paginationDate = localDate;
        paginationLabel.setText(formatPagination(paginationDate));
    }


    public void onBarcodeRead(String barcodeIdStr) {
        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        addAttendance.addAttendance(barcodeId, getDateOfPaginationOrNow());
        listPageForDate();
    }

    private LocalDateTime getDateOfPaginationOrNow() {
        LocalDateTime now = LocalDateTime.now();
        if (paginationDate.equals(now.toLocalDate())) {
           return now;
        }
        return paginationDate.atStartOfDay();
    }
}
