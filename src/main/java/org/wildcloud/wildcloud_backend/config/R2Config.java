package org.wildcloud.wildcloud_backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(R2Properties.class)
@RequiredArgsConstructor
@Slf4j
public class R2Config {
    private final R2Properties r2Properties;

    //    
//    @Bean
//    @Primary
//    public S3Client 
    @Bean
    @Primary
    public S3Client r2Client() {
        log.info("R2 Client created for account: {}", r2Properties.getAccountId());

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                r2Properties.getAccessKey(), r2Properties.getSecretKey()
        );

        return S3Client.builder()
                .endpointOverride(URI.create(r2Properties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        credentials))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .checksumValidationEnabled(false).build())
                .forcePathStyle(true)
                .build();
    }

    @Bean
    @Primary
    public S3Presigner r2Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                r2Properties.getAccessKey(), r2Properties.getSecretKey()
        );

        return S3Presigner.builder()
                .endpointOverride(
                        r2Properties.isUseCustomDomain()
                                ? URI.create(r2Properties.getCustomDomain())
                                : URI.create(r2Properties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        credentials))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .checksumValidationEnabled(false)
                        .pathStyleAccessEnabled(true).build())
                .build();
    }

    // TODO: async uploading kanske funkar bättre för batch uploads? testa
//    @Bean
//    public S3AsyncClient r2AsyncClient() {
//        return S3AsyncClient.builder()
//                .endpointOverride(URI.create(r2Properties.getEndpoint()))
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create(
//                                r2Properties.getAccessKey(),
//                                r2Properties.getSecretKey()
//                        )))
//                .region(Region.of("auto"))
//                .forcePathStyle(true)
//                .build();
//    }
//
//    @Bean
//    public S3TransferManager r2TransferManager(S3AsyncClient r2AsyncClient) {
//        return S3TransferManager.builder()
//                .s3Client(r2AsyncClient)
//                .build();
//    }
}
