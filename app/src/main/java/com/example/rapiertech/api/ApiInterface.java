package com.example.rapiertech.api;

import com.example.rapiertech.model.basicpays.BasicPays;
import com.example.rapiertech.model.department.Department;
import com.example.rapiertech.model.empdetail.EmpDetail;
import com.example.rapiertech.model.employee.Employee;
import com.example.rapiertech.model.empstatus.EmpStatus;
import com.example.rapiertech.model.event.Event;
import com.example.rapiertech.model.goal.Goal;
import com.example.rapiertech.model.home.Home;
import com.example.rapiertech.model.job.Job;
import com.example.rapiertech.model.leave.Leave;
import com.example.rapiertech.model.leave.LeaveType;
import com.example.rapiertech.model.login.Login;
import com.example.rapiertech.model.payslip.Payslip;
import com.example.rapiertech.model.project.Project;
import com.example.rapiertech.model.project.ProjectActivity;
import com.example.rapiertech.model.project.ProjectCategory;
import com.example.rapiertech.model.project.ProjectMember;
import com.example.rapiertech.model.role.Role;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login")
    Call<Login> loginResponse(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("home")
    Call<Home> homeRetrieveData();

    // Employee
    @GET("user")
    Call<Employee> employeeRetrieveData();

    @FormUrlEncoded
    @POST("user/updatepassword/{id}")
    Call<Employee> employeeChangePassword(
            @Path("id") int id,
            @Field("current_password") String currentPassword,
            @Field("password") String password,
            @Field("password_confirmation") String passwordConfirmation
    );

    @GET("emp_status")
    Call<EmpStatus> statusRetrieveData();

    @GET("emp_detail/{id}")
    Call<EmpDetail> employeeDetailData(@Path("id") int id);

    @FormUrlEncoded
    @POST("user/add/store")
    Call<EmpDetail> employeeCreateData(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("role") int role,
            @Field("address") String address,
            @Field("dept") int dept,
            @Field("job") int job,
            @Field("phone") String phone,
            @Field("gender") String gender,
            @Field("join_date") String join_date,
            @Field("last_date") String last_date,
            @Field("status") int status
    );

    @FormUrlEncoded
    @POST("user/edit/{id}")
    Call<EmpDetail> employeeUpdateData(
            @Path("id") int empId,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("role") int role,
            @Field("address") String address,
            @Field("dept") int dept,
            @Field("job") int job,
            @Field("phone") String phone,
            @Field("gender") String gender,
            @Field("join_date") String join_date,
            @Field("last_date") String last_date,
            @Field("status") int status
    );

    @DELETE("user/delete/{id}")
    Call<Employee> employeeDeleteData(@Path("id") int empId);

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

    // Leave
    @GET("leave/type")
    Call<LeaveType> leaveTypeRetrieveData();

    @GET("leave")
    Call<Leave> leaveRetrieveData();

    @GET("leave/user/{id}")
    Call<Leave> leaveRetrieveDataUser(@Path("id") int id);

    @GET("leave/pending")
    Call<Leave> leavePendingRetrieveData();

    @GET("leave/pending/{id}")
    Call<Leave> leavePendingRetrieveDataUser(@Path("id") int id);

    @FormUrlEncoded
    @POST("leave/add/store")
    Call<Leave> leaveCreateData(
            @Field("employee") int empId,
            @Field("type") int typeId,
            @Field("duration") String duration,
            @Field("fromdate") String fromDate,
            @Field("todate") String toDate,
            @Field("status") String status,
            @Field("reason") String reason
    );

    @DELETE("leave/delete/{id}")
    Call<Leave> leaveDeleteData(@Path("id") int id);

    @FormUrlEncoded
    @POST("leave/edit/{id}")
    Call<Leave> leaveUpdateData(
            @Path("id") int id,
            @Field("employee") int empId,
            @Field("type") int typeId,
            @Field("duration") String duration,
            @Field("fromdate") String fromDate,
            @Field("todate") String toDate,
            @Field("status") String status,
            @Field("reason") String reason
    );

    @GET("leave/approve/{id}")
    Call<Leave> leaveApproved(@Path("id") int id);

    @FormUrlEncoded
    @POST("leave/reject/{id}")
    Call<Leave> leaveRejected(@Path("id") int id, @Field("reason") String reason);

    // Payslip
    @GET("payslip")
    Call<Payslip> payslipRetrieveData();

    @GET("payslip/user/{id}")
    Call<Payslip> payslipRetrieveDataUser(@Path("id") int id);

    @DELETE("payslip/delete/{id}")
    Call<Payslip> payslipDeleteData(@Path("id") int id);

    @FormUrlEncoded
    @POST("payslip/autogenerate")
    Call<Payslip> payslipAutoGenerate(
            @Field("from_date") String fromDate,
            @Field("to_date") String toDate,
            @Field("payment") String payment
    );

    @FormUrlEncoded
    @POST("payslip/update/{id}")
    Call<Payslip> payslipUpdateData(
            @Path("id") int id,
            @Field("from_date") String fromDate,
            @Field("to_date") String toDate,
            @Field("allowance") int allowance,
            @Field("deduction") int deduction,
            @Field("overtime") int overtime,
            @Field("other") int other,
            @Field("payment") String payment,
            @Field("status") String status
    );

    @GET("payslip/pdf/{id}")
    Call<Payslip> payslipCreatePDF(@Path("id") int id);

    @GET("payslip/basic")
    Call<BasicPays> basicPaysRetrieveData();

    @DELETE("payslip/basic/{id}")
    Call<BasicPays> basicPaysDeleteData(@Path("id") int id);

    @FormUrlEncoded
    @POST("payslip/basic/add")
    Call<BasicPays> basicPaysCreateData(
            @Field("job") int jobId,
            @Field("salary") int salary
    );

    @GET("event/{month}")
    Call<Event> eventList(@Path("month") int month);

    @FormUrlEncoded
    @POST("event/add")
    Call<Event> eventCreateData(
            @Field("start") String start,
            @Field("end") String end,
            @Field("title") String title
    );

    @DELETE("event/delete/{id}")
    Call<Event> eventDeleteData(@Path("id") int id);

    // Project
    @GET("project")
    Call<Project> projectRetrieveData();

    @GET("project/user/{id}")
    Call<Project> projectUserRetrieveData(@Path("id") int id);

    @GET("project/ongoing")
    Call<Project> projectOngoingRetrieveData();

    @GET("project/ongoing/{id}")
    Call<Project> projectOngoingUserRetrieveData(@Path("id") int id);

    @GET("project/category")
    Call<ProjectCategory> projectCategoryRetrieveData();

    @GET("project/member/{id}")
    Call<ProjectMember> projectMemberRetrieveData(@Path("id") int id);

    @FormUrlEncoded
    @POST("project/category/add")
    Call<ProjectCategory> projectCategoryCreateData(@Field("category") String categoryName);

    @DELETE("project/category/delete/{id}")
    Call<ProjectCategory> projectCategoryDeleteData(@Path("id") int id);

    @FormUrlEncoded
    @POST("project/add/{userId}")
    Call<Project> projectCreateData(
            @Path("userId") int userId,
            @Field("project_name") String name,
            @Field("category") int catId,
            @Field("start_date") String startData,
            @Field("deadline") String deadline,
            @Field("member[]") List<Integer> member,
            @Field("summary") String summary,
            @Field("note") String note
    );

    @DELETE("project/delete/{id}")
    Call<Project> projectDeleteData(@Path("id") int id);

    @FormUrlEncoded
    @POST("project/update/{id}")
    Call<Project> projectUpdateData(
            @Path("id") int id,
            @Field("project_name") String name,
            @Field("category") int catId,
            @Field("start_date") String startData,
            @Field("deadline") String deadline,
            @Field("member[]") List<Integer> member,
            @Field("summary") String summary,
            @Field("note") String note,
            @Field("status") String status
    );

    @GET("project/activity/{id}")
    Call<ProjectActivity> projectActivityRetrieveData(@Path("id") int id);

    @DELETE("project/activity/delete/{id}")
    Call<ProjectActivity> projectActivityDeleteData(@Path("id") int id);

    @Multipart
    @POST("project/activity/submit")
    Call<ProjectActivity> projectActivitySubmitData(
            @Part("user_id") RequestBody id,
            @Part("project_id") RequestBody projectId,
            @Part("comment") RequestBody comment,
            @Part MultipartBody.Part file
    );

    @GET("goal")
    Call<Goal> goalRetrieveData();

    @GET("goal/user/{id}")
    Call<Goal> goalRetrieveDataUser(@Path("id") int id);
}
