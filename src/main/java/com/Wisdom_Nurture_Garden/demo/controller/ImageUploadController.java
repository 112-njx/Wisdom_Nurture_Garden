//此代码从来没有被调用过，是废稿代码，用于存储前端传来的图片，供后续应用更新使用
package com.Wisdom_Nurture_Garden.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件为空");
            }

            Files.createDirectories(Path.of(uploadDir));

            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return ResponseEntity.badRequest().body("无法解析图片，请检查格式");
            }

            // 生成 WebP 文件名
            String newFileName = UUID.randomUUID() + ".webp";
            Path filePath = Path.of(uploadDir, newFileName);

            // 使用 webp-imageio 写入 WebP 格式
            ImageIO.write(image, "webp", filePath.toFile());

            // 返回访问路径
            String fileUrl = "/uploads/" + newFileName;
            return ResponseEntity.ok("上传成功，WebP路径：" + fileUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("上传失败：" + e.getMessage());
        }
    }
}
