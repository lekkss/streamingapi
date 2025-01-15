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

        Video video = this.getVideo(videoId);
        String filePath = video.getFilePath();

        //path where to store data:
        Path videoPath = Paths.get(filePath);


//        String output360p = HSL_DIR + videoId + "/360p/";
//        String output720p = HSL_DIR + videoId + "/720p/";
//        String output1080p = HSL_DIR + videoId + "/1080p/";

        try {
//            Files.createDirectories(Paths.get(output360p));
//            Files.createDirectories(Paths.get(output720p));
//            Files.createDirectories(Paths.get(output1080p));

            // ffmpeg command
            Path outputPath = Paths.get(HSL_DIR, String.valueOf(videoId));

            Files.createDirectories(outputPath);


            String ffmpegCmd = String.format(
                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
                    videoPath, outputPath, outputPath
            );

//            StringBuilder ffmpegCmd = new StringBuilder();
//            ffmpegCmd.append("ffmpeg  -i ")
//                    .append(videoPath.toString())
//                    .append(" -c:v libx264 -c:a aac")
//                    .append(" ")
//                    .append("-map 0:v -map 0:a -s:v:0 640x360 -b:v:0 800k ")
//                    .append("-map 0:v -map 0:a -s:v:1 1280x720 -b:v:1 2800k ")
//                    .append("-map 0:v -map 0:a -s:v:2 1920x1080 -b:v:2 5000k ")
//                    .append("-var_stream_map \"v:0,a:0 v:1,a:0 v:2,a:0\" ")
//                    .append("-master_pl_name ").append(HSL_DIR).append(videoId).append("/master.m3u8 ")
//                    .append("-f hls -hls_time 10 -hls_list_size 0 ")
//                    .append("-hls_segment_filename \"").append(HSL_DIR).append(videoId).append("/v%v/fileSequence%d.ts\" ")
//                    .append("\"").append(HSL_DIR).append(videoId).append("/v%v/prog_index.m3u8\"");


            System.out.println(ffmpegCmd);
            //file this command
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCmd);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exit = process.waitFor();
            if (exit != 0) {
                throw new RuntimeException("video processing failed!!");
            }


        } catch (IOException ex) {
            throw new RuntimeException("Video processing fail!!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
