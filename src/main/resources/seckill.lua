
local productId = ARGV[1]
local userId = ARGV[2]
local orderId = ARGV[3]
local shopId = ARGV[4]
local status = ARGV[5]
local createTime = ARGV[6]

local stockKey = 'seckill:stock:' .. productId
local orderKey = 'seckill:order' .. productId

if (tonumber(redis.call('get', stockKey)) <= 0) then
    return 1
end

if (redis.call('sismember', orderKey, userId) == 1) then
    -- 重复下单
    return 2
end

-- 扣减库存
redis.call('incrby', stockKey, -1)

-- 下单
redis.call('sadd', orderKey, userId)

-- 消息队列
redis.call('xadd', 'stream.orders', '*', 'userId', userId, 'productId', productId, 'id', orderId, 'shopId', shopId, 'status', status, 'createTime', createTime)

return 0