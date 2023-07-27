package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserAttendanceRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.UserMembershipRepository;
import com.example.gambarucmsui.model.AttendanceCount;
import com.example.gambarucmsui.model.MembershipCount;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import static com.example.gambarucmsui.common.LayoutUtil.stretchInsideAnchorPance;

public class PanelStatisticsController implements PanelHeader{
    private final UserAttendanceRepository attendanceRepo;
    private final UserMembershipRepository membershipRepo;

    public PanelStatisticsController(Stage primaryStage, HashMap<Class, Repository> repositoryMap) {
        attendanceRepo = (UserAttendanceRepository) repositoryMap.get(UserAttendanceRepository.class);
        membershipRepo = (UserMembershipRepository) repositoryMap.get(UserMembershipRepository.class);
    }

    @FXML private AnchorPane graphAttendancePane;
    @FXML private AnchorPane graphMembershipPane;

    @FXML
    public void initialize() {
        System.out.println("PanelStatisticsController");
    }

    @Override
    public void viewSwitched() {
        System.out.println("Panel statistic");

        List<AttendanceCount> attendanceData = attendanceRepo.getAttendanceDataLast60Days();
        List<MembershipCount> membershipData = membershipRepo.getMembershipCountByMonthLastYear();

        populateAttendanceGraph(attendanceData);
        populateMembershipGraph(membershipData);
    }

    private void populateMembershipGraph(List<MembershipCount> membershipData) {
        CategoryAxis xAxis = new CategoryAxis();
//        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();
//        yAxis.setLabel("Membership Count");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        for (MembershipCount membershipCount : membershipData) {
            String monthLabel = membershipCount.getDate().format(formatter);
            long count = membershipCount.getCount();
            series.getData().add(new XYChart.Data<>(monthLabel, count));
        }

        lineChart.getData().add(series);

        // hide series name
        lineChart.setLegendVisible(false);

        stretchInsideAnchorPance(lineChart);
        graphMembershipPane.getChildren().clear();
        graphMembershipPane.getChildren().add(lineChart);
    }

    private void populateAttendanceGraph(List<AttendanceCount> attendanceData) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

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

        stretchInsideAnchorPance(lineChart);
        graphAttendancePane.getChildren().clear();
        graphAttendancePane.getChildren().add(lineChart);
    }
}
