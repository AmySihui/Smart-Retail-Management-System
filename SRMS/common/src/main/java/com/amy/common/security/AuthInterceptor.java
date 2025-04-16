package com.amy.common.security;

import io.grpc.*;

public class AuthInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> AUTH_HEADER =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String authHeader = headers.get(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            call.close(Status.PERMISSION_DENIED.withDescription("less Authorization head"), headers);
            return new ServerCall.Listener<>() {};
        }

        String token = authHeader.substring("Bearer ".length());

        try {
            String userId = JWTUtil.validateToken(token);

            Context contextWithUser = Context.current().withValue(Context.key("userId"), userId);

            return Contexts.interceptCall(contextWithUser, call, headers, next);

        } catch (Exception e) {
            call.close(Status.PERMISSION_DENIED.withDescription("error JWT").withCause(e), headers);
            return new ServerCall.Listener<>() {};
        }
    }
}
