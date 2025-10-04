## **1. 예외 처리 아키텍처 설계**

### **1.1 예외 처리 계층 구조**

```
┌─────────────────────────────────────┐
│     GlobalExceptionHandler          │  ← @RestControllerAdvice
├─────────────────────────────────────┤
│       BaseException                 │  ← 모든 커스텀 예외의 부모
├─────────────────────────────────────┤
│   Domain Exceptions                 │  ← MemberException, PostException
├─────────────────────────────────────┤
│   Exception Messages                │  ← Enum으로 메시지 관리
└─────────────────────────────────────┘

```

### **1.2 예외 처리 흐름**

```
Client Request → Controller → Service → Repository
                     ↓            ↓          ↓
                  Exception → Exception → Exception
                     ↓            ↓          ↓
              GlobalExceptionHandler (AOP)
                     ↓
              ErrorResponse (JSON)

```

---

## **2. 단계별 구현 가이드**

### **Step 1: BaseException 설계**

두 가지 주요 접근 방식이 있습니다:

### **방식 A: 중앙 집중식 (vtopia 스타일)**

```java
// 모든 예외를 하나의 enum으로 관리
public class BaseException extends RuntimeException {
    private final String type;
    private final HttpStatus status;

    public BaseException(ExceptionMessage message) {
        super(message.getMessage());
        this.type = message.name();
        this.status = message.getStatus();
    }
}

```

### **방식 B: 도메인 분리식 (현재 프로젝트 스타일)**

```java
// 도메인별로 예외 메시지 분리
@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    protected BaseException(HttpStatus httpStatus, String errorCode, String errorMessage) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}

```

### **Step 2: 예외 메시지 관리**

### **통합 관리 방식**

```java
@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    // 인증/인가
    UNAUTHORIZED_ACCESS("인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 사용자
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_DUPLICATE("이미 존재하는 사용자입니다.", HttpStatus.BAD_REQUEST),

    // 게시글
    POST_NOT_FOUND("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}

```

### **도메인별 분리 방식**

```java
@Getter
@RequiredArgsConstructor
public enum MemberExceptionMessage {
    MEMBER_NOT_FOUND("MBR001", "존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_LOGIN_ID("MBR002", "이미 존재하는 로그인 ID입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("MBR003", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}

```

### **Step 3: 도메인 예외 클래스 구현**

### **심플 버전**

```java
public class UserException extends BaseException {
    public UserException(ExceptionMessage message) {
        super(message);
    }
}

```

### **확장 버전 (권장)**

```java
public class MemberException extends BaseException {

    // 기본 생성자
    public MemberException(MemberExceptionMessage exceptionMessage) {
        super(exceptionMessage.getHttpStatus(),
              exceptionMessage.getErrorCode(),
              exceptionMessage.getMessage());
    }

    // 동적 메시지 지원
    public MemberException(MemberExceptionMessage exceptionMessage, Object... args) {
        super(exceptionMessage.getHttpStatus(),
              exceptionMessage.getErrorCode(),
              String.format(exceptionMessage.getMessage(), args));
    }

    // 정적 팩토리 메서드 (타입 안정성 + 가독성)
    public static MemberException notFound(Long memberId) {
        return new MemberException(
            MemberExceptionMessage.MEMBER_NOT_FOUND,
            memberId
        );
    }

    public static MemberException duplicateLoginId(String loginId) {
        return new MemberException(
            MemberExceptionMessage.DUPLICATE_LOGIN_ID,
            loginId
        );
    }
}

```


### **Step 4: ErrorResponse DTO 구현**

```java
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<FieldError> errors;

    // Validation 에러용 내부 클래스
    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final Object value;
        private final String reason;
    }

    // 일반 에러 응답 생성
    public static ErrorResponse of(BaseException e, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(e.getHttpStatus().value())
                .error(e.getHttpStatus().getReasonPhrase())
                .code(e.getErrorCode())
                .message(e.getErrorMessage())
                .path(path)
                .build();
    }

    // Validation 에러 응답 생성
    public static ErrorResponse of(BindingResult bindingResult, String path) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(error -> FieldError.builder()
                        .field(error.getField())
                        .value(error.getRejectedValue())
                        .reason(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .code("VALIDATION_ERROR")
                .message("입력값 검증에 실패했습니다.")
                .path(path)
                .errors(fieldErrors)
                .build();
    }
}

```

