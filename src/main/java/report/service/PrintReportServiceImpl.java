package report.service;

import report.model.Pair;

import java.util.List;

public class PrintReportServiceImpl implements PrintReportService {

    public <T> void printResults(List<Pair<T>> pairs, String message) {
        pairs.forEach(pair ->
                System.out.println("Employee with id=" + pair.id() + " " + message + " " + pair.diff()));
    }
}
