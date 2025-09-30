package org.example.internal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


/*
 * Handler Thrift : implémentation en mémoire du service InternalCRM.
 * - Stocke les InternalLeadDTO dans une map
 * - fullname = "Nom, Prénom"
 * - méthodes basiques thread-safe
 */
public class InternalCRMHandler implements InternalCRM.Iface {

    private final Map<Long, InternalLeadDTO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public InternalCRMHandler() {
        // données d'exemple
        InternalLeadDTO sample = new InternalLeadDTO();
        sample.setFirstName("Jean");
        sample.setLastName("Dupont");
        sample.setCompanyName("Acme");
        sample.setAnnualRevenue(50000.0);
        sample.setPhone("+33123456789");
        sample.setStreet("1 rue Exemple");
        sample.setPostalCode("49100");
        sample.setCity("Angers");
        sample.setState("Maine-et-Loire");
        sample.setCountry("France");
        sample.setCreationDate("2024-09-01T10:00:00Z");
        createLead(sample);

    }

    @Override
    public List<InternalLeadDTO> findLeads(double lowAnnualRevenue, double highAnnualRevenue, String province) {
        // Filtrage en mémoire : revenus dans l'intervalle + état (province) optionnel
        List<InternalLeadDTO> result = new ArrayList<>();
        for (InternalLeadDTO l : store.values()) {
            double rev = l.getAnnualRevenue();
            if (rev >= lowAnnualRevenue && rev <= highAnnualRevenue) {
                if (province == null || province.isEmpty() || province.equalsIgnoreCase(l.getState())) {
                    // On renvoie une copie IMMUTABLE côté appelant + nom au format "Nom, Prénom"
                    result.add(cloneLead(l));
                }
            }
        }
        return result;
    }

    @Override
    public List<InternalLeadDTO> findLeadsByDate(String fromIso, String toIso) {
        // Filtrage simple lexicographique sur des dates ISO-8601 (format triable)
        List<InternalLeadDTO> result = new ArrayList<>();
        for (InternalLeadDTO l : store.values()) {
            String d = l.getCreationDate();
            if (d == null) continue; // on ignore si pas de date
            boolean after = (fromIso == null || fromIso.isEmpty()) || d.compareTo(fromIso) >= 0;
            boolean before = (toIso == null || toIso.isEmpty()) || d.compareTo(toIso) <= 0;
            if (after && before) result.add(cloneLead(l));
        }
        return result;
    }

    @Override
    public long createLead(InternalLeadDTO lead) {
        // Création : on garde les champs firstName / lastName séparés EN INTERNE
        // Le format fusionné "Nom, Prénom" est appliqué uniquement sur les objets retournés.
        long id = idGenerator.getAndIncrement();
        // Normaliser si nécessaire (ex: "Nom, Prénom" pour usage interne)
        if (lead.getFirstName() == null) lead.setFirstName("");
        if (lead.getLastName() == null) lead.setLastName("");
        // Stocker une copie pour éviter la mutation externe
        store.put(id, cloneLeadWithId(lead, id));
        return id;
    }

    @Override
    public void deleteLead(InternalLeadDTO leadDto) {
        // Suppression par ID interne, si fourni, sinon suppression par comparaison complète
        store.values().removeIf(l -> l.equals(leadDto));
    }

    // Clonage shallow avec ID interne
    private InternalLeadDTO cloneLeadWithId(InternalLeadDTO src, long id) {
        // Copie shallow des champs (id non exposé dans la structure générée)
        InternalLeadDTO c = new InternalLeadDTO();
        c.setFirstName(src.getFirstName());
        c.setLastName(src.getLastName());
        c.setCompanyName(src.getCompanyName());
        c.setAnnualRevenue(src.getAnnualRevenue());
        c.setPhone(src.getPhone());
        c.setStreet(src.getStreet());
        c.setPostalCode(src.getPostalCode());
        c.setCity(src.getCity());
        c.setState(src.getState());
        c.setCountry(src.getCountry());
        c.setCreationDate(src.getCreationDate());
        return c;
    }

    // Optionnel : clone sans changer ID
    private InternalLeadDTO cloneLead(InternalLeadDTO src) {
        InternalLeadDTO c = cloneLeadWithId(src, 0);
        // Appliquer la RÈGLE SPÉCIFIQUE (section 2.2) : concaténer Nom + ", " + Prénom
        // On utilise lastName, firstName internes pour fabriquer un seul champ restitué.
        String last = Optional.ofNullable(c.getLastName()).orElse("").trim();
        String first = Optional.ofNullable(c.getFirstName()).orElse("").trim();
        String full = (last.isEmpty() && first.isEmpty()) ? "" : last + ", " + first;
        // On place le résultat dans firstName (choix arbitraire) et on vide lastName pour forcer le consommateur
        // à parser ce champ unique.
        c.setFirstName(full);
        c.setLastName("");
        return c;
    }
}
