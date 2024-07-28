package com.harena.api.service;

import com.harena.api.repository.PossessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@AllArgsConstructor
public class PossessionService {
    private final PossessionRepository possessionRepository;

    public void uploadPossession(File file, String bucketKey) {
        possessionRepository.uploadPossession(file, bucketKey);
    }
}
