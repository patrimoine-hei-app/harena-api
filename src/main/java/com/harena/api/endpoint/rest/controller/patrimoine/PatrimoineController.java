package com.harena.api.endpoint.rest.controller.patrimoine;

import com.harena.api.service.PatrimoineService;
import com.harena.api.endpoint.rest.model.GetPatrimoines200Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.hei.patrimoine.modele.Patrimoine;
import java.util.List;

@RestController
@AllArgsConstructor
public class PatrimoineController {
    private final PatrimoineService service;

    @GetMapping("/patrimoines")
    public GetPatrimoines200Response getPatrimoines(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
        List<Patrimoine> data = service.getPaginatedPatrimoines(page, pageSize);
        return new GetPatrimoines200Response().data(data);
    }

}
