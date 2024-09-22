package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

import static com.example.account.type.AccountStatus.IN_USE;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;
    /**
     * 사용자가 있는지 조회
     * 계좌의 번호 저장
     * 계좌를 저장후 정보 넘기기
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        // 사용자 계좌 수 확인
        long accountCount = accountRepository.countByAccountUserId(userId);
        //10개 이상일 경우 실패
        if (accountCount >= 10) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_LIMIT_EXCEEDED);
        }

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account ->(generateUniqueAccountNumber()))
                .orElse("1000000000");


        return AccountDto.fromEntity(
                accountRepository.save( Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()
                )
        );
    }
    public String generateUniqueAccountNumber() {
        String newAccountNumber;
        boolean isDuplicate;

        do {
            // 무작위 계좌번호 생성 (기존의 generateRandomCode 메소드 사용)
            newAccountNumber = generateRandomCode();

            // 중복 여부 확인
            isDuplicate = accountRepository.existsByAccountNumber(newAccountNumber);
        } while (isDuplicate); // 중복이면 다시 생성

        return newAccountNumber;
    }

    private String generateRandomCode() {
        // 가능한 문자와 숫자를 정의
        String characters = "0123456789";

        // 랜덤 코드 생성
        StringBuilder codeBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(index));
        }
        return codeBuilder.toString();
    }
    @Transactional
    public Account getAccount(Long id) {
        if(id < 0){
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }


    /**
     * 
     * 예외사항 ( 사용자 또는 계좌가 없는경우 
     * , 소유주가 다른경우 
     * , 계좌가 이미 해지상태
     * , 잔액이 있는경우 )
     * 
     * 성공시 사용자 아이디 계좌번호 해지일시 
     */
    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateDeleteAccount(accountUser,account);
        
        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());
        
        return AccountDto.fromEntity(account);
    }
    
    private void validateDeleteAccount(AccountUser accountUser,Account account) {
        if(! Objects.equals(accountUser.getId(),account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_UN_MATCH);
        }
        if(account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance()>0){
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
        }
    }
}
