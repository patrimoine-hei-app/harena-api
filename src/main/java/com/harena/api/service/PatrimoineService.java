package com.harena.api.service;

import com.harena.api.repository.PatrimoineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.patrimoine.modele.Patrimoine;

import java.io.File;
import java.util.List;

@Service
@AllArgsConstructor
public class PatrimoineService {
    private final PatrimoineRepository repository;

    public List<Patrimoine> getPaginatedPatrimoines(int page, int pageSize) {
        return repository.getAllPaginatedPatrimoines(pageSize, page);
    }
    public void uploadPatrimoine(File file, String bucketKey) {
        repository.uploadPatrimoine(file, bucketKey);
    }
}
