package graduation.plantcare.ui.home;

import static graduation.plantcare.ui.splash.SplashScreen.isInternetAvailable;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import graduation.plantcare.R;
import graduation.plantcare.adapters.ScanResultAdapter;
import graduation.plantcare.base.BaseActivity;
import graduation.plantcare.features.model_one.ModelOne;
import graduation.plantcare.features.model_three.ModelThree;
import graduation.plantcare.features.model_two.ModelTwo;
import graduation.plantcare.ui.auth.Login;
import graduation.plantcare.ui.profile.EditProfile;
import graduation.plantcare.utils.FirebaseAuthHelper;
import graduation.plantcare.utils.NoInternetConnection;
import graduation.plantcare.utils.UserSessionHelper;

public class HomePage extends BaseActivity {
    TextView homeGreetingMessage, emptyText;
    String uid;
    ImageView profileImageView;
    RecyclerView recyclerView;
    FrameLayout homeFrame1, homeFrame2, homeFrame3;
    Dialog dialog;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted!", Toast.LENGTH_SHORT).show();
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchScanResults();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_page);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        UserSessionHelper userSessionHelper = UserSessionHelper.getInstance(this);
        uid = userSessionHelper.getUser().getUID();

        setEmailExitsListener();

        homeGreetingMessage = findViewById(R.id.homeGreetingMessage);
        homeGreetingMessage.setText("Hi, " + userSessionHelper.getUser().getFirstName() + " 🔥");

        profileImageView = findViewById(R.id.homeProfileIcon);
        recyclerView = findViewById(R.id.homeRecyclerView);
        emptyText = findViewById(R.id.tv_empty_history);
        homeFrame1 = findViewById(R.id.homeFrame1);
        homeFrame2 = findViewById(R.id.homeFrame2);
        homeFrame3 = findViewById(R.id.homeFrame3);

        homeGreetingMessage.startAnimation(createFadeInAnimation(2000));

        homeFrame1.postDelayed(() -> {
            homeFrame1.setVisibility(View.VISIBLE);
            homeFrame1.startAnimation(createSlideUpAnimation(1000));
        }, 500);

        homeFrame2.postDelayed(() -> {
            homeFrame2.setVisibility(View.VISIBLE);
            homeFrame2.startAnimation(createSlideUpAnimation(1000));
        }, 800);

        homeFrame3.postDelayed(() -> {
            homeFrame3.setVisibility(View.VISIBLE);
            homeFrame3.startAnimation(createSlideUpAnimation(1000));
        }, 1000);


        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Uri selectedImageUri = data.getData();

                        try {
                            Bitmap bitmap;
                            if (selectedImageUri != null) {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            } else {
                                Bundle extras = data.getExtras();
                                bitmap = (Bitmap) extras.get("data");
                            }
                            File file = new File(getCacheDir(), System.currentTimeMillis() + ".jpg");
                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();

                            isInternetAvailable(this, isConnected -> {
                                if (isConnected) {
                                    Intent intent = new Intent(this, ModelOne.class);
                                    intent.putExtra("imagePath", file.getAbsolutePath());
                                    startActivity(intent);
                                    dialog.dismiss();
                                } else {
                                    runOnUiThread(() -> {
                                        Intent intent = new Intent(this, NoInternetConnection.class);
                                        startActivity(intent);
                                    });
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("ImagePicker", "Result not OK or data is null");
                    }
                });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Uri photoUrl = firebaseUser.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.no_internet)
                        .into(profileImageView);

            }
        }
    }

    private void setEmailExitsListener() {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users").child(uid);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    FirebaseAuthHelper firebaseAuthHelper = FirebaseAuthHelper.getInstance();
                    firebaseAuthHelper.signOut();
                    showDeleteAccountDialog();
                    Log.d("Firebase", "User was deleted from database");
                } else {
                    Log.d("Firebase", "User exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Database error", error.toException());
            }
        });

    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.CustomAlertDialog);
        builder.setCancelable(false);
        builder.setTitle("Account Deleted");
        builder.setMessage("Your account has been deleted from our system.\n\nIf this was a mistake, please contact support or create a new account.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            Intent intent = new Intent(HomePage.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setIcon(R.drawable.warning);
        builder.show();
    }

    private Animation createSlideUpAnimation(int duration) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        anim.setDuration(duration);
        return anim;
    }

    private Animation createFadeInAnimation(int duration) {
        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        anim.setDuration(duration);
        return anim;
    }

    public void showEmptyView() {
        emptyText.setVisibility(View.VISIBLE);
    }

    public void openSettingActivity(View view) {
        startActivity(new Intent(this, Settings.class));
    }

    public void newScan(View view2) {
        dialog = new Dialog(HomePage.this);
        dialog.setContentView(R.layout.model1_picture_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView uploadBtn = dialog.findViewById(R.id.uploadButton);
        ImageView takePhotoBtn = dialog.findViewById(R.id.takePhotoButton);
        MaterialButton removeBtn = dialog.findViewById(R.id.removeButton);

        uploadBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        takePhotoBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION
                );
            }
        });

        removeBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            imagePickerLauncher.launch(cameraIntent);
        } else {
            Log.e("Camera", "No app to handle camera intent");
        }
    }

    private void fetchScanResults() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ScanResultAdapter adapter = new ScanResultAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        ));

        db.collection("first_model_scans")
                .whereEqualTo("userId", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<HistoryItem> results = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        HistoryItem result = doc.toObject(HistoryItem.class);
                        if (result != null) {
                            result.setDocumentId(doc.getId()); // تعيين الـ ID
                            results.add(result);
                        }
                    }

                    ScanResultAdapter currentAdapter = (ScanResultAdapter) recyclerView.getAdapter();
                    if (currentAdapter != null) {
                        currentAdapter.updateData(results);
                    }

                    TextView emptyText = findViewById(R.id.tv_empty_history);
                    emptyText.setVisibility(results.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching scans", e);
                    Toast.makeText(this, "Failed to load history", Toast.LENGTH_SHORT).show();
                });

    }

    public void plantPick(View view) {
        startActivity(new Intent(this, ModelTwo.class));
    }

    public void fertlizer(View view) {
        startActivity(new Intent(this, ModelThree.class));
    }

    public void editProfileMethod(View view) {
        startActivity(new Intent(this, EditProfile.class));
    }
}
