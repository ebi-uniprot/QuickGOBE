package uk.ac.ebi.quickgo.index;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ExceptionMatcher extends TypeSafeMatcher<Throwable> {
    private final Class<? extends Throwable> type;
    private final String expectedMessage;

    public ExceptionMatcher(Class<? extends Throwable> type, String expectedMessage) {
        this.type = type;
        this.expectedMessage = expectedMessage;
    }

    @Override
    protected boolean matchesSafely(Throwable item) {
        return item.getClass().isAssignableFrom(type)
                && item.getMessage().contains(expectedMessage);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("expects type ")
                .appendValue(type)
                .appendText(" and a message ")
                .appendValue(expectedMessage);
    }
}
