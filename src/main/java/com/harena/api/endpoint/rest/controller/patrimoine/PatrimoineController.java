package com.harena.api.endpoint.rest.controller.patrimoine;

import com.harena.api.endpoint.rest.model.GetPatrimoines200Response;
import com.harena.api.service.PatrimoineService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.hei.patrimoine.modele.Patrimoine;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "https://harena-ui-uehz.vercel.app/"})
public class PatrimoineController {
  private final PatrimoineService service;

  @GetMapping("/patrimoines")
  public GetPatrimoines200Response getPatrimoines(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
    List<Patrimoine> data = service.getPaginatedPatrimoines(page, pageSize);

    return new GetPatrimoines200Response().data(data);
  }

  @PutMapping("/patrimoines")
  public ResponseEntity<String> uploadPatrimoine(@RequestBody String fileContent) {
    if (fileContent.isEmpty()) {
      return ResponseEntity.badRequest().body("File content is empty");
    }

    try {
      File file = convertStringToFile(fileContent, "uploaded-file.txt");
      service.uploadPatrimoine(file, "test/" + file.getName());
      Files.delete(file.toPath());
      return ResponseEntity.ok("File uploaded successfully");
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
    }
  }

  private File convertStringToFile(String fileContent, String fileName) throws IOException {
    File file = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(fileContent.getBytes());
    }
    return file;
  }

  @GetMapping("/patrimoines/{name}")
  public ResponseEntity<Patrimoine> getPatrimoineByName(@PathVariable String name) {
    Patrimoine patrimoine = service.getPatrimoineByName(name);
    if (patrimoine != null) {
      return ResponseEntity.ok(patrimoine);
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
