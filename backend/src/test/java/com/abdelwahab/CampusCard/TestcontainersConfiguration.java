package com.abdelwahab.CampusCard;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

private static GenericContainer<?> minioContainer;

@Bean
@ServiceConnection
PostgreSQLContainer<?> postgresContainer() {
return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));
}

@Bean
GenericContainer<?> minioContainer() {
if (minioContainer == null) {
minioContainer = new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
.withExposedPorts(9000, 9001)
.withEnv("MINIO_ROOT_USER", "minioadmin")
.withEnv("MINIO_ROOT_PASSWORD", "minioadmin123")
.withCommand("server", "/data", "--console-address", ":9001");
minioContainer.start();
}
return minioContainer;
}

@DynamicPropertySource
static void minioProperties(DynamicPropertyRegistry registry) {
// Wait for container to be ready
if (minioContainer != null && minioContainer.isRunning()) {
Integer minioPort = minioContainer.getMappedPort(9000);
registry.add("minio.url", () -> "http://localhost:" + minioPort);
registry.add("minio.access-key", () -> "minioadmin");
registry.add("minio.secret-key", () -> "minioadmin123");
registry.add("minio.bucket-name", () -> "uploads");
}
}

@Bean
@Primary
MinioClient testMinioClient(GenericContainer<?> minioContainer) {
try {
Integer minioPort = minioContainer.getMappedPort(9000);
MinioClient client = MinioClient.builder()
.endpoint("http://localhost:" + minioPort)
.credentials("minioadmin", "minioadmin123")
.build();

// Create bucket if it doesn't exist
boolean found = client.bucketExists(BucketExistsArgs.builder().bucket("uploads").build());
if (!found) {
client.makeBucket(MakeBucketArgs.builder().bucket("uploads").build());
}

return client;
} catch (Exception e) {
throw new RuntimeException("Failed to create test MinIO client", e);
}
}
}
