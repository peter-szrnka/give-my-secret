package io.github.gms.functions.api;

import io.github.gms.abstraction.AbstractIntegrationTest;
import io.github.gms.functions.api.grpc.ApiServiceGrpc;
import io.github.gms.functions.api.grpc.GetSecretRequest;
import io.github.gms.functions.api.grpc.GetSecretResponse;
import io.github.gms.functions.secret.dto.GetSecretRequestDto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public class ApiGrpcServiceIntegrationTest extends AbstractIntegrationTest {

    private Server server;
    private ManagedChannel channel;
    private ApiServiceGrpc.ApiServiceBlockingV2Stub blockingStub;

    @Autowired
    private ApiGrpcService grpcService;

    @MockitoBean
    private ApiService apiService;

    @BeforeEach
    void setUp() throws IOException {
        server = ServerBuilder
                .forPort(0)
                .addService(grpcService)
                .build()
                .start();

        int port = server.getPort();

        channel = ManagedChannelBuilder
                .forAddress("localhost", port)
                .usePlaintext()
                .build();

        blockingStub = ApiServiceGrpc.newBlockingV2Stub(channel);
    }

    @AfterEach
    void tearDown() {
        if (channel != null) {
            channel.shutdownNow();
        }

        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void shouldReturnSecret() throws Exception {
        // given
        Map<String, String> secret = Map.of(
                "username", "admin",
                "password", "secret"
        );

        when(apiService.getSecret(any(GetSecretRequestDto.class)))
                .thenReturn(secret);

        GetSecretRequest request = GetSecretRequest.newBuilder()
                .setApiKey("api-key")
                .setSecretId("secret-id")
                .build();

        // when
        GetSecretResponse response = blockingStub.getSecret(request);

        // then
        assertThat(response.getSecretMap())
                .containsEntry("username", "admin")
                .containsEntry("password", "secret");

        ArgumentCaptor<GetSecretRequestDto> captor =
                ArgumentCaptor.forClass(GetSecretRequestDto.class);

        verify(apiService).getSecret(captor.capture());

        assertThat(captor.getValue().getApiKey()).isEqualTo("api-key");
        assertThat(captor.getValue().getSecretId()).isEqualTo("secret-id");
    }
}
