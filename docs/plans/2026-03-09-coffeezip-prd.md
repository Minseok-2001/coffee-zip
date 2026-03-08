# CoffeeZip PRD — 브루잉 레시피 커뮤니티 앱

## Context

커피 홈브루잉 애호가들이 자신의 레시피를 기록하고 커뮤니티와 공유하는 앱이 필요함.
단순 레시피 저장을 넘어 타이머로 실제 브루잉을 가이드하고, 데일리 노트로 커피 경험을 누적하며,
캘린더로 히스토리를 돌아볼 수 있는 서비스. 초기 유저가 많지 않으므로 서버리스(Lambda)로 시작해 비용 최소화.

---

## 확정된 기술 스택

| 레이어 | 기술 |
|--------|------|
| 프론트엔드 | Next.js (PWA) — Vercel 호스팅 |
| 백엔드 | Quarkus 3.32.2 (Kotlin, GraalVM native) |
| 배포 | AWS Lambda + API Gateway (HTTP API) |
| 데이터베이스 | 기존 RDS MySQL (VPC 직접 연결) |
| 인증 | Google / Kakao OAuth 2.0 + JWT |
| IaC | AWS SAM 또는 CDK |

---

## 핵심 기능 (MVP)

### 1. 인증 (Auth)
- Google / Kakao OAuth 2.0 소셜 로그인
- JWT Access Token + Refresh Token 발급
- 사용자 프로필: userId, provider, email, nickname, profileImage

### 2. 레시피 (Recipe)
- **CRUD**: 레시피 작성 / 수정 / 삭제 / 조회
- **필드**:
  - 이름, 설명
  - 커피 원두 (이름, 원산지, 로스팅 레벨)
  - 그라인더 종류 + 분쇄도
  - 브루 비율 (커피:물 그램)
  - 물 온도 (°C)
  - 수율 목표 (ml)
  - 브루잉 단계: `[{ stepOrder, label, duration(초), waterAmount(ml) }]`
  - 태그 (에스프레소, 핸드드립, 콜드브루 등)
  - isPublic (공개 여부)
- **공개 피드**: 최신순 정렬, 무한 스크롤 (cursor 기반 페이지네이션)
- **좋아요**: 레시피에 좋아요/취소
- **댓글**: 댓글 작성/삭제 (대댓글 없이 flat)

### 3. 브루잉 타이머 (Timer)
- 레시피의 브루잉 단계를 순서대로 카운트다운
- 단계 완료 시 알림 (PWA Notification)
- 타이머 완료 시 **자동으로 오늘 데일리 노트에 로그 추가**
  - 타이머 로그: `{ recipeId, recipeName, startedAt, completedAt }`

### 4. 데일리 노트 (Daily Note)
- 날짜 1개당 노트 1개 (upsert)
- 자유 텍스트 메모
- 사용한 레시피 연결 (선택)
- 타이머 실행 로그 자동 첨부
- 평점 (1~5, 선택)

### 5. 캘린더 (Calendar)
- 월 뷰: 노트가 있는 날짜 표시
- 날짜 클릭 → 해당 날의 노트 + 타이머 로그 확인

---

## DB 스키마 (MySQL)

```sql
-- 사용자
users (id, provider, providerId, email, nickname, profileImage, createdAt)

-- 레시피
recipes (id, userId, title, description, coffeeBean, grinder, grindSize,
         ratio, waterTemp, targetYield, isPublic, likeCount, createdAt, updatedAt)

-- 브루잉 단계
recipe_steps (id, recipeId, stepOrder, label, duration, waterAmount)

-- 태그
recipe_tags (recipeId, tag)

-- 좋아요
recipe_likes (recipeId, userId, createdAt)  PK: (recipeId, userId)

-- 댓글
recipe_comments (id, recipeId, userId, content, createdAt)

-- 데일리 노트
daily_notes (id, userId, date DATE, content, rating, recipeId NULL, createdAt, updatedAt)
  UNIQUE(userId, date)

-- 타이머 로그
timer_logs (id, userId, noteId NULL, recipeId, recipeName, startedAt, completedAt)
```

