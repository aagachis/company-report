package report.service;

import report.model.Pair;

import java.util.List;

public interface PrintReportService {
    <T> void printResults(List<Pair<T>> pairs, String message);
}
