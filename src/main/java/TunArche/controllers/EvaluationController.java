package TunArche.controllers;

import TunArche.entities.Evaluation;
import TunArche.entities.Formation;
import TunArche.services.EvaluationImpl;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.util.*;

public class EvaluationController {

    @FXML private BarChart<String, Number> barChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Label averageLabel;
    @FXML private Label totalReviewsLabel;

    private EvaluationImpl evaluationService = new EvaluationImpl();

    public void setFormation(Formation formation) {
        List<Evaluation> evaluations = evaluationService.showByFormation(formation.getId());

        Map<Integer, Integer> noteCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            noteCounts.put(i, 0);
        }

        double total = 0;
        for (Evaluation e : evaluations) {
            noteCounts.put(e.getNote(), noteCounts.get(e.getNote()) + 1);
            total += e.getNote();
        }

        int totalReviews = evaluations.size();
        double average = totalReviews == 0 ? 0 : total / totalReviews;

        averageLabel.setText(String.format("%.1f ★", average));
        totalReviewsLabel.setText(totalReviews + " avis");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 5; i >= 1; i--) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), noteCounts.get(i)));
        }

        barChart.getData().clear();
        barChart.getData().add(series);
    }
}