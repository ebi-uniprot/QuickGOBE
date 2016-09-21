package uk.ac.ebi.quickgo.annotation.model;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ArrayPattern.Validator.class})
@Documented @interface ArrayPattern {
    String ERROR_MSG = "At least one '%s' value is invalid: %s";

    /**
     * Defines the regular expression that each element in the array must match
     */
    String regexp();

    String message() default ERROR_MSG;

    /**
     * The name of the parameter being validated
     */
    String paramName();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Flag[] flags() default {};

    /**
     * Regexp flags accepted by this validator
     */
    enum Flag {

        /**
         * Enables Unix lines mode.
         *
         * @see java.util.regex.Pattern#UNIX_LINES
         */
        UNIX_LINES(java.util.regex.Pattern.UNIX_LINES),

        /**
         * Enables case-insensitive matching.
         *
         * @see java.util.regex.Pattern#CASE_INSENSITIVE
         */
        CASE_INSENSITIVE(java.util.regex.Pattern.CASE_INSENSITIVE),

        /**
         * Permits whitespace and comments in pattern.
         *
         * @see java.util.regex.Pattern#COMMENTS
         */
        COMMENTS(java.util.regex.Pattern.COMMENTS),

        /**
         * Enables multiline mode.
         *
         * @see java.util.regex.Pattern#MULTILINE
         */
        MULTILINE(java.util.regex.Pattern.MULTILINE),

        /**
         * Enables dotall mode.
         *
         * @see java.util.regex.Pattern#DOTALL
         */
        DOTALL(java.util.regex.Pattern.DOTALL),

        /**
         * Enables Unicode-aware case folding.
         *
         * @see java.util.regex.Pattern#UNICODE_CASE
         */
        UNICODE_CASE(java.util.regex.Pattern.UNICODE_CASE),

        /**
         * Enables canonical equivalence.
         *
         * @see java.util.regex.Pattern#CANON_EQ
         */
        CANON_EQ(java.util.regex.Pattern.CANON_EQ);

        private final int value;

        Flag(int value) {
            this.value = value;
        }

        /**
         * @return flag value as defined in {@link java.util.regex.Pattern}
         */
        public int getValue() {
            return value;
        }
    }

    class Validator implements ConstraintValidator<ArrayPattern, String[]> {
        private Pattern pattern;
        private String paramName;

        @Override
        public void initialize(ArrayPattern validator) {
            int intFlag = maskFlags(validator.flags());

            paramName = validator.paramName();

            try {
                pattern = Pattern.compile(validator.regexp(), intFlag);
            } catch (PatternSyntaxException e) {
                throw new IllegalArgumentException("Unable to create pattern for validation");
            }
        }

        /**
         * Creates an bit mask for the selected flags. The resulting int value is then fed to {@link Pattern} to
         * create a regular expression matcher.
         *
         * @param flags the selects flags to maks
         * @return a bit mask value
         */
        private int maskFlags(Flag[] flags) {
            int intFlag = 0;

            for (Flag flag : flags) {
                intFlag = intFlag | flag.getValue();
            }

            return intFlag;
        }

        @Override
        public boolean isValid(String[] items, ConstraintValidatorContext context) {
            boolean isValid = true;

            if (items != null) {
                List<String> invalidItems = new ArrayList<>();

                for (String next : items) {
                    Matcher matcher = pattern.matcher(next);
                    if (!matcher.matches()) {
                        invalidItems.add(next);
                        isValid = false;
                    }
                }

                if (!isValid) {
                    context.disableDefaultConstraintViolation();

                    String invalidItemsText = invalidItems.stream().collect(Collectors.joining(","));

                    context.buildConstraintViolationWithTemplate(
                            String.format(ERROR_MSG, paramName, invalidItemsText)).addConstraintViolation();
                }
            }

            return isValid;
        }
    }
}