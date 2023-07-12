package com.example.healthappttt.Routine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.healthappttt.Data.Exercise.ExerciseData;
import com.example.healthappttt.Data.Exercise.RoutineData;
import com.example.healthappttt.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CRInputDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CRInputDetailFragment extends Fragment {
    private TextView ResetBtn, ScheduleTxt;
    private CardView NextBtn;
    private TextView NextTxt;

    private RecyclerView recyclerView;
    private ExerciseInputAdapter adapter;

    private ArrayList<ExerciseData> exercises;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onRoutineExDetail(ArrayList<ExerciseData> exercises);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public CRInputDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExerciseDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CRInputDetailFragment newInstance(String param1, String param2) {
        CRInputDetailFragment fragment = new CRInputDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_cr_input_detail, container, false);

        ResetBtn = view.findViewById(R.id.reset);
        ScheduleTxt = view.findViewById(R.id.schedule);
        recyclerView = view.findViewById(R.id.recyclerView);

        NextBtn = view.findViewById(R.id.nextBtn);
        NextTxt = view.findViewById(R.id.nextTxt);

        exercises = new ArrayList<>();

        if (getArguments() != null) {
            RoutineData routine = (RoutineData) getArguments().getSerializable("routine");
            exercises = routine.getExercises();
            setRoutineTime(routine.getDayOfWeek(), routine.getStartTime(), routine.getEndTime());
        }

        if (exercises != null)
            setRecyclerView();

        NextTxt.setBackgroundColor(Color.parseColor("#05c78c"));
        NextTxt.setTextColor(Color.parseColor("#ffffff"));

        NextBtn.setOnClickListener(v -> {
            mListener.onRoutineExDetail(exercises);
        });

        return view;
    }

    private void setRoutineTime(int dayOfWeek, String startTime, String endTime) {
        String DayOfWeek = "";

        switch (dayOfWeek) {
            case 0: DayOfWeek = "일요일"; break;
            case 1: DayOfWeek = "월요일"; break;
            case 2: DayOfWeek = "화요일"; break;
            case 3: DayOfWeek = "수요일"; break;
            case 4: DayOfWeek = "목요일"; break;
            case 5: DayOfWeek = "금요일"; break;
            case 6: DayOfWeek = "토요일"; break;
        }

        String StartTime = TimeParse(startTime);
        String EndTime = TimeParse(endTime);
        String result = DayOfWeek + " · " + StartTime + " - " + EndTime;

        ScheduleTxt.setText(result);
    }

    private String TimeParse(String time) {
        String hour = time.substring(0, time.indexOf(":"));
        String minSec = time.substring(time.indexOf(":")+1);
        String min = minSec.substring(0, minSec.indexOf(":"));
        String am_pm = "오전";

        int tempHour = Integer.parseInt(hour);

        if (tempHour > 12) {
            am_pm = "오후";
            tempHour -= 12;
        }

        @SuppressLint("DefaultLocale") String result = String.format("%02d:%s", tempHour, min);

        return am_pm + " " + result;
    }

    private void setRecyclerView() {
        adapter = new ExerciseInputAdapter(exercises);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}