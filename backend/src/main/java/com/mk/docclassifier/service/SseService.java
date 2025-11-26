package com.mk.docclassifier.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long documentId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(documentId, emitter);

        emitter.onCompletion(() -> emitters.remove(documentId));
        emitter.onTimeout(() -> emitters.remove(documentId));
        emitter.onError((e) -> emitters.remove(documentId));

        return emitter;
    }

    public void sendEvent(Long documentId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(documentId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                emitters.remove(documentId);
            }
        }
    }
}
