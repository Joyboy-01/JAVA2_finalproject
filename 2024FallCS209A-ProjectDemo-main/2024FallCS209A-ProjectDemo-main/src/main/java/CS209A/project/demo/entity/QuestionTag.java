package CS209A.project.demo.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "QuestionTags")
public class QuestionTag {

    @EmbeddedId
    private QuestionTagId id;

    public QuestionTagId getId() {
        return id;
    }

    public void setId(QuestionTagId id) {
        this.id = id;
    }

    @Embeddable
    public static class QuestionTagId implements Serializable {

        @Column(name = "question_id")
        private Long questionId;

        @Column(name = "tag", length = 255)  // 保留原长度
        private String tag;



        // Getters and Setters

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        // equals and hashCode

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QuestionTagId)) return false;
            QuestionTagId that = (QuestionTagId) o;
            return Objects.equals(getQuestionId(), that.getQuestionId()) &&
                    Objects.equals(getTag(), that.getTag());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getQuestionId(), getTag());
        }
    }
}