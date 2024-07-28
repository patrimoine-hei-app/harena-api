package com.harena.api.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

@Component
public class ExtendedBucket {
  private final BucketComponent bucketComponent;
  private final S3Client s3Client;

  public ExtendedBucket(BucketComponent bucketComponent, BucketConf bucketConf) {
    this.bucketComponent = bucketComponent;
    this.s3Client = bucketConf.getS3Client();
  }

  public List<File> getFilesFromS3(int limit, int offset) {
    List<File> results = new ArrayList<>();
    ListObjectsV2Request listObjectsV2Request =
            ListObjectsV2Request.builder()
                    .bucket(bucketComponent.getBucketName())
                    .maxKeys(limit)
                    .build();
    ListObjectsV2Iterable listObjectsV2Iterable =
            s3Client.listObjectsV2Paginator(listObjectsV2Request);
    int currentPage = 0;

    for (ListObjectsV2Response page : listObjectsV2Iterable) {
      currentPage++;
      if (currentPage == offset + 1) {
        page.contents().stream()
                .forEach(
                        object -> {
                          results.add(bucketComponent.download(object.key()));
                        });
      }
    }

    return results;
  }
}