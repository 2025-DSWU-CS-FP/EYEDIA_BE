package com.eyedia.eyedia.controller;

import com.eyedia.eyedia.dto.DeviceTask;
import com.eyedia.eyedia.service.DeviceTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceTaskController {
    private final DeviceTaskService taskService;

    // 내부/테스트 - 임의 작업 넣기
    @PostMapping("/{deviceId}/enqueue")
    public ResponseEntity<DeviceTask> enqueue(
            @PathVariable String deviceId,
            @RequestBody DeviceTask task
    ) {
        if (task.getTaskId() == null || task.getTaskId().isBlank()) {
            task.setTaskId(UUID.randomUUID().toString());  // 자동 생성
        }
        if (task.getCreatedAt() == 0) {
            task.setCreatedAt(System.currentTimeMillis()); // 현재 epoch ms
        }

        taskService.enqueue(deviceId, task);
        return ResponseEntity.ok(task);
    }

    // 젯슨이 다음 작업 가져가기 (폴링)
    @GetMapping("/{deviceId}/next-task")
    public ResponseEntity<DeviceTask> next(@PathVariable String deviceId,
                                           @RequestParam(defaultValue="800") long waitMs) throws InterruptedException {
        var t = taskService.pollNext(deviceId, Math.min(waitMs, 5000));
        return (t != null) ? ResponseEntity.ok(t) : ResponseEntity.noContent().build(); // 204
    }

}
