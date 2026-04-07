# FutureFinder

청년을 위한 금융·취업 정보 통합 플랫폼 백엔드 API 서버

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.5 |
| Build | Gradle (멀티모듈) |
| Database | MySQL 8 (AWS RDS) |
| ORM | Spring Data JPA / Hibernate |
| Auth | JWT (JJWT 0.11.2) + Spring Security |
| Storage | AWS S3 |
| HTTP Client | Spring WebFlux (WebClient) |
| API Docs | springdoc-openapi 2.8.12 (Swagger UI) |
| AI | OpenAI API (gpt-3.5-turbo) |

## 모듈 구조

```
futurefinder/
├── futurefinder-api        # REST API, 컨트롤러, 보안 설정, Swagger
├── futurefinder-domain     # 비즈니스 로직, 서비스, Facade
├── futurefinder-external   # 외부 API 연동 클라이언트
├── futurefinder-storage    # JPA 엔티티, Repository, DB 설정
└── futurefinder-common     # 공통 에러코드, 응답 포맷
```

## 주요 기능

### 인증 / 사용자
- 회원가입 · 로그인 · 로그아웃
- 카카오 OAuth 소셜 로그인
- JWT Access / Refresh Token 발급 및 갱신
- 비밀번호 찾기 · 변경
- 프로필 조회 · 수정 · 프로필 이미지 업로드 (S3)

### 홈 대시보드
- 경제 뉴스, 주식 등락, 채용 정보를 한 화면에 통합 제공

### 주거 (House)
- 관심 지역 · 현재 거주지 관리
- 청약 계좌 등록 및 입금 내역 관리
- AI 주거 금융 챗봇 (OpenAI 연동)

### 취업 (Job)
- 학력 · 활동 · 수상 이력 관리
- AI 기반 취업 추천

### 경제 정보
- 주식 일일 등락 종목 조회 (KRX 공공데이터)
- 경제 뉴스 조회 (네이버 OpenAPI)
- 경제 용어 사전 검색 (한국은행 ECOS)
- 공공기관 채용 공고 조회 (공공데이터포털)

### 자산 관리
- 자산 등록 · 조회 (예금, 투자 등 유형별)

## 외부 API 연동

| API | 용도 |
|-----|------|
| 카카오 OAuth | 소셜 로그인 |
| 네이버 OpenAPI | 경제 뉴스 검색 |
| 한국은행 ECOS | 경제 통계 · 경제 용어 |
| KRX (공공데이터포털) | 주식 시세 정보 |
| 공공데이터포털 채용 API | 공공기관 채용 공고 |
| OpenAI | AI 챗봇 · 취업 추천 |

## 빌드 및 실행

### 환경 변수

```
DB_USER_NAME, DB_USER_PASSWORD
AWS_ACCESS_KEY, AWS_SECRET_KEY, S3_BUCKET
ECOS_API_KEY
NAVER_CLIENT_ID, NAVER_CLIENT_SECRET
MOEF_RECRUITMENT_SERVICE_KEY
KRX_STOCK_SERVICE_KEY
OPENAI_API_KEY
```

### 빌드

```bash
./gradlew build
```

### 실행

```bash
./gradlew :futurefinder-api:bootRun
```

### API 문서

서버 실행 후 Swagger UI 접속: `http://localhost:8080/swagger-ui.html`

## 프로젝트 구조 상세

```
futurefinder-api
├── controller/       # REST 컨트롤러 (auth, user, home, house, job, stock, news, recruitment, word, asset)
├── config/           # SecurityConfig, SwaggerConfig, WebConfig
├── dto/              # 요청/응답 DTO
└── util/security/    # JWT 필터, 토큰 유틸, @CurrentUser 리졸버

futurefinder-domain
├── service/          # 도메인 서비스
├── facade/           # 복합 비즈니스 로직 (AccountFacade, HomeFacade 등)
├── implementation/   # CQRS 스타일 (Appender, Reader, Updater, Remover)
├── model/            # 도메인 모델, Value Object
├── external/         # 외부 클라이언트 인터페이스
└── repository/       # 저장소 인터페이스

futurefinder-external
├── config/           # 외부 API WebClient 설정
├── external/         # 클라이언트 구현체 (Kakao, Naver, KRX, ECOS, OpenAI 등)
└── property/         # 외부 API 설정 프로퍼티

futurefinder-storage
├── jpaentity/        # JPA 엔티티 (User, House, Job, Asset 등)
├── jparepository/    # Spring Data JPA Repository
└── repository/jpa/   # Repository 구현체

futurefinder-common
├── error/            # ErrorCode, 커스텀 예외
└── response/         # 공통 응답 포맷
```
