package uk.ac.ebi.quickgo.rest.comm;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * Created 11/08/16
 * @author Edd
 */
public class ConversionContextTest {

    private ConversionContext context;

    @Before
    public void setUp() {
        context = new ConversionContext();
    }

    @Test
    public void canFetchingFromEmptyContextFindsNull() {
        FakeEntity retrievedEntity = context.get(FakeEntity.class);
        assertThat(retrievedEntity, is(nullValue()));
    }

    @Test
    public void canPutAndGetFromContext() {
        FakeEntity entity = new FakeEntity();
        entity.value = "something interesting";
        context.put(FakeEntity.class, entity);

        FakeEntity retrievedEntity = context.get(FakeEntity.class);
        assertThat(retrievedEntity, is(entity));
    }

    @Test
    public void canMergeTwoContextsIntoOne() {
        FakeEntity fakeEntity = new FakeEntity();
        fakeEntity.value = "something interesting";
        context.put(FakeEntity.class, fakeEntity);

        ConversionContext anotherContext = new ConversionContext();
        AnotherFakeEntity anotherFakeEntity = new AnotherFakeEntity();
        anotherFakeEntity.anotherValue = "something interesting";
        anotherContext.put(AnotherFakeEntity.class, anotherFakeEntity);

        ConversionContext mergedContext = context.merge(anotherContext);

        FakeEntity retrievedFakeEntity = mergedContext.get(FakeEntity.class);
        assertThat(retrievedFakeEntity, is(fakeEntity));

        AnotherFakeEntity retrievedAnotherFakeEntity = mergedContext.get(AnotherFakeEntity.class);
        assertThat(retrievedAnotherFakeEntity, is(anotherFakeEntity));
    }

    private static class FakeEntity {
        String value;
    }

    private static class AnotherFakeEntity {
        String anotherValue;
    }
}