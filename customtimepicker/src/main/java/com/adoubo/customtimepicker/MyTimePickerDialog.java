package com.adoubo.customtimepicker;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * 注：默认为24小时制
 *
 * Created by caoweixin
 * 2017/2/27
 * Email: caoweixin@hikvision.com.cn
 */

public class MyTimePickerDialog extends DialogFragment implements RadialPickerLayout.OnValueSelectedListener {

    private static final String TAG = MyTimePickerDialog.class.getSimpleName();

    private String mLayoutMode = "normal";

    private static final String KEY_HOUR_OF_DAY = "hour_of_day";
    private static final String KEY_MINUTE = "minute";
    private static final String KEY_CURRENT_ITEM_SHOWING = "current_item_showing";

    public static final int HOUR_INDEX = 0;
    public static final int MINUTE_INDEX = 1;
    // NOT a real index for the purpose of what's showing.
    public static final int AMPM_INDEX = 2;
    // Also NOT a real index, just used for keyboard mode.
    public static final int ENABLE_PICKER_INDEX = 3;

    public static final int AM = 0;
    public static final int PM = 1;

    private OnTimeSetListener mCallback;

    private WeekOnClick weekOnClick;
    private boolean[] choose = {false, false, false, false, false, false, false};

    private TextView mMon;
    private TextView mTue;
    private TextView mWed;
    private TextView mThu;
    private TextView mFri;
    private TextView mSat;
    private TextView mSun;
    private TextView mStartHour;
    private TextView mStartMinute;
    private TextView mEndHour;
    private TextView mEndMinute;
    private Button mHtpCancel;
    private Button mHtpOk;
    private RadialPickerLayout mTimePicker;

    private int settingMode = 0; //当前正在设置哪个时间位，1:starthour,2:startminute,3:endhour,4:endminute

    private int mRed;
    private int mDefault;

    private int mInitialStartHour;
    private int mInitialStartMinute;
    private int mInitialEndHour;
    private int mInitialEndMinute;
    // Accessibility strings.
    private String mHourPickerDescription;
    private String mMinutePickerDescription;


    public interface OnTimeSetListener {

        void onTimeSet(RadialPickerLayout view, int startHour, int startMinute, int endHour, int endMinute, int week);

        void onCancel();
    }


    public MyTimePickerDialog() {

    }

    public static MyTimePickerDialog newInstance(OnTimeSetListener callback, TimeSection defaultTime) {
        MyTimePickerDialog ret = new MyTimePickerDialog();
        ret.initialize(callback, defaultTime.getStartHour(), defaultTime.getStartMinute(),
                defaultTime.getEndHour(), defaultTime.getEndMinute());
        return ret;
    }

    public void initialize(OnTimeSetListener callback, int startHour,
                           int startMinute, int endHour, int endMinute) {
        mCallback = callback;

        mInitialStartHour = startHour;
        mInitialStartMinute = startMinute;
        mInitialEndHour = endHour;
        mInitialEndMinute = endMinute;
    }

    public void setOnTimeSetListener(OnTimeSetListener callback) {
        mCallback = callback;
    }

    public void setStartTime(int hourOfDay, int minute) {
        mInitialStartHour = hourOfDay;
        mInitialStartMinute = minute;
    }

