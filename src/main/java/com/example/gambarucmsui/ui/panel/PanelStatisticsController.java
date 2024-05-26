package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.PersonMembershipEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.attendance.GetAttendanceCountForMonth;
import com.example.gambarucmsui.ports.interfaces.attendance.GetAttendancesByUser;
import com.example.gambarucmsui.ports.interfaces.membership.LoadPersonMembership;
import com.example.gambarucmsui.ui.dto.core.MembershipFeeDetail;
import com.example.gambarucmsui.ui.dto.statistics.AttendanceCount;
import com.example.gambarucmsui.ui.dto.statistics.AttendanceUserCount;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.util.FormatUtil.toDateFormat;
import static com.example.gambarucmsui.util.LayoutUtil.stretchInsideAnchorPance;

public class PanelStatisticsController implements PanelHeader{
    private LocalDate paginationMonth;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private final GetAttendanceCountForMonth attendanceCountForMonth;
    private final LoadPersonMembership loadPersonMembership;

    @FXML private AnchorPane graphAttendancePane;
    @FXML private AnchorPane graphMembershipPane;
    @FXML
    Label paginationLabel;
    @FXML
    private Label lblMembershipFeeTotal;
    @FXML
    private TableView<MembershipFeeDetail> tblMembershipTbl;

    @FXML
    protected void goPrevPage() {
        paginationMonth = paginationMonth.minusMonths(1);
        updatePaginationLbl(paginationMonth);
        showStatisticsForDate(paginationMonth);
    }

    private void updatePaginationLbl(LocalDate paginationMonth) {
        paginationLabel.setText(formatter.format(paginationMonth));
    }

    @FXML
    protected void goNextPage() {
        paginationMonth = paginationMonth.plusMonths(1);
        updatePaginationLbl(paginationMonth);
        showStatisticsForDate(paginationMonth);
    }
    @FXML
    private TableView<AttendanceUserCount> tblUserStats;
    private final GetAttendancesByUser attendancesByUser;

    public PanelStatisticsController(Stage primaryStage) {
        this.paginationMonth = LocalDate.now();
        this.attendancesByUser = Container.getBean(GetAttendancesByUser.class);
        this.attendanceCountForMonth = Container.getBean(GetAttendanceCountForMonth.class);
        loadPersonMembership = Container.getBean(LoadPersonMembership.class);
    }


    @FXML
    public void initialize() {
        System.out.println("PanelStatisticsController");
        updatePaginationLbl(paginationMonth);
    }

    @Override
    public void viewSwitched() {
        System.out.println("Switched to panel Statistic.");
        showStatisticsForDate(paginationMonth);
    }

    private void showStatisticsForDate(LocalDate forDate) {
        populateAttendanceGraph(forDate);
        pupulateAttendanceForPerson(forDate);
        populateMembership(forDate);
    }

    private void populateMembership(LocalDate forDate) {
        tblMembershipTbl.getItems().clear();

        List<PersonMembershipEntity> membershipsEn = loadPersonMembership.getAllMembershipForMonth(forDate);
        if (membershipsEn.isEmpty()) {
            return;
        }

        List<MembershipFeeDetail> details = new ArrayList<>();
        int totalFee = 0;

        Map<Integer, List<PersonMembershipEntity>> groupedByDate =  membershipsEn.stream()
                .collect(Collectors.groupingBy(
                        PersonMembershipEntity::getCompactDate,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        for (Map.Entry<Integer, List<PersonMembershipEntity>> entries : groupedByDate.entrySet()) {
            List<PersonMembershipEntity> memberships = entries.getValue();


            int totalFeeForDate = 0;
            List<MembershipFeeDetail> detailsForDate = new ArrayList<>();
            for (PersonMembershipEntity m : memberships) {
                detailsForDate.add(MembershipFeeDetail.fromEntityToFull(m));
                totalFeeForDate += m.getFee();
                totalFee += m.getFee();
            }

            details.add(new MembershipFeeDetail("ukupno:", detailsForDate.size() + "", totalFeeForDate, detailsForDate.get(0).getDate()));
            detailsForDate.add(MembershipFeeDetail.empty());
            details.addAll(detailsForDate);
        }


        lblMembershipFeeTotal.setText(String.format("UKUPNO: %S RSD", totalFee));
        tblMembershipTbl.getItems().setAll(details);
    }

    private void pupulateAttendanceForPerson(LocalDate forDate) {
        tblUserStats.getItems().clear();
        List<AttendanceUserCount> attendancesByUsers = attendancesByUser.getAttendancesByUsers(forDate);
        for (AttendanceUserCount byUser : attendancesByUsers) {
            tblUserStats.getItems().add(byUser);
        }
    }

    private void populateAttendanceGraph(LocalDate forDate) {
        List<AttendanceCount> attendanceData = attendanceCountForMonth.getAttendanceCount(forDate);

//        graphAttendancePane.getChildren().clear();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(2);
        yAxis.setAutoRanging(false); // Disable auto-ranging for Y-axis
        yAxis.setLowerBound(0); // Set lower bound of Y-axis
        yAxis.setUpperBound(40);

        LineChart<String, Number> lineChart = new LineChart(xAxis, yAxis);
        lineChart.setAnimated(false);

        stretchInsideAnchorPance(lineChart);
//        graphAttendancePane.getChildren().clear();
        graphAttendancePane.getChildren().add(lineChart);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM");
        for (int i = 0; i < attendanceData.size(); i++) {
            AttendanceCount attendance = attendanceData.get(i);
            series.getData().add(new XYChart.Data<>(formatter.format(attendance.getDate()), attendance.getCount()));
        }
        lineChart.getData().add(series);

        // hide series name
        lineChart.setLegendVisible(false);


    }
}
