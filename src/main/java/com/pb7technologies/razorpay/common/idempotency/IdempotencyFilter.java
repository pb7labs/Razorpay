package com.pb7technologies.razorpay.common.idempotency;

import com.pb7technologies.razorpay.common.exception.IdempotencyConflictException;
import com.pb7technologies.razorpay.merchant.security.MerchantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final Set<String> GUARDED_METHODS = Set.of("POST", "PUT", "PATCH");
    private static final Duration IN_PROGRESS_TTL = Duration.ofSeconds(30);
    private static final Duration COMPLETED_TTL = Duration.ofHours(24);
    private final MerchantContext merchantContext;
    private final IdempotencyStore idempotencyStore;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final static String SEPARATOR = "|";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        if (!GUARDED_METHODS.contains(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String rawKey = request.getHeader("X-Idempotency-Key");
        if (rawKey == null || rawKey.isBlank()) { // No Idempotency key found
            chain.doFilter(request, response);
            return;
        }

        UUID merchantId = merchantContext.getMerchantId();
        String key = merchantId != null ? merchantId + ":" + rawKey : rawKey;

        boolean claimed = idempotencyStore.setIfAbsent(key, IN_PROGRESS_TTL);

        if (!claimed) {
            //another thread has already claimed the idempotency key and working on it
            Optional<String> existing = idempotencyStore.get(key);

            // if existing below condition is met it means earlier thread already completed its task
            // So we can return the same request again
            //Else throw the exception
            if (existing.isPresent() && !IdempotencyStore.IN_PROGRESS.equals(existing.get())) {
                //It's not in progress, but coming from the actual value stored in redis
                replay(request, response, existing.get());
            } else {
                // it is in progress by another thread
                var ex = new IdempotencyConflictException("A Request with this Idempotency key is in progress");
                handlerExceptionResolver.resolveException(request, response, null, ex);
            }
            return;
        }
        // First time claim
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        try {
            chain.doFilter(request, wrapper);
        } finally {
            int status = wrapper.getStatus();
            byte[] bodyBytes = wrapper.getContentAsByteArray();
            String body = new String(bodyBytes, StandardCharsets.UTF_8);

            if (status < 400 && bodyBytes.length > 0) {
                //SUCCESS - store the completed response for future replays
                String stored = status + SEPARATOR + body;
                idempotencyStore.store(key, stored, COMPLETED_TTL);
                log.debug("IdempotencyFilter: Stored response status={} key={}", status, key);
            } else {
                //ERROR or empty - delete placeholder so client can retry cleanly
                idempotencyStore.deletes(key);
                log.debug("IdempotencyFilter: Deleted placeholder after error status={} key={}", status, key);
            }

            //Always flush buffered body to the actual response
            //Fill the response or there will be empty body
            wrapper.copyBodyToResponse();
        }


    }

    private void replay(HttpServletRequest request, HttpServletResponse response, String stored) throws IOException {
        int separatorIndex = stored.indexOf(SEPARATOR);
        if (separatorIndex < 0) {
            var ex = new IdempotencyConflictException("A Request with this Idempotency key is in progress");
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

        int status = Integer.parseInt(stored.substring(0, separatorIndex));
        String body = stored.substring(separatorIndex + 1);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }
}
