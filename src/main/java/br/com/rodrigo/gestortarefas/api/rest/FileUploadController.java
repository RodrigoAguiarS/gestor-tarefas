package br.com.rodrigo.gestortarefas.api.rest;

import br.com.rodrigo.gestortarefas.api.services.FirebaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/storage/upload")
@RequiredArgsConstructor
public class FileUploadController extends ControllerBase<String> {

    private final FirebaseStorageService firebaseStorageService;

    @PostMapping()
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = firebaseStorageService.uploadFile(file);
            return responderItemCriado(fileUrl);
        } catch (IOException e) {
            return responderRequisicaoMalSucedida();
        }
    }
}
