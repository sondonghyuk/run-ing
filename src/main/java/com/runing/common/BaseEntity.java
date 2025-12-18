package com.runing.common;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

@Getter
@MappedSuperclass // 해당 BaseEntity를 엔티티로 인식되지 않게 하며, 데이터베이스에 테이블이 생성되지 않게 한다.
@EntityListeners(AuditingEntityListener.class) // Auditing 을 적용, Entity 의 변화를 감지하여 Entity 와 매핑된 테이블의 데이터 조작
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 내부 pk

	@Column(nullable = false, updatable = false, unique = true)
	private UUID uuid; // 외부 노출용

	@CreatedDate
	@Column(name = "created_at",nullable = false, updatable = false) // 초기 값이 update 되지 않음
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at",nullable = false)
	private LocalDateTime updatedAt;

	/**
	 * 엔티티가 영속화 되기 직전에 실행된다.
	 * UUID 가 설정되지 않은 경우 자동으로 UUID 를 생성한다.
	 */
	@PrePersist
	protected void onCreate() {
		if(uuid == null) {
			uuid = UUID.randomUUID();
		}
	}
}
