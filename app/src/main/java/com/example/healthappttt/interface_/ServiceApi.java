package com.example.healthappttt.interface_;

import com.example.healthappttt.Data.ExerciseResponse;
import com.example.healthappttt.Data.RoutineData;
import com.example.healthappttt.Data.RoutineExerciseData;
import com.example.healthappttt.Data.RoutineResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceApi {
    @POST("/CreateRoutine")
    Call<RoutineResponse> createRoutine(@Body RoutineData data);

    @POST("/CreateExercise")
    Call<ExerciseResponse> createExercise(@Body RoutineExerciseData data);
}
