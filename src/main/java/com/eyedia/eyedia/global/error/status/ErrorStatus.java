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
    PAINTING_CONFLICT(HttpStatus.CONFLICT, "PAINTING409", "그림의 결과값이 여러개 입니다."),

    // 설명(description) 관련
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTIST404", "작가 정보를 찾을 수 없습니다."),
    BACKGROUND_NOT_FOUND(HttpStatus.NOT_FOUND, "BACKGROUND404", "배경 정보를 찾을 수 없습니다."),
    OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "OBJECT404", "객체 정보를 찾을 수 없습니다."),

    // 파일/입출력 관련
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE500", "파일 업로드 중 오류가 발생했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE404", "요청한 파일을 찾을 수 없습니다."),

    // 전시 관련
    EXHIBITION_NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBITION404", "전시를 찾을 수 없습니다."),
    INVALID_EXHIBITION_ID(HttpStatus.BAD_REQUEST, "EXHIBITION400", "잘못된 전시 ID입니다."),
    VISIT_RECORD_NOT_FOUND(HttpStatus.BAD_REQUEST, "EXHIBITION402", "사용자가 방문한 적 없습니다."),
    EXHIBITION_NULL_EXCEPTION(HttpStatus.BAD_REQUEST, "EXHIBITION405", "전시 ID값이 null입니다."),

    // viewedSort enum 관련
    INVALID_SORT_FILTER(HttpStatus.BAD_REQUEST, "EXHIBITION403", "유효하지 않은 정렬 기준입니다. RECENT, DATE만 사용 가능"),

    // 사용자 관련
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER404", "해당하는 유저 정보가 없습니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "USER403", "비밀번호가 틀렸습니다."),
    ALREADY_USER_PASSWORD_SAME(HttpStatus.BAD_REQUEST, "USER408", "사용자의 현재 비밀번호와 동일합니다."),
    ALREADY_USER_ID_EXISTS(HttpStatus.BAD_REQUEST, "USER401", "이미 존재하는 아이디입니다."),
    ALREADY_USER_ID_SAME(HttpStatus.BAD_REQUEST, "USER406", "사용자의 아이디와 동일합니다."),
    ALREADY_USER_NAME_EXISTS(HttpStatus.BAD_REQUEST, "USER405", "이미 존재하는 닉네임입니다."),
    ALREADY_USER_NAME_SAME(HttpStatus.BAD_REQUEST, "USER407", "사용자의 닉네임과 동일합니다."),

    // 즐겨찾기 관련
    ALREADY_BOOKMARK_EXISTS(HttpStatus.BAD_REQUEST, "BOOKMARK405", "이미 즐겨찾기되었습니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.BAD_REQUEST, "BOOKMARK404", "즐겨찾기가 되어 있지 않습니다.");



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
    public static ErrorStatus fromCode(String code) {
        for (ErrorStatus status : values()) {
            if (status.name().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
