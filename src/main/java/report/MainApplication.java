package report;

import report.service.EmployeeReportService;
import report.service.EmployeeReportServiceImpl;

import static report.configuration.ParametersConfig.FILE_NAME;

public class MainApplication {

    public static void main(String... args) {
        EmployeeReportService reportService = new EmployeeReportServiceImpl();
        reportService.generateCompanyReport(args != null && args.length > 0 ? args[0] : FILE_NAME);
    }
}