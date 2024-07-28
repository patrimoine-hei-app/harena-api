package com.harena.api.service;

import com.harena.api.repository.PatrimoineRepository;
import java.io.File;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.patrimoine.modele.Patrimoine;

@Service
@AllArgsConstructor
public class PatrimoineService {
  private final PatrimoineRepository repository;

  public List<Patrimoine> getPaginatedPatrimoines(int page, int pageSize) {
    return repository.getAllPaginatedPatrimoines(pageSize, page);
  }

  public Optional<Patrimoine> getPatrimoineByName(String name) {
    return repository.getPatrimoineByName(name);
  }

  public void uploadPatrimoine(File file, String bucketKey) {
    repository.uploadPatrimoine(file, bucketKey);
  }
}
