package com.example.mygamedemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mygamedemo.databinding.ActivityDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    ActivityDashboardBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    long sumExpense = 0;
    long sumIncome = 0;

    ArrayList<TransactionModel> transactionModelsArrayList;
    TransactionAdapter transactionAdapter;

    private LinearLayout menuHome, menuExpense, menuIncome, menuChart, menuSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase setup
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        transactionModelsArrayList = new ArrayList<>();
        binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.historyRecyclerView.setHasFixedSize(true);

        // Floating button actions
        binding.addFloatingBtn.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, AddTransactionActivity.class));
        });
        binding.refreshBtn.setOnClickListener(view -> {
            loadData(); // Tải lại dữ liệu thay vì tạo lại Activity
        });

        // Menu bar setup
        menuHome = findViewById(R.id.menu_home);
        menuExpense = findViewById(R.id.menu_expense);
        menuIncome = findViewById(R.id.menu_income);
        menuChart = findViewById(R.id.menu_chart);
        menuSetting = findViewById(R.id.menu_setting);

        // Menu click listeners
        menuHome.setOnClickListener(v -> selectMenu(menuHome, DashboardActivity.class));
        menuExpense.setOnClickListener(v -> selectMenu(menuExpense, ExpenseActivity.class));
        menuIncome.setOnClickListener(v -> selectMenu(menuIncome, IncomeActivity.class));
        menuChart.setOnClickListener(v -> selectMenu(menuChart, ChartActivity.class));
        menuSetting.setOnClickListener(v -> selectMenu(menuSetting, SettingActivity.class));

        // Default menu selection
        selectMenu(menuHome, null);

        // Load data
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        firebaseFirestore.collection("Expenses").document(firebaseAuth.getUid()).collection("Note")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        transactionModelsArrayList.clear();
                        sumExpense = 0; // Reset totals
                        sumIncome = 0;

                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot ds : task.getResult()) {
                                String id = ds.getString("id");
                                String note = ds.getString("note");
                                String amountStr = ds.getString("amount");
                                String type = ds.getString("type");
                                String date = ds.getString("date");

                                try {
                                    long amount = Long.parseLong(amountStr != null ? amountStr : "0");
                                    if ("Expense".equals(type)) {
                                        sumExpense += amount;
                                    } else if ("Income".equals(type)) {
                                        sumIncome += amount;
                                    }
                                    transactionModelsArrayList.add(new TransactionModel(id, note, amountStr, type, date));
                                } catch (NumberFormatException e) {
                                    Log.e("DashboardActivity", "Invalid amount: " + amountStr, e);
                                }
                            }

                            binding.totalIncome.setText(String.valueOf(sumIncome));
                            binding.totalExpense.setText(String.valueOf(sumExpense));
                            binding.totalBalance.setText(String.valueOf(sumIncome - sumExpense));

                            transactionAdapter = new TransactionAdapter(DashboardActivity.this, transactionModelsArrayList);
                            binding.historyRecyclerView.setAdapter(transactionAdapter);
                        } else {
                            Log.e("DashboardActivity", "Error loading data: " + task.getException());
                        }
                    }
                });
    }

    private void selectMenu(LinearLayout selectedMenu, Class<?> activityClass) {
        // Reset all menu items
        resetMenuStyles();

        // Highlight selected menu
        ImageView icon = (ImageView) selectedMenu.getChildAt(0);
        TextView text = (TextView) selectedMenu.getChildAt(1);
        icon.setColorFilter(Color.BLACK); // Use color resource
        text.setTextColor(Color.BLACK);

        // Navigate to the selected activity if applicable
        if (activityClass != null && activityClass != this.getClass()) {
            startActivity(new Intent(this, activityClass));
        }
    }

    private void resetMenuStyles() {
        resetMenu(menuHome);
        resetMenu(menuExpense);
        resetMenu(menuIncome);
        resetMenu(menuChart);
        resetMenu(menuSetting);
    }

    private void resetMenu(LinearLayout menu) {
        ImageView icon = (ImageView) menu.getChildAt(0);
        TextView text = (TextView) menu.getChildAt(1);
        icon.setColorFilter(Color.BLACK); // Use color resource
        text.setTextColor(Color.BLACK);
    }
}
