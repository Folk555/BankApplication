package folk.BankApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private Set<String> emails = new HashSet<>();
    private Set<String> phones = new HashSet<>();
    private BigDecimal balance;
}