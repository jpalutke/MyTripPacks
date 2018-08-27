package com.crystaltowerdesigns.mytrippacks.data;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.crystaltowerdesigns.mytrippacks.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Validation {
    public final static int NOT_NULL = 1;
    public final static int NOT_EMPTY = 2;
    public final static int IS_NUMERIC = 3;
    public final static int IS_WHOLE_NUMBER = 4;
    public final static int IS_DATE = 5;
    public final static int IS_POSITIVE = 6;

    // Constructor to prevent accidentally instantiating the Validation class
    private Validation() {
        throw new AssertionError("No instances for you!");
    }

    /**
     * {link} Method to be used for field validation. No toasts will be shown.
     *
     * @param value      A string containing the text to check.
     * @param checkFlags Any combination of NOT_NULL, NOT_EMPTY, IS_NUMERIC, IS_WHOLE_NUMBER, IS_POSITIVE
     *                   Separate your selections with a comma.
     *
     * @return A boolean value containing the results of the check(s).
     */
    public static boolean isValid(@Nullable String value, int... checkFlags) {
        return isValid(null, null, value, checkFlags);
    }

    /**
     * {link} Method to be used for field validation.
     *
     * @param toastContext Context used for toasts. If toastContext is null then no Toast is shown.
     * @param fieldName    String containing the name of the field being validated.
     * @param value        A string containing the text to check.
     * @param checkFlags   Any combination of NOT_NULL, NOT_EMPTY, IS_NUMERIC, IS_WHOLE_NUMBER, IS_POSITIVE
     *                     Separate your selections with a comma.
     *
     * @return A boolean value containing the results of the check(s).
     */
    public static boolean isValid(@Nullable Context toastContext, String fieldName, @Nullable String value, int... checkFlags) {
        boolean result = true;
        for (int checkFlag : checkFlags) {
            switch (checkFlag) {
                case NOT_NULL:
                    result = result && (value != null);
                    break;
                case NOT_EMPTY:
                    result = result && (value != null && !value.isEmpty());
                    break;
                case IS_NUMERIC:
                    result = result && (isNumeric(value));
                    break;
                case IS_WHOLE_NUMBER:
                    assert value != null;
                    result = result && (isNumeric(value) && !value.contains("."));
                    break;
                case IS_DATE:
                    result = result && isValidDate(value);
                    break;
                case IS_POSITIVE:
                    result = result && isNumeric(value) && value.contains("-");
                    break;
            }
        }
        if (toastContext != null && !result)
            Toast.makeText(toastContext, String.format(toastContext.getString(R.string.invalid_field_value_format), fieldName.toUpperCase()), Toast.LENGTH_SHORT).show();
        return result;
    }

    /**
     * {@link}
     *
     * @param value A string to check for numeric content.
     *
     * @return boolean Returns true if the string was numeric, false if not.
     * Numerical is determined to be any number, approximately
     * ±3.40282347E+38F (6-7 significant decimal digits),
     * Java implements IEEE 754 standard, with or without decimal places.
     */
    @SuppressWarnings("SpellCheckingInspection")
    public static boolean isNumeric(String value) {
        Float floatValue;
        // avoid nulls
        String stringToCheck = "" + value;

        // Floats will need at least one decimal place. Add if needed.
        if (!stringToCheck.contains("."))
            stringToCheck = stringToCheck + ".0";

        try {
            floatValue = Float.parseFloat(stringToCheck);
        } catch (NullPointerException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return ("" + floatValue).equals(stringToCheck);
    }

    /**
     * {@link}
     *
     * @param value String containing the date to validate for format and validity.
     *              Valid format is yyyy-MM-dd
     *
     * @return boolean result as to whether or not it was a valid date.
     */
    private static boolean isValidDate(String value) {
        if (value != null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                Date date;
                date = fmt.parse(value);
            } catch (Exception e) {
                return false;
            }
            String yearString = value.substring(0, 4);
            String monthString = value.substring(5, 7);
            String dayString = value.substring(8, 10);
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.set(Integer.parseInt(yearString), Integer.parseInt(monthString), Integer.parseInt(dayString));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * {link} Method to be used for field validation.
     *
     * @param toastContext Context used for toasts. If toastContext is null then no Toast is shown.
     * @param fieldName    String containing the name of the field being validated.
     * @param value        An int containing the value to check.
     * @param validValues  A list of valid int values separated by commas.
     *
     * @return A boolean value containing the results of the check(s).
     */
    public static boolean isOneOf(@Nullable Context toastContext, String fieldName, int value, int... validValues) {
        boolean result = false;
        for (int validValue : validValues)
            if (validValue == value) {
                result = true;
                break;
            }
        if (toastContext != null && !result)
            Toast.makeText(toastContext, String.format(toastContext.getString(R.string.invalid_field_value_format), fieldName.toUpperCase()), Toast.LENGTH_SHORT).show();
        return result;
    }

    /**
     * {link} Method to be used for field validation.
     *
     * @param toastContext Context used for toasts. If toastContext is null then no Toast is shown.
     * @param fieldName    String containing the name of the field being validated.
     * @param value        A String containing the value to check.
     * @param validValues  A list of valid String values separated by commas.
     *
     * @return A boolean value containing the results of the check(s).
     */
    public static boolean isOneOf(@Nullable Context toastContext, String fieldName, String value, String... validValues) {
        boolean result = false;
        for (String validValue : validValues)
            if (validValue.equals(value)) {
                result = true;
                break;
            }
        if (toastContext != null && !result)
            Toast.makeText(toastContext, String.format(toastContext.getString(R.string.invalid_field_value_format), fieldName.toUpperCase()), Toast.LENGTH_SHORT).show();
        return result;
    }


}
