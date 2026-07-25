local key = KEYS[1]
local nowMs = tonumber(ARGV[1])
local maxRequests = tonumber(ARGV[2])
local windowSeconds = tonumber(ARGV[3])
local requestId = ARGV[4]

local windowMs = windowSeconds * 1000
local floorMs = nowMs - windowMs

redis.call('ZREMRANGEBYSCORE', key, '-inf', floorMs)

local current = redis.call('ZCARD', key)

if current >= maxRequests then
    local oldest = redis.call('ZRANGE', key, 0, 0, 'WITHSCORES')
    local retryAfter = 1

    if oldest[2] ~= nil then
        local windowExpiresMs = tonumber(oldest[2]) + windowMs
        retryAfter = math.ceil((windowExpiresMs - nowMs) / 1000)
        if retryAfter < 1 then
            retryAfter = 1
        end
    end

    return {0, 0, retryAfter}
end

redis.call('ZADD', key, nowMs, requestId)
redis.call('EXPIRE', key, windowSeconds + 1)

return {1, maxRequests - current - 1, 0}
