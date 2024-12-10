package com.example.mygamedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mygamedemo.databinding.ActivityUpdateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateActivity extends AppCompatActivity {
    ActivityUpdateBinding binding;
    String newType;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        String id=getIntent().getStringExtra("id");
        String amount=getIntent().getStringExtra("amount");
        String note=getIntent().getStringExtra("note");
        String type=getIntent().getStringExtra("type");

        binding.userAmountAdd.setText(amount);
        binding.userNodeAdd.setText(note);

        switch (type){
            case "Income":
                newType="Income";
            binding.incomeCheckBox.setChecked(true);
            break;

            case "Expense":
                newType="Expense";
                binding.expenseCheckBox.setChecked(true);
                break;
        }
        binding.incomeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newType="Income";
                binding.incomeCheckBox.setChecked(true);
                binding.expenseCheckBox.setChecked(false);
            }
        });
        binding.expenseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newType="Expense";
                binding.incomeCheckBox.setChecked(false);
                binding.expenseCheckBox.setChecked(true);
            }
        });

        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(UpdateActivity.this,DashboardActivity.class));
                }catch (Exception e){

                }
            }
        });


        binding.btnUpdateTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = binding.userAmountAdd.getText().toString();
                String note = binding.userNodeAdd.getText().toString();

                // Sử dụng newType thay vì type
                firebaseFirestore.collection("Expenses")
                        .document(firebaseAuth.getUid())
                        .collection("Note")
                        .document(id)
                        .update("amount", amount, "note", note, "type", newType) // Thay đổi "type" thành "newType"
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onBackPressed();
                                Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.btnDeleteTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Expenses").document(firebaseAuth.getUid()).collection("Note")
                        .document(id).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onBackPressed();
                                Toast.makeText(UpdateActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}