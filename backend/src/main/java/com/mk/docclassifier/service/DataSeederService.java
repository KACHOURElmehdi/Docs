package com.mk.docclassifier.service;

import com.mk.docclassifier.domain.entity.Category;
import com.mk.docclassifier.domain.entity.Tag;
import com.mk.docclassifier.repository.CategoryRepository;
import com.mk.docclassifier.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSeederService {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void seedCategoriesAndTags() {
        log.info("Starting to seed categories and tags...");
        
        // Check if already seeded
        if (categoryRepository.count() > 5 || tagRepository.count() > 5) {
            log.info("Database already contains data. Skipping seed.");
            return;
        }
        
        seedCategories();
        seedTags();
        
        log.info("Seeding completed successfully!");
    }

    private void seedCategories() {
        List<Category> categories = new ArrayList<>();

        // 📁 Administration
        categories.add(createCategory("Contrats", "Tous les contrats et accords"));
        categories.add(createCategory("Factures", "Factures émises et reçues"));
        categories.add(createCategory("Devis", "Devis et estimations"));
        categories.add(createCategory("Reçus", "Reçus et justificatifs"));
        categories.add(createCategory("Documents légaux", "Documents juridiques administratifs"));
        categories.add(createCategory("Documents fiscaux", "Déclarations et documents fiscaux"));
        categories.add(createCategory("Procédures internes", "Procédures et politiques internes"));

        // 🧾 Finance
        categories.add(createCategory("Comptabilité", "Documents comptables"));
        categories.add(createCategory("Budget", "Budgets et prévisions financières"));
        categories.add(createCategory("Paiements", "Ordres de paiement et virements"));
        categories.add(createCategory("Audit financier", "Rapports d'audit financier"));
        categories.add(createCategory("Déclarations fiscales", "Déclarations TVA, impôts, taxes"));

        // ⚖️ Juridique
        categories.add(createCategory("NDA", "Accords de confidentialité"));
        categories.add(createCategory("Contrats juridiques", "Contrats légaux et accords"));
        categories.add(createCategory("Litiges", "Documents de litiges et contentieux"));
        categories.add(createCategory("Propriété intellectuelle", "Brevets, marques, droits d'auteur"));
        categories.add(createCategory("Conformité RGPD", "Documents de conformité et protection des données"));

        // 👥 Ressources Humaines
        categories.add(createCategory("Dossiers employés", "Dossiers personnels des employés"));
        categories.add(createCategory("Onboarding", "Documents d'intégration nouveaux employés"));
        categories.add(createCategory("Offboarding", "Documents de départ et fin de contrat"));
        categories.add(createCategory("Paie", "Fiches de paie et documents salariaux"));
        categories.add(createCategory("Congés", "Demandes de congés et absences"));
        categories.add(createCategory("Formations", "Documents de formation et certifications"));

        // 📊 Projets
        categories.add(createCategory("Cahier des charges", "Spécifications et exigences projet"));
        categories.add(createCategory("Planning", "Plannings et calendriers projet"));
        categories.add(createCategory("Documentation technique", "Documentation technique détaillée"));
        categories.add(createCategory("Documentation fonctionnelle", "Spécifications fonctionnelles"));
        categories.add(createCategory("Livrables", "Livrables et résultats projet"));
        categories.add(createCategory("Comptes-rendus réunions", "CR de réunions et décisions"));
        categories.add(createCategory("Tests QA", "Documents de tests et assurance qualité"));

        // 🖥️ Technique / IT
        categories.add(createCategory("Architecture", "Architecture système et infrastructure"));
        categories.add(createCategory("API", "Documentation API et endpoints"));
        categories.add(createCategory("Serveurs", "Configuration et maintenance serveurs"));
        categories.add(createCategory("DevOps", "Documentation DevOps et déploiement"));
        categories.add(createCategory("Backups", "Sauvegardes et restaurations"));
        categories.add(createCategory("Sécurité IT", "Sécurité informatique et protocoles"));
        categories.add(createCategory("Certificats Clés", "Certificats SSL et clés d'accès"));

        // 🎨 Créatif / Médiatique
        categories.add(createCategory("Artwork", "Créations graphiques et visuelles"));
        categories.add(createCategory("Collections", "Collections et portfolios"));
        categories.add(createCategory("Conception UX-UI", "Design d'interface et expérience utilisateur"));
        categories.add(createCategory("Prototypes Maquettes", "Prototypes et maquettes design"));
        categories.add(createCategory("Médias", "Images, vidéos et contenus multimédias"));

        // 🗃️ Templates / Modèles
        categories.add(createCategory("Modèles contrats", "Templates de contrats réutilisables"));
        categories.add(createCategory("Modèles factures", "Templates de factures"));
        categories.add(createCategory("Modèles internes", "Templates pour usage interne"));

        // 🏛️ Entreprise
        categories.add(createCategory("Marketing", "Documents marketing et communication"));
        categories.add(createCategory("Vente", "Documents commerciaux et ventes"));
        categories.add(createCategory("Support", "Documentation support client"));
        categories.add(createCategory("Fournisseurs", "Documents fournisseurs et prestataires"));
        categories.add(createCategory("Partenaires", "Documents partenariats et collaborations"));
        categories.add(createCategory("Stock Inventaire", "Gestion stock et inventaires"));

        // 📦 Divers
        categories.add(createCategory("Brouillons", "Documents en cours de rédaction"));
        categories.add(createCategory("Archives", "Documents archivés"));
        categories.add(createCategory("Documents personnels", "Documents personnels et divers"));

        categoryRepository.saveAll(categories);
        log.info("Inserted {} categories", categories.size());
    }

    private void seedTags() {
        List<Tag> tags = new ArrayList<>();

        // 🔥 Tags généraux
        tags.add(createTag("Urgent"));
        tags.add(createTag("Important"));
        tags.add(createTag("À vérifier"));
        tags.add(createTag("À signer"));
        tags.add(createTag("À archiver"));
        tags.add(createTag("En attente"));
        tags.add(createTag("Terminé"));
        tags.add(createTag("Interne"));
        tags.add(createTag("Externe"));

        // 🏢 Tags entreprise
        tags.add(createTag("Marketing"));
        tags.add(createTag("Finance"));
        tags.add(createTag("RH"));
        tags.add(createTag("Juridique"));
        tags.add(createTag("Vente"));
        tags.add(createTag("Client"));
        tags.add(createTag("Fournisseur"));
        tags.add(createTag("Partenaire"));

        // 🛠️ Tags techniques
        tags.add(createTag("Backend"));
        tags.add(createTag("Frontend"));
        tags.add(createTag("API"));
        tags.add(createTag("Serveur"));
        tags.add(createTag("DevOps"));
        tags.add(createTag("Base de données"));
        tags.add(createTag("Sécurité"));
        tags.add(createTag("Docker"));
        tags.add(createTag("Kubernetes"));
        tags.add(createTag("CI-CD"));

        // 📅 Tags temporels
        tags.add(createTag("2025"));
        tags.add(createTag("2024"));
        tags.add(createTag("2023"));
        tags.add(createTag("Q1"));
        tags.add(createTag("Q2"));
        tags.add(createTag("Q3"));
        tags.add(createTag("Q4"));

        // 🎨 Tags créatifs
        tags.add(createTag("Design"));
        tags.add(createTag("UX"));
        tags.add(createTag("UI"));
        tags.add(createTag("Media"));
        tags.add(createTag("Artwork"));
        tags.add(createTag("Prototype"));

        // 📄 Tags documentaires
        tags.add(createTag("PDF"));
        tags.add(createTag("Word"));
        tags.add(createTag("Excel"));
        tags.add(createTag("Image"));
        tags.add(createTag("Vidéo"));
        tags.add(createTag("Blueprint"));
        tags.add(createTag("Draft"));
        tags.add(createTag("Version 1"));
        tags.add(createTag("Version 2"));

        // ⚖️ Tags légaux
        tags.add(createTag("NDA"));
        tags.add(createTag("Contrat"));
        tags.add(createTag("Clause"));
        tags.add(createTag("Confidential"));
        tags.add(createTag("RGPD"));

        // 🔐 Tags sécurité / confidentialité
        tags.add(createTag("Privé"));
        tags.add(createTag("Sensible"));
        tags.add(createTag("Confidentiel"));
        tags.add(createTag("Public"));
        tags.add(createTag("Restreint"));

        // 🧑‍💼 Tags RH
        tags.add(createTag("Employé"));
        tags.add(createTag("Manager"));
        tags.add(createTag("Candidat"));
        tags.add(createTag("Stage"));
        tags.add(createTag("CDD"));
        tags.add(createTag("CDI"));

        tagRepository.saveAll(tags);
        log.info("Inserted {} tags", tags.size());
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }
}
