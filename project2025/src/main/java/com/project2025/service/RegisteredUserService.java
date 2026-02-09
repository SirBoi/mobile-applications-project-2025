package com.project2025.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project2025.model.Admin;
import com.project2025.model.Driver;
import com.project2025.model.Passenger;
import com.project2025.model.RegisteredUser;
import com.project2025.repository.RegisteredUserRepository;

@Service
public class RegisteredUserService {

	public record LoadedImage(byte[] bytes, MediaType mediaType) {}
	
	@Value("${app.upload.dir:uploads}")
    private String uploadDir;
	
    private final RegisteredUserRepository repository;

    public RegisteredUserService(RegisteredUserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RegisteredUser create(RegisteredUser entity) {
        return repository.save(entity);
    }

    @Transactional
    public Optional<RegisteredUser> update(Long id, RegisteredUser updated) {

        Optional<RegisteredUser> opt = repository.findById(id);
        if (opt.isEmpty()) return Optional.empty();

        RegisteredUser existing = opt.get();

        // ===== COMMON FIELDS =====
        existing.setPassword(updated.getPassword());
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setAddress(updated.getAddress());
        existing.setPhoneNumber(updated.getPhoneNumber());

        // ===== DRIVER =====
        if (existing instanceof Driver && updated instanceof Driver) {
            Driver e = (Driver) existing;
            Driver u = (Driver) updated;

            e.setModel(u.getModel());
            e.setType(u.getType());
            e.setPlateNumber(u.getPlateNumber());
            e.setNumberOfSeats(u.getNumberOfSeats());
            e.setIsBabyFriendly(u.getIsBabyFriendly());
            e.setIsAnimalFriendly(u.getIsAnimalFriendly());
        }

        // ===== PASSENGER =====
        if (existing instanceof Passenger && updated instanceof Passenger) {
            Passenger e = (Passenger) existing;
            Passenger u = (Passenger) updated;

            e.setFavouriteRoutes(u.getFavouriteRoutes());
        }

        // ===== ADMIN =====
        if (existing instanceof Admin && updated instanceof Admin) {
            Admin e = (Admin) existing;
            Admin u = (Admin) updated;
        }

        return Optional.of(repository.save(existing));
    }

    @Transactional(readOnly = true)
    public Optional<RegisteredUser> findOne(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<RegisteredUser> findAll() {
        return repository.findAll();
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
    
    // Login handling
    @Transactional(readOnly = true)
    public Optional<RegisteredUser> findByMailAndPassword(String mail, String password) {
        return repository.findByMailAndPassword(mail, password);
    }

    // Profile picture handling
    @Transactional
    public boolean updateProfilePicture(Long userId, MultipartFile file) {
        Optional<RegisteredUser> opt = repository.findById(userId);
        if (opt.isEmpty()) return false;

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String ct = file.getContentType();
        if (ct == null || !(ct.equals("image/jpeg") || ct.equals("image/png") || ct.equals("image/webp"))) {
            throw new IllegalArgumentException("Only JPG/PNG/WEBP allowed");
        }

        RegisteredUser user = opt.get();

        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path dir = base.resolve("profile");
            Files.createDirectories(dir);

            // keep extension
            String ext = ct.equals("image/png") ? ".png" : (ct.equals("image/webp") ? ".webp" : ".jpg");
            String filename = "u" + userId + "_" + UUID.randomUUID() + ext;

            // delete old file (optional but nice)
            if (user.getPicture() != null && !user.getPicture().isBlank()) {
                Path old = dir.resolve(user.getPicture()).normalize();
                if (old.startsWith(dir)) Files.deleteIfExists(old);
            }

            Path target = dir.resolve(filename).normalize();
            if (!target.startsWith(dir)) throw new SecurityException("Invalid path");

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            user.setPicture(filename);
            repository.save(user);
            return true;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<LoadedImage> loadProfilePicture(Long userId) {
        return repository.findById(userId).flatMap(user -> {
            String name = user.getPicture();
            if (name == null || name.isBlank()) return Optional.empty();

            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("profile");
            Path file = dir.resolve(name).normalize();
            if (!file.startsWith(dir) || !Files.exists(file)) return Optional.empty();

            try {
                byte[] bytes = Files.readAllBytes(file);

                MediaType mt = name.endsWith(".png") ? MediaType.IMAGE_PNG :
                               name.endsWith(".webp") ? MediaType.valueOf("image/webp") :
                               MediaType.IMAGE_JPEG;

                return Optional.of(new LoadedImage(bytes, mt));
            } catch (IOException e) {
                return Optional.empty();
            }
        });
    }
}
