package org.example;

import org.example.internal.InternalLeadDTO;

import java.util.Scanner;

/**
 * Application en ligne de commande pour :
 *   - merge : importer tous les leads Salesforce vers InternalCRM
 *   - add   : ajouter manuellement un lead dans InternalCRM
 *   - delete: supprimer manuellement un lead dans InternalCRM
 *
 * Usage typique :
 *   ./gradlew :manage:run --args='merge'
 *   ./gradlew :manage:run --args='merge --clear-internal'
 *   ./gradlew :manage:run --args='add'
 *   ./gradlew :manage:run --args='delete'
 */
public class LeadMergerApp {

    private static final String DEFAULT_INTERNAL_HOST = "localhost";
    private static final int DEFAULT_INTERNAL_PORT = 9090;

    public static void main(String[] args) {
        String mode = (args.length > 0) ? args[0].toLowerCase() : "merge";
        boolean clearInternal = false;
        for (String arg : args) {
            if ("--clear-internal".equalsIgnoreCase(arg)) {
                clearInternal = true;
            }
        }

        try (InternalCRMThriftClient internalClient =
                     new InternalCRMThriftClient(DEFAULT_INTERNAL_HOST, DEFAULT_INTERNAL_PORT)) {

            SalesforceLeadProvider sfProvider = new StubSalesforceLeadProvider();
            LeadMerger merger = new LeadMerger(internalClient, sfProvider);

            switch (mode) {
                case "merge":
                    runMerge(merger, clearInternal);
                    break;
                case "add":
                    runAdd(merger);
                    break;
                case "delete":
                    runDelete(merger);
                    break;
                default:
                    printUsage();
                    break;
            }

        } catch (Exception e) {
            System.err.println("Erreur dans LeadMergerApp : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runMerge(LeadMerger merger, boolean clearInternal) throws Exception {
        System.out.println("=== Merge Salesforce -> InternalCRM ===");
        if (clearInternal) {
            System.out.println("(Option) Suppression des leads existants activée.");
        }
        merger.mergeLeads(clearInternal);
    }

    private static void runAdd(LeadMerger merger) throws Exception {
        System.out.println("=== Ajout manuel d'un Lead dans InternalCRM ===");
        Scanner sc = new Scanner(System.in);

        InternalLeadDTO dto = new InternalLeadDTO();

        System.out.print("Prénom : ");
        dto.setFirstName(sc.nextLine().trim());

        System.out.print("Nom : ");
        dto.setLastName(sc.nextLine().trim());

        System.out.print("Entreprise : ");
        dto.setCompanyName(sc.nextLine().trim());

        System.out.print("Revenu annuel (nombre) : ");
        String revStr = sc.nextLine().trim();
        double rev = 0.0;
        if (!revStr.isEmpty()) {
            rev = Double.parseDouble(revStr);
        }
        dto.setAnnualRevenue(rev);

        System.out.print("Téléphone : ");
        dto.setPhone(sc.nextLine().trim());

        System.out.print("Rue : ");
        dto.setStreet(sc.nextLine().trim());

        System.out.print("Code postal : ");
        dto.setPostalCode(sc.nextLine().trim());

        System.out.print("Ville : ");
        dto.setCity(sc.nextLine().trim());

        System.out.print("Département / État : ");
        dto.setState(sc.nextLine().trim());

        System.out.print("Pays : ");
        dto.setCountry(sc.nextLine().trim());

        System.out.print("Date de création (ISO-8601, ex: 2024-09-15T10:00:00Z) : ");
        dto.setCreationDate(sc.nextLine().trim());

        long id = merger.addLead(dto);
        System.out.println("Lead créé avec succès. ID généré = " + id);
    }

    private static void runDelete(LeadMerger merger) throws Exception {
        System.out.println("=== Suppression manuelle d'un Lead dans InternalCRM ===");
        System.out.println("ATTENTION : la suppression se fait par correspondance EXACTE du template.");
        Scanner sc = new Scanner(System.in);

        InternalLeadDTO dto = new InternalLeadDTO();

        System.out.print("Prénom (laisser vide si inconnu) : ");
        dto.setFirstName(emptyToNull(sc.nextLine()));

        System.out.print("Nom (laisser vide si inconnu) : ");
        dto.setLastName(emptyToNull(sc.nextLine()));

        System.out.print("Entreprise (laisser vide si inconnu) : ");
        dto.setCompanyName(emptyToNull(sc.nextLine()));

        System.out.print("Revenu annuel (laisser vide si inconnu) : ");
        String revStr = sc.nextLine().trim();
        if (revStr.isEmpty()) {
            dto.setAnnualRevenue(0.0);
        } else {
            dto.setAnnualRevenue(Double.parseDouble(revStr));
        }

        System.out.print("Téléphone (laisser vide si inconnu) : ");
        dto.setPhone(emptyToNull(sc.nextLine()));

        System.out.print("Rue (laisser vide si inconnu) : ");
        dto.setStreet(emptyToNull(sc.nextLine()));

        System.out.print("Code postal (laisser vide si inconnu) : ");
        dto.setPostalCode(emptyToNull(sc.nextLine()));

        System.out.print("Ville (laisser vide si inconnu) : ");
        dto.setCity(emptyToNull(sc.nextLine()));

        System.out.print("Département / État (laisser vide si inconnu) : ");
        dto.setState(emptyToNull(sc.nextLine()));

        System.out.print("Pays (laisser vide si inconnu) : ");
        dto.setCountry(emptyToNull(sc.nextLine()));

        System.out.print("Date de création (laisser vide si inconnu) : ");
        dto.setCreationDate(emptyToNull(sc.nextLine()));

        System.out.println("Tentative de suppression du lead correspondant...");
        merger.deleteLead(dto);
        System.out.println("Suppression effectuée (si un lead correspondant existait).");
    }

    private static String emptyToNull(String s) {
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static void printUsage() {
        System.out.println("Usage :");
        System.out.println("  merge [--clear-internal]   : importe tous les leads Salesforce vers InternalCRM");
        System.out.println("  add                         : ajoute manuellement un lead dans InternalCRM");
        System.out.println("  delete                      : supprime manuellement un lead par template");
        System.out.println();
        System.out.println("Exemples :");
        System.out.println("  ./gradlew :manage:run --args='merge'");
        System.out.println("  ./gradlew :manage:run --args='merge --clear-internal'");
        System.out.println("  ./gradlew :manage:run --args='add'");
        System.out.println("  ./gradlew :manage:run --args='delete'");
    }
}

