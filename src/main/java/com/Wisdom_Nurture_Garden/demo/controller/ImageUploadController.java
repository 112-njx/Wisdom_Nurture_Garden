package com.Wisdom_Nurture_Garden.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {

    @Value("D:/Wisdom_Nurture_Garden/demo/demo/Pictures")
    private String uploadDir;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String newFileName = UUID.randomUUID() + ".webp";
            File webpFile = new File(uploadDir, newFileName);

            BufferedImage image = ImageIO.read(file.getInputStream());

            ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
            try (FileImageOutputStream output = new FileImageOutputStream(webpFile)) {
                writer.setOutput(output);
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.8f);
                writer.write(null, new IIOImage(image, null, null), param);
            } finally {
                writer.dispose();
            }

            String url = "http://localhost:8080/Wisdom_Nurture_Garden/demo/demo/Pictures" + newFileName;
            return ResponseEntity.ok(url);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }
}

