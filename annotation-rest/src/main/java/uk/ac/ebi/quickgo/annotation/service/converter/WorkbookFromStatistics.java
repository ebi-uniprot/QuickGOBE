package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;

import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Defines the method to generate an Excel Workbook from a list of {@link StatisticsGroup}.
 * @author Tony Wardell
 * Date: 03/10/2017
 * Time: 13:53
 * Created with IntelliJ IDEA.
 */
@FunctionalInterface
public interface WorkbookFromStatistics {

    Workbook generate(List<StatisticsGroup> statisticsGroups);
}
