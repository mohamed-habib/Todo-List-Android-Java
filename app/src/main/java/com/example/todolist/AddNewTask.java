package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.todolist.data.Task;
import com.example.todolist.data.TodoListRepository;
import com.example.todolist.notification.AlarmScheduler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class AddNewTask extends AppCompatActivity {

    String selectedDate = "";
    String selectedTime = "";
    EditText taskNameEditText;
    EditText taskDescEditText;
    TextView dateTextView, timeTextView;
    TodoListRepository todoListRepository;
    LocationRequest locationRequest;
    ArrayList<String> locationPermissions = new ArrayList<>();
    ArrayList<String> permissionToRequest;
    public static final int PERMISSION_REQUEST_CODE = 1001;
    public static final int CAMER_REQUEST_CODE = 1002;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView currentLocationTextView;
    String userLocation;
    int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    String password;

    EditText sensitiveDataEditText, encryptedDataEditText;
    byte[] key;
    MasterKey masterKey = null;

    public native String getPasswordFromJni();

    public native String getRandomKey();

    static {
        System.loadLibrary("keys");
    }

    //upwork, freelancer.com, 5amsat,
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        taskNameEditText = (EditText) findViewById(R.id.task_name_edit_text);
        taskDescEditText = (EditText) findViewById(R.id.task_description_edit_text);
        dateTextView = findViewById(R.id.date_text_view);
        timeTextView = findViewById(R.id.time_text_view);

        sensitiveDataEditText = findViewById(R.id.sensitive_data_edit_text);
        encryptedDataEditText = findViewById(R.id.encrypted_data_edit_text);


        password = getPasswordFromJni();

        Toast.makeText(this, "Password is: " + password, Toast.LENGTH_SHORT).show();

        String randomKey = getRandomKey();

        key = EncryptionUtils.generateKey(randomKey.getBytes());

        try {
            masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentLocationTextView = findViewById(R.id.current_location_text_view);


        todoListRepository = new TodoListRepository(this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        locationPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        locationPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }

    public void onAddDateClicked(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                alarmDay = dayOfMonth;
                alarmYear = year;
                alarmMonth = month;
                selectedDate = year + "/" + month + "/" + dayOfMonth;
                dateTextView.setText(selectedDate);
                showToast("You selected: " + selectedDate);
            }
        }, 2020, 9, 22);
        datePickerDialog.show();
    }

    public void onAddTimeClicked(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                alarmHour = hourOfDay;
                alarmMinute = minute;

                selectedTime = hourOfDay + ":" + minute;
                timeTextView.setText(selectedTime);
                showToast("You selected: " + selectedTime);
            }
        }, 12, 0, true);
        timePickerDialog.show();
    }

    public void onSaveClicked(View view) {
        String taskName = taskNameEditText.getText().toString();
        String taskDescription = taskDescEditText.getText().toString();
        String locationName = userLocation;

        if (dataIsValid(taskName)) {
            Task task = new Task(taskName, taskDescription, selectedDate, selectedTime, locationName);

            TodoListRepository.TaskCreatedCallback callBack = new TodoListRepository.TaskCreatedCallback() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onTaskCreated(long taskId) {
                    //create alarm manager for the task.
                    AlarmScheduler.createAlarmForTask(AddNewTask.this, taskId, alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute);
                }
            };

            todoListRepository.addNewTask(task, callBack);


            showToast("task created successfully");

            //close the activity.
            finish();// finishes the current activity only.


        } else {
            showErrorMessage("Please complete the data before saving");
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showErrorMessage(String errorMessage) {
        Snackbar.make(findViewById(R.id.save_button), errorMessage, Snackbar.LENGTH_LONG).show();
    }

    boolean dataIsValid(String name) {
        boolean isValid = true;

        if (name.isEmpty()) {
            taskNameEditText.setError("Name is required");
            isValid = false;
        }

        return isValid;
    }

    public void onGetCurrentLocationClick(View view) {

        permissionToRequest = permissionsToRequest(locationPermissions);

        if (permissionToRequest.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionToRequest.toArray(new String[permissionToRequest.size()]), PERMISSION_REQUEST_CODE);
            }
        }

        requestUserLocation();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (final String permission : permissionToRequest) {
                if (!hasPermission(permission)) {
                    //permission reject
                    permissionsRejected.add(permission);
                }
                if (permissionsRejected.size() > 0) {
                    //Permission Rejected.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            //ask the user again to allow permission and explain why we need this permission.

                            new AlertDialog.Builder(AddNewTask.this)
                                    .setMessage("The location permission is mandatory to be able to use the application, please allow or the application will not work.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), PERMISSION_REQUEST_CODE);
                                        }
                                    }).setNegativeButton("Cancel", null)
                                    .create().show();
                            return;
                        }
                    }
                } else {
                    //Permission Allowed.
                    //request user location.

                    requestUserLocation();

                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestUserLocation() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location lastLocation = locationResult.getLastLocation();
                userLocation = lastLocation.getLatitude() + " - " + lastLocation.getLongitude();

                currentLocationTextView.setText(userLocation);
                //set location to edit text
                //set location at the task object,
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String wantedPerm : wantedPermissions) {
            if (!hasPermission(wantedPerm)) {
                //ask for permission
                permissionsToRequest.add(wantedPerm);
            }
        }
        return permissionsToRequest;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onDecryptClick(View view) {

        String fileName = "my_secure_file.txt";

        try {
            EncryptedFile encryptedFile = new EncryptedFile.Builder(this, new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName),
                    masterKey, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB).build();


            InputStream inputStream = encryptedFile.openFileInput();
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }

            sensitiveDataEditText.setText(line);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onEncryptClicked(View view) {

        String sensitiveData = sensitiveDataEditText.getText().toString();

        try {
            String fileName = "my_secure_file.txt";

            EncryptedFile encryptedFile = new EncryptedFile.Builder(this, new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName),
                    masterKey, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB).build();

            OutputStream outputStream = encryptedFile.openFileOutput();
            outputStream.write(sensitiveData.getBytes());
            outputStream.close();
            outputStream.flush();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        encryptedDataEditText.setText(EncryptionUtils.encrypt(key, sensitiveData.getBytes()));
    }


    public void saveEncryptedSharedPreferencesData() {
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(this, "encrypted_shared_preferences",
                    masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            sharedPreferences.edit().putString("Password", password).apply();

            sharedPreferences.edit().putBoolean("keepLogin", true).apply();


        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}