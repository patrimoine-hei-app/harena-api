package com.harena.api.endpoint.rest.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.hei.patrimoine.modele.Patrimoine;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPatrimoines200Response {
  private List<Patrimoine> data;
  private int currentPage;
  private int totalPages;
  private long totalItems;

  public GetPatrimoines200Response data(List<Patrimoine> data) {
    this.data = data;
    return this;
  }
}
