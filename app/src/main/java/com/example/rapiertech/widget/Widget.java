package com.example.rapiertech.widget;

import android.app.Activity;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.rapiertech.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import www.sanju.motiontoast.MotionToast;

public class Widget {

    public void noConnectToast(String message, FragmentActivity fragmentActivity) {
        MotionToast.Companion.createColorToast(fragmentActivity, "Cannot connect server",
                message,
                MotionToast.TOAST_NO_INTERNET,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(fragmentActivity, R.font.helvetica_regular)
        );
    }

    public void errorToast(String message, FragmentActivity fragmentActivity) {
        MotionToast.Companion.createColorToast(fragmentActivity, "Error",
                message,
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(fragmentActivity,R.font.helvetica_regular)
        );
    }

    public void successToast(String message, FragmentActivity fragmentActivity) {
        MotionToast.Companion.createColorToast((Activity) fragmentActivity, "Success",
                message,
                MotionToast.TOAST_SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(fragmentActivity,R.font.helvetica_regular)
        );
    }

    public void disableAutoCompleteTextView(AutoCompleteTextView actv) {
        actv.setFocusable(false);
        actv.setEnabled(false);
        actv.setClickable(false);
    }

    public void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
    }

    public void enableAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.setFocusable(true);
        autoCompleteTextView.setEnabled(true);
        autoCompleteTextView.setClickable(true);
    }

    public void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
    }

    public void setActvFocusableFalse(AutoCompleteTextView actv) {
        actv.setFocusable(false);
    }

    public String capitalizeText(String text) {
        char[] charArray = text.toCharArray();
        boolean foundSpace = true;

        for(int i = 0; i < charArray.length; i++) {
            if(Character.isLetter(charArray[i])) {
                if(foundSpace) {
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
            }
            else {
                foundSpace = true;
            }
        }

        return String.valueOf(charArray);
    }

    public String changeDateFormat(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parseDate;
        String newDate = null;

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));
        try {
            parseDate = dateFormat.parse(date);
            dateFormat.applyPattern("dd MMM yyyy");
            newDate = dateFormat.format(parseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }
}
