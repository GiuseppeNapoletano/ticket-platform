package org.java.milestone.ticket_platform.repository;

import java.util.List;

import org.java.milestone.ticket_platform.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Integer> {
    List<Note> findByTicketId(Integer id);
}
