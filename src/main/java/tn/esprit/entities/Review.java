package tn.esprit.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Review {
    private int id;
    private int evenement_id;
    private int produit_id;
    private String comment;
    private int note;
    private LocalDateTime created_at;
    public Review() {}

    public Review(int id, int evenement_id, int produit_id, String comment, int note, LocalDateTime created_at) {
        this.id = id;
        this.evenement_id = evenement_id;
        this.produit_id = produit_id;
        this.comment = comment;
        this.note = note;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvenement_id() {
        return evenement_id;
    }

    public void setEvenement_id(int evenement_id) {
        this.evenement_id = evenement_id;
    }

    public int getProduit_id() {
        return produit_id;
    }

    public void setProduit_id(int produit_id) {
        this.produit_id = produit_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return evenement_id == review.evenement_id && produit_id == review.produit_id && note == review.note && Objects.equals(comment, review.comment) && Objects.equals(created_at, review.created_at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evenement_id, produit_id, comment, note, created_at);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format(
                "Review { comment=\"%s\", note=%d, createdAt=%s }",
                comment,
                note,
                created_at.format(formatter)
        );
    }
}
