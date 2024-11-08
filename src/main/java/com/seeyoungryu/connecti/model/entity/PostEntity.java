package com.seeyoungryu.connecti.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@NoArgsConstructor
@Table(name = "posts")
@Entity
@SQLDelete(sql = "UPDATE posts SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")    // @todo: 이 어노테이션이 왜 ~ 소프트 딜리트 기능을 하는지?
public class PostEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Setter
    private String title;


    @Column(nullable = false, columnDefinition = "TEXT") //타입 변경 ~ 텍스트 타입으로 컬럼이 생성됨
    @Setter
    private String body;


    //외래키 설정
    @ManyToOne(fetch = FetchType.LAZY)  //@Todo 지연로딩 -> 성능 최적화  (n+1 확실하게 알기 ~ 블로그)
    @JoinColumn(name = "user_id")
    @Setter
    private UserEntity user;

    @Column(name = "registered_at", updatable = false)
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;


    public PostEntity(String title, String body) {
        this.title = title;
        this.body = body;
    }

    @PrePersist
    protected void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    protected void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }


    //post entity 생성 메서드 (기타 필드는 자동으로 만들어짐)
    public static PostEntity of(String title, String body, UserEntity user) {
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(title);
        postEntity.setBody(body);
        postEntity.setUser(user);
        return postEntity;
    }


    public PostEntity(String title, String body, UserEntity user) {
        this.title = title;
        this.body = body;
        this.user = user;
    }


    public PostEntity(Long id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

}


/*
내 코드: @SQLDelete와 @Where 어노테이션이 포함되어 있어 소프트 삭제(논리 삭제)가 적용됩니다. 이로 인해 deleted_at이 NULL이 아닌 경우 데이터는 조회에서 제외되며, 실제 삭제는 수행되지 않습니다.
 소프트 삭제 설정의 목적 (@SQLDelete와 @Where)
소프트 삭제는 데이터의 영구 삭제 대신 삭제 표시를 해 데이터 일관성을 유지하고 삭제된 데이터도 필요 시 조회할 수 있도록 하기 위한 방법입니다.

@SQLDelete: 이 어노테이션을 통해 삭제 요청 시 SQL DELETE 대신 UPDATE 구문을 실행하여 deleted_at 필드에 현재 시간을 기록합니다.
@Where: deleted_at IS NULL 조건을 추가해 조회 시 삭제되지 않은 데이터만 조회되도록 합니다.
소프트 삭제의 이점은 데이터 복구가 가능하고, 히스토리를 유지할 수 있다는 점입니다.



 */


/*
지연 로딩 (FetchType.LAZY): 내 코드에서는 FetchType.LAZY를 사용해 성능 최적화를 합니다. 즉, PostEntity를 조회할 때 UserEntity는 즉시 로드되지 않고, 접근할 때 로드됩니다. 강사 코드에도 FetchType.LAZY를 추가하는 것이 성능 면에서 유리할 수 있습니다.
지연 로딩 (Lazy Fetch): 강사 코드에 FetchType.LAZY를 추가하면 불필요한 UserEntity 로드를 줄일 수 있어 성능 최적화에 기여할 수 있습니다.
 */


