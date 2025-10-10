package org.example.internal.model;

import org.example.internal.model.exception.NoSuchLeadException;

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
 */
public class LeadModelImpl implements LeadModel {

    private final Map<Long, Lead> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Lead findById(long id) throws NoSuchLeadException {
        Lead l = store.get(id);
        if (l == null) throw new NoSuchLeadException("Lead not found: " + id);
        return l;
    }

    @Override
    public List<Lead> findByRevenueRange(double low, double high, String state) {
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
    public List<Lead> findByDateRange(String fromIso, String toIso) {
        List<Lead> res = new ArrayList<>();
        for (Lead l : store.values()) {
            String d = l.getCreationDate();
            if (d == null) continue;
            boolean after = (fromIso == null || fromIso.isEmpty()) || d.compareTo(fromIso) >= 0;
            boolean before = (toIso == null || toIso.isEmpty()) || d.compareTo(toIso) <= 0;
            if (after && before) res.add(copyForReturn(l));
        }
        return res;
    }

    @Override
    public long createLead(Lead lead) {
        long id = idGenerator.getAndIncrement();
        Lead copy = copyForStorage(lead);
        copy.setId(id);
        store.put(id, copy);
        return id;
    }

    @Override
    public void deleteLead(long id) throws NoSuchLeadException {
        if (store.remove(id) == null) throw new NoSuchLeadException("Lead not found: " + id);
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
        c.setCreationDate(src.getCreationDate());
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
}
