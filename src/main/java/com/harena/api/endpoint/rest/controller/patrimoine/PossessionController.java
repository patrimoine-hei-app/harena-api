package com.harena.api.endpoint.rest.controller.patrimoine;

import com.harena.api.service.PatrimoineService;
import com.harena.api.service.PossessionService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.modele.possession.Possession;

@RestController
@AllArgsConstructor
public class PossessionController {
  private final PossessionService service;
  private final PatrimoineService patrimoineService;
  private static final Logger LOGGER = Logger.getLogger(PossessionController.class.getName());

  private File convertStringToFile(String fileContent, String fileName) throws IOException {
    File file = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(fileContent.getBytes());
    }
    return file;
  }

  @PutMapping("/patrimoines/{nom_patrimoine}/possession")
  public ResponseEntity<String> uploadPossessions(@PathVariable String nom_patrimoine) {
    LOGGER.info("Received request to upload possessions for patrimoine: " + nom_patrimoine);
    Optional<Patrimoine> patrimoine = patrimoineService.getPatrimoineByName(nom_patrimoine);

    if (patrimoine.isPresent()) {
      List<Possession> possessionList =
          patrimoine.get().possessions().stream().collect(Collectors.toList());
      for (Possession possession : possessionList) {
        String possessionContent = possession.toString();
        if (possessionContent.isEmpty()) {
          LOGGER.warning("Possession content is empty for possession: " + possession);
          return ResponseEntity.badRequest().body("Possession content is empty");
        }
        try {
          File file = convertStringToFile(possessionContent, "possession.txt");
          service.uploadPossession(file, "possessions/" + file.getName());
          Files.delete(file.toPath());
        } catch (IOException e) {
          LOGGER.severe("Error uploading possession: " + e.getMessage());
          return ResponseEntity.status(500).body("Error uploading possession: " + e.getMessage());
        }
      }
      LOGGER.info("Possessions uploaded successfully for patrimoine: " + nom_patrimoine);
      return ResponseEntity.ok("Possessions uploaded successfully");
    } else {
      LOGGER.warning("Patrimoine not found: " + nom_patrimoine);
      return ResponseEntity.badRequest().body("Patrimoine not found");
    }
  }
}
