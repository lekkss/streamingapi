package com.lekkss.streamingapi.services;

import com.lekkss.streamingapi.models.Video;
import com.lekkss.streamingapi.utils.Response;
import org.springframework.web.multipart.MultipartFile;



public interface VideoService {
    Response<?> save(Video video, MultipartFile file, Integer categoryId);
    Video getVideo(Integer videoId);
    Response<?> getAllVideos();
    void processVideo(Integer videoId);
}