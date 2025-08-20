package com.newez.backend.repository;

import com.newez.backend.domain.LiveChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LiveChannelRepository extends JpaRepository<LiveChannel, Long> {


    List<LiveChannel> findAllByOrderByDisplayOrderAsc();


    Page<LiveChannel> findAllByOrderByDisplayOrderAsc(Pageable pageable);
}