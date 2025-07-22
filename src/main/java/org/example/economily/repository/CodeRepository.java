package org.example.economily.repository;

import org.example.economily.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code,Long> {

    @Query(value = "select * from codes as c where c.user_id=?1 and c.created_at is not null order by c.created_at desc  limit 1",nativeQuery = true)
    Optional<Code> findByUserId(Long id);

    @Modifying
    @Query("DELETE FROM Code c WHERE c.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);}
