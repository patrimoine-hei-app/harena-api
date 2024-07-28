package com.harena.api.repository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.harena.api.deserializer.PatrimoineDeserializer;
import com.harena.api.exception.InternalServerErrorException;
import com.harena.api.file.BucketComponent;
import com.harena.api.file.ExtendedBucket;
import org.springframework.stereotype.Repository;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.modele.possession.Possession;
import school.hei.patrimoine.serialisation.Serialiseur;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Repository
public class PossessionRepository {
    private final ExtendedBucket bucketComponent;
    private final Serialiseur serialiseur;
    private final BucketComponent uploadComponent;
    private final ObjectMapper objectMapper;

    public PossessionRepository(ExtendedBucket bucketComponent, BucketComponent uploadComponent) {
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


    public void uploadPossession(File file, String bucketKey) {
        try {
            uploadComponent.upload(file, bucketKey);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }
}