package com.harena.api.patrimoine;

import com.harena.api.repository.PatrimoineRepository;
import com.harena.api.service.PatrimoineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.modele.Personne;
import school.hei.patrimoine.modele.possession.Argent;
import school.hei.patrimoine.modele.possession.Materiel;
import school.hei.patrimoine.modele.possession.Possession;

import java.io.File;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PatrimoineServiceTest {

    @Mock
    private PatrimoineRepository patrimoineRepository;

    @InjectMocks
    private PatrimoineService patrimoineService;

    private Patrimoine patrimoine1;
    private Patrimoine patrimoine2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create possessions
        Set<Possession> possessions1 = new HashSet<>();
        possessions1.add(new Argent("Argent 1", LocalDate.of(2024, 7, 26), 10000));
        possessions1.add(new Materiel("Materiel 1", LocalDate.of(2024, 7, 26), 5000, LocalDate.of(2023, 7, 26), 0.05));

        Set<Possession> possessions2 = new HashSet<>();
        possessions2.add(new Argent("Argent 2", LocalDate.of(2024, 7, 26), 20000));
        possessions2.add(new Materiel("Materiel 2", LocalDate.of(2024, 7, 26), 10000, LocalDate.of(2023, 7, 26), 0.05));

        // Create patrimoines
        patrimoine1 = new Patrimoine("Patrimoine1", new Personne("Fifa"), LocalDate.of(2024, 7, 26), possessions1);
        patrimoine2 = new Patrimoine("Patrimoine2", new Personne("Maharavo"), LocalDate.of(2024, 7, 26), possessions2);
    }

    @Test
    public void get_all_patrimoine_test() {
        List<Patrimoine> mockPatrimoines = List.of(patrimoine1, patrimoine2);

        when(patrimoineRepository.getAllPaginatedPatrimoines(anyInt(), anyInt())).thenReturn(mockPatrimoines);

        List<Patrimoine> result = patrimoineService.getPaginatedPatrimoines(0, 10);

        assertEquals(2, result.size());
        assertEquals("Patrimoine1", result.get(0).nom());
        assertEquals("Patrimoine2", result.get(1).nom());
        verify(patrimoineRepository, times(1)).getAllPaginatedPatrimoines(10, 0);
    }

    @Test
    public void get_patrimoine_by_name_test() {
        String name = "Patrimoine1";
        when(patrimoineRepository.getPatrimoineByName(name)).thenReturn(Optional.of(patrimoine1));

        Optional<Patrimoine> result = patrimoineService.getPatrimoineByName(name);

        assertTrue(result.isPresent());
        assertEquals(name, result.get().nom());
        verify(patrimoineRepository, times(1)).getPatrimoineByName(name);
    }

    @Test
    public void upload_patrimoine_test() {
        File file = new File("patrimoineFile");
        String bucketKey = "PatrimoineBucketKey";

        doNothing().when(patrimoineRepository).uploadPatrimoine(file, bucketKey);

        patrimoineService.uploadPatrimoine(file, bucketKey);

        verify(patrimoineRepository, times(1)).uploadPatrimoine(file, bucketKey);
    }
}

