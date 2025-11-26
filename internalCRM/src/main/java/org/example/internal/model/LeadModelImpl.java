package org.example.internal.model;

import org.example.internal.model.exception.NoSuchLeadException;
import org.example.internal.model.exception.WrongDateFormatException;
import org.example.internal.model.exception.WrongOrderForDateException;
import org.example.internal.model.exception.WrongOrderForRevenueException;
import org.example.internal.model.exception.WrongStateException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implémentation mémoire du modèle LeadModel.
 *
 * Caractéristiques :
 * - stockage concurrent via ConcurrentHashMap
 * - génération d'ID atomique
 * - copy-on-write : on stocke des copies pour éviter les effets de bord
 *
 * IMPORTANT : cette implémentation réalise les validations métiers et lance
 * les exceptions Thrift correspondantes (définies dans l'IDL).
 */
public class LeadModelImpl implements LeadModel {

    private final Map<Long, Lead> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Lead> findLeads(double low, double high, String state)
            throws WrongOrderForRevenueException, WrongStateException {
        /**
         * Recherche par fourchette de revenus et filtre optionnel par état.
         * - Validations : vérifie que low <= high et que l'état n'est pas invalide.
         * - Retourne des copies formatées (nom au format "Nom, Prénom").
         */
        // Validation : bornes
        if (low > high) {
            throw new WrongOrderForRevenueException("La borne basse est supérieure à la borne haute");
        }
        // Validation : état simple (exemple)
        if (state != null && state.matches("\\d+")) {
            throw new WrongStateException("État invalide fourni : " + state);
        }

        List<Lead> res = new ArrayList<>();
        for (Lead l : store.values()) {
            double r = l.getAnnualRevenue();
            if (r >= low && r <= high) {
                if (state == null || state.isEmpty() || state.equalsIgnoreCase(l.getState())) {
                    res.add(copyForReturn(l));
                }
            }
        }
        return res;
    }

    @Override
    public List<Lead> findLeadsByDate(java.util.Calendar from, java.util.Calendar to)
            throws WrongDateFormatException, WrongOrderForDateException {
        /**
         * Recherche par intervalle de dates (Calendar).
         * - Valide l'ordre from <= to si les deux fournis.
         * - Renvoie la liste des prospects correspondants (copies sécurisées).
         */
        if (from != null && to != null && from.after(to)) {
            throw new WrongOrderForDateException("La date de début est après la date de fin");
        }

        List<Lead> res = new ArrayList<>();
        for (Lead l : store.values()) {
            java.util.Calendar d = l.getCreationDate();
            if (d == null) continue;
            boolean after = (from == null) || !d.before(from);
            boolean before = (to == null) || !d.after(to);
            if (after && before) res.add(copyForReturn(l));
        }
        return res;
    }

    @Override
    public long createLead(Lead lead) throws WrongStateException {
        /**
         * Création d'un prospect en mémoire.
         * - Valide l'état (simple check) et génère un identifiant unique.
         * - Stocke une copie pour éviter effets de bord.
         */
        // Validation état
        if (lead == null) throw new WrongStateException("Lead vide");
        if (lead.getState() != null && lead.getState().matches("\\d+")) {
            throw new WrongStateException("État invalide fourni : " + lead.getState());
        }

        long id = idGenerator.getAndIncrement();
        Lead copy = copyForStorage(lead);
        copy.setId(id);
        store.put(id, copy);
        return id;
    }

    @Override
    public void deleteLead(Lead template) throws NoSuchLeadException {
        // Collecter les IDs à supprimer d'abord pour éviter les problèmes de modification concurrente
        java.util.List<Long> idsToRemove = new java.util.ArrayList<>();
        for (Lead candidate : store.values()) {
            if (equalsWithoutId(template, candidate)) {
                idsToRemove.add(candidate.getId());
            }
        }
        if (idsToRemove.isEmpty()) {
            throw new NoSuchLeadException("Aucun prospect correspondant trouvé pour suppression");
        }
        // Supprimer tous les leads correspondants
        for (Long id : idsToRemove) {
            store.remove(id);
        }
    }

    private Lead copyForStorage(Lead src) {
        Lead c = new Lead();
        c.setFirstName(src.getFirstName());
        c.setLastName(src.getLastName());
        c.setAnnualRevenue(src.getAnnualRevenue());
        c.setPhone(src.getPhone());
        c.setStreet(src.getStreet());
        c.setPostalCode(src.getPostalCode());
        c.setCity(src.getCity());
        c.setCountry(src.getCountry());
        // Copier le Calendar si présent
        java.util.Calendar cd = src.getCreationDate();
        if (cd != null) {
            java.util.Calendar copyCal = (java.util.Calendar) cd.clone();
            c.setCreationDate(copyCal);
        } else {
            c.setCreationDate(null);
        }
        c.setCompanyName(src.getCompanyName());
        c.setState(src.getState());
        return c;
    }

    private Lead copyForReturn(Lead src) {
        Lead c = copyForStorage(src);
        // Mise en forme : "Nom, Prénom" dans firstName, lastName vidé
        String nom = Optional.ofNullable(c.getLastName()).orElse("").trim();
        String prenom = Optional.ofNullable(c.getFirstName()).orElse("").trim();
        String nomComplet = (nom.isEmpty() && prenom.isEmpty()) ? "" : nom + ", " + prenom;
        c.setFirstName(nomComplet);
        c.setLastName("");
        c.setId(src.getId());
        return c;
    }

    /**
     * Compare deux leads en ignorant l'ID.
     * Pour la suppression, on compare SEULEMENT les champs renseignés du template.
     * Si un champ est null/vide dans le template, il est ignoré (pas de comparaison).
     * 
     * @param template Le template de recherche (peut avoir des champs vides)
     * @param candidate Le lead candidat à comparer
     * @return true si tous les champs renseignés du template correspondent
     */
    private boolean equalsWithoutId(Lead template, Lead candidate) {
        if (template == null || candidate == null) return false;
        
        // Vérifier qu'au moins firstName OU lastName est renseigné (obligatoire)
        boolean hasFirstName = template.getFirstName() != null && !template.getFirstName().trim().isEmpty();
        boolean hasLastName = template.getLastName() != null && !template.getLastName().trim().isEmpty();
        
        if (!hasFirstName && !hasLastName) {
            // Aucun nom/prénom renseigné, impossible de faire une correspondance
            return false;
        }
        
        // Comparer firstName si renseigné
        if (hasFirstName && !safeEq(template.getFirstName(), candidate.getFirstName())) {
            return false;
        }
        
        // Comparer lastName si renseigné
        if (hasLastName && !safeEq(template.getLastName(), candidate.getLastName())) {
            return false;
        }
        
        // Comparer annualRevenue SEULEMENT si défini (>= 0.0 signifie défini, < 0.0 = sentinelle "non défini")
        if (template.getAnnualRevenue() >= 0.0) {
            if (Double.compare(template.getAnnualRevenue(), candidate.getAnnualRevenue()) != 0) {
                return false;
            }
        }
        // Sinon, on ignore ce champ (pas de comparaison)
        
        // Comparer les autres champs string SEULEMENT s'ils sont renseignés
        if (template.getPhone() != null && !template.getPhone().trim().isEmpty()) {
            if (!safeEq(template.getPhone(), candidate.getPhone())) return false;
        }
        
        if (template.getStreet() != null && !template.getStreet().trim().isEmpty()) {
            if (!safeEq(template.getStreet(), candidate.getStreet())) return false;
        }
        
        if (template.getPostalCode() != null && !template.getPostalCode().trim().isEmpty()) {
            if (!safeEq(template.getPostalCode(), candidate.getPostalCode())) return false;
        }
        
        if (template.getCity() != null && !template.getCity().trim().isEmpty()) {
            if (!safeEq(template.getCity(), candidate.getCity())) return false;
        }
        
        if (template.getCountry() != null && !template.getCountry().trim().isEmpty()) {
            if (!safeEq(template.getCountry(), candidate.getCountry())) return false;
        }
        
        if (template.getCompanyName() != null && !template.getCompanyName().trim().isEmpty()) {
            if (!safeEq(template.getCompanyName(), candidate.getCompanyName())) return false;
        }
        
        if (template.getState() != null && !template.getState().trim().isEmpty()) {
            if (!safeEq(template.getState(), candidate.getState())) return false;
        }
        
        // Comparer creationDate SEULEMENT si renseignée
        if (template.getCreationDate() != null) {
            if (!safeCalEq(template.getCreationDate(), candidate.getCreationDate())) {
                return false;
            }
        }
        // Sinon, on ignore ce champ (pas de comparaison)
        
        // Tous les champs renseignés correspondent
        return true;
    }

    private boolean safeEq(String x, String y) {
        if (x == null) return y == null;
        return x.equals(y);
    }

    private boolean safeCalEq(java.util.Calendar a, java.util.Calendar b) {
        if (a == null) return b == null;
        if (b == null) return false;
        return a.getTimeInMillis() == b.getTimeInMillis();
    }
}
