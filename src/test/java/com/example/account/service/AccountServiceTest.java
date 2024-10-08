package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;
    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountSuccess(){
    //given
        AccountUser accountUser = AccountUser.builder()
                                      .id(12L)
                                      .name("pobi").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findFirstByOrderByIdDesc()).willReturn(
                Optional.of(Account.builder()
                                .accountUser(accountUser)
                                 .accountNumber("1000000012")
                                 .build()));
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                .accountUser(accountUser)
                .accountNumber("1000000015")
                .build());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
    //when
        AccountDto accountDto = accountService.createAccount(1L,1000L);
    //then
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        assertEquals(12L, accountDto.getUserid());
        assertEquals("1000000015", accountDto.getAccountNumber());
        //랜덤으로 잘나오는지 확인완료
        System.out.println(accountCaptor.getValue().getAccountNumber());
    }



    @Test
    @DisplayName("해당유저없음-계좌 생성 실패")
    void createAccount_UserNotFound() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(12L)
                .name("pobi").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        //when
        AccountException exception = assertThrows(AccountException.class
                , () -> accountService.createAccount(1L, 1000L));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

//

    @Test
    void deleteAccountSuccess(){
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(12L)
                .name("pobi").build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountNumber(anyString())).willReturn(
                Optional.of(Account.builder()
                        .accountUser(accountUser)
                        .accountNumber("1000000012")
                        .balance(0L)
                        .build()));


        //when
        AccountDto accountDto = accountService
                .deleteAccount(1L,"1234567890");
        //then
        assertEquals(12L, accountDto.getUserid());
    }



    @Test
    void successGetAccountsByUserId(){
    //given
        AccountUser pobi = AccountUser.builder()
                .id(12L)
                .name("dohi").build();
        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .accountUser(pobi)
                        .accountNumber("1111111111")
                        .balance(1000L)
                        .build(),
                Account.builder()
                        .accountUser(pobi)
                        .accountNumber("2222222222")
                        .balance(2000L)
                        .build(),
                Account.builder()
                        .accountUser(pobi)
                        .accountNumber("3333333333")
                        .balance(3000L)
                        .build()
        );

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));

        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);
    //when
        List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);
    //then
        assertEquals(3, accountDtos.size());
        assertEquals("1111111111", accountDtos.get(0).getAccountNumber());
        assertEquals("2222222222", accountDtos.get(1).getAccountNumber());
        assertEquals("3333333333", accountDtos.get(2).getAccountNumber());
        assertEquals(1000L, accountDtos.get(0).getBalance());
        assertEquals(2000L, accountDtos.get(1).getBalance());
        assertEquals(3000L, accountDtos.get(2).getBalance());
    }

    @Test
    void failedToGetAccounts(){
    //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
    //when
        AccountException exception = assertThrows(AccountException.class,
                ()->accountService.getAccountsByUserId(1L));
    //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }










    @Test
    @DisplayName("계좌 조회 성공")
    void testXXX() {
        //given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        //when
        Account account = accountService.getAccount(4555L);

        //then
        verify(accountRepository, times(1)).findById(captor.capture());
        verify(accountRepository, times(0)).save(any());
        assertEquals(4555L, captor.getValue());
        assertNotEquals(45515L, captor.getValue());
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    @DisplayName("계좌 조회 실패 - 음수로 조회")
    void testFailedToSearchAccount() {
        //given
        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.getAccount(-10L));

        //then
        assertEquals("Minus", exception.getMessage());
    }

    @Test
    @DisplayName("Test 이름 변경")
    void testGetAccount() {
        //given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    void testGetAccount2() {
        //given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }
}