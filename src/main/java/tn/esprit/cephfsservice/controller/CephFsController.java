package com.cephfs.cephfsservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.*;

@RestController
@RequestMapping("/api/cephfs")
public class CephFsController {
    // 👇 On configure le chemin monté depuis CephFS (via CephFS PVC monté sur le pod Kubernetes)
    @Value("${cephfs.mount.path:/mnt/cephfs}")
    private String cephFsMountPath;

    @PostMapping("/write")
    public ResponseEntity<String> writeFile(@RequestParam String filename, @RequestParam String content) {
        try {
            Path filePath = Paths.get(cephFsMountPath, filename);
            Files.write(filePath, content.getBytes());
            return ResponseEntity.ok("✅ Fichier écrit dans CephFS !");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("❌ Erreur d’écriture : " + e.getMessage());
        }
    }

    @GetMapping("/read")
    public ResponseEntity<String> readFile(@RequestParam String filename) {
        try {
            Path filePath = Paths.get(cephFsMountPath, filename);
            String content = Files.readString(filePath);
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("❌ Erreur de lecture : " + e.getMessage());
        }
    }

    @PostMapping("/mkdir")
    public ResponseEntity<String> createDirectory(@RequestParam String dirname) {
        try {
            Path dirPath = Paths.get(cephFsMountPath, dirname);
            Files.createDirectories(dirPath);
            return ResponseEntity.ok("📁 Dossier créé dans CephFS !");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("❌ Erreur de création de dossier : " + e.getMessage());
        }
    }
}
