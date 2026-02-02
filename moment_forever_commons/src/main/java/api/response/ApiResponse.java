package api.response;

public class ApiResponse<T> {
    private int code;
    private String status;
    private String msg;
    private T response;

    // Private constructor - only Builder can create instances
    ApiResponse(ApiResponseBuilder<T> builder) {
        this.code = builder.code;
        this.status = builder.status;
        this.msg = builder.msg;
        this.response = builder.response;
    }

    public ApiResponse (final int code, final String status, final String message) {
        this.code = code;
        this.status = status;
        this.msg = message;
    }

    // Remove all other constructors and the created() method

    // Getters (no setters for immutability)
    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getResponse() {
        return response;
    }

    // Static method to get a builder
    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<T>();
    }

    // Builder class
    public static class ApiResponseBuilder<T> {
        private int code;
        private String status;
        private String msg;
        private T response;

        // Private constructor
        private ApiResponseBuilder() {}

        public ApiResponseBuilder<T> setCode(int code) {
            this.code = code;
            return this;
        }

        public ApiResponseBuilder<T> setStatus(String status) {
            this.status = status;
            return this;
        }

        public ApiResponseBuilder<T> setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public ApiResponseBuilder<T> setResponse(T response) {
            this.response = response;
            return this;
        }

        public ApiResponse<T> build() {
            // You can add validation here if needed
            return new ApiResponse<>(this);
        }
    }
}