package folk.BankApplication.service;

import folk.BankApplication.dto.UserDto;
import folk.BankApplication.model.EmailData;
import folk.BankApplication.model.PhoneData;
import folk.BankApplication.model.User;
import folk.BankApplication.repository.AccountRepository;
import folk.BankApplication.repository.EmailDataRepository;
import folk.BankApplication.repository.PhoneDataRepository;
import folk.BankApplication.repository.UserRepository;
import folk.BankApplication.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureAlgorithm;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDate;
import java.util.Date;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUserById(Long userId) {
        log.debug("Поиск юзера по ID: {}", userId);
        return userRepository.findById(userId)
                .map(User::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User не найден в БД"));
    }

    @Cacheable(value = "users", key = "{#pageable.pageNumber, #pageable.pageSize, #name, #email, #phone, #dateOfBirth}")
    public Page<UserDto> searchUsers(String name, String email, String phone, LocalDate dateOfBirth, Pageable pageable) {
        log.debug("Поис юзера по фильтрам - name: {}, email: {}, phone: {}, dateOfBirth: {}",
                name, email, phone, dateOfBirth);

        return userRepository.findWithFilters(name, email, phone, dateOfBirth, pageable).map(User::toDto);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void addUserEmail(Long userId, String newEmail) {
        log.info("Добавление email для юзера {}: {}", userId, newEmail);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Юзер не найден"));
        if (emailDataRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email уже кем-то занят");
        }
        EmailData emailData = new EmailData();
        emailData.setUser(user);
        emailData.setEmail(newEmail);
        emailDataRepository.save(emailData);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void removeUserEmail(Long userId, String email) {
        log.info("Удаление email для юзера {}: {}", userId, email);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Юзер не найден"));
        if (user.getEmails().size() <= 1) {
            throw new IllegalStateException("Нельзя удалять единственный email");
        }
        EmailData emailToRemove = user.getEmails().stream()
                .filter(e -> e.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("У данного юзера не найден email"));

        user.getEmails().remove(emailToRemove);
        emailDataRepository.delete(emailToRemove);
        userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void addUserPhone(Long userId, String phone) {
        log.info("Добавление телефона {} для юзера {}", phone, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Юзер не найден"));
        if (phoneDataRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Телефон уже кем-то занят");
        }

        PhoneData phoneData = new PhoneData();
        phoneData.setPhone(phone);
        phoneData.setUser(user);
        phoneDataRepository.save(phoneData);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void removeUserPhone(Long userId, String phone) {
        log.info("удаление телефона {} юзера {}", phone, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Юзер не найден"));
        if (user.getPhones().size() <= 1) {
            throw new IllegalStateException("Нельзя удалять единственный телефон");
        }

        PhoneData phoneData = user.getPhones().stream()
                .filter(p -> p.getPhone().equals(phone))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("У данного юзера не найден телефон"));
        phoneDataRepository.delete(phoneData);
    }

    @Transactional
    public String authenticate(String login, String password) {
        log.debug("Аутентификация для логина: {}", login);

        User user = emailDataRepository.findByEmail(login)
                .map(emailData -> {
                    User u = emailData.getUser();
                    u.getId();
                    return u;
                })
                .orElseThrow(() -> new AuthenticationException("Неверный логин") {});
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Неверный пароль") {};
        }

        return JwtUtil.generateJwtToken(user.getId());
    }

}
