local key = KEYS[1]
local nowMs = tonumber(ARGV[1])
local capacity = tonumber(ARGV[2])
local windowSeconds = tonumber(ARGV[3])

local windowMs = windowSeconds * 1000
local refillRatePerMs = capacity / windowMs

local tokens = tonumber(redis.call('HGET', key, 'tokens'))
local lastRefillMs = tonumber(redis.call('HGET', key, 'lastRefillMs'))

if tokens == nil then
    tokens = capacity
end

if lastRefillMs == nil then
    lastRefillMs = nowMs
end

local elapsedMs = nowMs - lastRefillMs
if elapsedMs > 0 then
    tokens = math.min(capacity, tokens + (elapsedMs * refillRatePerMs))
    lastRefillMs = nowMs
end

if tokens >= 1 then
    tokens = tokens - 1
    redis.call('HSET', key, 'tokens', tostring(tokens), 'lastRefillMs', tostring(lastRefillMs))
    redis.call('EXPIRE', key, windowSeconds + 1)

    return {1, math.floor(tokens), 0}
end

local tokensNeeded = 1 - tokens
local retryAfter = math.ceil((tokensNeeded / refillRatePerMs) / 1000)
if retryAfter < 1 then
    retryAfter = 1
end

redis.call('HSET', key, 'tokens', tostring(tokens), 'lastRefillMs', tostring(lastRefillMs))
redis.call('EXPIRE', key, windowSeconds + 1)

return {0, 0, retryAfter}
