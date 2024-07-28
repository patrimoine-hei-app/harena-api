package com.harena.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.modele.Personne;
import school.hei.patrimoine.modele.possession.Argent;
import school.hei.patrimoine.modele.possession.Materiel;
import school.hei.patrimoine.modele.possession.Possession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PatrimoineDeserializer extends JsonDeserializer<Patrimoine> {

    @Override
    public Patrimoine deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);

        String nom = node.get("nom").asText();
        LocalDate t = LocalDate.parse(node.get("t").asText(), DateTimeFormatter.ISO_LOCAL_DATE);

        JsonNode possesseurNode = node.get("possesseur");
        String possesseurNom = possesseurNode.get("nom").asText();
        Personne possesseur = new Personne(possesseurNom);

        Set<Possession> possessions = new HashSet<>();
        Iterator<JsonNode> elements = node.get("possessions").elements();
        while (elements.hasNext()) {
            JsonNode possessionNode = elements.next();
            String type = possessionNode.get("type").asText();

            String possessionNom = possessionNode.get("nom").asText();
            LocalDate possessionT = LocalDate.parse(possessionNode.get("t").asText(), DateTimeFormatter.ISO_LOCAL_DATE);
            int valeurComptable = possessionNode.get("valeurComptable").asInt();

            switch (type) {
                case "Argent":
                    possessions.add(new Argent(possessionNom, possessionT, valeurComptable));
                    break;
                case "Materiel":
                    LocalDate dateAcquisition = LocalDate.parse(possessionNode.get("dateAcquisition").asText(), DateTimeFormatter.ISO_LOCAL_DATE);
                    double tauxDAppreciationAnnuelle = possessionNode.get("tauxDAppreciationAnnuelle").asDouble();
                    possessions.add(new Materiel(possessionNom, possessionT, valeurComptable, dateAcquisition, tauxDAppreciationAnnuelle));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown possession type: " + type);
            }
        }

        return new Patrimoine(nom, possesseur, t, possessions);
    }
}