    public void setLayoutMode(String mode) {
        mLayoutMode = mode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_HOUR_OF_DAY)
                && savedInstanceState.containsKey(KEY_MINUTE)) {
            mInitialStartHour = savedInstanceState.getInt(KEY_HOUR_OF_DAY);
            mInitialStartMinute = savedInstanceState.getInt(KEY_MINUTE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View view = null;
        if (mLayoutMode.equals("normal")) {
            view = inflater.inflate(R.layout.my_time_picker_dialog, null);
        } else if (mLayoutMode.equals("strengthen")) {
            view = inflater.inflate(R.layout.my_time_picker_dialog_strengthen, null);
            mMon = (TextView) view.findViewById(R.id.monday);
            mTue = (TextView) view.findViewById(R.id.tuesday);
            mWed = (TextView) view.findViewById(R.id.wednesday);
            mThu = (TextView) view.findViewById(R.id.thursday);
            mFri = (TextView) view.findViewById(R.id.friday);
            mSat = (TextView) view.findViewById(R.id.saturday);
            mSun = (TextView) view.findViewById(R.id.sunday);
            weekOnClick = new WeekOnClick();
            mMon.setOnClickListener(weekOnClick);
            mTue.setOnClickListener(weekOnClick);
            mWed.setOnClickListener(weekOnClick);
            mThu.setOnClickListener(weekOnClick);
            mFri.setOnClickListener(weekOnClick);
            mSat.setOnClickListener(weekOnClick);
            mSun.setOnClickListener(weekOnClick);
        }


        Resources res = getResources();
        mHourPickerDescription = res.getString(R.string.hour_picker_description);
        mMinutePickerDescription = res.getString(R.string.minute_picker_description);

        mRed = res.getColor(R.color.bg_fade_color);
        mDefault = res.getColor(R.color.default_color);


        mStartHour = (TextView) view.findViewById(R.id.start_hour);
        mStartMinute = (TextView) view.findViewById(R.id.start_minute);
        mEndHour = (TextView) view.findViewById(R.id.end_hour);
        mEndMinute = (TextView) view.findViewById(R.id.end_minute);
        mHtpCancel = (Button) view.findViewById(R.id.ctp_cancel);
        mHtpOk = (Button) view.findViewById(R.id.ctp_ok);

        mTimePicker = (RadialPickerLayout) view.findViewById(R.id.time_picker);
        mTimePicker.setOnValueSelectedListener(this);
        mTimePicker.initialize(getActivity(), mInitialStartHour, mInitialStartMinute, true, false);
        int currentItemShowing = HOUR_INDEX;
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(KEY_CURRENT_ITEM_SHOWING)) {
            currentItemShowing = savedInstanceState.getInt(KEY_CURRENT_ITEM_SHOWING);
        }
        setCurrentItemShowing(currentItemShowing, false);
        mTimePicker.invalidate();

        mStartHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingMode = 1;
                int hour = Integer.valueOf(mStartHour.getText().toString());
                mTimePicker.setTime(hour, mTimePicker.getMinutes());
                setCurrentItemShowing(HOUR_INDEX, true);
            }
        });
        mStartMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingMode = 2;
                int minute = Integer.valueOf(mStartMinute.getText().toString());
                mTimePicker.setTime(mTimePicker.getHours(), minute);
                setCurrentItemShowing(MINUTE_INDEX, true);
            }
        });
        mEndHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingMode = 3;
                setEndHour(mTimePicker.getHours());
                setCurrentItemShowing(HOUR_INDEX, true);
            }
        });
        mEndMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingMode = 4;
                setEndMinute(mTimePicker.getMinutes());
                setCurrentItemShowing(MINUTE_INDEX, true);
            }
        });

        mHtpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingMode = 0;
                mCallback.onCancel();
                dismiss();
            }
        });

        mHtpOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneButtonClick();
            }
        });
        setStartHour(mInitialStartHour);
        setStartMinute(mInitialStartMinute);
        setEndHour(mInitialEndHour);
        setEndMinute(mInitialEndMinute);

        return view;
    }

    private void onDoneButtonClick() {
        if (!isTypedTimeFullyLegal()) {
            Toast.makeText(getContext(), "时间设置不合法", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCallback != null) {
            if (mLayoutMode.equals("normal")) {
                mCallback.onTimeSet(mTimePicker,
                        Integer.valueOf(mStartHour.getText().toString()), Integer.valueOf(mStartMinute.getText().toString()),
                        Integer.valueOf(mEndHour.getText().toString()), Integer.valueOf(mEndMinute.getText().toString()), -1);
            } else if (mLayoutMode.equals("strengthen")) {
                int week = 0;
                for (int i = 0; i < choose.length; i++) {
                    if (choose[i]) {
                        week += Math.pow(2, i);
                    }
                }
                mCallback.onTimeSet(mTimePicker,
                        Integer.valueOf(mStartHour.getText().toString()), Integer.valueOf(mStartMinute.getText().toString()),
                        Integer.valueOf(mEndHour.getText().toString()), Integer.valueOf(mEndMinute.getText().toString()), week);
            }
        }


        settingMode = 0;
        dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mTimePicker != null) {
            outState.putInt(KEY_HOUR_OF_DAY, mTimePicker.getHours());
            outState.putInt(KEY_MINUTE, mTimePicker.getMinutes());
            outState.putInt(KEY_CURRENT_ITEM_SHOWING, mTimePicker.getCurrentItemShowing());
        }
    }

    @Override
    public void onValueSelected(int pickerIndex, int newValue, boolean autoAdvance) {
        if (pickerIndex == HOUR_INDEX) {
            if (settingMode == 1) {
                setStartHour(newValue);
            } else if (settingMode == 3) {
                // 开始时间大于结束时间，结束时间为0时，则默认为24点
                if (Integer.valueOf(mStartHour.getText().toString()) > Integer.valueOf(mEndHour.getText().toString()) &&
                        mEndHour.getText().toString().equals("00") && mEndMinute.getText().toString().equals("00")) {
                    newValue = 24;
                }
                setEndHour(newValue);
            }
        } else if (pickerIndex == MINUTE_INDEX) {
           if (settingMode == 2) {
               setStartMinute(newValue);
           } else if (settingMode == 4) {
               setEndMinute(newValue);
           }
        }
    }

    private void setStartHour(int value) {
        CharSequence text = String.format(Locale.getDefault(), "%02d", value);
        mStartHour.setText(text);
    }

    private void setStartMinute(int value) {
        if (value == 60) {
            value = 0;
        }
        CharSequence text = String.format(Locale.getDefault(), "%02d", value);
        mStartMinute.setText(text);
    }

    private void setEndHour(int value) {
        CharSequence text = String.format(Locale.getDefault(), "%02d", value);
        mEndHour.setText(text);
    }

    private void setEndMinute(int value) {
        if (value == 60) {
            value = 0;
        }
        CharSequence text = String.format(Locale.getDefault(), "%02d", value);
        mEndMinute.setText(text);
    }

    // Show either Hours or Minutes.
    private void setCurrentItemShowing(int index, boolean animateCircle) {
        mTimePicker.setCurrentItemShowing(index, animateCircle);

        if (index == HOUR_INDEX) {
            int hours = mTimePicker.getHours();
            mTimePicker.setContentDescription(mHourPickerDescription + ": " + hours);
        } else {
            int minutes = mTimePicker.getMinutes();
            mTimePicker.setContentDescription(mMinutePickerDescription + ": " + minutes);
        }

        if (settingMode == 1) {
            mStartHour.setBackgroundColor(mRed);
            mStartMinute.setBackgroundColor(mDefault);
            mEndHour.setBackgroundColor(mDefault);
            mEndMinute.setBackgroundColor(mDefault);
        } else if (settingMode == 2) {
            mStartHour.setBackgroundColor(mDefault);
            mStartMinute.setBackgroundColor(mRed);
            mEndHour.setBackgroundColor(mDefault);
            mEndMinute.setBackgroundColor(mDefault);
        } else if (settingMode == 3) {
            mStartHour.setBackgroundColor(mDefault);
            mStartMinute.setBackgroundColor(mDefault);
            mEndHour.setBackgroundColor(mRed);
            mEndMinute.setBackgroundColor(mDefault);
        } else if (settingMode == 4) {
            mStartHour.setBackgroundColor(mDefault);
            mStartMinute.setBackgroundColor(mDefault);
            mEndHour.setBackgroundColor(mDefault);
            mEndMinute.setBackgroundColor(mRed);
        }
    }

    private boolean isTypedTimeFullyLegal() {

        int startHour = Integer.valueOf(mStartHour.getText().toString());
        int startMinute = Integer.valueOf(mStartMinute.getText().toString());
        int endHour = Integer.valueOf(mEndHour.getText().toString());
        int endMinute = Integer.valueOf(mEndMinute.getText().toString());
        if (startHour >= 0 && startHour <= 24 && startMinute >= 0 && startMinute < 60 &&
                endHour >= 0 && endHour <= 24 && endMinute >= 0 && endMinute < 60) {
            if ((startHour < endHour) || (startHour == endHour && startMinute <= endMinute)) {
                return true;
            }
        }
        return false;
    }

    private class WeekOnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.monday) {
                if (!choose[0]) {
                    mMon.setBackgroundResource(R.drawable.week_down_shape);
                    mMon.setTextColor(getResources().getColor(R.color.bg_color));
                    choose[0] = true;
                } else {
                    mMon.setBackgroundResource(R.drawable.week_selector);
                    mMon.setTextColor(getResources().getColor(R.color.white));
                    choose[0] = false;
                }

            } else if (view.getId() == R.id.tuesday) {
                if (!choose[1]) {
                    mTue.setBackgroundResource(R.drawable.week_down_shape);
                    mTue.setTextColor(getResources().getColor(R.color.bg_color));
                    choose[1] = true;
                } else {
                    mTue.setBackgroundResource(R.drawable.week_selector);
                    mTue.setTextColor(getResources().getColor(R.color.white));
                    choose[1] = false;
                }
            } else if (view.getId() == R.id.wednesday) {
                if (!choose[2]) {
                    mWed.setBackgroundResource(R.drawable.week_down_shape);
                    mWed.setTextColor(getResources().getColor(R.color.bg_color));
                    choose[2] = true;
                } else {
                    mWed.setBackgroundResource(R.drawable.week_selector);
                    mWed.setTextColor(getResources().getColor(R.color.white));
                    choose[2] = false;
                }
            } else if (view.getId() == R.id.thursday) {
                if (!choose[3]) {
                    mThu.setBackgroundResource(R.drawable.week_down_shape);
                    mThu.setTextColor(getResources().getColor(R.color.bg_color));
                    choose[3] = true;
                } else {
                    mThu.setBackgroundResource(R.drawable.week_selector);
                    mThu.setTextColor(getResources().getColor(R.color.white));
                    choose[3] = false;
                }
            } else if (view.getId() == R.id.friday) {
                if (!choose[4]) {
                    mFri.setBackgroundResource(R.drawable.week_down_shape);
                    mFri.setTextColor(getResources().getColor(R.color.bg_color));
                    choose[4] = true;
                } else {
                    mFri.setBackgroundResource(R.drawable.week_selector);
                    mFri.setTextColor(getResources().getColor(R.color.white));
                    choose[4] = false;
                }
            } else if (view.getId() == R.id.saturday) {
                if (!choose[5]) {
                    mSat.setBackgroundResource(R.drawable.week_down_shape);
                    mSat.setTextColor(getResources().getColor(R.color.bg_color));
                    choose[5] = true;
                } else {
                    mSat.setBackgroundResource(R.drawable.week_selector);
                    mSat.setTextColor(getResources().getColor(R.color.white));
                    choose[5] = false;
                }
            } else if (view.getId() == R.id.sunday) {
                if (!choose[6]) {
                    mSun.setBackgroundResource(R.drawable.week_down_shape);
                    mSun.setTextColor(getResources().getColor(R.color.bg_color));
                    choose[6] = true;
                } else {
                    mSun.setBackgroundResource(R.drawable.week_selector);
                    mSun.setTextColor(getResources().getColor(R.color.white));
                    choose[6] = false;
                }
            }
            mStartHour.setBackgroundColor(mDefault);
            mStartMinute.setBackgroundColor(mDefault);
            mEndHour.setBackgroundColor(mDefault);
            mEndMinute.setBackgroundColor(mDefault);
        }
    }
}
