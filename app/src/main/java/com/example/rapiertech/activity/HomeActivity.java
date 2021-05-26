package com.example.rapiertech.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rapiertech.R;
import com.example.rapiertech.ui.admin.DepartmentFragment;
import com.example.rapiertech.ui.admin.EmployeeFragment;
import com.example.rapiertech.ui.admin.HomeFragment;
import com.example.rapiertech.ui.admin.JobFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import www.sanju.motiontoast.MotionToast;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;

    SessionManager sessionManager;

    TextView name, email;
    ImageView image;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(HomeActivity.this);
        if (!sessionManager.isLoggedIn()){
            moveToLogin();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);
        name = headerView.findViewById(R.id.nameNavHeader);
        email = headerView.findViewById(R.id.emailNavHeader);
        image = headerView.findViewById(R.id.imageNavHeader);

        name.setText(sessionManager.getUserDetail().get(SessionManager.NAME));
        email.setText(sessionManager.getUserDetail().get(SessionManager.EMAIL));

        //initial Fragment
//        HomeFragment homeFragment = new HomeFragment();
        EmployeeFragment departmentFragment = new EmployeeFragment();
        setMyFragment(departmentFragment);
    }

    private void setMyFragment(Fragment fragment) {
        //get current fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        //get fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //set new fragment in fragment_container (FrameLayout)
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void moveToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void logout() {
        sessionManager.logoutSession();
        moveToLogin();
        MotionToast.Companion.createColorToast(HomeActivity.this, "Success",
                "Logout Success",
                MotionToast.TOAST_SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(HomeActivity.this,R.font.helvetica_regular)
        );
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                HomeFragment homeFragment = new HomeFragment();
                setMyFragment(homeFragment);
                break;

            case R.id.nav_profile:

                break;

            case R.id.nav_employee:
                EmployeeFragment employeeFragment = new EmployeeFragment();
                setMyFragment((employeeFragment));
                break;

            case R.id.nav_department:
                DepartmentFragment departmentFragment = new DepartmentFragment();
                setMyFragment(departmentFragment);
                break;

            case R.id.nav_job:
                JobFragment jobFragment = new JobFragment();
                setMyFragment(jobFragment);
                break;

            case R.id.nav_logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}