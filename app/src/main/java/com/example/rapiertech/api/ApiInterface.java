package com.example.rapiertech.api;

import com.example.rapiertech.model.department.Department;
import com.example.rapiertech.model.login.Login;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login")
    Call<Login> loginResponse(
            @Field("email") String email,
            @Field("password") String password
    );

    // Department
    @GET("department")
    Call<Department> departmentRetrieveData();

    @FormUrlEncoded
    @POST("department/add")
    Call<Department> departmentCreateData(
            @Field("name") String name
    );

    @FormUrlEncoded
    @POST("department/edit/{id}")
    Call<Department> departmentUpdateData(
            @Path("id") int deptId,
            @Field("name") String name
    );

    @DELETE("department/delete/{id}")
    Call<Department> departmentDeleteData(@Path("id") int deptId);

}
