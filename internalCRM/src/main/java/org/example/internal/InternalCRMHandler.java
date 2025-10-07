package org.example.internal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 * CRM INTERNE : stocke et filtre les prospects commerciaux en mémoire
 * 
 * QUE FAIT CE SERVICE :
 *   - Garde une liste de prospects (nom, entreprise, chiffre d'affaires, région...)
 *   - Trouve les prospects par critères (revenus entre X et Y, région donnée)
 *   - Trouve les prospects par date de création
 *   - Ajoute/supprime des prospects
 * 
 * EXEMPLE D'USAGE :
 *   findLeads(50000, 150000, "Loire-Atlantique") 
 *   → tous les prospects entre 50k€ et 150k€ en Loire-Atlantique
 */
public class InternalCRMHandler implements InternalCRM.Iface {

    private final Map<Long, InternalLeadDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public InternalCRMHandler() {
        // On crée un prospect d'exemple pour tester
        InternalLeadDTO exemple = new InternalLeadDTO();
        exemple.setFirstName("Jean");
        exemple.setLastName("Dupont");
        exemple.setCompanyName("Acme");
        exemple.setAnnualRevenue(50000.0);  // 50k€ de chiffre d'affaires
        exemple.setPhone("+33123456789");
        exemple.setStreet("1 rue Exemple");
        exemple.setPostalCode("49100");
        exemple.setCity("Angers");
        exemple.setState("Maine-et-Loire");    // région/département
        exemple.setCountry("France");
        exemple.setCreationDate("2024-09-01T10:00:00Z");  // format ISO (2024-09-01 à 10h)
        createLead(exemple);
    }

    /**
     * RECHERCHE PAR REVENUS : trouve les prospects dans une fourchette de chiffre d'affaires
     * @param lowAnnualRevenue minimum (ex: 50000 = 50k€)
     * @param highAnnualRevenue maximum (ex: 150000 = 150k€)  
     * @param province région/département (ex: "Loire-Atlantique") ou null pour toutes
     * @return liste des prospects qui correspondent
     */
    @Override
    public List<InternalLeadDTO> findLeads(double lowAnnualRevenue, double highAnnualRevenue, String province) {
        // On regarde tous nos prospects stockés
        List<InternalLeadDTO> result = new ArrayList<>();
        for (InternalLeadDTO prospect : store.values()) {
            double revenus = prospect.getAnnualRevenue();
            
            // 1. Vérifier si les revenus sont dans la fourchette
            if (revenus >= lowAnnualRevenue && revenus <= highAnnualRevenue) {
                // 2. Vérifier la région (si précisée)
                if (province == null || province.isEmpty() || province.equalsIgnoreCase(prospect.getState())) {
                    // 3. Format spécial pour le retour : "Nom, Prénom" dans un seul champ
                    result.add(cloneLead(prospect));
                }
            }
        }
        return result;
    }

    /**
     * RECHERCHE PAR DATES : trouve les prospects créés dans une période
     * @param fromIso date de début (ex: "2024-01-01T00:00:00Z") ou null pour "depuis toujours"
     * @param toIso date de fin (ex: "2024-12-31T23:59:59Z") ou null pour "jusqu'à maintenant"
     * @return liste des prospects créés dans cette période
     */
    @Override
    public List<InternalLeadDTO> findLeadsByDate(String fromIso, String toIso) {
        // Astuce : on compare les dates en texte (format ISO = triable alphabétiquement)
        List<InternalLeadDTO> result = new ArrayList<>();
        for (InternalLeadDTO prospect : store.values()) {
            String dateCreation = prospect.getCreationDate();
            if (dateCreation == null) continue; // pas de date = on ignore
            
            // Vérifier si la date est après le début (ou pas de limite de début)
            boolean apresDebut = (fromIso == null || fromIso.isEmpty()) || dateCreation.compareTo(fromIso) >= 0;
            // Vérifier si la date est avant la fin (ou pas de limite de fin)
            boolean avantFin = (toIso == null || toIso.isEmpty()) || dateCreation.compareTo(toIso) <= 0;
            
            if (apresDebut && avantFin) {
                result.add(cloneLead(prospect));
            }
        }
        return result;
    }

    /**
     * CRÉER UN PROSPECT : ajoute un nouveau prospect dans notre base
     * @param lead données du prospect (nom, entreprise, revenus, région...)
     * @return ID unique du prospect créé (1, 2, 3...)
     */
    @Override
    public long createLead(InternalLeadDTO lead) {
        // Générer un ID unique pour ce prospect (1, 2, 3...)
        long id = idGenerator.getAndIncrement();
        
        // Vérifier que les champs obligatoires sont remplis
        if (lead.getFirstName() == null) lead.setFirstName("");
        if (lead.getLastName() == null) lead.setLastName("");
        
        // Sauvegarder une copie du prospect (pour éviter les modifications externes)
        store.put(id, cloneLeadWithId(lead, id));
        return id;
    }

    /**
     * SUPPRIMER UN PROSPECT : enlève un prospect de notre base
     * @param leadDto prospect à supprimer (tous les champs doivent correspondre)
     */
    @Override
    public void deleteLead(InternalLeadDTO leadDto) {
        // Supprimer tous les prospects qui correspondent exactement
        store.values().removeIf(prospect -> prospect.equals(leadDto));
    }

    // MÉTHODES UTILITAIRES (pour la gestion interne)
    
    // Copie un prospect avec un ID spécifique
    private InternalLeadDTO cloneLeadWithId(InternalLeadDTO src, long id) {
        // Faire une copie de tous les champs du prospect
        InternalLeadDTO copie = new InternalLeadDTO();
        copie.setFirstName(src.getFirstName());
        copie.setLastName(src.getLastName());
        copie.setCompanyName(src.getCompanyName());
        copie.setAnnualRevenue(src.getAnnualRevenue());
        copie.setPhone(src.getPhone());
        copie.setStreet(src.getStreet());
        copie.setPostalCode(src.getPostalCode());
        copie.setCity(src.getCity());
        copie.setState(src.getState());
        copie.setCountry(src.getCountry());
        copie.setCreationDate(src.getCreationDate());
        return copie;
    }

    // Copie un prospect avec format spécial "Nom, Prénom"
    private InternalLeadDTO cloneLead(InternalLeadDTO src) {
        InternalLeadDTO copie = cloneLeadWithId(src, 0);
        
        // RÈGLE SPÉCIALE : fusionner nom + prénom en un seul champ "Dupont, Jean"
        String nom = Optional.ofNullable(copie.getLastName()).orElse("").trim();
        String prenom = Optional.ofNullable(copie.getFirstName()).orElse("").trim();
        String nomComplet = (nom.isEmpty() && prenom.isEmpty()) ? "" : nom + ", " + prenom;
        
        // On met le nom complet dans firstName et on vide lastName
        copie.setFirstName(nomComplet);
        copie.setLastName("");
        return copie;
    }
}
