package com.lekkss.streamingapi.controllers;

import com.lekkss.streamingapi.models.Video;
import com.lekkss.streamingapi.services.VideoService;
import com.lekkss.streamingapi.utils.Response;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ResponseEntity<Response<?>> getAllVideos() {
        Response<?> videos = videoService.getAllVideos();
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/stream/{videoId}")
    public ResponseEntity<Resource> stream(@PathVariable Integer videoId) {

        Video video = videoService.getVideo(videoId);
        String contentType = video.getContentType();
        String filePath = video.getFilePath();
        Resource resource = new FileSystemResource(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
    }


    @PostMapping
    public ResponseEntity<Response<?>> createVideo(
            @RequestParam("video") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Integer categoryId) {
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);

        Response<?> response = videoService.save(video, file, categoryId);
        return ResponseEntity.ok(response);
    }
}
