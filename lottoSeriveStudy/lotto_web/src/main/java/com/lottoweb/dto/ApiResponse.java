package com.lottoweb.dto;

/**
 * API 응답의 공통 구조를 담는 DTO
 * 
 * ⭐ 왜 공통 응답 구조를 만들까요?
 * 1. 일관성: 모든 API 응답이 같은 형식으로 반환됨
 * 2. 에러 처리: 성공/실패 여부를 명확히 표시
 * 3. 확장성: 나중에 메타데이터(응답 시간, 요청 ID 등) 추가 가능
 * 4. 클라이언트 처리: 클라이언트가 일관된 방식으로 응답 처리
 * 
 * 성공 응답 예시:
 * {
 *   "success": true,
 *   "message": "성공",
 *   "data": { ... }
 * }
 * 
 * 실패 응답 예시:
 * {
 *   "success": false,
 *   "message": "에러 메시지",
 *   "data": null
 * }
 */
public class ApiResponse<T> {
    
    private boolean success;    // 성공 여부
    private String message;    // 응답 메시지
    private T data;            // 실제 데이터 (제네릭으로 타입 동적 지정)
    
    // 기본 생성자
    public ApiResponse() {}
    
    // 전체 데이터를 받는 생성자
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // 성공 응답을 쉽게 만들기 위한 정적 메서드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "성공", data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    // 실패 응답을 쉽게 만들기 위한 정적 메서드
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
    
    // Getter와 Setter
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}
