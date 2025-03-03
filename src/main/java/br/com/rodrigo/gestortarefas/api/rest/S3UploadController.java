package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.services.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3UploadController extends ControllerBase<Map<String, String>> {

    private final S3StorageService s3StorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = s3StorageService.uploadArquivo(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            return responderItemCriado(response);
        } catch (IOException e) {
            return responderRequisicaoMalSucedida();
        }
    }

    @DeleteMapping("/apagar/{fileName}")
    public ResponseEntity<Void> deleteImage(@PathVariable String fileName) {
        try {
            s3StorageService.apagarArquivo(fileName);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }
}

