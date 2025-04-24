package folk.BankApplication.dto;

import folk.BankApplication.model.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private Account account;
    private Set<String> emails = new HashSet<>();
    private Set<String> phones = new HashSet<>();
    private BigDecimal balance;
}