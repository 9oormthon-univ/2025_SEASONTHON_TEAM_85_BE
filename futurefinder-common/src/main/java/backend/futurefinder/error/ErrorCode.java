package backend.futurefinder.error;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // AUTH
    NOT_AUTHORIZED("AUTH_1", "인증되지 않았습니다."),


    // Common
    PATH_WRONG("COMMON_1", "잘못된 메세드입니다."), // 코드, 메시지 순
    VARIABLE_WRONG("COMMON_2", "요청 변수가 잘못되었습니다."),
    WRONG_ACCESS("COMMON_3", "잘못된 접근입니다."),
    INTERNAL_SERVER_ERROR("COMMON_4", "Internal Server Error");


    // AI


    // 등등등




    private final String code;
    private final String message;

    private static final Map<String, ErrorCode> ERROR_CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values())
                    .collect(Collectors.toMap(ErrorCode::getMessage, Function.identity())));
    // 맵 만들어 반환하기 위함
    // 예시
//    {
//        "잘못된 요청입니다.": ErrorCode.BAD_REQUEST,
//    }


    public static ErrorCode from(final String message){
        if(ERROR_CODE_MAP.containsKey(message)){
            return ERROR_CODE_MAP.get(message);
        }// 정의된 에러 처리

        return ErrorCode.INTERNAL_SERVER_ERROR; // 그 외는 서버 내부에러로 간주

    }




}
