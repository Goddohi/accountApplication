package com.example.account.dto;

import com.example.account.domain.Account;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateAccount {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{
        @NotNull
        @Min(1)
        private Long userId;

        @NotNull
        @Min(100)
        private Long initialBalance;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long userId;
        private String accountNumber;
        private LocalDateTime registereAt;

        public static Response fromAccountDto(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserid())
                    .accountNumber(accountDto.getAccountNumber())
                    .registereAt(accountDto.getRegisteredAt())
                    .build();
        }
    }
}
