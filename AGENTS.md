# 저장소 가이드라인

## 프로젝트 구조 및 모듈 구성
- 백엔드 소스는 `src/main/java/com/yoosang/oauth2_practice`에 있으며, 신규 패키지는 Spring 컴포넌트 기준(`controller`, `service`, `repository`)으로 정리합니다.
- 웹 자산과 뷰 템플릿은 각각 `src/main/resources/static`, `src/main/resources/templates`에 두고, 설정 파일은 `src/main/resources`에 유지합니다.
- 테스트 코드는 `src/test/java`에서 본 코드와 동일한 패키지 경로로 구성하여 대응하는 클래스를 명확히 합니다.

## 빌드·테스트·개발 명령어
- `./gradlew bootRun`으로 Spring Boot OAuth2 예제를 로컬에서 구동하며 핫 리로드를 활용합니다.
- `./gradlew build`는 컴파일, 단위 테스트 실행 후 실행 가능한 JAR을 `build/libs`에 생성합니다.
- `./gradlew test`는 JUnit 5 테스트 스위트를 실행합니다. 장애 분석 시 `--info` 옵션을 추가합니다.

## 코딩 스타일 및 네이밍 규칙
- Java 17 표준과 4칸 공백 들여쓰기를 사용하고, Spring 관례에 맞춰 괄호를 배치합니다.
- 클래스는 PascalCase(`UserOAuthClient`), 메서드는 camelCase, 상수는 UPPER_SNAKE_CASE로 명명합니다.
- 반복 코드를 줄이기 위해 활성화된 Lombok(`@Getter`, `@RequiredArgsConstructor`)을 활용하되, 비직관적인 사용은 클래스 주석으로 설명합니다.
- `application.properties` 속성 키는 케밥 케이스(`spring.security.oauth2.client-id`)로 작성합니다.

## 테스트 가이드라인
- 테스트는 JUnit 5, AssertJ 또는 Spring 테스트 유틸을 사용해 `src/test/java`에 작성하며, 대상 클래스와 동일한 패키지 구조를 유지합니다.
- 테스트 클래스명은 `<ClassName>Tests`, 메서드는 `methodName_shouldExpectedBehavior` 패턴을 따릅니다.
- `@SpringBootTest`는 필요한 경우만 사용하고, 빠른 피드백을 위해 `@WebMvcTest`, `@DataJpaTest` 등 슬라이스 테스트를 우선 적용합니다. 푸시 전 `./gradlew test` 실행을 필수로 합니다.

## 커밋 및 PR 가이드라인
- Git 히스토리와 동일하게 짧은 명령형 요약을 사용합니다(예: `Add GitHub OAuth login flow`).
- 커밋은 관련 변경만 묶고, 동작에 영향이 있을 경우 테스트나 설정 변경을 함께 포함합니다.
- PR에는 변경 요약, 검증 절차(`./gradlew test` 결과), 관련 이슈·티켓을 명시합니다.

## 보안 및 설정 팁
- 비밀 값은 환경 변수 또는 버전 관리에서 제외된 `application-local.properties`에 보관하고, 자격 증명은 커밋하지 않습니다.
- 푸시 전 OAuth2 클라이언트 등록 정보를 검토해 리다이렉트 URI와 스코프가 의도와 일치하는지 확인합니다.
