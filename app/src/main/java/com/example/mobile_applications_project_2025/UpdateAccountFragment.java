package com.example.mobile_applications_project_2025;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

public class UpdateAccountFragment extends Fragment {
    private static final String PREF = "session";
    private static final String KEY_PICTURE_URI = "profile_picture_uri";
    private static final String KEY_CAR_MODEL = "profile_car_model";
    private static final String KEY_CAR_TYPE = "profile_car_type";
    private static final String KEY_PLATE = "profile_plate";
    private static final String KEY_SEATS = "profile_seats";
    private static final String KEY_BABY = "profile_baby_friendly";
    private static final String KEY_PET = "profile_pet_friendly";

    private Uri selectedImageUri;
    private ActivityResultLauncher<String> pickImageLauncher;

    public UpdateAccountFragment() {
        // Required empty public constructor
    }

    public static UpdateAccountFragment newInstance(String param1, String param2) {
        UpdateAccountFragment fragment = new UpdateAccountFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                View v = getView();
                if (v != null) {
                    ImageView iv = v.findViewById(R.id.ivProfile);
                    iv.setImageURI(uri);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView ivProfile = view.findViewById(R.id.ivProfile);
        MaterialButton btnChangeImage = view.findViewById(R.id.btnChangeImage);

        TextInputEditText etFirstName = view.findViewById(R.id.etFirstName);
        TextInputEditText etLastName = view.findViewById(R.id.etLastName);
        TextInputEditText etAddress = view.findViewById(R.id.etAddress);
        TextInputEditText etPhone = view.findViewById(R.id.etPhone);

        View driverFieldsContainer = view.findViewById(R.id.driverFieldsContainer);
        TextInputEditText etCarModel = view.findViewById(R.id.etCarModel);
        TextInputEditText etCarType = view.findViewById(R.id.etCarType);
        TextInputEditText etPlate = view.findViewById(R.id.etPlateNumber);
        TextInputEditText etSeats = view.findViewById(R.id.etCarSeats);
        MaterialSwitch swBaby = view.findViewById(R.id.swBabyFriendly);
        MaterialSwitch swPet = view.findViewById(R.id.swPetFriendly);

        MaterialButton btnSave = view.findViewById(R.id.btnSaveChanges);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        User u = SessionManager.getUser(requireContext());

        String pic = prefs.getString(KEY_PICTURE_URI, null);
        if (!TextUtils.isEmpty(pic)) {
            selectedImageUri = Uri.parse(pic);
            ivProfile.setImageURI(selectedImageUri);
        } else {
            ivProfile.setImageResource(R.drawable.ic_launcher_foreground);
        }

        if (u != null) {
            etFirstName.setText(u.firstName != null ? u.firstName : "");
            etLastName.setText(u.lastName != null ? u.lastName : "");
            etAddress.setText(u.address != null ? u.address : "");
            etPhone.setText(u.phoneNumber != null ? u.phoneNumber : "");

            boolean isDriver = u.role == Role.driver;
            driverFieldsContainer.setVisibility(isDriver ? View.VISIBLE : View.GONE);

            if (isDriver) {
                etCarModel.setText(prefs.getString(KEY_CAR_MODEL, ""));
                etCarType.setText(prefs.getString(KEY_CAR_TYPE, ""));
                etPlate.setText(prefs.getString(KEY_PLATE, ""));
                etSeats.setText(prefs.getString(KEY_SEATS, ""));
                swBaby.setChecked(prefs.getBoolean(KEY_BABY, false));
                swPet.setChecked(prefs.getBoolean(KEY_PET, false));
            }
        } else {
            //driverFieldsContainer.setVisibility(View.GONE);

            // izbrisi nakon kt1
            etFirstName.setText("Aleksa");
            etLastName.setText("Aleksic");
            etAddress.setText("Alekse Santica 1");
            etPhone.setText("+123456789");
            driverFieldsContainer.setVisibility(View.VISIBLE);
            etCarModel.setText("Yugo");
            etCarType.setText("Luxury");
            etPlate.setText("YU 60 45");
            etSeats.setText("3");
            swBaby.setChecked(false);
            swPet.setChecked(false);
        }

        btnChangeImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            User user = SessionManager.getUser(requireContext());
            if (user != null) {
                user.firstName = value(etFirstName);
                user.lastName = value(etLastName);
                user.address = value(etAddress);
                user.phoneNumber = value(etPhone);
                SessionManager.setUser(requireContext(), user);

                SharedPreferences.Editor ed = prefs.edit();
                if (selectedImageUri != null) ed.putString(KEY_PICTURE_URI, selectedImageUri.toString());

                if (user.role == Role.driver) {
                    ed.putString(KEY_CAR_MODEL, value(etCarModel));
                    ed.putString(KEY_CAR_TYPE, value(etCarType));
                    ed.putString(KEY_PLATE, value(etPlate));
                    ed.putString(KEY_SEATS, value(etSeats));
                    ed.putBoolean(KEY_BABY, swBaby.isChecked());
                    ed.putBoolean(KEY_PET, swPet.isChecked());
                }

                ed.apply();
            }

            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
        });
    }

    private String value(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}