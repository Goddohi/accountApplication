package com.example.account.dto;

import com.example.account.domain.Account;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private Long userid;
    private String accountNumber;
    private Long balance;


    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;


    //특정 class에서 바꾸는 방법
    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .userid(account.getAccountUser().getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .registeredAt(account.getRegisteredAt())
                .unRegisteredAt(account.getUnRegisteredAt())
                .build();
    }
}
