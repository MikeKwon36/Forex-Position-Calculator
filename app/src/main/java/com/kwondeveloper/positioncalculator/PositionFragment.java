package com.kwondeveloper.positioncalculator;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PositionFragment extends Fragment {

    Context mContext;

    //UI objects
    private EditText atr, entry, risk;
    private TextView quantity, stop, target, expirationDate;
    private FloatingActionButton fab;
    private Spinner position;

    //Calculation variables
    private FragmentInteractionListener mListener;
    private float mCapital,mEntry,mATR,mRisk;
    private String mExpirationDate,mStop,mTarget;
    private int mQuantity;

    //Global Constants
    public static final String LONG = "Long";
    public static final String SHORT = "Short";

    //default constructor and factory
    public PositionFragment() {}
    public static PositionFragment newInstance() {
        PositionFragment fragment = new PositionFragment();
        fragment.mCapital = 0;
        return fragment;
    }

    //interface to pull capital value from Main Activity
    public interface FragmentInteractionListener {
        float onFragmentInteraction();
    }

    //Fragment override instantiates interface to pull capital value from Main Activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof FragmentInteractionListener) {mListener = (FragmentInteractionListener) context;}
    }

    //Fragment override instantiates fragment UI and calculation behavior
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        atr = (EditText) view.findViewById(R.id.XMLatr);
        risk = (EditText) view.findViewById(R.id.XMLriskpercent);
        entry = (EditText) view.findViewById(R.id.XMLentry);
        position = (Spinner) view.findViewById(R.id.XMLposition);
        quantity = (TextView) view.findViewById(R.id.XMLquantity);
        stop = (TextView) view.findViewById(R.id.XMLstop);
        target = (TextView) view.findViewById(R.id.XMLtarget);
        expirationDate = (TextView) view.findViewById(R.id.XMLexpirationdate);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.Position));
        position.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveCapital();
                if(atr.getText()==null || risk.getText()==null || entry.getText()==null || mCapital == 0){
                    Toast.makeText(mContext, "Populate all required fields", Toast.LENGTH_SHORT).show();
                } else {
                    calculateExpirationDate();
                    expirationDate.setText(mExpirationDate);
                    setStopTarget();
                    setQuantity();
                }
            }
        });

        return view;
    }

    //Method to pull capital value from Main Activity using customer interface
    public float retrieveCapital() {
        if (mListener != null) {
            return mCapital = mListener.onFragmentInteraction();
        }
        return mCapital;
    }

    public void setStopTarget(){
        if(position.getSelectedItem().toString().equals(LONG)){
            mEntry = Float.parseFloat(entry.getText().toString());
            mATR = Float.parseFloat(atr.getText().toString());
            mStop = String.valueOf(mEntry-(mATR));
            mTarget = String.valueOf(mEntry+(mATR));
            stop.setText(mStop);
            target.setText(mTarget);
        } else {
            mEntry = Float.parseFloat(entry.getText().toString());
            mATR = Float.parseFloat(atr.getText().toString());
            mStop = String.valueOf(mEntry+(mATR));
            mTarget = String.valueOf(mEntry-(mATR));
            stop.setText(mStop);
            target.setText(mTarget);
        }
    }

    public void setQuantity(){
        mRisk = Float.parseFloat(risk.getText().toString());
        mQuantity = (int) (mCapital * (mRisk/mATR));
        quantity.setText(String.valueOf(mQuantity));
    }

    //Method to calculate position's expiration date from current time of entry
    public void calculateExpirationDate(){
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 36); // adds 36 hours
        mExpirationDate = cal.getTime().toString();
    }
}