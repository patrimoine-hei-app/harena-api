package com.harena.api.file;

import com.harena.api.PojaGenerated;

@PojaGenerated
@SuppressWarnings("all")
public record FileHash(FileHashAlgorithm algorithm, String value) {}
