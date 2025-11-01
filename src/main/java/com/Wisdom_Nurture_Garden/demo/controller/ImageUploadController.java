package com.Wisdom_Nurture_Garden.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {

    @Value("D:/Wisdom_Nurture_Garden/demo/demo/Pictures/")
    private String uploadDir;

    @PostMapping("/api/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件为空");
            }

            String uploadDir = "D:/Wisdom_Nurture_Garden/demo/demo/Pictures/";
            Files.createDirectories(Path.of(uploadDir));

            // 原文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                return ResponseEntity.badRequest().body("文件名不合法");
            }

            // 检查格式
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!List.of("jpg", "jpeg", "png").contains(ext)) {
                return ResponseEntity.badRequest().body("仅支持 JPG / PNG 图片");
            }

            // 读入图片内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return ResponseEntity.badRequest().body("无法解析图片，请检查格式");
            }

            // 保存文件
            String newFileName = UUID.randomUUID() + "." + ext;
            Path filePath = Path.of(uploadDir, newFileName);
            ImageIO.write(image, ext, filePath.toFile());

            return ResponseEntity.ok("上传成功，路径：" + filePath);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("上传失败：" + e.getMessage());
        }
    }

}

