package br.com.rodrigo.gestortarefas.api.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Response;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    Logger logger = LogManager.getLogger(S3StorageService.class);

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3StorageService(@Value("${aws.s3.access-key}") String accessKey,
                            @Value("${aws.s3.secret-key}") String secretKey,
                            @Value("${aws.s3.region}") String region) {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String uploadArquivo(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        S3Response response = s3Client.putObject(putObjectRequest,
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

        if (response.sdkHttpResponse().isSuccessful()) {
            return "https://" + bucketName + ".s3.amazonaws.com/" + URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        } else {
            throw new RuntimeException("Erro ao fazer upload para o S3");

        }
    }

    public void apagarArquivo(String fileUrl) {

        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            DeleteObjectResponse response = s3Client.deleteObject(deleteObjectRequest);

            if (!response.sdkHttpResponse().isSuccessful()) {
                logger.error("Erro ao deletar o arquivo do S3: {}", fileUrl);
            }
        } catch (Exception e) {
            logger.error("Erro ao tentar apagar o arquivo: {}. Detalhes: {}", fileUrl, e.getMessage(), e);
        }
    }
}
