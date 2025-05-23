package com.eyedia.eyedia.global.error.status;

import com.eyedia.eyedia.global.code.BaseErrorCode;
import com.eyedia.eyedia.global.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),

    // 그림(Painting) 관련
    PAINTING_NOT_FOUND(HttpStatus.NOT_FOUND, "PAINTING404", "그림을 찾을 수 없습니다."),
    INVALID_PAINTING_ID(HttpStatus.BAD_REQUEST, "PAINTING400", "잘못된 그림 ID입니다."),

    // 설명(description) 관련
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTIST404", "작가 정보를 찾을 수 없습니다."),
    BACKGROUND_NOT_FOUND(HttpStatus.NOT_FOUND, "BACKGROUND404", "배경 정보를 찾을 수 없습니다."),
    OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "OBJECT404", "객체 정보를 찾을 수 없습니다."),

    // 파일/입출력 관련
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE500", "파일 업로드 중 오류가 발생했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE404", "요청한 파일을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
