package folk.BankApplication.service;

import folk.BankApplication.config.JwtTokenProvider;
import folk.BankApplication.model.Account;
import folk.BankApplication.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    @CacheEvict(value = "balances", allEntries = true)
    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        log.info("Перевод денег на аккаунт юзера {}", toUserId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Неверная сумма перевода " + amount);
        }
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Отправитель и получатель не могут совпадать");
        }

        Long firstUserId = fromUserId.compareTo(toUserId) < 0 ? fromUserId : toUserId;
        Long secondUserId = fromUserId.compareTo(toUserId) < 0 ? toUserId : fromUserId;

        Account firstAccount = accountRepository.findByUserIdWithLock(firstUserId)
                .orElseThrow(() -> new EntityNotFoundException("Аккаунт отправитель не найден"));
        Account secondAccount = accountRepository.findByUserIdWithLock(secondUserId)
                .orElseThrow(() -> new EntityNotFoundException("Аккаунт получатель не найден"));

        Account fromAccount = firstUserId.equals(fromUserId) ? firstAccount : secondAccount;
        Account toAccount = secondUserId.equals(toUserId) ? secondAccount : firstAccount;

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Недостаточно средств на балансе отправителя");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.saveAll(List.of(fromAccount, toAccount));
        log.info("Перевод {} с аккаунта {} на аккаунт {} выполнен", amount, fromAccount.getId(), toAccount.getId());
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    @CacheEvict(value = "balances", allEntries = true)
    public void applyInterest() {
        log.debug("Повышаем балансы пользователей");
        accountRepository.increaseBalanceAllAccounts();
        log.debug("Повышение балансов пользователей завершено");
    }

}