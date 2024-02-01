package uk.ac.ebi.quickgo.rest.search.results.transformer;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.ebi.quickgo.rest.comm.FilterContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created 12/08/16
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class ResultTransformerChainTest {

    @Mock
    private ResultTransformer<FakeResult> transformer1;

    @Mock
    private ResultTransformer<FakeResult> transformer2;

    @Mock
    private FilterContext context;

    private ResultTransformerChain<FakeResult> transformerChain;
    private FakeResult originalResult;

    @BeforeEach
    void setUp() {
        transformerChain = new ResultTransformerChain<>();
    }

    @Test
    void transformationOfEmptyResultChainReturnsOriginal() {
        originalResult = new FakeResult("hello");

        FakeResult transformedResult = transformerChain.applyTransformations(originalResult, context);

        assertThat(transformedResult.value, is(originalResult.value));
    }

    @Test
    void transformationWithOneTransformerReturnsTransformedResult() {
        originalResult = new FakeResult("hello");

        FakeResult transformationResult1 = new FakeResult("transformationResult1");
        when(transformer1.transform(any(), any())).thenReturn(transformationResult1);

        transformerChain.addTransformer(transformer1);
        FakeResult transformedResult = transformerChain.applyTransformations(originalResult, context);

        assertThat(transformedResult.value, is(transformationResult1.value));
    }

    @Test
    void transformationWithTwoTransformersReturnsTransformedResult() {
        originalResult = new FakeResult("hello");

        FakeResult transformationResult1 = new FakeResult("transformationResult1");
        when(transformer1.transform(any(), any())).thenReturn(transformationResult1);

        FakeResult transformationResult2 = new FakeResult("transformationResult2");
        when(transformer2.transform(any(), any())).thenReturn(transformationResult2);

        transformerChain.addTransformer(transformer1);
        transformerChain.addTransformer(transformer2);
        FakeResult transformedResult = transformerChain.applyTransformations(originalResult, context);

        assertThat(transformedResult.value, is(transformationResult2.value));
    }

    private record FakeResult(String value) {

    }

}