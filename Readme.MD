# Video Streaming API Documentation

This API provides functionality for managing users, uploading videos, categorizing content, and streaming video using HLS (HTTP Live Streaming). The application integrates Spring Boot for backend services, Spring Security for authentication and authorization, and FFmpeg for video processing.

## Table of Contents
1. [Authentication](#authentication)
2. [Video Management](#video-management)
3. [Category Management](#category-management)
4. [Video Streaming](#video-streaming)
5. [Error Handling](#error-handling)
6. [FFmpeg and HLS](#ffmpeg-and-hls)

---

## Authentication
The API uses JWT for authentication. Secure endpoints require a valid JWT token in the `Authorization` header.

### Endpoints:

#### **Register a User**
- **POST** `/api/v1/auth/register`
- **Request Body:**
  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "username": "johndoe"
  }
  ```
- **Response:**
  ```json
  {
    "status": true,
    "message": "User registered successfully",
    "data": {}
  }
  ```

#### **Login a User**
- **POST** `/api/v1/auth/login`
- **Request Body:**
  ```json
  {
    "email": "john.doe@example.com",
    "password": "password123"
  }
  ```
- **Response:**
  ```json
  {
    "status": true,
    "message": "Login successful",
    "data": {
      "token": "<JWT_TOKEN>"
    }
  }
  ```

---

## Video Management
This section covers video upload, retrieval, and processing.

### Endpoints:

#### **Upload Video**
- **POST** `/api/v1/videos/upload`
- **Headers:**
    - `Authorization: Bearer <JWT_TOKEN>`
- **Request Body:**
    - Form data with file input:
      ```
      video: <video_file>
      title: "Sample Video"
      description: "A sample video description"
      categoryId: 1
      ```
- **Response:**
  ```json
  {
    "status": true,
    "message": "Video uploaded successfully",
    "data": {
      "videoId": 123
    }
  }
  ```

#### **Get Video by ID**
- **GET** `/api/v1/videos/{id}`
- **Headers:**
    - `Authorization: Bearer <JWT_TOKEN>`
- **Response:**
  ```json
  {
    "status": true,
    "message": "Video retrieved successfully",
    "data": {
      "id": 123,
      "title": "Sample Video",
      "description": "A sample video description",
      "category": "Education",
      "streamUrl": "http://localhost:8080/api/v1/videos/123/master.m3u8"
    }
  }
  ```

---

## Category Management
Categories help organize videos into meaningful groups.

### Endpoints:

#### **Create a Category**
- **POST** `/api/v1/categories`
- **Headers:**
    - `Authorization: Bearer <JWT_TOKEN>`
- **Request Body:**
  ```json
  {
    "name": "Education",
    "description": "Educational videos"
  }
  ```
- **Response:**
  ```json
  {
    "status": true,
    "message": "Category created successfully",
    "data": {}
  }
  ```

#### **Get All Categories**
- **GET** `/api/v1/categories`
- **Response:**
  ```json
  {
    "status": true,
    "message": "Categories retrieved successfully",
    "data": [
      {
        "id": 1,
        "name": "Education",
        "description": "Educational videos"
      }
    ]
  }
  ```

---

## Video Streaming
Videos are streamed using HLS (HTTP Live Streaming). The video processing pipeline ensures efficient playback across different devices.

### Streaming Endpoint:
- **GET** `/api/v1/videos/{id}/master.m3u8`
- **Headers:**
    - `Authorization: Bearer <JWT_TOKEN>`
- This endpoint provides the `.m3u8` playlist for the requested video. Players like HLS.js or native HLS players on supported devices can be used to stream the video.

---

## Error Handling
The API follows a consistent error-handling mechanism.

### Error Response Format:
```json
{
  "status": false,
  "message": "Error message",
  "data": {}
}
```

### Common Error Codes:
- `401 Unauthorized`: Invalid or missing JWT token.
- `404 Not Found`: Resource not found.
- `500 Internal Server Error`: Server-side error.

---

## FFmpeg and HLS
The video processing pipeline uses FFmpeg to generate HLS streams. Uploaded videos are transcoded into HLS format with a `master.m3u8` playlist and `.ts` segments.

### FFmpeg Command:
```bash
ffmpeg -i <input_video> \
  -c:v libx264 -c:a aac -strict -2 \
  -f hls -hls_time 10 -hls_list_size 0 \
  -hls_segment_filename "<output_dir>/segment_%03d.ts" \
  "<output_dir>/master.m3u8"
```

This command ensures compatibility across a wide range of devices by using HLS, a popular video streaming protocol.

---

## Additional Notes
- The application adheres to SOLID principles, ensuring maintainable and scalable code.
- Video uploads and streaming are secured with JWT authentication to prevent unauthorized access.
- Ensure FFmpeg is installed and configured properly on the server for video processing.

---

**Author:** Oluwasegun Afolabi 
**Contact:** leksguy97@gmail.com

