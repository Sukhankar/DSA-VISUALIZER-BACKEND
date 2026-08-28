package com.codeloom.dsa.roadmap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "roadmap_modules")
public class RoadmapModule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoadmapTier tier;

    @Column(name = "icon_name", length = 50)
    private String iconName;

    @Column(name = "category_slug", length = 100)
    private String categorySlug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_module_id")
    private RoadmapModule prerequisiteModule;

    @Column(name = "xp_reward")
    private Integer xpReward = 100;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id")
    private com.codeloom.dsa.learning.entity.LearningPath learningPath;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber ASC")
    private List<RoadmapStep> steps = new ArrayList<>();

    public RoadmapModule() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public RoadmapTier getTier() {
        return tier;
    }

    public void setTier(RoadmapTier tier) {
        this.tier = tier;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public RoadmapModule getPrerequisiteModule() {
        return prerequisiteModule;
    }

    public void setPrerequisiteModule(RoadmapModule prerequisiteModule) {
        this.prerequisiteModule = prerequisiteModule;
    }

    public Integer getXpReward() {
        return xpReward;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }

    public com.codeloom.dsa.learning.entity.LearningPath getLearningPath() {
        return learningPath;
    }

    public void setLearningPath(com.codeloom.dsa.learning.entity.LearningPath learningPath) {
        this.learningPath = learningPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public List<RoadmapStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RoadmapStep> steps) {
        this.steps = steps;
    }
}
