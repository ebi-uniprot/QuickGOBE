package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;

import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Defines the method(s) to convert from a list of statistics objects to an Excel Workbook.
 * @author Tony Wardell
 * Date: 03/10/2017
 * Time: 13:53
 * Created with IntelliJ IDEA.
 */
public interface StatisticsConverter {

    Workbook convert(List<StatisticsGroup> statisticsGroups);
}
