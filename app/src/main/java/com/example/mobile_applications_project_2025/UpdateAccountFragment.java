package com.example.mobile_applications_project_2025;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mobile_applications_project_2025.DTO.UpdateDriverDTO;
import com.example.mobile_applications_project_2025.Model.Admin;
import com.example.mobile_applications_project_2025.Model.Driver;
import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;
import com.example.mobile_applications_project_2025.Model.Passenger;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAccountFragment extends Fragment {
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Uri selectedImageUri;

    private ImageView ivProfile;

    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etAddress;
    private TextInputEditText etPhone;

    private LinearLayout driverFieldsContainer;
    private TextInputEditText etCarModel;
    private AutoCompleteTextView spCarType;
    private TextInputEditText etPlateNumber;
    private TextInputEditText etCarSeats;
    private MaterialSwitch swBabyFriendly;
    private MaterialSwitch swPetFriendly;

    private MaterialButton btnChangeImage;
    private MaterialButton btnSaveChanges;

    private final ExecutorService imgExecutor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_update_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // ===== Image picker =====
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        if (ivProfile != null) {
                            // local preview
                            ivProfile.setImageURI(uri);
                        }
                    }
                }
        );

        // ===== Bind views =====
        ivProfile = view.findViewById(R.id.ivProfile);

        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etAddress = view.findViewById(R.id.etAddress);
        etPhone = view.findViewById(R.id.etPhone);

        driverFieldsContainer = view.findViewById(R.id.driverFieldsContainer);
        etCarModel = view.findViewById(R.id.etCarModel);
        spCarType = view.findViewById(R.id.spCarType);
        etPlateNumber = view.findViewById(R.id.etPlateNumber);
        etCarSeats = view.findViewById(R.id.etCarSeats);
        swBabyFriendly = view.findViewById(R.id.swBabyFriendly);
        swPetFriendly = view.findViewById(R.id.swPetFriendly);

        btnChangeImage = view.findViewById(R.id.btnChangeImage);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);

        // ===== Car type dropdown =====
        String[] carTypes = new String[CarType.values().length];
        for (int i = 0; i < CarType.values().length; i++) {
            carTypes[i] = CarType.values()[i].name();
        }

        spCarType.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                carTypes
        ));

        // ===== Button listeners =====
        btnChangeImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // ===== Prefill =====
        RegisteredUser user = SessionManager.getUser();
        if (user == null) return;

        final RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);
        api.getProfilePicture(user.id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                ResponseBody body = response.body();
                imgExecutor.execute(() -> {
                    try {
                        byte[] bytes = body.bytes();
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (bmp == null) return;
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> ivProfile.setImageBitmap(bmp));
                    } catch (IOException ignored) {}
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });

        etFirstName.setText(user.firstName);
        etLastName.setText(user.lastName);
        etAddress.setText(user.address);
        etPhone.setText(user.phoneNumber);

        driverFieldsContainer.setVisibility(View.GONE);
        if (user instanceof Driver) {
            Driver d = (Driver) user;
            driverFieldsContainer.setVisibility(View.VISIBLE);

            etCarModel.setText(d.model);
            if (d.type != null) spCarType.setText(d.type.name(), false);
            etPlateNumber.setText(d.plateNumber);
            etCarSeats.setText(d.numberOfSeats != null ? String.valueOf(d.numberOfSeats) : "");
            swBabyFriendly.setChecked(Boolean.TRUE.equals(d.isBabyFriendly));
            swPetFriendly.setChecked(Boolean.TRUE.equals(d.isAnimalFriendly));
        }

        // ===== Save =====
        btnSaveChanges.setOnClickListener(v -> {
            RegisteredUser current = SessionManager.getUser();
            if (current == null) return;

            // 1) If user selected an image -> upload it FIRST to:
            //    PUT /api/users/{id}/picture with @RequestPart("file")
            if (selectedImageUri != null) {
                MultipartBody.Part part;
                try {
                    part = uriToMultipart(selectedImageUri);
                } catch (IOException e) {
                    Toast.makeText(requireContext(), "Failed to read selected image", Toast.LENGTH_SHORT).show();
                    return;
                }

                api.uploadProfilePicture(current.id, part).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(requireContext(), "Picture upload failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 2) After upload succeeds -> continue existing save logic
                        doExistingSaveLogic(current);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(requireContext(), "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                return; // IMPORTANT: wait for callback
            }

            // no image selected -> just do existing save logic
            doExistingSaveLogic(current);
        });
    }

    private void doExistingSaveLogic(@NonNull RegisteredUser current) {

        RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);

        // ===== DRIVER: send update request to admin =====
        if (current instanceof Driver) {

            String firstName = safeText(etFirstName);
            String lastName = safeText(etLastName);
            String address = safeText(etAddress);
            String phone = safeText(etPhone);

            String model = safeText(etCarModel);
            String plate = safeText(etPlateNumber);

            String seatsStr = safeText(etCarSeats);
            Integer seats = seatsStr.isEmpty() ? null : Integer.parseInt(seatsStr);

            String ct = spCarType.getText() != null ? spCarType.getText().toString().trim() : "";
            CarType type = ct.isEmpty() ? null : CarType.valueOf(ct);

            Boolean babyFriendly = swBabyFriendly.isChecked();
            Boolean petFriendly = swPetFriendly.isChecked();

            UpdateDriverDTO dto = new UpdateDriverDTO(
                    firstName, lastName, address, phone,
                    model, type, plate, seats,
                    babyFriendly, petFriendly
            );

            api.createDriverAccountUpdateRequest(current.id, dto)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!response.isSuccessful()) return;

                            Toast.makeText(requireContext(),
                                    "Update request sent to admin",
                                    Toast.LENGTH_SHORT).show();

                            NavHostFragment.findNavController(UpdateAccountFragment.this)
                                    .popBackStack();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) { }
                    });

            return;
        }

        // ===== PASSENGER / ADMIN: update immediately =====
        current.firstName = safeText(etFirstName);
        current.lastName = safeText(etLastName);
        current.address = safeText(etAddress);
        current.phoneNumber = safeText(etPhone);

        api.updateUser(current.id, current)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        JsonObject json = response.body();
                        Gson gson = new Gson();

                        String role = json.get("role").getAsString();
                        RegisteredUser freshUser;

                        switch (role) {
                            case "Driver":
                                freshUser = gson.fromJson(json, Driver.class);
                                break;
                            case "Passenger":
                                freshUser = gson.fromJson(json, Passenger.class);
                                break;
                            case "Admin":
                                freshUser = gson.fromJson(json, Admin.class);
                                break;
                            default:
                                return;
                        }

                        SessionManager.setUser(freshUser);

                        NavHostFragment.findNavController(UpdateAccountFragment.this)
                                .popBackStack();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) { }
                });
    }

    private String safeText(@Nullable TextInputEditText et) {
        return (et != null && et.getText() != null) ? et.getText().toString().trim() : "";
    }

    // ===== Uri -> Multipart ("file") =====
    private MultipartBody.Part uriToMultipart(@NonNull Uri uri) throws IOException {
        ContentResolver resolver = requireContext().getContentResolver();

        String fileName = getFileName(uri);
        if (fileName == null) fileName = "profile.jpg";

        File tempFile = new File(requireContext().getCacheDir(), fileName);

        try (InputStream in = resolver.openInputStream(uri);
             FileOutputStream out = new FileOutputStream(tempFile)) {

            if (in == null) throw new IOException("Cannot open input stream");

            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
        }

        String mime = resolver.getType(uri);
        if (mime == null) mime = "image/*";

        RequestBody rb = RequestBody.create(tempFile, MediaType.parse(mime));

        // MUST match backend: @RequestPart("file")
        return MultipartBody.Part.createFormData("file", tempFile.getName(), rb);
    }

    @Nullable
    private String getFileName(@NonNull Uri uri) {
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = requireContext().getContentResolver()
                        .query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0) return cursor.getString(idx);
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        String path = uri.getPath();
        if (path == null) return null;
        int cut = path.lastIndexOf('/');
        return (cut != -1) ? path.substring(cut + 1) : path;
    }
}
