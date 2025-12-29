package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class UserSearchFragment extends Fragment {

    public UserSearchFragment() {
        // Required empty public constructor
    }

    public static UserSearchFragment newInstance(String param1, String param2) {
        UserSearchFragment fragment = new UserSearchFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etSearch = view.findViewById(R.id.etSearch);
        MaterialButton btnSearch = view.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            String q = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
            if (q.isEmpty()) {
                Toast.makeText(requireContext(), "Unesi tekst za pretragu.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(), "UI-only search: " + q, Toast.LENGTH_SHORT).show();
        });

        // Klik na kartice (UI-only)
        view.findViewById(R.id.cardUser1).setOnClickListener(v -> toast("Otvoren korisnik: Marko Marković"));
        view.findViewById(R.id.cardUser2).setOnClickListener(v -> toast("Otvoren korisnik: Jovana Jovanović"));
        view.findViewById(R.id.cardUser3).setOnClickListener(v -> toast("Otvoren korisnik: Nikola Nikolić"));
        view.findViewById(R.id.cardUser4).setOnClickListener(v -> toast("Otvoren korisnik: Ana Anić"));
        view.findViewById(R.id.cardUser5).setOnClickListener(v -> toast("Otvoren korisnik: Petar Petrović"));
    }

    private void toast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
