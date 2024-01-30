package uk.ac.ebi.quickgo.rest.comm;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created 11/08/16
 * @author Edd
 */
class FilterContextTest {

    private FilterContext context;

    @BeforeEach
    void setUp() {
        context = new FilterContext();
    }

    @Test
    void canFetchingFromEmptyContextFindsNull() {
        Optional<FakeEntity> fakeEntity = context.get(FakeEntity.class);
        assertThat(fakeEntity, is(Optional.empty()));
    }

    @Test
    void canPutAndGetFromContext() {
        FakeEntity entity = new FakeEntity();
        entity.value = "something interesting";
        context.save(FakeEntity.class, entity);

        FakeEntity retrievedEntity = context.get(FakeEntity.class).orElse(new FakeEntity());
        assertThat(retrievedEntity, is(entity));
    }

    @Test
    void canMergeTwoContextsIntoOne() {
        FakeEntity fakeEntity = new FakeEntity();
        fakeEntity.value = "something interesting";
        context.save(FakeEntity.class, fakeEntity);

        FilterContext anotherContext = new FilterContext();
        AnotherFakeEntity anotherFakeEntity = new AnotherFakeEntity();
        anotherFakeEntity.anotherValue = "something interesting";
        anotherContext.save(AnotherFakeEntity.class, anotherFakeEntity);

        FilterContext mergedContext = context.merge(anotherContext);

        FakeEntity retrievedFakeEntity = mergedContext.get(FakeEntity.class).orElse(new FakeEntity());
        assertThat(retrievedFakeEntity, is(fakeEntity));

        AnotherFakeEntity retrievedAnotherFakeEntity =
                mergedContext.get(AnotherFakeEntity.class).orElse(new AnotherFakeEntity());
        assertThat(retrievedAnotherFakeEntity, is(anotherFakeEntity));
    }

    private static class FakeEntity {
        String value;
    }

    private static class AnotherFakeEntity {
        String anotherValue;
    }
}