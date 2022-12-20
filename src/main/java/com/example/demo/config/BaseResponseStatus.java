package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    FAILED_TO_FAVORITE(false, 2004, "이미 찜했습니다."),
    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),
    EMPTY_NAVER_ACCESS_TOKEN(false, 2011, "NAVER-ACCESS-TOKEN을 입력해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    FAILED_TO_SIGNUP(false, 2018, "이미 존재하는 회원입니다."),
    POST_USERS_INVALID_PASSWORD(false, 2037, "비밀번호 형식을 확인해주세요."),
    POST_USERS_INVALID_NAME(false, 2038, "이름 길이를 확인해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 2039, "비밀번호를 입력해주세요."),
    POST_USERS_EMPTY_NAME(false, 2040, "이름을 입력해주세요."),
    POST_USERS_EXISTS_PASSWORD(false, 2041, "중복되는 비밀번호입니다."),
    POST_USERS_EXISTS_NAME(false, 2042, "중복되는 이름입니다."),
    INVALID_USER_LOGIN(false, 2043, "이메일이 잘못되었거나 해당 이메일의 회원은 탈퇴한 상태입니다."),
    INVALID_FOLLOWEE_USER(false, 2045, "팔로우 회원의 Index가 유효하지 않습니다."),
    INVALID_REVIEWEE_USER(false, 2046, "리뷰 대상 회원의 Index가 유효하지 않습니다."),
    INVALID_RATE(false, 2047, "평점을 0~5의 수로 입력해주세요."),
    POST_REVIEW_INVALID_CONTENTS(false, 2048, "후기의 길이를 확인해주세요."),
    DELETED_FOLLOW(false, 2049, "이미 언팔로우되었습니다."),
    PRODUCT_USER_MISS_MATCH(false, 2050, "후기 대상 회원이 올린 상품이 아닙니다."),

    // /products
    INVALID_PRODUCT(false, 2019, "유효하지 않은 상품 Index 입니다."),
    EMPTY_PRODUCT_IDX(false, 2020, "상품 Index를 입력해주세요."),
    DELETED_FAVORITE(false, 2021, "이미 찜 해제한 항목입니다."),
    POST_PRD_INVALID_TITLE(false, 2022, "제목의 길이를 확인주세요.(제목은 2~40자를 입력해주세요.)"),
    EMPTY_PRODUCT_TITLE(false, 2023, "제목을 입력해주세요."),
    POST_PRD_INVALID_CONTENTS(false, 2024, "내용의 길이를 확인주세요.(내용은 10~2000자를 입력해주세요.)"),
    EMPTY_PRODUCT_CONTENTS(false, 2025, "내용을 입력해주세요."),
    EMPTY_PRODUCT_PRICE(false, 2026, "가격을 입력해주세요."),
    POST_PRD_INVALID_IMAGE(false, 2027, "이미지의 개수를 확인해주세요.(이미지를 12개 이하로 등록해주세요.)"),
    EMPTY_PRODUCT_IMAGE(false, 2028, "이미지를 입력해주세요."),
    EMPTY_PRODUCT_SAFEPAY(false, 2029, "안전결제 여부를 입력해주세요."),
    EMPTY_PRODUCT_USER(false, 2030, "회원 Index를 입력해주세요."),
    INVALID_USER(false, 2031, "유효하지 않은 회원 Index입니다."),
    EMPTY_REQUEST(false, 2032, "BODY에 값이 없습니다."),
    EMPTY_PRODUCT_TRADE_STATUS(false, 2033, "상품 판매 상태를 입력해주세요"),
    INVALID_TRADE_STATUS(false, 2034, "상품 판매 상태 형식을 확인해주세요."),
    POST_PRD_INVALID_STATUS(false, 2044, "상품 상태 형식을 확인해주세요."),


    // /categories
    INVALID_CATEGORY(false, 2035,"유효하지 않은 카테고리 Index 입니다."),
    INVALID_CATEGORY_LENGTH(false, 2036, "카테고리 Index의 형식을 확인해주세요."),
    INVALID_MAIN_CATEGORY(false, 2051, "유효하지 않은 대분류 카테고리 Index 입니다."),
    INVALID_MIDDLE_CATEGORY(false, 2052, "유효하지 않은 중분류 카테고리 Index 입니다."),
    INVALID_SUB_CATEGORY(false, 2053, "유효하지 않은 소분류 카테고리 Index 입니다."),
    MAIN_CTG_MIDDLE_CTG_MISS_MATCH(false, 2054, "해당 대분류 카테고리에 속하는 중분류 카테고리가 아닙니다."),
    MIDDLE_CTG_SUB_CTG_MISS_MATCH(false, 2055, "해당 중분류 카테고리에 속하는 소분류 카테고리가 아닙니다."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_SOCIAL_LOGIN(false,3014,"존재하지 않은 소셜 계정입니다."),
    FAILED_TO_LOGIN(false,3015,"비밀번호가 틀렸습니다."),





    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"회원 이름 수정에 실패했습니다."),
    MODIFY_FAIL_USER_STATUS(false, 4015, "회원 계정 상태 변경에 실패했습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),
    MODIFY_FAIL_INTRODUCE(false, 4016, "회원 소개글 수정에 실패했습니다."),
    MODIFY_DELETE_USER_FAIL(false, 4017, "회원 탈퇴에 실패했습니다."),
    MODIFY_FAIL_ACTIVE_FOLLOW(false, 4018, "다시 찜하기에 실패했습니다."),
    MODIFY_FAIL_DELETE_FOLLOW(false, 4019, "찜 해제에 실패했습니다."),
    MODIFY_UNFOLLOW_FAIL(false, 4020, "언팔로우에 실패했습니다."),

    // /products
    MODIFY_FAVORITE_FAIL(false, 4013, "찜 수정에 실패했습니다."),
    MODIFY_PRODUCT_FAIL(false, 4014, "상품 수정에 실패했습니다."),
    DELETE_PRODUCT_FAIL(false, 4015, "상품 수정에 실패했습니다."),





    ;


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
