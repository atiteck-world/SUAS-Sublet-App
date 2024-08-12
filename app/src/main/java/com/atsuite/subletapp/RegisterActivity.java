package com.atsuite.subletapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtFullname;
    private EditText edtEmail;
    private EditText edtDOB;
    private EditText edtPhone;
    private EditText edtAddress;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private ProgressBar progressBar;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonGenderSelected;
    private TextView textViewLogin;
    //private Toolbar toolbar;
    private DatePickerDialog picker;

    private  static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*// Set title of toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sublet App");*/

        Toast.makeText(RegisterActivity.this, "Welcome, enter your details to get registered.", Toast.LENGTH_LONG).show();

        edtFullname = findViewById(R.id.editTextName);
        edtEmail = findViewById(R.id.editTextEmail);
        edtDOB = findViewById(R.id.editTextDoB);
        edtPhone = findViewById(R.id.editTextPhone);
        edtAddress = findViewById(R.id.editTextAddress);
        edtPassword = findViewById(R.id.editTextPassword);
        edtConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        // radioButton for gender
         radioGroupGender = findViewById(R.id.radioGroupGender);
         radioGroupGender.clearCheck();

         // Setting up datepicker on dob editText
        edtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // Date picker dialog
                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edtDOB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        progressBar = findViewById(R.id.progressBar);

        textViewLogin = findViewById(R.id.textViewLogin);

        Button btnRegister = findViewById(R.id.buttonRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                radioButtonGenderSelected = findViewById(selectedGenderId);
                
                // Obtain entered data
                String fullName = edtFullname.getText().toString();
                String email = edtEmail.getText().toString();
                String DOB = edtDOB.getText().toString();
                String phone = edtPhone.getText().toString();
                String address = edtAddress.getText().toString();
                String password = edtPassword.getText().toString();
                String C_password = edtConfirmPassword.getText().toString();
                String gender; // Can't obtain the value before verifying if any button was selected or not

                // Validate mobile number
                String mobileRegex = "[4][9][0-9]";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(phone);

                if (TextUtils.isEmpty(fullName)){
                    Toast.makeText(RegisterActivity.this, "Please enter your full name.", Toast.LENGTH_LONG).show();
                    edtFullname.setError("Full name is required");
                    edtFullname.requestFocus();
                }else if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "Please enter your email.", Toast.LENGTH_LONG).show();
                    edtEmail.setError("Email is required");
                    edtEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your email.", Toast.LENGTH_LONG).show();
                    edtEmail.setError("Valid email is required");
                    edtEmail.requestFocus();
                }else if (TextUtils.isEmpty(DOB)){
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth.", Toast.LENGTH_LONG).show();
                    edtDOB.setError("Date of birth is required");
                    edtDOB.requestFocus();
                }else if(radioGroupGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(RegisterActivity.this, "Please select your gender.", Toast.LENGTH_LONG).show();
                    radioButtonGenderSelected.setError("Gender is required");
                    radioButtonGenderSelected.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile number.", Toast.LENGTH_LONG).show();
                    edtPhone.setError("Mobile number is not valid");
                    edtPhone.requestFocus();
                } else if(TextUtils.isEmpty(phone)){
                    Toast.makeText(RegisterActivity.this, "Please enter your mobile number.", Toast.LENGTH_LONG).show();
                    edtPhone.setError("Mobile number is required");
                    edtPhone.requestFocus();
                } else if(TextUtils.isEmpty(address)){
                    Toast.makeText(RegisterActivity.this, "Please enter your address.", Toast.LENGTH_LONG).show();
                    edtPhone.setError("Address is required");
                    edtPhone.requestFocus();
                }else if ((phone.length() < 10) || (phone.length() > 13)) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile number.", Toast.LENGTH_LONG).show();
                    edtPhone.setError("Mobile number should not be less than 10 or greater than 13");
                    edtPhone.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your password.", Toast.LENGTH_LONG).show();
                    edtPassword.setError("Password is required");
                    edtPassword.requestFocus();
                } else if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 characters.", Toast.LENGTH_LONG).show();
                    edtPassword.setError("Password is too weak");
                    edtPassword.requestFocus();
                } else if (TextUtils.isEmpty(C_password)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password.", Toast.LENGTH_LONG).show();
                    edtConfirmPassword.setError("Password confirmation is required");
                    edtConfirmPassword.requestFocus();
                } else if (!password.equals(C_password)) {
                    Toast.makeText(RegisterActivity.this, "Please passwords do not match.", Toast.LENGTH_LONG).show();
                    edtConfirmPassword.setError("Passwords mismatches");
                    edtConfirmPassword.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    gender = radioButtonGenderSelected.getText().toString();
                    
                    registerUser(fullName, email, DOB, gender, phone, address, password);
                }

            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // Register User using the details given
    private void registerUser(String fullName, String email, String dob, String gender, String phone, String address, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);

                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    // Store user data into firebase realtime database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(fullName, email, dob, gender, phone, address);

                    // Extracting user reference from database for "Registered Users"
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
                    databaseReference.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                // Send email verification
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(RegisterActivity.this, "User Registration successful. An email has been sent, Please verify your email.", Toast.LENGTH_LONG).show();

                                // Open user profile after successful registration
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); // close register activity

                            }else {
                                Toast.makeText(RegisterActivity.this, "User Registration failed.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    // If registration fails for whatever reason
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        edtPassword.setError("Your password is too weak.");
                        edtPassword.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        edtEmail.setError("Your email is invalid or already in use.");
                        edtEmail.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        edtEmail.setError("User is already registered with this email.");
                        edtEmail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
   /* private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.GONE);
                            // Update UI with the user's information
                        } else {
                            // If registration fails, display a message to the user
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                // Weak password
                                Toast.makeText(RegisterActivity.this, "Password is too weak. Try a stronger one.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Invalid email
                                Toast.makeText(RegisterActivity.this, "Email is invalid. Enter a valid email.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                // Email already exists
                                Toast.makeText(RegisterActivity.this, "Email already exist. Try a different email.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                // Other errors
                                Toast.makeText(RegisterActivity.this, "An unknown error occurred. Try again.", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }*/
}