package com.web.curation.repository.memory;

import com.web.curation.domain.Memory;
import com.web.curation.domain.Pin;
import com.web.curation.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemoryRepository extends JpaRepository<Memory, Long> {

    List<Memory> findByUser(User user);

    Page<Memory> findByUser(User user, Pageable pageable);

    Long countByUser(User user);
}
