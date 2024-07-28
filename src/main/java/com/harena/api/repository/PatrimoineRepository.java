package com.harena.api.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.harena.api.deserializer.PatrimoineDeserializer;
import com.harena.api.exception.InternalServerErrorException;
import com.harena.api.file.BucketComponent;
import com.harena.api.file.ExtendedBucket;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.serialisation.Serialiseur;

@Repository
public class PatrimoineRepository {
  private final ExtendedBucket bucketComponent;
  private final Serialiseur serialiseur;
  private final BucketComponent uploadComponent;
  private final ObjectMapper objectMapper;

  public PatrimoineRepository(ExtendedBucket bucketComponent, BucketComponent uploadComponent) {
    this.bucketComponent = bucketComponent;
    this.uploadComponent = uploadComponent;
    this.serialiseur = new Serialiseur<Patrimoine>();
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    this.objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    this.objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

    SimpleModule module = new SimpleModule();
    module.addDeserializer(Patrimoine.class, new PatrimoineDeserializer());
    this.objectMapper.registerModule(module);
  }

  public List<Patrimoine> getAllPaginatedPatrimoines(int limit, int offset) {
    List<File> patrimoineFiles = bucketComponent.getFilesFromS3(limit, offset);
    return patrimoineFiles.stream().map(this::createPatrimoineFrom).toList();
  }

  public Optional<Patrimoine> getPatrimoineByName(String name) {
    List<File> patrimoineFiles = bucketComponent.getFilesFromS3(Integer.MAX_VALUE, 0);
    return patrimoineFiles.stream()
            .map(this::createPatrimoineFrom)
            .filter(patrimoine -> patrimoine.nom().equals(name))
            .findFirst();
  }

  private Patrimoine createPatrimoineFrom(File patrimoineFile) {
    String patrimoineAsString;
    try {
      patrimoineAsString = Files.readString(patrimoineFile.toPath());
      return objectMapper.readValue(patrimoineAsString, Patrimoine.class);
    } catch (IOException e) {
      throw new InternalServerErrorException(e);
    }
  }

  public void uploadPatrimoine(File file, String bucketKey) {
    try {
      uploadComponent.upload(file, bucketKey);
    } catch (Exception e) {
      throw new InternalServerErrorException(e);
    }
  }
}
