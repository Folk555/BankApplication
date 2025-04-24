package folk.BankApplication.controller;

import folk.BankApplication.config.JwtTokenProvider;
import folk.BankApplication.model.Account;
import folk.BankApplication.repository.AccountRepository;
import folk.BankApplication.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AccountService accountService;

    private static final String TOKEN = "Bearer some-jwt-token";

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        fromAccount = new Account(1L, null, new BigDecimal("500"), new BigDecimal("500"));
        toAccount = new Account(2L, null, new BigDecimal("100"), new BigDecimal("100"));
    }

    @Test
    void transferMoney_SuccessfulTransfer() {
        when(jwtTokenProvider.getUserIdFromToken(TOKEN.replace("Bearer ", ""))).thenReturn("1");
        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(toAccount));

        accountService.transferMoney(TOKEN, 2L, new BigDecimal("100"));

        assertEquals(new BigDecimal("400"), fromAccount.getBalance());
        assertEquals(new BigDecimal("200"), toAccount.getBalance());

        verify(accountRepository, times(1)).saveAll(List.of(fromAccount, toAccount));
    }

    @Test
    void transferMoney_InsufficientFunds() {
        when(jwtTokenProvider.getUserIdFromToken(TOKEN.replace("Bearer ", ""))).thenReturn("1");
        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(toAccount));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.transferMoney(TOKEN, 2L, new BigDecimal("600"));
        });

        assertEquals("Недостаточно средств на балансе отправителя", exception.getMessage());
    }

}