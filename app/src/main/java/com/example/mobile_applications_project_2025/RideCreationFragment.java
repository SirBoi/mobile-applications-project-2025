// FILE: app/src/main/java/com/example/mobile_applications_project_2025/RideCreationFragment.java
package com.example.mobile_applications_project_2025;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mobile_applications_project_2025.DTO.PageResponseDTO;
import com.example.mobile_applications_project_2025.DTO.RideCreateWithCriteriaRequestDTO;
import com.example.mobile_applications_project_2025.Model.Address;
import com.example.mobile_applications_project_2025.Model.Config;
import com.example.mobile_applications_project_2025.Model.Driver;
import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;
import com.example.mobile_applications_project_2025.Model.Enumerator.RideStatus;
import com.example.mobile_applications_project_2025.Model.Passenger;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Model.Ride;
import com.example.mobile_applications_project_2025.Model.Route;
import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.APIs.RideAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideCreationFragment extends Fragment {

    // UI
    private TextInputEditText etPassenger;
    private TextInputEditText etStop;
    private MaterialButton btnSetPassenger;
    private MaterialButton btnSetStop;
    private MaterialButton btnPickFavouriteRoute;

    private MaterialButton btnPickDateTime;
    private MaterialTextView tvSelectedDateTime;

    private Spinner spVehicleType;

    private MaterialCheckBox cbBabyFriendly;
    private MaterialCheckBox cbAnimalFriendly;

    private MaterialTextView tvDistanceValue;
    private MaterialTextView tvPriceValue;

    private ChipGroup cgPassengers;
    private ChipGroup cgStops;

    // State
    private final Calendar selected = Calendar.getInstance();
    private final Random random = new Random();

    private final List<String> passengerEmails = new ArrayList<>();
    private final List<StopEntry> stops = new ArrayList<>();
    private Config carTypeRates;

    private static final float PRICE_PER_KM = 120f;

    public RideCreationFragment() {}

    private static class StopEntry {
        final String address;
        final float distance; // km
        StopEntry(String address, float distance) {
            this.address = address;
            this.distance = distance;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ride_creation, container, false);

        // Bind views
        etPassenger = view.findViewById(R.id.etPassenger);
        etStop = view.findViewById(R.id.etStop);
        btnSetPassenger = view.findViewById(R.id.btnSetPassenger);
        btnSetStop = view.findViewById(R.id.btnSetStop);
        btnPickFavouriteRoute = view.findViewById(R.id.btnPickFavouriteRoute);
        btnPickFavouriteRoute.setOnClickListener(v -> openFavouriteRoutesDialog());

        btnPickDateTime = view.findViewById(R.id.btnPickDateTime);
        tvSelectedDateTime = view.findViewById(R.id.tvSelectedDateTime);

        spVehicleType = view.findViewById(R.id.spVehicleType);

        cbBabyFriendly = view.findViewById(R.id.cbBabyFriendly);
        cbAnimalFriendly = view.findViewById(R.id.cbAnimalFriendly);

        tvDistanceValue = view.findViewById(R.id.tvDistanceValue);
        tvPriceValue = view.findViewById(R.id.tvPriceValue);

        cgPassengers = view.findViewById(R.id.cgPassengers);
        cgStops = view.findViewById(R.id.cgStops);

        // Spinner (normal dropdown)
        String[] vehicleTypes = new String[]{"Standard", "Luxury", "Van"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                vehicleTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVehicleType.setAdapter(adapter);
        spVehicleType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View v, int pos, long id) {
                updatePriceLabel();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        // Prefilled datetime = now + 5 minutes
        Calendar prefill = Calendar.getInstance();
        prefill.add(Calendar.MINUTE, 5);
        floorToMinute(prefill);

        selected.setTimeInMillis(prefill.getTimeInMillis());
        floorToMinute(selected);
        updateDateTimeText();

        btnPickDateTime.setOnClickListener(v -> openDateThenTimePicker());

        // Default stop (prefilled, non-removable)
        addStopChip("Fruskogorska 1", 0f, false);

        // Default passenger = logged-in user's email (NON-REMOVABLE)
        RegisteredUser u = SessionManager.getUser();
        if (u != null && u.getMail() != null && !u.getMail().trim().isEmpty()) {
            String mail = u.getMail().trim();
            if (!passengerEmails.contains(mail)) {
                passengerEmails.add(mail);
                addPassengerChip(mail, false); // non-removable default passenger
            }
        }

        // Passenger add: must look like email (has @ and .)
        btnSetPassenger.setOnClickListener(v -> {
            String text = etPassenger.getText() == null ? "" : etPassenger.getText().toString().trim();
            if (TextUtils.isEmpty(text)) return;

            boolean looksLikeMail = text.contains("@") && text.contains(".");
            if (!looksLikeMail) {
                Toast.makeText(requireContext(), "Passenger must be a valid email.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (passengerEmails.contains(text)) {
                Toast.makeText(requireContext(), "Passenger already added.", Toast.LENGTH_SHORT).show();
                return;
            }

            passengerEmails.add(text);
            addPassengerChip(text, true); // removable for manually added passengers
            etPassenger.setText("");
        });

        // Stop add: add address + random distance (0.3..1.8, 1 decimal)
        btnSetStop.setOnClickListener(v -> {
            String address = etStop.getText() == null ? "" : etStop.getText().toString().trim();
            if (TextUtils.isEmpty(address)) return;

            for (StopEntry s : stops) {
                if (s.address.equalsIgnoreCase(address)) {
                    Toast.makeText(requireContext(), "Stop already added.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            float dist = generateRandomDistance();
            addStopChip(address, dist, true);
            etStop.setText("");
        });

        // Fetch car type rates when page loads
        fetchCarTypeRatesAndUpdatePrice();

        // Create ride button validations + existing dialog behavior
        MaterialButton btnCreateRide = view.findViewById(R.id.btnCreateRide);
        btnCreateRide.setOnClickListener(v -> {

            // Must have >= 1 passenger (default one is OK)
            if (passengerEmails.size() < 1) {
                Toast.makeText(requireContext(), "Add at least one passenger.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Must have one stop besides default -> total stops must be >= 2
            if (stops.size() < 2) {
                Toast.makeText(requireContext(), "Add at least one additional stop.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Button press validation:
            // MIN allowed = CURRENT TIME (not +5)
            // MAX allowed = now + 5 hours
            Calendar min = Calendar.getInstance(); // now
            Calendar max = Calendar.getInstance();
            max.add(Calendar.HOUR_OF_DAY, 5);

            floorToMinute(min);
            floorToMinute(max);

            Calendar chosen = (Calendar) selected.clone();
            floorToMinute(chosen);

            if (chosen.before(min)) {
                Toast.makeText(requireContext(), "Pick a time that is not in the past.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (chosen.after(max)) {
                Toast.makeText(requireContext(), "Pick a time within 5 hours from now.", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- BUILD REQUEST BODY ---
            String carType = spVehicleType.getSelectedItem().toString();
            boolean baby = cbBabyFriendly.isChecked();
            boolean animal = cbAnimalFriendly.isChecked();

            Ride ride = new Ride();
            Route route = new Route();
            List<Address> routeAddresses = new ArrayList<Address>();

            for (int i = 0; i < stops.size(); i++) {
                routeAddresses.add(getAddress(i));
            }

            route.addresses = routeAddresses;
            ride.origin = getAddress(0);
            ride.destination = getAddress(stops.size() - 1);
            ride.route = route;
            ride.ridePrice = computeRidePriceRsd();
            ride.passenger = (Passenger)u;
            ride.passengers = passengerEmails;
            ride.rideStartDatetime = LocalDateTime.ofInstant(chosen.toInstant(), ZoneId.systemDefault()).toString();
            ride.hasStarted = Boolean.FALSE;
            ride.status = RideStatus.Scheduled;
            ride.isPanicPressed = Boolean.FALSE;

            Log.i("RIDEEEEEEEEEEEEEEEEEE", ride.toString());

            RideCreateWithCriteriaRequestDTO body = new RideCreateWithCriteriaRequestDTO();
            body.setRide(ride);
            body.setCarType(CarType.valueOf(carType));
            body.setBabyFriendly(baby);
            body.setAnimalFriendly(animal);

            Log.i("BODYYYYYYYYYYYYYYYYYYYY", body.toString());

            RideAPI api = ApiClient.getRetrofit().create(RideAPI.class);

            api.createWithDriverMatch(body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        // 409 -> no eligible driver
                        if (response.code() == 409) {
                            Toast.makeText(requireContext(), "No eligible driver available.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(requireContext(), "Failed to create ride. (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Success
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Ride created")
                            .setMessage("Your ride has been created successfully.")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                                NavHostFragment.findNavController(RideCreationFragment.this).navigateUp();
                            })
                            .show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Initialize labels
        updateDistanceLabel(); // also updates price

        return view;
    }

    // --- Chips ---

    private void addPassengerChip(String email, boolean removable) {
        Chip chip = new Chip(requireContext());
        chip.setText(email);
        chip.setCloseIconVisible(removable);

        if (removable) {
            chip.setOnCloseIconClickListener(v -> {
                passengerEmails.remove(email);
                cgPassengers.removeView(chip);
            });
        }

        cgPassengers.addView(chip);
    }

    private void addStopChip(String address, float distance, boolean removable) {
        StopEntry entry = new StopEntry(address, distance);
        stops.add(entry);

        Chip chip = new Chip(requireContext());
        chip.setText(address);
        chip.setCloseIconVisible(removable);

        if (removable) {
            chip.setOnCloseIconClickListener(v -> {
                for (int i = 0; i < stops.size(); i++) {
                    if (stops.get(i).address.equalsIgnoreCase(address)) {
                        stops.remove(i);
                        break;
                    }
                }
                cgStops.removeView(chip);
                updateDistanceLabel();
            });
        }

        cgStops.addView(chip);
        updateDistanceLabel();
    }

    // --- Distance + Price ---

    private float generateRandomDistance() {
        float value = 0.3f + random.nextFloat() * (1.8f - 0.3f);
        return Math.round(value * 10f) / 10f;
    }

    private float totalDistanceKm() {
        float total = 0f;
        for (StopEntry s : stops) total += s.distance;
        return total;
    }

    private void updateDistanceLabel() {
        float total = totalDistanceKm();
        tvDistanceValue.setText(String.format(Locale.getDefault(), "%.1f km", total));
        updatePriceLabel();
    }

    private void updatePriceLabel() {
        if (carTypeRates == null) {
            tvPriceValue.setText("—");
            return;
        }

        String carType = (spVehicleType != null && spVehicleType.getSelectedItem() != null)
                ? spVehicleType.getSelectedItem().toString()
                : null;

        float baseRate = 0f;
        if (Objects.equals(carType, "Standard")) {
            baseRate = carTypeRates.standardPrice;
        } else if (Objects.equals(carType, "Luxury")) {
            baseRate = carTypeRates.luxuryPrice;
        } else if (Objects.equals(carType, "Van")) {
            baseRate = carTypeRates.vanPrice;
        }

        float price = baseRate + totalDistanceKm() * PRICE_PER_KM;
        tvPriceValue.setText(String.format(Locale.getDefault(), "%.0f RSD", price));
    }

    private float computeRidePriceRsd() {
        if (carTypeRates == null) return 0f;

        String carType = spVehicleType.getSelectedItem().toString();
        float baseRate = 0f;

        if ("Standard".equals(carType)) baseRate = carTypeRates.standardPrice;
        else if ("Luxury".equals(carType)) baseRate = carTypeRates.luxuryPrice;
        else if ("Van".equals(carType)) baseRate = carTypeRates.vanPrice;

        return baseRate + totalDistanceKm() * PRICE_PER_KM;
    }

    // --- Date/Time ---

    private void openDateThenTimePicker() {
        // Picker bounds:
        // MIN selectable = now + 5 minutes
        // MAX selectable = now + 5 hours
        Calendar minSelectable = Calendar.getInstance();
        Calendar maxSelectable = Calendar.getInstance();
        maxSelectable.add(Calendar.HOUR_OF_DAY, 5);

        floorToMinute(minSelectable);
        floorToMinute(maxSelectable);

        Calendar nowForPicker = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(
                requireContext(),
                (datePicker, year, month, dayOfMonth) -> {
                    Calendar temp = (Calendar) selected.clone();
                    temp.set(Calendar.YEAR, year);
                    temp.set(Calendar.MONTH, month);
                    temp.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog tp = new TimePickerDialog(
                            requireContext(),
                            (timePicker, hourOfDay, minute) -> {

                                temp.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                temp.set(Calendar.MINUTE, minute);

                                // 🔽 ADD THIS LINE RIGHT HERE
                                floorToMinute(temp);

                                if (temp.before(minSelectable)) {
                                    Toast.makeText(requireContext(),
                                            "Pick a time that is not in the past.",
                                            Toast.LENGTH_SHORT).show();
                                    selected.setTimeInMillis(minSelectable.getTimeInMillis());
                                    updateDateTimeText();
                                    return;
                                }

                                if (temp.after(maxSelectable)) {
                                    Toast.makeText(requireContext(),
                                            "Pick a time within 5 hours from now.",
                                            Toast.LENGTH_SHORT).show();
                                    selected.setTimeInMillis(maxSelectable.getTimeInMillis());
                                    updateDateTimeText();
                                    return;
                                }

                                selected.setTimeInMillis(temp.getTimeInMillis());
                                updateDateTimeText();
                            },
                            nowForPicker.get(Calendar.HOUR_OF_DAY),
                            nowForPicker.get(Calendar.MINUTE),
                            true
                    );
                    tp.show();
                },
                nowForPicker.get(Calendar.YEAR),
                nowForPicker.get(Calendar.MONTH),
                nowForPicker.get(Calendar.DAY_OF_MONTH)
        );

        dp.getDatePicker().setMinDate(nowForPicker.getTimeInMillis());
        dp.show();
    }

    private void updateDateTimeText() {
        String s = String.format(
                Locale.getDefault(),
                "%04d-%02d-%02d %02d:%02d",
                selected.get(Calendar.YEAR),
                selected.get(Calendar.MONTH) + 1,
                selected.get(Calendar.DAY_OF_MONTH),
                selected.get(Calendar.HOUR_OF_DAY),
                selected.get(Calendar.MINUTE)
        );
        tvSelectedDateTime.setText(s);
    }

    // --- Backend rates ---
    private void fetchCarTypeRatesAndUpdatePrice() {
        RideAPI api = ApiClient.getRetrofit().create(RideAPI.class);
        api.getConfig().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed to load car type rates.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    carTypeRates = gson.fromJson(json, Config.class);
                    updatePriceLabel();
                } catch (IOException e) {
                    Log.e("RideCreationFragment", "Config parse error", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load car type rates.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void floorToMinute(Calendar c) {
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private Address getAddress(int i) {
        Address address = new Address();
        String street = stops.get(i).address;
        int lastSpace = street.lastIndexOf(" ");

        address.setCity("Novi Sad");
        address.setCountry("Serbia");

        if (lastSpace != -1) {
            address.setStreet(street.substring(0, lastSpace));
            address.setNumber(street.substring(lastSpace + 1));
        } else {
            address.setStreet(street);
            address.setNumber("");
        }

        return address;
    }

    private void openFavouriteRoutesDialog() {
        RegisteredUser u = SessionManager.getUser();
        if (u == null || u.getId() == null) {
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_favourite_routes, null, false);
        ListView lv = dialogView.findViewById(R.id.lvRoutes);
        Button btnPrev = dialogView.findViewById(R.id.btnPrev);
        Button btnNext = dialogView.findViewById(R.id.btnNext);
        TextView tvPage = dialogView.findViewById(R.id.tvPage);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        lv.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Favourite routes")
                .setView(dialogView)
                .setNegativeButton("Close", (d, which) -> d.dismiss())
                .create();

        final int pageSize = 8;
        final int[] currentPage = {0};
        final int[] totalPages = {1};
        final List<Route> currentRoutes = new ArrayList<>();

        Runnable loadPage = () -> {
            RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);
            api.getFavouriteRoutesPaged(u.getId(), currentPage[0], pageSize).enqueue(new Callback<PageResponseDTO<Route>>() {
                @Override
                public void onResponse(Call<PageResponseDTO<Route>> call, Response<PageResponseDTO<Route>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(requireContext(), "Failed to load favourites (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    PageResponseDTO<Route> page = response.body();
                    totalPages[0] = Math.max(1, page.totalPages);

                    currentRoutes.clear();
                    if (page.content != null) currentRoutes.addAll(page.content);

                    List<String> labels = new ArrayList<>();
                    for (int i = 0; i < currentRoutes.size(); i++) {
                        Route r = currentRoutes.get(i);
                        labels.add(buildRouteListLabel(r));
                    }

                    adapter.clear();
                    adapter.addAll(labels);
                    adapter.notifyDataSetChanged();

                    tvPage.setText((currentPage[0] + 1) + " / " + totalPages[0]);

                    btnPrev.setEnabled(currentPage[0] > 0);
                    btnNext.setEnabled(currentPage[0] < totalPages[0] - 1);
                }

                @Override
                public void onFailure(Call<PageResponseDTO<Route>> call, Throwable t) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        };

        btnPrev.setOnClickListener(v -> {
            if (currentPage[0] > 0) {
                currentPage[0]--;
                loadPage.run();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage[0] < totalPages[0] - 1) {
                currentPage[0]++;
                loadPage.run();
            }
        });

        lv.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= currentRoutes.size()) return;
            Route selectedRoute = currentRoutes.get(position);
            openFavouriteRouteDetailsDialog(selectedRoute, dialog);
        });

        dialog.show();
        loadPage.run();
    }

    private String buildRouteListLabel(Route r) {
        if (r == null || r.addresses == null || r.addresses.isEmpty()) return "(empty route)";
        Address first = r.addresses.get(0);
        Address last = r.addresses.get(r.addresses.size() - 1);
        return formatAddressLine(first) + "  →  " + formatAddressLine(last);
    }

    private void openFavouriteRouteDetailsDialog(Route route, AlertDialog parentDialog) {
        if (route == null) return;

        View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_favourite_route_details, null, false);
        TextView tv = v.findViewById(R.id.tvRouteDetails);
        MaterialButton btnAdd = v.findViewById(R.id.btnAddRoute);

        tv.setText(buildRouteDetailsText(route));

        AlertDialog details = new AlertDialog.Builder(requireContext())
                .setTitle("Route details")
                .setView(v)
                .setNegativeButton("Back", (d, which) -> d.dismiss())
                .create();

        btnAdd.setOnClickListener(x -> {
            applyFavouriteRouteToStops(route);
            details.dismiss();
            if (parentDialog != null) parentDialog.dismiss();
            Toast.makeText(requireContext(), "Route loaded.", Toast.LENGTH_SHORT).show();
        });

        details.show();
    }

    private String buildRouteDetailsText(Route r) {
        StringBuilder sb = new StringBuilder();
        if (r.addresses == null || r.addresses.isEmpty()) {
            return "No addresses.";
        }
        sb.append("Addresses:\n\n");
        for (int i = 0; i < r.addresses.size(); i++) {
            sb.append(i + 1).append(". ").append(formatAddressFull(r.addresses.get(i))).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Overwrites current stop chips + stop list with addresses from favourite route.
     * - First stop becomes route[0]
     * - First stop is NON-REMOVABLE
     * - Distances remain internal only (not displayed)
     */
    private void applyFavouriteRouteToStops(Route route) {
        if (route == null || route.addresses == null || route.addresses.isEmpty()) {
            Toast.makeText(requireContext(), "Selected route has no addresses.", Toast.LENGTH_SHORT).show();
            return;
        }

        // clear existing
        stops.clear();
        cgStops.removeAllViews();

        for (int i = 0; i < route.addresses.size(); i++) {
            Address a = route.addresses.get(i);
            String text = addressToStopString(a);

            boolean removable = (i != 0);               // first is non-removable
            float dist = (i == 0) ? 0f : generateRandomDistance(); // keep hidden simulation

            addStopChip(text, dist, removable);
        }

        // also overwrite stop input text, optional
        etStop.setText("");
    }

    /** Converts Address -> the same format your getAddress() expects: "Street 1" (last token is number). */
    private String addressToStopString(Address a) {
        if (a == null) return "";
        String street = a.street != null ? a.street.trim() : "";
        String number = a.number != null ? a.number.trim() : "";

        if (!street.isEmpty() && !number.isEmpty()) return street + " " + number;
        return street.isEmpty() ? number : street;
    }

    private String formatAddressLine(Address a) {
        if (a == null) return "-";
        String street = a.street != null ? a.street.trim() : "";
        String number = a.number != null ? a.number.trim() : "";
        String city = a.city != null ? a.city.trim() : "";

        String s = (street + " " + number).trim();
        if (!s.isEmpty() && !city.isEmpty()) return s + ", " + city;
        if (!s.isEmpty()) return s;
        if (!city.isEmpty()) return city;
        return "-";
    }

    private String formatAddressFull(Address a) {
        if (a == null) return "-";
        List<String> parts = new ArrayList<>();
        String s = (safe(a.street) + " " + safe(a.number)).trim();
        if (!s.isEmpty()) parts.add(s);
        if (!safe(a.city).isEmpty()) parts.add(a.city.trim());
        if (!safe(a.country).isEmpty()) parts.add(a.country.trim());
        return parts.isEmpty() ? "-" : TextUtils.join(", ", parts);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}