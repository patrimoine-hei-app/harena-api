package com.harena.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.modele.Personne;
import school.hei.patrimoine.modele.possession.Argent;
import school.hei.patrimoine.modele.possession.Materiel;
import school.hei.patrimoine.modele.possession.Possession;

public class PatrimoineDeserializer extends JsonDeserializer<Patrimoine> {

  @Override
  public Patrimoine deserialize(JsonParser p, DeserializationContext ctxt)
          throws IOException, JsonProcessingException {
    JsonNode node = p.getCodec().readTree(p);

    String nom = node.has("nom") ? node.get("nom").asText() : "DefaultNom";
    LocalDate t = node.has("t") ? LocalDate.parse(node.get("t").asText(), DateTimeFormatter.ISO_LOCAL_DATE) : LocalDate.now();

    JsonNode possesseurNode = node.get("possesseur");
    String possesseurNom = (possesseurNode != null && possesseurNode.has("nom")) ? possesseurNode.get("nom").asText() : "DefaultPerson";
    Personne possesseur = new Personne(possesseurNom);

    Set<Possession> possessions = new HashSet<>();
    JsonNode possessionsNode = node.get("possessions");
    if (possessionsNode != null && possessionsNode.isArray()) {
      Iterator<JsonNode> elements = possessionsNode.elements();
      while (elements.hasNext()) {
        JsonNode possessionNode = elements.next();
        String type = possessionNode.has("type") ? possessionNode.get("type").asText() : "Unknown";

        String possessionNom = possessionNode.has("nom") ? possessionNode.get("nom").asText() : "DefaultPossession";
        LocalDate possessionT = possessionNode.has("t") ? LocalDate.parse(possessionNode.get("t").asText(), DateTimeFormatter.ISO_LOCAL_DATE) : LocalDate.now();
        int valeurComptable = possessionNode.has("valeurComptable") ? possessionNode.get("valeurComptable").asInt() : 0;

        switch (type) {
          case "Argent":
            possessions.add(new Argent(possessionNom, possessionT, valeurComptable));
            break;
          case "Materiel":
            LocalDate dateAcquisition = possessionNode.has("dateAcquisition") ? LocalDate.parse(possessionNode.get("dateAcquisition").asText(), DateTimeFormatter.ISO_LOCAL_DATE) : LocalDate.now();
            double tauxDAppreciationAnnuelle = possessionNode.has("tauxDAppreciationAnnuelle") ? possessionNode.get("tauxDAppreciationAnnuelle").asDouble() : 0.0;
            possessions.add(new Materiel(possessionNom, possessionT, valeurComptable, dateAcquisition, tauxDAppreciationAnnuelle));
            break;
          default:
            throw new IllegalArgumentException("Unknown possession type: " + type);
        }
      }
    }
    return new Patrimoine(nom, possesseur, t, possessions);
  }
}
