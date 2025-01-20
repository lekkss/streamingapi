package com.lekkss.streamingapi.services.impl;

import com.lekkss.streamingapi.models.Category;
import com.lekkss.streamingapi.models.User;
import com.lekkss.streamingapi.models.Video;
import com.lekkss.streamingapi.models.VideoType;
import com.lekkss.streamingapi.repositories.CategoryRepository;
import com.lekkss.streamingapi.repositories.UserRepository;
import com.lekkss.streamingapi.repositories.VideoRepository;
import com.lekkss.streamingapi.services.VideoService;
import com.lekkss.streamingapi.utils.Response;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class VideoServiceImpl implements VideoService {

    @Value("${files.video}")
    String DIR;

    @Value("${file.video.hsl}")
    String HSL_DIR;

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public VideoServiceImpl(VideoRepository videoRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    public void init() {
        File file = new File(DIR);
        try {
            Files.createDirectories(Paths.get(HSL_DIR));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!file.exists()) {
            file.mkdir();
            System.out.println("Folder Created:");
        } else {
            System.out.println("Folder already created");
        }
    }

    @Override
    public Response<?> save(Video video, MultipartFile file, Integer categoryId) {
        try {
            String filename = file.getOriginalFilename();
            String contentType = file.getContentType();
            InputStream inputStream = file.getInputStream();

            // file path
            String cleanFileName = StringUtils.cleanPath(filename);

            //folder path : create
            String cleanFolder = StringUtils.cleanPath(DIR);

            // folder path with  filename
            Path path = Paths.get(cleanFolder, cleanFileName);

            System.out.println(contentType);
            System.out.println(path);

            // copy file to the folder
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

            // video meta data
            video.setContentType(contentType);
            video.setFilePath(path.toString());
            video.setVideoType(VideoType.FILE);
            video.setUrl("");
            // Retrieve the logged-in user
            String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(loggedInUserEmail)
                    .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

            video.setUser(user);
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            video.setCategory(category);
            Video savedVideo = videoRepository.save(video);
            //processing video
            processVideo(savedVideo.getId());

            //delete actual video file and database entry  if exception

            // metadata save
            return new Response<>(true, "Video uploaded successfully", savedVideo);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in processing video ");
        }
    }


    @Override
    public Video getVideo(Integer videoId) {
        return videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("video not found"));
    }

    @Override
    public Response<?> getAllVideos() {
        return new Response<>(true,"Videos fetched successfully", videoRepository.findAll());
    }

    @Override
    public void processVideo(Integer videoId) {
        // Retrieve video details from the database
        Video video = this.getVideo(videoId);
        String filePath = video.getFilePath();

        // Path to store HLS output files
        Path videoPath = Paths.get(filePath);
        Path outputPath = Paths.get(HSL_DIR, String.valueOf(videoId));

        try {
            // Create directories for storing HLS files
            Files.createDirectories(outputPath);

            // Construct the ffmpeg command
            String ffmpegCmd = String.format(
                    "ffmpeg -i \"%s\" " +                 // Input file
                            "-c:v libx264 -c:a aac -strict -2 " + // Video and audio codecs
                            "-f hls -hls_time 10 -hls_list_size 0 " + // HLS parameters
                            "-hls_segment_filename \"%s/segment_%%3d.ts\" " + // Segment files
                            "\"%s/master.m3u8\"", // Master playlist
                    videoPath, outputPath, outputPath
            );

            // Print the ffmpeg command for debugging
            System.out.println("Executing FFmpeg command: " + ffmpegCmd);

            // Execute the ffmpeg command
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCmd);
            processBuilder.inheritIO(); // Inherit IO streams for debugging
            Process process = processBuilder.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Video processing failed with exit code: " + exitCode);
            }

        } catch (IOException ex) {
            throw new RuntimeException("Video processing failed due to IO error!", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            throw new RuntimeException("Video processing interrupted!", ex);
        }
    }

}
