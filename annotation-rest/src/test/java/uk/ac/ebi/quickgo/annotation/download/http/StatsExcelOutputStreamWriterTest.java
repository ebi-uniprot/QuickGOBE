package uk.ac.ebi.quickgo.annotation.download.http;

import uk.ac.ebi.quickgo.annotation.service.converter.WorkbookFromStatistics;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Tony Wardell
 * Date: 03/10/2017
 * Time: 13:41
 * Created with IntelliJ IDEA.
 */
class StatsExcelOutputStreamWriterTest {

    private Workbook mockWorkbook;
    private OutputStream mockOutputStream;

    private StatsExcelDispatchWriter dispatchWriter;
    private QueryResult inputObject;

    @BeforeEach
    void setup(){
        WorkbookFromStatistics mockConverter = mock(WorkbookFromStatistics.class);
        mockWorkbook = mock(Workbook.class);
        mockOutputStream = mock(OutputStream.class);
        dispatchWriter = new StatsExcelDispatchWriter(mockConverter);
        inputObject =  new QueryResult.Builder<String>(0, Collections.emptyList()).build();
        when(mockConverter.generate(Collections.emptyList())).thenReturn(mockWorkbook);
    }

    @Test
    void successfulOutputWorkbook() throws Exception{
        dispatchWriter.write(inputObject, mockOutputStream);

        verify(mockWorkbook).write(mockOutputStream);
        verify(mockOutputStream).flush();
    }

    @Test
    void exceptionFromWorkBookWriteIsNotPropagated() throws Exception{
        doThrow(new IOException()).when(mockWorkbook).write(mockOutputStream);

        dispatchWriter.write(inputObject, mockOutputStream);
    }

    @Test
    void writeErrorIfDispatchWriterIsPassedAnError() throws Exception{
        Object errorObject = new ResponseExceptionHandler.ErrorInfo("","");

        dispatchWriter.write(errorObject, mockOutputStream);

        verify(mockWorkbook, never()).write(mockOutputStream);
        verify(mockOutputStream, atLeast(2)).write(any());
    }

    @Test
    void exceptionThrownIfConverterIsNull(){
        assertThrows(IllegalArgumentException.class, () -> new StatsExcelDispatchWriter(null));
    }
}