---

## **3. 실무 예외 처리 전략 비교**

### **🔵 방식 A: 중앙 집중식 (vtopia 스타일)**

### **장점**

- ✅**단순성**: 모든 예외를 한 곳에서 관리
- ✅**빠른 개발**: 새 예외 추가가 매우 간단
- ✅**일관성**: 모든 예외가 동일한 구조
- ✅**검색 용이**: 한 파일에서 모든 예외 확인 가능

### **단점**

- ❌**확장성 부족**: enum이 거대해짐
- ❌**팀 협업 어려움**: 하나의 파일을 여러 팀이 수정
- ❌**도메인 경계 모호**: 모든 도메인 예외가 섞임
- ❌**동적 메시지 제한**: enum 값만 사용 가능

### **적합한 경우**

```
✓ 스타트업 MVP
✓ 소규모 프로젝트 (도메인 5개 이하)
✓ 개발자 1-3명
✓ 빠른 프로토타이핑

```

### **🔴 방식 B: 도메인 분리식 (현재 프로젝트 스타일)**

### **장점**

- ✅**확장성**: 도메인별 독립적 관리
- ✅**팀 협업**: 도메인별로 담당 팀 분리 가능
- ✅**유연성**: 동적 메시지, 정적 팩토리 메서드
- ✅**MSA 준비**: 향후 마이크로서비스 분리 용이
- ✅**타입 안정성**: 컴파일 타임 체크

### **단점**

- ❌**복잡성**: 초기 설정이 복잡
- ❌**파일 수 증가**: 도메인별 예외 클래스 필요
- ❌**중복 가능성**: 비슷한 예외가 여러 도메인에 존재

### **적합한 경우**

```
✓ 중대형 프로젝트
✓ 도메인 5개 이상
✓ 여러 팀 협업
✓ 장기적 유지보수 중요
✓ MSA 전환 예정

```


---

## **4. GlobalExceptionHandler 구현**

