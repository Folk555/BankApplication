package folk.BankApplication.model;

import folk.BankApplication.dto.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(min = 1)
    private Set<EmailData> emails = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(min = 1)
    private Set<PhoneData> phones = new HashSet<>();

    public UserDto toDto() {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName(name);
        userDto.setDateOfBirth(dateOfBirth);
        userDto.setEmails(emails.stream().map(EmailData::getEmail).collect(Collectors.toSet()));
        userDto.setPhones(phones.stream().map(PhoneData::getPhone).collect(Collectors.toSet()));
        userDto.setBalance(account.getBalance());
        return userDto;
    }
}