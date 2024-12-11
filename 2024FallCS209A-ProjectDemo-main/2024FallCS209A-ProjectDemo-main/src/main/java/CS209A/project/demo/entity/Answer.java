package CS209A.project.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @Column(name = "external_answer_Id")//, unique = true)
    private Long externalAnswerId;  // 外部 API 提供的 answer_id

    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne
    @JoinColumn(name = "external_question_id", insertable = false, updatable = false,unique = true)
    private Question question;

    @Column(name = "content",  columnDefinition = "TEXT")
    private String content;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "user_id")
    private Long userId; 

    @Column(name = "is_accepted")
    private Boolean isAccepted;


    private Integer score;


    @Transient
    private Integer contentLength; // 新增字段，不存储在数据库中
    // Getters, Setters

    public Long getExternalAnswerId() {
        return externalAnswerId;
    }

    public void setExternalAnswerId(Long externalAnswerId) {
        this.externalAnswerId = externalAnswerId;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public int getContentLength() {
        return (content != null) ? content.length() : 0;
    }
}