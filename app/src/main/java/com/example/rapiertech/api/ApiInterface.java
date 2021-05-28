package com.example.rapiertech.api;

import com.example.rapiertech.model.department.Department;
import com.example.rapiertech.model.empdetail.EmpDetail;
import com.example.rapiertech.model.employee.Employee;
import com.example.rapiertech.model.empstatus.EmpStatus;
import com.example.rapiertech.model.job.Job;
import com.example.rapiertech.model.login.Login;
import com.example.rapiertech.model.role.Role;

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

    // Employee
    @GET("user")
    Call<Employee> employeeRetrieveData();

    @GET("emp_status")
    Call<EmpStatus> statusRetrieveData();

    @FormUrlEncoded
    @POST("user/add")
    Call<Employee> employeeCreateData(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("role") String role,
            @Field("address") String address,
            @Field("dept") String dept,
            @Field("job") String job,
            @Field("phone") String phone,
            @Field("gender") String gender,
            @Field("join_date") String join_date,
            @Field("last_date") String last_date,
            @Field("status") String status
    );

    @GET("emp_detail/{id}")
    Call<EmpDetail> employeeDetailData(@Path("id") int id);

    // Role
    @GET("role")
    Call<Role> roleRetrieveData();

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

    // Job
    @GET("job")
    Call<Job> jobRetrieveData();

    @FormUrlEncoded
    @POST("job/add")
    Call<Job> jobCreateData(
            @Field("name") String name
    );

    @FormUrlEncoded
    @POST("job/edit/{id}")
    Call<Job> jobUpdateData(
            @Path("id") int deptId,
            @Field("name") String name
    );

    @DELETE("job/delete/{id}")
    Call<Job> jobDeleteData(@Path("id") int id);
}
