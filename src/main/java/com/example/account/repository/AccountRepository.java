package com.example.account.repository;

import com.example.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findFirstByOrderByIdDesc();
    // 계좌번호 중복 여부를 확인하는 메소드
    boolean existsByAccountNumber(String accountNumber);
    //계좌갯수세기
    long countByAccountUserId(Long userId);

    Optional<Account> findByAccountNumber(String accountNumber);
}
