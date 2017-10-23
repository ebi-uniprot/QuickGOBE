package uk.ac.ebi.quickgo.annotation.download.http;

import uk.ac.ebi.quickgo.annotation.service.converter.StatisticsConverter;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;

/**
 * @author Tony Wardell
 * Date: 03/10/2017
 * Time: 13:41
 * Created with IntelliJ IDEA.
 */
public class StatsExcelOutputStreamWriterTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Workbook mockWorkbook;
    private OutputStream mockOutputStream;

    private StatsExcelDispatchWriter dispatchWriter;
    private QueryResult inputObject;

    @Before
    public void setup(){
        StatisticsConverter mockConverter = mock(StatisticsConverter.class);
        mockWorkbook = mock(Workbook.class);
        mockOutputStream = mock(OutputStream.class);
        dispatchWriter = new StatsExcelDispatchWriter(mockConverter);
        inputObject =  new QueryResult.Builder<String>(0, Collections.emptyList()).build();
        when(mockConverter.convert(Collections.emptyList())).thenReturn(mockWorkbook);

    }

    @Test
    public void successfulOutputWorkbook() throws Exception{
        dispatchWriter.write(inputObject, mockOutputStream);

        verify(mockWorkbook).write(mockOutputStream);
        verify(mockOutputStream).flush();

    }

    @Test
    public void exceptionFromWorkBookWriteIsNotPropogated() throws Exception{
        doThrow(new IOException()).when(mockWorkbook).write(mockOutputStream);

        dispatchWriter.write(inputObject, mockOutputStream);
    }

    @Test
    public void writeErrorIfDispatchWriterIsPassedAnError() throws Exception{
        Object errorObject = new ResponseExceptionHandler.ErrorInfo("","");

        dispatchWriter.write(errorObject, mockOutputStream);

        verify(mockWorkbook, never()).write(mockOutputStream);
        verify(mockOutputStream, atLeast(2)).write(any());
    }

    @Test
    public void exceptionThrownIfConverterIsNull(){
        exception.expect(IllegalArgumentException.class);
        new StatsExcelDispatchWriter(null);
    }
}
