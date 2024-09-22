package com.example.account.dto;

import com.example.account.aop.AccountLockIdInterface;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class DeleteAccount {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request implements AccountLockIdInterface {
        @NotNull
        @Min(1)
        private Long userId;

        @NotBlank //notnull보다 강력
        @Size(min = 10, max = 10)
        private String accountNumber;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long userId;
        private String accountNumber;
        private LocalDateTime unRegistereAt;

        public static Response fromAccountDto(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserid())
                    .accountNumber(accountDto.getAccountNumber())
                    .unRegistereAt(accountDto.getUnRegisteredAt())
                    .build();
        }
    }
}
