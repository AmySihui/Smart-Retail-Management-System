package com.amy.common.security;

import io.grpc.*;

public class AuthClientInterceptor implements ClientInterceptor {

    private final String jwtToken;

    public AuthClientInterceptor(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Metadata.Key<String> AUTHORIZATION_KEY =
                        Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

                headers.put(AUTHORIZATION_KEY, "Bearer " + jwtToken);
                super.start(responseListener, headers);
            }
        };
    }
}
