package com.mk.docclassifier.controller;

import com.mk.docclassifier.domain.entity.Document;
import com.mk.docclassifier.domain.entity.User;
import com.mk.docclassifier.repository.DocumentRepository;
import com.mk.docclassifier.repository.UserRepository;
import com.mk.docclassifier.security.JwtService;
import com.mk.docclassifier.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long id, @RequestParam(required = false) String token) {
        // Validate token if provided
        if (token != null && !token.isEmpty()) {
            try {
                String username = jwtService.extractUsername(token);
                User user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
                
                // Verify user has access to this document
                Document document = documentRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
                
                boolean isAdmin = user.getRole().name().equals("ADMIN");
                boolean isOwner = document.getUser() != null && document.getUser().getId().equals(user.getId());
                
                if (!isAdmin && !isOwner) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
                }
            } catch (Exception e) {
                if (e instanceof ResponseStatusException) {
                    throw e;
                }
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }
        }
        
        return sseService.subscribe(id);
    }
}
