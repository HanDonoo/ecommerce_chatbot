package com.small.ecommerce_chatbot.response;

public class Response <T> {

    private int code; // 状态码
    private String message; // 提示信息
    private T data; // 泛型数据

    public Response() {
    }

    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    // 静态方法创建成功响应
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "Success", data);
    }

    // 静态方法创建失败响应
    public static <T> Response<T> failure(int code, String message) {
        return new Response<>(code, message, null);
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
