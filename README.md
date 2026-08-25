# Ride Sharing App

Uber-like ride sharing system — Spring Boot backend + Android mobile app.

## Structure
- `/backend` — Spring Boot REST API (Java, Maven, PostgreSQL)
- `/mobile` — Android app (Java)

## Running the backend

**Requirements:** Java 17+, Maven, PostgreSQL

1. Create a PostgreSQL database.
2. Set your DB credentials in `src/main/resources/application.properties`:
   ```
   spring.datasource.url=jdbc:postgresql://localhost:5432/project2025
   spring.datasource.username=<username>
   spring.datasource.password=<password>
   ```
3. Run the app:
   - **Eclipse:** right-click the main class (e.g. `Project2025Application.java`) → `Run As` → `Java Application`
   - **Or via Maven:** `./mvnw spring-boot:run`

   API available at `http://localhost:8080`.

## Running the mobile app

**Requirements:** Android Studio, JDK 17

1. Open `app/` in Android Studio.
2. Set the backend URL in `Network/BaseUrl.java`:
   - Emulator: `http://10.0.2.2:8080/`
   - Physical device: your computer's LAN IP (e.g. `http://192.168.1.6:8080/`) — device and computer must be on the same Wi-Fi network.
3. Build & run.

## Test accounts
- Admin: (add credentials)
- Driver: created via admin panel
- Passenger: register via the app (activation link sent by email)
