package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("사용자가 없습니다."),
    MAX_ACCOUNT_LIMIT_EXCEEDED("최대 보유 가능한 계좌의 수를 넘으셨습니다.");


    private final String description;
}
