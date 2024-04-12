package report;

import report.service.EmployeeReportService;
import report.service.EmployeeReportServiceImpl;

public class MainApplication {

    public static void main(String... args) {
        EmployeeReportService reportService = new EmployeeReportServiceImpl();
        reportService.generateCompanyReport(args);
    }
}