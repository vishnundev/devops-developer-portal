package com.amazon.seller.devopsportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "deployments", indexes = {@Index(name = "idx_deployments_service_id", columnList = "service_id"), @Index(name = "idx_deployments_environment_id", columnList = "environment_id")})
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Deployment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false, foreignKey = @ForeignKey(name = "fk_deployments_service"))
    private Service service;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "environment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_deployments_environment"))
    private Environment environment;
    @Column(nullable = false, length = 50)
    private String version;
    @Enumerated(EnumType.STRING) @Column(name = "deployment_status", nullable = false, length = 20)
    private DeploymentStatus deploymentStatus;
    @Column(name = "deployment_time", nullable = false)
    private LocalDateTime deploymentTime;
}
