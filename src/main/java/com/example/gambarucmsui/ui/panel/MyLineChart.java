package com.example.gambarucmsui.ui.panel;

import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;

public class MyLineChart extends LineChart<String, Number> {

    public MyLineChart(
            @NamedArg("xAxis") final Axis<String> xAxis,
            @NamedArg("yAxis") final Axis<Number> yAxis) {

        super(xAxis, yAxis);
        yAxis.setTickLength(1);
        yAxis.setTickLabelGap(1);
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        Axis<Number> yAxis = getYAxis();
        double unit = 1d;

        ObservableList<Axis.TickMark<Number>> tickMarks = getYAxis().getTickMarks();
        for (Axis.TickMark<Number> tickMark : tickMarks) {
            double value = tickMark.getValue().doubleValue();
            boolean isWholeNumber = Math.abs(value - Math.round(value)) < 0.0001;
            tickMark.setTextVisible(isWholeNumber);
            tickMark.setTextVisible(isWholeNumber);
        }
    }
}
