package org.example.internal.model;

/**
 * Factory pour obtenir une instance unique (Singleton) du modèle LeadModel.
 * 
 * Usage :
 *   LeadModel model = LeadModelFactory.getModel();
 * 
 * Cette approche garantit que tous les services/handlers utilisent
 * la même instance de stockage en mémoire.
 */
public class LeadModelFactory {
    private static final LeadModel INSTANCE = new LeadModelImpl();

    public static LeadModel getModel() {
        return INSTANCE;
    }
}
