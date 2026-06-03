package io.github.gms.functions.api;

import io.github.gms.functions.api.grpc.GetSecretRequest;
import io.github.gms.functions.api.grpc.GetSecretResponse;
import io.github.gms.functions.api.grpc.ApiServiceGrpc;
import io.github.gms.functions.secret.dto.GetSecretRequestDto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
@GrpcService
@RequiredArgsConstructor
@ConditionalOnProperty(name = "config.grpc.enabled", havingValue = "true", matchIfMissing = true)
public class ApiGrpcService extends ApiServiceGrpc.ApiServiceImplBase {

    private final ApiService apiService;

    @Override
    public void getSecret(GetSecretRequest request, StreamObserver<GetSecretResponse> responseObserver) {
        GetSecretRequestDto serviceRequest = buildRequest(request);

        Map<String, String> response = apiService.getSecret(serviceRequest);

        responseObserver.onNext(GetSecretResponse.newBuilder().putAllSecret(response).build());
        responseObserver.onCompleted();
    }

    private static GetSecretRequestDto buildRequest(GetSecretRequest request) {
        return new GetSecretRequestDto(request.getApiKey(), request.getSecretId());
    }
}