---

## API 설계 (REST)

```
# Auth
POST   /auth/google/callback   소셜 로그인 처리 → JWT 발급
POST   /auth/kakao/callback
POST   /auth/refresh           토큰 갱신

# Recipes
GET    /recipes                공개 피드 (cursor, limit)
POST   /recipes                레시피 생성
GET    /recipes/{id}           레시피 상세
PUT    /recipes/{id}           수정
DELETE /recipes/{id}           삭제
POST   /recipes/{id}/like      좋아요 토글
GET    /recipes/{id}/comments  댓글 목록
POST   /recipes/{id}/comments  댓글 작성
DELETE /recipes/{id}/comments/{commentId}

# My recipes
GET    /me/recipes             내 레시피 목록

# Daily Notes
GET    /notes?date=2026-03-09  특정 날 노트 조회
PUT    /notes/{date}           노트 생성/수정 (upsert)

# Calendar
GET    /calendar?year=2026&month=3   해당 월 노트 있는 날짜 목록

# Timer
POST   /timer/log              타이머 완료 로그 저장 (노트에 자동 첨부)
```

---

## 생성할 파일 목록

### 백엔드 (Quarkus)
```
src/main/kotlin/org/coffeezip/
├── auth/
│   ├── AuthResource.kt
│   ├── JwtService.kt
│   └── OAuthService.kt
├── recipe/
│   ├── RecipeResource.kt
│   ├── RecipeService.kt
│   └── RecipeRepository.kt
├── note/
│   ├── NoteResource.kt
│   ├── NoteService.kt
│   └── NoteRepository.kt
├── timer/
│   └── TimerResource.kt
├── calendar/
│   └── CalendarResource.kt
└── entity/
    ├── User.kt
    ├── Recipe.kt
    ├── RecipeStep.kt
    ├── RecipeComment.kt
    ├── RecipeLike.kt
    ├── DailyNote.kt
    └── TimerLog.kt
```

### 인프라
```
sam/
├── template.yaml
└── samconfig.toml
```

### 프론트엔드
```
frontend/
├── pages/
│   ├── index.tsx
│   ├── recipes/[id].tsx
│   ├── timer/[id].tsx
│   ├── notes/[date].tsx
│   └── calendar.tsx
└── ...
```

---

## 비용 예상 (저트래픽 기준)

| 서비스 | 예상 비용 |
|--------|----------|
| Lambda | 무료 (월 100만 호출 이하) |
| API Gateway | ~$0 (프리티어) |
| RDS MySQL | 기존 인스턴스 활용 |
| Vercel (프론트) | 무료 플랜 |
| **합계** | **~$0** (RDS 기존 비용 제외) |

---

## 검증 계획

1. `./gradlew quarkusDev` 로컬 실행 후 각 API 엔드포인트 cURL 테스트
2. `./gradlew test` — `@QuarkusTest` 통합 테스트 (H2 인메모리 또는 Testcontainers MySQL)
3. `./gradlew build -Dquarkus.native.enabled=true` 네이티브 빌드 확인
4. SAM local invoke 로 Lambda 핸들러 로컬 테스트
5. 실제 AWS 배포 후 Postman/Thunder Client 로 E2E 시나리오 테스트

---

## 실행 순서

1. PRD.md 를 `docs/plans/2026-03-09-coffeezip-prd.md` 에 저장 + 커밋
2. DB 스키마 SQL 작성 + import.sql 업데이트
3. Quarkus 의존성 추가
4. Entity + Resource 구현 (레시피 → 인증 → 노트 → 캘린더 순)
5. SAM template.yaml 작성 + 배포
6. 프론트엔드 Next.js 프로젝트 셋업
