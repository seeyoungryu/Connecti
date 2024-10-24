# Connecti - SNS 서비스 플랫폼

<img src="https://github.com/user-attachments/assets/fa462508-0bf2-420a-9b50-b0140e0e5597" alt="Connecti Logo" width="600">

Connecti는 사람과 아이디어를 원활하게 연결해주는 소셜 네트워킹 서비스(SNS) 플랫폼입니다. 
이 프로젝트는 최신 기술과 프레임워크를 사용해 확장 가능하고, 보안이 뛰어나며, 대규모 트래픽 처리가 가능한 효율적인 SNS 플랫폼을 만드는 것을 목표로 합니다.

## 목차
1. [개요](#개요)
2. [TDD 적용](#tdd-적용)
3. [기술 스택](#기술-스택)
4. [주요 기능](#주요-기능)
5. [배포 시나리오](#배포-시나리오)
6. [설치 방법](#설치-방법)
7. [사용 방법](#사용-방법)

## 개요
Connecti는 확장 가능한 SNS 플랫폼으로, 사용자들이 다음과 같은 기능을 사용할 수 있습니다:
- 회원가입 및 로그인
- 게시물 작성, 수정, 삭제, 조회
- 좋아요 및 댓글 기능
- 상호작용에 따른 실시간 알림 기능
- 캐시와 Server-Side Events(SSE)를 통한 대규모 트래픽 처리

백엔드는 **Spring Boot**로 구현되었으며, **PostgreSQL** 데이터베이스와 연결되어 있습니다. **JWT Authorization**을 통해 API 접근에 대한 보안이 보장됩니다.

<img width="398" alt="Pasted Graphic 6" src="https://github.com/user-attachments/assets/235ed17c-f503-4089-9933-33faf005e8ce">

## TDD 적용
Connecti 프로젝트에서는 **TDD (Test Driven Development)** 방식을 적용해 개발을 진행했습니다. 이는 코드 작성 전에 테스트를 먼저 설계하여, 코드가 의도대로 동작하는지 지속적으로 검증할 수 있도록 하는 개발 방식입니다.

### TDD의 중요성과 적용 이유
1. **신뢰성 향상**: 기능 구현 전에 테스트를 먼저 작성하므로, 코드가 의도한 대로 동작하는지 확인하며 개발할 수 있습니다. 이를 통해 예상하지 못한 버그를 사전에 방지할 수 있습니다.
2. **유지보수성**: 코드 수정 시 기존 테스트를 통해 코드의 안정성을 유지하며, 새로운 기능을 쉽게 추가할 수 있으며, 기존의 코드가 테스트를 통과하도록 보장되기 때문에, 안정적인 유지보수가 가능합니다.
3. **개발 효율성**: 초기에는 다소 시간이 소요되지만, 테스트를 통한 자동화된 검증으로 인해 장기적인 개발 속도와 품질이 높아집니다.

### Connecti의 TDD 적용 사례
- **회원가입 및 로그인 기능**: 사용자 인증 로직을 구현하기 전에 테스트를 작성하고, 이를 기반으로 기능을 개발했습니다. JWT 기반 인증 체계에서 발생할 수 있는 다양한 시나리오를 미리 정의하여, 코드가 모든 경우에 대해 올바르게 동작하도록 보장하고자 했습니다.
- **게시물 CRUD 및 상호작용 기능**: 게시물 작성, 수정, 삭제, 조회와 관련된 기능도 TDD 방식을 적용하여 개발하여, 이를 통해 게시물과 사용자 간 상호작용이 안정적으로 동작함을 확인했습니다.

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
- **Docker**: 컨테이너화된 배포 환경 설정
- **Kubernetes**: 컨테이너 오케스트레이션을 통해 확장성과 자동화를 관리
- **Heroku**: 초기 배포를 위한 PaaS 플랫폼

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

## 배포 시나리오
### Heroku, GitHub Actions, Docker, Kubernetes를 활용한 배포 시나리오
1. **코드 푸시**: GitHub 저장소의 `main` 브랜치로 코드가 푸시되면 GitHub Actions가 자동으로 트리거됩니다.
2. **빌드 및 테스트**: GitHub Actions는 푸시된 코드를 빌드하고, 정의된 테스트가 있으면 실행합니다. 이때, Docker 이미지를 빌드합니다. Docker를 사용하는 목적은 개발 환경과 배포 환경을 일관되게 유지하고, 애플리케이션의 종속성을 컨테이너로 캡슐화하여 배포할 수 있기 때문입니다.
3. **Docker 이미지 생성**: 코드가 변경될 때마다 GitHub Actions가 Docker 이미지를 빌드하고, 이미지가 잘 빌드되는지 테스트합니다.
4. **쿠버네티스 클러스터에 배포**: 빌드된 Docker 이미지를 컨테이너 레지스트리에 푸시하고, Kubernetes 클러스터에 배포합니다. 이를 통해 컨테이너 인스턴스를 다중으로 배포하여 안정성을 높입니다.
5. **자동화된 Docker 배포**: 이후 `main` 브랜치로 코드가 푸시될 때마다 GitHub Actions가 Docker 이미지를 빌드하고, Kubernetes 클러스터에 자동으로 배포합니다. 코드 변경 사항이 바로 반영됩니다.

### Docker를 사용하는 목적
- **일관된 환경 제공**: 애플리케이션의 환경 설정을 컨테이너로 고정하여, 어디서나 동일한 환경에서 애플리케이션을 실행할 수 있도록 함.
- **프로젝트 독립성**: 각 애플리케이션을 독립적으로 관리하여 다른 프로젝트와의 의존성을 분리함.

### Kubernetes를 사용하는 목적
1. **자동 스케일링**: 트래픽 증가 시 자동으로 새로운 컨테이너를 추가하여 트래픽 대응. 리소스 효율성을 통해 비용을 절감.
2. **자동화된 배포와 관리**: 여러 인스턴스를 손쉽게 배포 및 관리하며, 롤링 업데이트로 사용자는 중단 없이 새로운 버전 사용 가능.
3. **서비스 디스커버리 및 로드 밸런싱**: 컨테이너 간의 통신을 쉽게 설정하며, 트래픽을 여러 컨테이너에 균등하게 분배함.

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

3. PostgreSQL 및 Redis를 로컬 환경에 설정하고 Docker를 사용해 필요한 서비스를 실행:
    ```bash
    docker-compose up -d
    ```

4. 애플리케이션 실행:
    ```bash
    ./gradlew bootRun
    ```

## 사용 방법
애플리케이션은 `http://localhost:8080`에서 접근할 수 있습니다.

로그인 후 게시물 작성 및 상호작용 기능을 통해 Connecti의 기능을 이용할 수 있습니다. 
