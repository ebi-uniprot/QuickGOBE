package uk.ac.ebi.quickgo.common.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * New class to repopulate arrays depending on requirements.
 */

public class ArrayPopulation {

    private ArrayPopulation() {
    }

    /**
     * If the targetArray array doesn't contain the value, then add it, else do nothing.
     * @param targetArray value array to check and update if necessary.
     * @param value to check for, update with.
     * @return a String array that WILL contain the value.
     */
    public static String[] ensureArrayContains(String[] targetArray, String value) {
        if (isNull(targetArray)) {
            return new String[]{value};
        } else {
            List<String> targetList = Arrays.asList(targetArray);
            if (targetList.contains(value)) {
                return targetArray;
            } else {
                List<String> fullList = new ArrayList<>(targetArray.length + 1);
                fullList.addAll(targetList);
                fullList.add(value);
                return fullList.toArray(new String[0]);
            }
        }
    }

    /**
     * If checkArray contains a value, make sure it exists in targetArray.
     * @param checkArray the array of values to check if value exists.
     * @param targetArray the array of values to add the value to (if it does already exist).
     * @param value to check for and update to targetArray (if it does already exist).
     * @return targetArray content, including value if checkArray contains value.
     */
    public static String[] updateFieldsWithCheckFields(String[] checkArray, String[] targetArray, String value) {
        if (isNull(checkArray)) {
            return targetArray;
        } else {
            List<String> checkList = Arrays.asList(checkArray);
            if (checkList.contains(value)) {
                return ensureArrayContains(targetArray, value);
            } else {
                return isNull(targetArray) ? new String[0] : targetArray;
            }
        }
    }
}
