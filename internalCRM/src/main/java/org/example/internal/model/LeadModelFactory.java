package org.example.internal.model;

public class LeadModelFactory {
    private static final LeadModel INSTANCE = new LeadModelImpl();

    public static LeadModel getModel() {
        return INSTANCE;
    }
}
