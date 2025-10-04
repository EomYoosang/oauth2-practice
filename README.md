# oauth2-practice
OAuth2 예제코드 작성 (with Codex)

## Git Workflow

### Branch Strategy
- `main`: 항상 배포 가능한 상태를 유지하는 보호 브랜치입니다.
- `develop`: 기능 개발이 통합되는 기본 작업 브랜치입니다. 스프린트 종료 시 `main`으로 머지하고 태그를 생성합니다.
- `feature/<issue-id>-<short-name>`: 단일 기능이나 수정 작업마다 분기합니다. 작업 완료 후 `develop`으로 PR을 생성합니다.
- `release/<version>`: 배포 준비가 필요할 때 `develop`에서 분기해 QA를 진행하고, 검증 후 `main`과 `develop`에 모두 머지합니다.
- `hotfix/<issue-id>-<short-name>`: 프로덕션 긴급 수정 시 `main`에서 분기해 수정 후 `main`과 `develop`에 모두 머지합니다.

### Pull Request 규칙
- 모든 PR은 최소 1명의 리뷰 승인을 받아야 하며, CI(Jenkins) 빌드가 성공해야 합니다.
- PR 제목은 작업 범위를 설명하는 명령형 문장으로 작성합니다.
- 본문에 관련 이슈 번호(`Closes #123`)와 테스트/검증 결과를 명시합니다.

## Commit Convention

### 메시지 형식 (Conventional Commits)
```
<type>(<scope>): <subject>

<body>

<footer>
```

- `type`: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `build`, `ci`, `chore`, `revert` 중 하나를 사용합니다.
- `scope`: 변경 대상 모듈/패키지를 짧게 기재하며 생략 가능합니다.
- `subject`: 50자 이내 명령형 문장, 첫 글자 소문자, 마침표 미사용.
- `body`: 필요한 경우 변경 이유나 상세 내용을 추가합니다. 한 줄 공백 후 작성합니다.
- `footer`: 이슈 연관(`Closes #123`), 브레이킹 체인지(`BREAKING CHANGE: ...`) 등을 기술합니다.

### 작성 규칙
- 한 커밋에는 하나의 목적만 담고, 불필요한 리포매팅 커밋을 피합니다.
- 커밋 전에 `./gradlew test` 등 관련 검증을 수행하고 필요한 경우 결과를 PR 본문에 공유합니다.
- 브랜치 리베이스가 필요한 경우 `develop` 최신 커밋 위로 정리한 뒤 푸시합니다.

## API 명세

### AuthController (`/api/auth`)

#### POST `/api/auth/signup`
- **설명**: 이메일과 비밀번호로 신규 사용자를 등록합니다. 이메일은 고유해야 하며 비밀번호는 BCrypt로 암호화됩니다.
- **요청 본문 (JSON)**

| 필드 | 타입 | 필수 | 제약 | 설명 |
| ---- | ---- | ---- | ---- | ---- |
| `email` | string | Y | RFC 5322 형식 / 최대 320자 | 사용자 로그인용 이메일 |
| `password` | string | Y | 8~64자 | 원문 비밀번호 (서버에서 암호화) |

- **성공 응답 (201 Created)**

```json
{
  "id": 1,
  "email": "user@example.com",
  "emailVerified": false,
  "displayName": "user",
  "role": "USER",
  "createdAt": "2025-10-04T07:30:12.345678"
}
```

- `emailVerified`: 이메일 검증 여부(회원가입 직후에는 `false`).
- `displayName`: 기본 표시 이름(이메일 로컬 파트에서 추출, 추후 프로필 편집으로 변경 가능).
- `role`: 부여된 기본 역할(`USER`).

- **오류 코드**

| HTTP 상태 | 코드 | 설명 |
| --------- | ---- | ---- |
| 400 | `VALIDATION_ERROR` | 요청 본문이 유효성 검증을 통과하지 못한 경우 |
| 409 | `USR002` | 이미 등록된 이메일일 때 |
| 500 | `INTERNAL_SERVER_ERROR` | 예상치 못한 서버 오류 |

#### 테스트 예제

1. **정상 요청**

```bash
curl -i \
  -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
        "email": "tester1@example.com",
        "password": "Passw0rd!"
      }'
```

2. **중복 이메일 확인** – 위 요청을 한 번 더 실행하면 409 Conflict와 함께 `USR002` 오류가 반환됩니다.

```bash
curl -i \
  -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
        "email": "tester1@example.com",
        "password": "AnotherPass1!"
      }'
```

3. **검증 실패 예시** – 잘못된 이메일 형식이나 짧은 비밀번호로 요청합니다.

```bash
curl -i \
  -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
        "email": "invalid-email",
        "password": "123"
      }'
```

### 실행 및 자동화 테스트
- 애플리케이션 실행: `./gradlew bootRun`
- 단위/슬라이스 테스트: `./gradlew test`
