package io.github.gms.functions.api;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.functions.api.grpc.GetSecretRequest;
import io.github.gms.functions.api.grpc.GetSecretResponse;
import io.github.gms.functions.secret.dto.GetSecretRequestDto;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
class ApiGrpcServiceTest extends AbstractUnitTest {

    @Mock
    private ApiService apiService;
    @InjectMocks
    private ApiGrpcService service;

    @Test
    void getSecret_whenResultsProvided_thenCallCompleted() {
        // given
        GetSecretRequestDto requestDto = new GetSecretRequestDto("apiKey", "secret");
        Map<String, String> mockResult = Map.of();
        when(apiService.getSecret(requestDto)).thenReturn(mockResult);

        StreamObserver<GetSecretResponse> responseObserver = mock(StreamObserver.class);

        // when
        service.getSecret(GetSecretRequest.newBuilder().setApiKey("apiKey").setSecretId("secret").build(), responseObserver);

        // then
        ArgumentCaptor<GetSecretResponse> responseArgumentCaptor = ArgumentCaptor.forClass(GetSecretResponse.class);
        verify(responseObserver).onNext(responseArgumentCaptor.capture());
        GetSecretResponse captured = responseArgumentCaptor.getValue();
        assertThat(captured.getSecretMap()).isEqualTo(mockResult);
        verify(responseObserver).onCompleted();
    }
}
