package folk.BankApplication.controller;

import folk.BankApplication.dto.UserDto;
import folk.BankApplication.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User API", description = "Управление пользователями")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Получен запрос на поиск User");
        return ResponseEntity.ok(userService.searchUsers(name, email, phone, dateOfBirth, pageable));
    }

    @PostMapping("/phone")
    public ResponseEntity<String> addPhone(
            @RequestParam String phone) {
        Long userId = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        userService.addUserPhone(userId, phone);
        return ResponseEntity.ok("Телефон добавлен");
    }

    @DeleteMapping("/phone")
    public ResponseEntity<String> removePhone(
            @RequestParam String phone) {
        Long userId = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        userService.removeUserPhone(userId, phone);
        return ResponseEntity.ok("Телефон удален");
    }

    @PostMapping("/email")
    public ResponseEntity<String> addEmail(
            @RequestParam String email) {
        Long userId = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        userService.addUserEmail(userId, email);
        return ResponseEntity.ok("Email добавлен");
    }

    @DeleteMapping("/email")
    public ResponseEntity<String> removeEmail(
            @RequestParam String email) {
        Long userId = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        userService.removeUserEmail(userId, email);
        return ResponseEntity.ok("Email удален");
    }

}
