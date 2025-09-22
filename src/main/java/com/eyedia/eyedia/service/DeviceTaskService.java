package com.eyedia.eyedia.service;

import com.eyedia.eyedia.dto.DeviceTask;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class DeviceTaskService {
    private final Map<String, BlockingQueue<DeviceTask>> queues = new ConcurrentHashMap<>();

    public void enqueue(String deviceId, DeviceTask task) {
        queues.computeIfAbsent(deviceId, k -> new LinkedBlockingQueue<>(1000)).offer(task);
    }

    /** Jetson이 /next-task 폴링할 때 사용 */
    public DeviceTask pollNext(String deviceId, long waitMs) throws InterruptedException {
        var q = queues.computeIfAbsent(deviceId, k -> new LinkedBlockingQueue<>(1000));
        return q.poll(waitMs, TimeUnit.MILLISECONDS);
    }
}
