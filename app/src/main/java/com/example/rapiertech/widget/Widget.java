package com.example.rapiertech.widget;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.rapiertech.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
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
        actv.setEnabled(false);
        actv.setClickable(false);
    }

    public void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
    }

    public void enableAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.setEnabled(true);
        autoCompleteTextView.setClickable(true);
    }

    public void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
    }

    public void setEnableEditText(EditText editText, boolean b) {
        editText.setFocusable(b);
        editText.setFocusableInTouchMode(b);
        editText.setEnabled(b);
    }

    public void setEnableAutoCompleteTextView(AutoCompleteTextView actv, boolean b) {
        actv.setEnabled(b);
        actv.setClickable(b);
    }

    public void setEnableMultiAutoCompleteTextView(MultiAutoCompleteTextView mactv, boolean b) {
        mactv.setEnabled(b);
        mactv.setClickable(b);
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
            newDate = dateFormat.format(Objects.requireNonNull(parseDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }

    public String changeDateFormatToDateTime(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parseDate;
        String newDate = null;

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));
        try {
            parseDate = dateFormat.parse(date);
            dateFormat.applyPattern("HH:mm | dd MMM yyyy");
            newDate = dateFormat.format(Objects.requireNonNull(parseDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }

    public String formatRupiah(Double amount) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(amount);
    }

    public String editTextFormatRupiah(Double amount) {
        Locale localeID = new Locale("IND", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String result = formatRupiah.format(amount);
        String[] split = result.split(",");
        int length = split[0].length();
        return split[0].substring(0,2)+split[0].substring(2, length);
    }

    public int getDigitOnly(String string){
        return Integer.parseInt(string.replaceAll("\\D+",""));
    }

    public void editTextFormatRupiahChangeListener(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)){
                    editText.removeTextChangedListener(this);
                    String replace = s.toString().replaceAll("[Rp.]", "");
                    if (!replace.isEmpty()){
                        current = editTextFormatRupiah(Double.valueOf(replace));
                    } else {
                        current = "";
                    }
                    editText.setText(current);
                    editText.setSelection(current.length());
                    editText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
