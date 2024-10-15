
# Connecti - SNS 서비스 플랫폼

Connecti는 사람과 아이디어를 원활하게 연결해주는 소셜 네트워킹 서비스(SNS) 플랫폼입니다. 이 프로젝트는 최신 기술과 프레임워크를 사용해 확장 가능하고, 보안이 뛰어나며, 대규모 트래픽 처리가 가능한 효율적인 SNS 플랫폼을 만드는 것을 목표로 합니다.

## 목차
1. [개요](#개요)
2. [기술 스택](#기술-스택)
3. [주요 기능](#주요-기능)
4. [설치 방법](#설치-방법)
5. [사용 방법](#사용-방법)

## 개요
Connecti는 확장 가능한 SNS 플랫폼으로, 사용자들이 다음과 같은 기능을 사용할 수 있습니다:
- 회원가입 및 로그인
- 게시물 작성, 수정, 삭제, 조회
- 좋아요 및 댓글 기능
- 상호작용에 따른 실시간 알림 기능
- 캐시와 Server-Side Events(SSE)를 통한 대규모 트래픽 처리

백엔드는 **Spring Boot**로 구현되었으며, **PostgreSQL** 데이터베이스와 연결되어 있습니다. **JWT Authorization**을 통해 API 접근에 대한 보안이 보장됩니다.

<img width="398" alt="Pasted Graphic 6" src="https://github.com/user-attachments/assets/235ed17c-f503-4089-9933-33faf005e8ce">

## 기술 스택
Connecti 프로젝트에서 사용된 기술과 도구는 다음과 같습니다:

### 백엔드
- **Java 11**: 주요 프로그래밍 언어
- **Spring Boot 2.6.7**: 백엔드 프레임워크
- **Spring JPA**: 데이터베이스와의 통합을 위한 ORM 기술
- **Spring Security**: JWT를 사용한 인증 및 권한 부여
- **Gradle**: 빌드 자동화 도구
- **Lombok**: 반복되는 코드를 줄여주는 애너테이션 제공
- **PostgreSQL**: 관계형 데이터베이스, 사용자 및 게시물 데이터 저장
- **Redis**: 캐싱을 통한 성능 향상
- **Apache Kafka**: 메시지 브로커를 통해 이벤트 기반 데이터 스트림 처리
- **Heroku**: 애플리케이션 배포를 위한 PaaS 플랫폼

### 테스트
- **JUnit 5**: 단위 테스트 프레임워크로 코드 품질을 보장

### 버전 관리
- **Git & GitHub**: 버전 관리 및 협업 도구

### 기타
- **SSE (Server-Side Event)**: 실시간 알림 기능을 위한 푸시 기술


## 주요 기능
1. **사용자 인증**: JWT 기반의 보안 인증 및 권한 부여
2. **게시물 CRUD**: 게시물 작성, 수정, 삭제 및 조회 기능
3. **좋아요 및 댓글**: 사용자 간 상호작용을 위한 기능
4. **알림**: Server-Side Events(SSE)를 이용한 실시간 알림
5. **확장성**: Redis 캐싱과 Kafka 이벤트 스트림을 통한 대규모 트래픽 처리

## 설치 방법
로컬 환경에서 프로젝트를 실행하려면:

1. 레파지토리 클론:
    ```bash
    git clone https://github.com/your-username/Connecti.git
    cd Connecti
    ```

2. 의존성 설치:
    ```bash
    ./gradlew build
    ```

3. PostgreSQL 및 Redis를 로컬 환경에 설정하세요.

4. 애플리케이션 실행:
    ```bash
    ./gradlew bootRun
    ```

## 사용 방법
애플리케이션은 `http://localhost:8080`에서 접근할 수 있습니다.

