package com.smarthireflow.hrbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthireflow.hrbackend.model.Employee;
import com.smarthireflow.hrbackend.model.LeaveRequest;
import com.smarthireflow.hrbackend.repository.AttendanceRecordRepository;
import com.smarthireflow.hrbackend.repository.EmployeeRepository;
import com.smarthireflow.hrbackend.repository.InventoryItemRepository;
import com.smarthireflow.hrbackend.repository.LeaveRequestRepository;
import com.smarthireflow.hrbackend.security.TotpUtil;
import com.smarthireflow.hrbackend.user.entity.UserEntity;
import com.smarthireflow.hrbackend.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/employee")
public class ProfileController {

    private final UserService userService;
    private final EmployeeRepository employeeRepo;
    private final AttendanceRecordRepository attendanceRepo;
    private final LeaveRequestRepository leaveRepo;
    private final InventoryItemRepository inventoryRepo;
    private final PasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    @Value("${jwt.issuer:smarthireflow}")
    private String issuer;

    public ProfileController(UserService userService,
                             EmployeeRepository employeeRepo,
                             AttendanceRecordRepository attendanceRepo,
                             LeaveRequestRepository leaveRepo,
                             InventoryItemRepository inventoryRepo,
                             PasswordEncoder encoder,
                             ObjectMapper objectMapper) {
        this.userService = userService;
        this.employeeRepo = employeeRepo;
        this.attendanceRepo = attendanceRepo;
        this.leaveRepo = leaveRepo;
        this.inventoryRepo = inventoryRepo;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> payload, Authentication auth) {
        if (payload.containsKey("employeeId")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "employeeId cannot be updated"));
        }
        UserEntity user = userService.findByEmail(auth.getName()).orElseThrow();
        // Update allowed fields
        if (payload.containsKey("fullName")) user.setFullName(stringVal(payload.get("fullName")));
        if (payload.containsKey("department")) user.setDepartment(stringVal(payload.get("department")));
        if (payload.containsKey("phone")) user.setPhone(stringVal(payload.get("phone")));
        if (payload.containsKey("address")) user.setAddress(stringVal(payload.get("address")));
        if (payload.containsKey("email")) {
            String newEmail = stringVal(payload.get("email"));
            if (!newEmail.equalsIgnoreCase(user.getEmail())) {
                boolean taken = userService.existsByEmailAndIdNot(newEmail, user.getId());
                if (taken) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email already in use"));
                user.setEmail(newEmail);
            }
        }
        user = userService.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<?> uploadAvatar(@RequestPart("avatar") MultipartFile file, Authentication auth) throws Exception {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "No file uploaded"));
        }
        if (file.getSize() > 2 * 1024 * 1024) { // 2MB cap
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of("message", "Avatar too large (max 2MB)"));
        }
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") )) {
            return ResponseEntity.badRequest().body(Map.of("message", "Only image uploads are allowed"));
        }
        UserEntity user = userService.findByEmail(auth.getName()).orElseThrow();
        user.setAvatar(file.getBytes());
        user.setAvatarContentType(contentType);
        userService.save(user);
        return ResponseEntity.ok(Map.of("photoUrl", "/employee/profile/avatar"));
    }

    @GetMapping("/profile/avatar")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<byte[]> getAvatar(Authentication auth) {
        UserEntity user = userService.findByEmail(auth.getName()).orElseThrow();
        if (user.getAvatar() == null || user.getAvatarContentType() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        MediaType mt = MediaType.parseMediaType(user.getAvatarContentType());
        return ResponseEntity.ok().contentType(mt).body(user.getAvatar());
    }

    @DeleteMapping("/profile/avatar")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<Void> deleteAvatar(Authentication auth) {
        UserEntity user = userService.findByEmail(auth.getName()).orElseThrow();
        user.setAvatar(null);
        user.setAvatarContentType(null);
        userService.save(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body, Authentication auth) {
        String current = body.get("currentPassword");
        String next = body.get("newPassword");
        if (current == null || next == null || next.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "currentPassword and newPassword are required"));
        }
        UserEntity user = userService.findByEmail(auth.getName()).orElseThrow();
        if (!encoder.matches(current, user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Current password is incorrect"));
        }
        user.setPasswordHash(encoder.encode(next));
        userService.save(user);
        return ResponseEntity.ok(Map.of("message", "Password changed"));
    }

    @GetMapping("/2fa/setup")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<?> setup2fa(Authentication auth) {
        UserEntity user = userService.findByEmail(auth.getName()).orElseThrow();
        String secret = TotpUtil.generateBase32Secret(20);
        user.setTwoFactorSecret(secret);
        user.setTwoFactorEnabled(false);
        userService.save(user);
        String otpauth = TotpUtil.buildOtpAuthUrl(issuer, user.getEmail(), secret);
        return ResponseEntity.ok(Map.of("secret", secret, "otpauthUrl", otpauth));
    }

    @PostMapping("/2fa/enable")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<?> enable2fa(@RequestBody Map<String, String> body, Authentication auth) {
        String code = body.get("code");
        if (code == null || code.isBlank()) return ResponseEntity.badRequest().body(Map.of("message", "code is required"));
        UserEntity user = userService.findByEmail(auth.getName()).orElseThrow();
        if (user.getTwoFactorSecret() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "2FA not initialized"));
        if (!TotpUtil.verifyCode(user.getTwoFactorSecret(), code)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid code"));
        }
        user.setTwoFactorEnabled(true);
        userService.save(user);
        return ResponseEntity.ok(Map.of("message", "2FA enabled"));
    }

    @PostMapping("/2fa/disable")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<?> disable2fa(@RequestBody(required = false) Map<String, String> body, Authentication auth) {
        UserEntity user = userService.findByEmail(auth.getName()).orElseThrow();
        String code = body != null ? body.get("code") : null;
        if (code != null && user.getTwoFactorSecret() != null && !TotpUtil.verifyCode(user.getTwoFactorSecret(), code)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid code"));
        }
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userService.save(user);
        return ResponseEntity.ok(Map.of("message", "2FA disabled"));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<?> export(@RequestParam(defaultValue = "json") String format, Authentication auth) throws Exception {
        UserEntity user = userService.findByEmail(auth.getName()).orElseThrow();
        Optional<Employee> empOpt = employeeRepo.findByEmail(user.getEmail());

        Map<String, Object> export = new HashMap<>();
        export.put("profile", Map.of(
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRole(),
                "department", user.getDepartment(),
                "phone", user.getPhone(),
                "address", user.getAddress(),
                "twoFactorEnabled", user.isTwoFactorEnabled()
        ));
        empOpt.ifPresent(emp -> export.put("employee", Map.of(
                "id", emp.getId(),
                "name", emp.getName(),
                "email", emp.getEmail(),
                "department", emp.getDepartment(),
                "role", emp.getRole()
        )));
        if (empOpt.isPresent()) {
            var emp = empOpt.get();
            export.put("attendance", attendanceRepo.findByEmployee(emp));
            export.put("leaves", leaveRepo.findByEmployee(emp));
            export.put("inventory", inventoryRepo.findByAssignedTo(emp));
        } else {
            export.put("attendance", List.of());
            export.put("leaves", List.of());
            export.put("inventory", List.of());
        }

        if ("json".equalsIgnoreCase(format)) {
            return ResponseEntity.ok(export);
        }
        if ("zip".equalsIgnoreCase(format)) {
            byte[] jsonBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(export);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                ZipEntry entry = new ZipEntry("profile.json");
                zos.putNextEntry(entry);
                zos.write(jsonBytes);
                zos.closeEntry();
            }
            byte[] zipBytes = baos.toByteArray();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.zip")
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .body(zipBytes);
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Unsupported format"));
    }

    private static String stringVal(Object o) { return o == null ? null : String.valueOf(o); }
}