### **완성된 GlobalExceptionHandler**

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 커스텀 비즈니스 예외 처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException e,
            HttpServletRequest request) {

        log.error("Business Exception: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.of(e, request.getRequestURI());
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    // 2. Validation 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        log.error("Validation Exception: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.of(
            e.getBindingResult(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 3. 타입 불일치 예외
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e,
            HttpServletRequest request) {

        log.error("Message Not Readable: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .code("INVALID_INPUT_FORMAT")
                .message("요청 본문 형식이 올바르지 않습니다.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 4. 지원하지 않는 HTTP 메서드
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request) {

        log.error("Method Not Supported: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .code("METHOD_NOT_ALLOWED")
                .message(String.format("%s 메서드는 지원되지 않습니다.", e.getMethod()))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    // 5. 리소스 없음
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException e,
            HttpServletRequest request) {

        log.error("No Handler Found: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .code("RESOURCE_NOT_FOUND")
                .message("요청하신 리소스를 찾을 수 없습니다.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 6. 인증 실패
    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleSecurityException(
            Exception e,
            HttpServletRequest request) {

        log.error("Security Exception: {}", e.getMessage());

        HttpStatus status = (e instanceof AuthenticationException)
            ? HttpStatus.UNAUTHORIZED
            : HttpStatus.FORBIDDEN;

        String code = (e instanceof AuthenticationException)
            ? "AUTHENTICATION_FAILED"
            : "ACCESS_DENIED";

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    // 7. 데이터베이스 관련 예외
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException e,
            HttpServletRequest request) {

        log.error("Data Integrity Violation: {}", e.getMessage());

        String message = "데이터 무결성 제약 조건을 위반했습니다.";

        // 중복 키 예외 처리
        if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
            if (cve.getConstraintName() != null && cve.getConstraintName().contains("UNIQUE")) {
                message = "이미 존재하는 데이터입니다.";
            }
        }

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .code("DATA_INTEGRITY_VIOLATION")
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 8. 일반 예외 처리 (최후의 방어선)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception e,
            HttpServletRequest request) {

        log.error("Unexpected Exception: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .code("INTERNAL_SERVER_ERROR")
                .message("서버 내부 오류가 발생했습니다.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

```


---

## **5. 실전 활용 및 베스트 프랙티스**

### **5.1 서비스 계층에서의 예외 처리**

```java
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberResponse findById(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> MemberException.notFound(memberId));

        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse create(MemberCreateRequest request) {
        // 중복 체크
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw MemberException.duplicateLoginId(request.getLoginId());
        }

        Member member = memberRepository.save(request.toEntity());
        return MemberResponse.from(member);
    }
}

```


### **5.2 컨트롤러에서의 활용**

```java
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> findById(@PathVariable Long id) {
        // 예외는 GlobalExceptionHandler가 처리
        return ResponseEntity.ok(memberService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> create(
            @Valid @RequestBody MemberCreateRequest request) {
        // @Valid로 발생한 예외도 GlobalExceptionHandler가 처리
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(memberService.create(request));
    }
}

```

### **5.3 로깅 전략**

```java
@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    @AfterThrowing(
        pointcut = "@within(org.springframework.stereotype.Service)",
        throwing = "exception"
    )
    public void logServiceException(JoinPoint joinPoint, Exception exception) {
        if (exception instanceof BaseException) {
            // 비즈니스 예외는 WARN 레벨
            log.warn("Business exception in {}.{}: {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception.getMessage());
        } else {
            // 시스템 예외는 ERROR 레벨
            log.error("System exception in {}.{}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception);
        }
    }
}

```

### **5.4 테스트 코드 작성**

```java
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("존재하지 않는 회원 조회시 404 응답")
    void findById_NotFound() throws Exception {
        // given
        Long memberId = 999L;
        given(memberService.findById(memberId))
            .willThrow(MemberException.notFound(memberId));

        // when & then
        mockMvc.perform(get("/api/members/{id}", memberId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("MBR001"))
            .andExpect(jsonPath("$.message").value(containsString("존재하지 않는 회원")))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.path").value("/api/members/" + memberId));
    }

    @Test
    @DisplayName("유효하지 않은 입력값으로 회원 생성시 400 응답")
    void create_ValidationFailed() throws Exception {
        // given
        String invalidRequest = """
            {
                "loginId": "",
                "password": "123",
                "name": ""
            }
            """;

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[?(@.field == 'loginId')]").exists())
            .andExpect(jsonPath("$.errors[?(@.field == 'password')]").exists());
    }
}

```

---

## **📊 의사결정 플로우차트**

```
프로젝트 시작
    ↓
도메인 개수가 5개 이상?
    ├─ Yes → 도메인 분리식 (방식 B)
    └─ No
        ↓
    개발자가 3명 이상?
        ├─ Yes → 도메인 분리식 (방식 B)
        └─ No
            ↓
        MSA 전환 계획?
            ├─ Yes → 도메인 분리식 (방식 B)
            └─ No → 중앙 집중식 (방식 A)

```

---

## **🎯 실무 체크리스트**

### **프로젝트 초기**

- [ ]  BaseException 구조 결정
- [ ]  ErrorResponse 형식 정의
- [ ]  GlobalExceptionHandler 기본 구현
- [ ]  로깅 전략 수립

### **개발 중**

- [ ]  도메인별 예외 클래스 생성
- [ ]  예외 메시지 enum 관리
- [ ]  정적 팩토리 메서드 추가
- [ ]  단위 테스트 작성

### **운영 단계**

- [ ]  예외 모니터링 설정
- [ ]  알림 규칙 구성
- [ ]  에러 대시보드 구축
- [ ]  정기적인 에러 로그 분석

---

## **💡 Pro Tips**

1. **예외는 예외적인 상황에만**: 정상 흐름 제어용으로 예외를 사용하지 마세요
2. **구체적인 예외 메시지**: "오류 발생" 보다는 "회원 ID 123을 찾을 수 없습니다"
3. **민감 정보 노출 금지**: 에러 메시지에 비밀번호, 개인정보 포함 금지
4. **일관된 에러 코드**: 프론트엔드와 협의한 에러 코드 체계 유지
5. **모니터링 연동**: Sentry, DataDog 등과 연동하여 실시간 모니터링