package com.codeloom.dsa.algorithm.entity;

import com.codeloom.dsa.learning.entity.ExperienceLevel;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "algorithm_learning_content",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"algorithm_id", "level"})
        }
)
public class AlgorithmLearningContent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id", nullable = false)
    private Algorithm algorithm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExperienceLevel level;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "problem_statement", columnDefinition = "TEXT")
    private String problemStatement;

    @Column(columnDefinition = "TEXT")
    private String intuition;

    @Column(name = "why_it_works", columnDefinition = "TEXT")
    private String whyItWorks;

    @Column(name = "how_it_works", columnDefinition = "TEXT")
    private String howItWorks;

    @Column(columnDefinition = "TEXT")
    private String pseudocode;

    @Column(name = "complexity_summary", columnDefinition = "TEXT")
    private String complexitySummary;

    @Column(name = "when_to_use", columnDefinition = "TEXT")
    private String whenToUse;

    @Column(name = "when_not_to_use", columnDefinition = "TEXT")
    private String whenNotToUse;

    @Column(columnDefinition = "TEXT")
    private String advantages;

    @Column(columnDefinition = "TEXT")
    private String limitations;

    @Column(name = "common_mistakes", columnDefinition = "TEXT")
    private String commonMistakes;

    @Column(name = "interview_tips", columnDefinition = "TEXT")
    private String interviewTips;

    @Column(name = "implementation_notes", columnDefinition = "TEXT")
    private String implementationNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected AlgorithmLearningContent() {
    }

    public AlgorithmLearningContent(
            Algorithm algorithm,
            ExperienceLevel level,
            String introduction,
            String problemStatement,
            String intuition,
            String whyItWorks,
            String howItWorks,
            String pseudocode,
            String complexitySummary,
            String whenToUse,
            String whenNotToUse,
            String advantages,
            String limitations,
            String commonMistakes,
            String interviewTips,
            String implementationNotes
    ) {
        this.algorithm = algorithm;
        this.level = level;
        this.introduction = introduction;
        this.problemStatement = problemStatement;
        this.intuition = intuition;
        this.whyItWorks = whyItWorks;
        this.howItWorks = howItWorks;
        this.pseudocode = pseudocode;
        this.complexitySummary = complexitySummary;
        this.whenToUse = whenToUse;
        this.whenNotToUse = whenNotToUse;
        this.advantages = advantages;
        this.limitations = limitations;
        this.commonMistakes = commonMistakes;
        this.interviewTips = interviewTips;
        this.implementationNotes = implementationNotes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public ExperienceLevel getLevel() {
        return level;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public String getIntuition() {
        return intuition;
    }

    public String getWhyItWorks() {
        return whyItWorks;
    }

    public String getHowItWorks() {
        return howItWorks;
    }

    public String getPseudocode() {
        return pseudocode;
    }

    public String getComplexitySummary() {
        return complexitySummary;
    }

    public String getWhenToUse() {
        return whenToUse;
    }

    public String getWhenNotToUse() {
        return whenNotToUse;
    }

    public String getAdvantages() {
        return advantages;
    }

    public String getLimitations() {
        return limitations;
    }

    public String getCommonMistakes() {
        return commonMistakes;
    }

    public String getInterviewTips() {
        return interviewTips;
    }

    public String getImplementationNotes() {
        return implementationNotes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
