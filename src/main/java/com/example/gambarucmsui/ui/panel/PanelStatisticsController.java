package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.attendance.GetAttendanceCountForMonth;
import com.example.gambarucmsui.ports.interfaces.attendance.GetAttendancesByUser;
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
import java.util.List;

import static com.example.gambarucmsui.util.LayoutUtil.stretchColumnsToEqualSize;
import static com.example.gambarucmsui.util.LayoutUtil.stretchInsideAnchorPance;

public class PanelStatisticsController implements PanelHeader{
    private LocalDate paginationMonth;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private final GetAttendanceCountForMonth attendanceCountForMonth;

    @FXML private AnchorPane graphAttendancePane;
    @FXML private AnchorPane graphMembershipPane;
    @FXML
    Label paginationLabel;
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
        yAxis.setUpperBound(20);

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
