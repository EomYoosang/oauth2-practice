# Todo List (PRD 기반)

## 1단계: 이메일/비밀번호 로그인 및 토큰 인프라
- [ ] [#11](https://github.com/EomYoosang/oauth2-practice/issues/11) 회원가입 플로우 구현 (이메일 중복 검사, BCrypt 비밀번호 해시)
- [ ] [#12](https://github.com/EomYoosang/oauth2-practice/issues/12) 이메일 검증 토큰 발송 및 검증 완료 후 로그인 허용 로직 완료
- [ ] [#13](https://github.com/EomYoosang/oauth2-practice/issues/13) `/login` 화면에서 이메일/비밀번호 UI + 소셜 버튼 배치 기본 골격 구성
- [ ] [#14](https://github.com/EomYoosang/oauth2-practice/issues/14) 로그인 실패 5회 잠금 로직 및 Redis 기반 잠금 상태 저장 구현
- [ ] [#15](https://github.com/EomYoosang/oauth2-practice/issues/15) JWT 액세스 토큰 발급 로직 작성 (만료 15분, jti 포함)
- [ ] [#16](https://github.com/EomYoosang/oauth2-practice/issues/16) 리프레시 토큰 발급 및 Redis 저장 (UUID, 사용자/제공자/만료 메타데이터, TTL)
- [ ] [#17](https://github.com/EomYoosang/oauth2-practice/issues/17) 로그아웃/비밀번호 변경 시 리프레시 토큰 폐기 & 액세스 토큰 블랙리스트 처리
- [ ] [#18](https://github.com/EomYoosang/oauth2-practice/issues/18) 이메일 로그인 성공/실패 E2E 테스트 및 토큰 재발급 테스트 작성
- [ ] [#19](https://github.com/EomYoosang/oauth2-practice/issues/19) Redis 장애 시 MySQL 8 폴백 토큰 저장소 전환 및 운영 알림 트리거 로직 정의
- [ ] [#20](https://github.com/EomYoosang/oauth2-practice/issues/20) RabbitMQ `integration.events` 익스체인지 및 DLQ(`integration.events.dlq`) 기본 구성
- [ ] [#21](https://github.com/EomYoosang/oauth2-practice/issues/21) Jenkins에서 Redis 폴백/복구 배치 잡 실행 템플릿 작성

## 2단계: 구글 OAuth2 로그인
- [ ] [#22](https://github.com/EomYoosang/oauth2-practice/issues/22) Spring Security OAuth2 Client에 구글 제공자 등록 및 PKCE 플로우 적용
- [ ] [#23](https://github.com/EomYoosang/oauth2-practice/issues/23) `openid email profile` 스코프 기반 프로필 정규화 -> `User` + `GoogleAccount` 매핑
- [ ] [#24](https://github.com/EomYoosang/oauth2-practice/issues/24) `GOOGLE_ALLOWED_DOMAIN` 환경 변수 필터링 로직 및 예외 메시지 처리
- [ ] [#25](https://github.com/EomYoosang/oauth2-practice/issues/25) 최초/재로그인 시 프로필 갱신 & 로컬 설정 유지 시나리오 테스트
- [ ] [#26](https://github.com/EomYoosang/oauth2-practice/issues/26) 구글 로그인 토큰 발급/갱신 자동화 테스트 추가

## 3단계: 애플 OAuth2 로그인
- [ ] [#27](https://github.com/EomYoosang/oauth2-practice/issues/27) 애플 클라이언트 시크릿 JWT 자동 생성 및 5분 주기 갱신 배치 구현
- [ ] [#28](https://github.com/EomYoosang/oauth2-practice/issues/28) 애플 인가 코드 플로우 처리 및 `name`, `email` 스코프 수집 -> `AppleAccount` 매핑
- [ ] [#29](https://github.com/EomYoosang/oauth2-practice/issues/29) 최초 로그인 시 full name 저장, 재로그인 시 프로필 업데이트 로직 준비
- [ ] [#30](https://github.com/EomYoosang/oauth2-practice/issues/30) Sign in with Apple JS 위젯 지원 여부 결정 및 통합 가이드 문서화
- [ ] [#31](https://github.com/EomYoosang/oauth2-practice/issues/31) 애플 로그인/토큰 재발급 종단 테스트 작성

## 4단계: 카카오 OAuth2 로그인 및 계정 통합 UX
- [ ] [#32](https://github.com/EomYoosang/oauth2-practice/issues/32) 카카오 REST API 키 기반 인가 코드 플로우 구현 (`account_email`, `profile_nickname` 스코프)
- [ ] [#33](https://github.com/EomYoosang/oauth2-practice/issues/33) 이메일 미제공 시 사용자 입력 요청 UX 및 검증 흐름 추가
- [ ] [#34](https://github.com/EomYoosang/oauth2-practice/issues/34) 카카오 프로필 이미지 -> 내부 아바타 URL 매핑 처리
- [ ] [#35](https://github.com/EomYoosang/oauth2-practice/issues/35) 동일 이메일 소셜 계정 감지 시 계정 통합 플로우, 실패 시 `ACCOUNT_LINK_FAILED` 처리
- [ ] [#36](https://github.com/EomYoosang/oauth2-practice/issues/36) 계정 통합 UX (안내 모달, 오류 메시지, 재시도 옵션) 구현 및 테스트

## 도메인 모델 및 보안 공통
- [ ] [#37](https://github.com/EomYoosang/oauth2-practice/issues/37) `User`, `EmailAccount`, `AppleAccount`, `GoogleAccount`, `KakaoAccount` 엔티티 및 연관관계 설계
- [ ] [#38](https://github.com/EomYoosang/oauth2-practice/issues/38) 제공자별 계정 유일성 보장 및 데이터 정합성 트랜잭션 처리
- [ ] [#39](https://github.com/EomYoosang/oauth2-practice/issues/39) JWT/리프레시 토큰 키 관리 전략 문서화 (HMAC vs RSA, 키 순환 절차)
- [ ] [#40](https://github.com/EomYoosang/oauth2-practice/issues/40) OAuth 제공자 자격 증명 환경 변수 관리 및 비밀값 검증 로직 추가
- [ ] [#41](https://github.com/EomYoosang/oauth2-practice/issues/41) state/nonce, JWT 서명 검증 실패 로깅 및 경고 알람 구성

## 모니터링, 분석, 비기능 요구사항
- [ ] [#42](https://github.com/EomYoosang/oauth2-practice/issues/42) Micrometer 계측 추가 (`oauth2.login.success/failure`, `auth.refresh.success`, `account.link.failure` 등)
- [ ] [#43](https://github.com/EomYoosang/oauth2-practice/issues/43) `/actuator/metrics` 노출 항목 대시보드 설계 및 경보 조건 정의
- [ ] [#44](https://github.com/EomYoosang/oauth2-practice/issues/44) 인증 API 95퍼센타일 3초 이내 목표 충족 위한 성능 테스트 계획
- [ ] [#45](https://github.com/EomYoosang/oauth2-practice/issues/45) 가용성 99.5% 목표 대비 운영 점검 및 장애 대응 Runbook 작성
- [ ] [#46](https://github.com/EomYoosang/oauth2-practice/issues/46) Redis 장애/네트워크 분리 시 MySQL 8 폴백 저장소 및 복구 동기화 배치 전략 확정
- [ ] [#47](https://github.com/EomYoosang/oauth2-practice/issues/47) DR 리허설(월 1회) 시나리오 정의 및 체크리스트 작성

## 운영 자동화 및 알림
- [ ] [#48](https://github.com/EomYoosang/oauth2-practice/issues/48) Resilience4j 기반 자동 통합 실패 재시도 정책(지수형 백오프 3회, 서킷브레이커) 구현
- [ ] [#49](https://github.com/EomYoosang/oauth2-practice/issues/49) RabbitMQ DLQ 적체 시 Jenkins 재처리 잡 트리거 로직 구성
- [ ] [#50](https://github.com/EomYoosang/oauth2-practice/issues/50) Slack Webhook 및 고객센터 티켓 연동으로 실패 알림 파이프라인 구성
- [ ] [#51](https://github.com/EomYoosang/oauth2-practice/issues/51) 운영 Runbook에 수동 계정 통합 절차, 사용자 공지 템플릿 문서화
- [ ] [#52](https://github.com/EomYoosang/oauth2-practice/issues/52) Jenkins 기반 CI/CD 파이프라인 설계 및 배포 스크립트 정리

## 미결 사항 및 위험 대응
- [ ] [#53](https://github.com/EomYoosang/oauth2-practice/issues/53) Resilience4j 재시도·RabbitMQ DLQ·Slack 알림 기반 계정 통합 실패 대응 프로세스 문서화
- [ ] [#54](https://github.com/EomYoosang/oauth2-practice/issues/54) `MailGateway` 추상화 및 `NaverSmtpClient` 초기 구현, 백업 공급자 스위치 전략 정리
- [ ] [#55](https://github.com/EomYoosang/oauth2-practice/issues/55) SMTP 발송 SLA(99%/5분) 모니터링·알림 규칙 정의
- [ ] [#56](https://github.com/EomYoosang/oauth2-practice/issues/56) 애플 Private Key 보관/로테이션을 온프레미스 시크릿 관리 + Jenkins 스케줄로 자동화하는 전략 확정
- [ ] [#57](https://github.com/EomYoosang/oauth2-practice/issues/57) 키 로테이션 Runbook 및 반기 모의훈련 계획 수립
- [ ] [#58](https://github.com/EomYoosang/oauth2-practice/issues/58) JWT 서명 키 회전 주기와 Jenkins 기반 자동화 방법 결정

## 출시 준비
- [ ] [#59](https://github.com/EomYoosang/oauth2-practice/issues/59) 단계별 기능 점검 체크리스트 작성 및 릴리스 게이트 설정
- [ ] [#60](https://github.com/EomYoosang/oauth2-practice/issues/60) 보안 리뷰, 부하 테스트, 프로덕션 배포 절차 완료
- [ ] [#61](https://github.com/EomYoosang/oauth2-practice/issues/61) 문서화 (개발자 가이드, 운영 Runbook, 사용자 FAQ) 정리
