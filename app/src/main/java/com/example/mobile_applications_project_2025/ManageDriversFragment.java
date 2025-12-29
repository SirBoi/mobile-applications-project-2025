package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobile_applications_project_2025.Model.Driver;
import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;
import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class ManageDriversFragment extends Fragment {
    public ManageDriversFragment() {
        // Required empty public constructor
    }

    public static ManageDriversFragment newInstance(String param1, String param2) {
        ManageDriversFragment fragment = new ManageDriversFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_drivers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialButton btnCreate = view.findViewById(R.id.btnCreateDriver);
        LinearLayout list = view.findViewById(R.id.driversList);

        btnCreate.setOnClickListener(v -> androidx.navigation.fragment.NavHostFragment.findNavController(this)
                .navigate(R.id.driverCreationFragment));

        List<Driver> drivers = seed();

        LayoutInflater inf = LayoutInflater.from(requireContext());
        for (Driver d : drivers) {
            View card = inf.inflate(R.layout.card_driver, list, false);

            TextView tvName = card.findViewById(R.id.tvDriverName);
            TextView tvMail = card.findViewById(R.id.tvDriverMail);
            MaterialButton btnConfirm = card.findViewById(R.id.btnConfirm);
            MaterialButton btnDeny = card.findViewById(R.id.btnDeny);

            tvName.setText(d.firstName + " " + d.lastName);
            tvMail.setText(d.mail);

            btnConfirm.setOnClickListener(v -> { });
            btnDeny.setOnClickListener(v -> { });

            card.setOnClickListener(v -> showDetails(d));

            list.addView(card);
        }
    }

    private void showDetails(Driver d) {
        View content = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_driver_details, null, false);

        ImageView iv = content.findViewById(R.id.ivProfile);
        TextView tvFirst = content.findViewById(R.id.tvFirstNameValue);
        TextView tvLast = content.findViewById(R.id.tvLastNameValue);
        TextView tvEmail = content.findViewById(R.id.tvEmailValue);
        TextView tvAddress = content.findViewById(R.id.tvAddressValue);
        TextView tvPhone = content.findViewById(R.id.tvPhoneValue);
        TextView tvCarModel = content.findViewById(R.id.tvCarModelValue);
        TextView tvCarType = content.findViewById(R.id.tvCarTypeValue);
        TextView tvPlate = content.findViewById(R.id.tvPlateValue);
        TextView tvSeats = content.findViewById(R.id.tvSeatsValue);
        TextView tvBaby = content.findViewById(R.id.tvBabyFriendlyValue);
        TextView tvAnimal = content.findViewById(R.id.tvAnimalFriendlyValue);

        iv.setImageResource(R.drawable.ic_launcher_foreground);

        tvFirst.setText(d.firstName);
        tvLast.setText(d.lastName);
        tvEmail.setText(d.mail);
        tvAddress.setText(d.address);
        tvPhone.setText(d.phoneNumber);
        tvCarModel.setText(d.carModel);
        tvCarType.setText(d.carType.name());
        tvPlate.setText(d.plateNumber);
        tvSeats.setText(String.valueOf(d.numberOfSeats));
        tvBaby.setText(d.isBabyFriendly ? "Yes" : "No");
        tvAnimal.setText(d.isAnimalFriendly ? "Yes" : "No");

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Driver details")
                .setView(content)
                .setPositiveButton("Close", (dlg, which) -> dlg.dismiss())
                .show();
    }

    private List<Driver> seed() {
        List<Driver> list = new ArrayList<>();
        list.add(new Driver("1", Role.driver, "marko.jovanovic@mail.com", "", "Marko", "Jovanovic", "Bulevar Oslobodjenja 12, Novi Sad", "+381 64 111 222", null, 67, "Skoda Octavia", CarType.standard, "NS-123-AB", 4, true, true));
        list.add(new Driver("2", Role.driver, "ana.petrovic@mail.com", "", "Ana", "Petrovic", "Bulevar Oslobodjenja 12, Novi Sad", "+381 64 111 222", null, 67, "Skoda Octavia", CarType.standard, "NS-123-AB", 4, true, true));
        list.add(new Driver("3", Role.driver, "nikola.ilic@mail.com", "", "Nikola", "Ilic", "Bulevar Oslobodjenja 12, Novi Sad", "+381 64 111 222", null, 67, "Skoda Octavia", CarType.standard, "NS-123-AB", 4, true, true));
        list.add(new Driver("4", Role.driver, "jelena.stojanovic@mail.com", "", "Jelena", "Stojanovic", "Bulevar Oslobodjenja 12, Novi Sad", "+381 64 111 222", null, 67, "Skoda Octavia", CarType.standard, "NS-123-AB", 4, true, true));
        list.add(new Driver("5", Role.driver, "milan.kovacevic@mail.com", "", "Milan", "Kovacevic", "Bulevar Oslobodjenja 12, Novi Sad", "+381 64 111 222", null, 67, "Skoda Octavia", CarType.standard, "NS-123-AB", 4, true, true));
        return list;
    }
}