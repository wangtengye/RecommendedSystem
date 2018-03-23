package com.se.mapper;

import com.se.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by takahiro on 2017/7/2.
 */
public interface ChannelRepository extends JpaRepository<Channel,Integer> {
    Channel findById(int id);
    List<Channel> findByType(String type);
    @Transactional
    @Query("select distinct type from Channel")
    List<String> findTypes();
    @Modifying
    @Query("update Channel set clickTime=clickTime+1 where id=:id")
    @Transactional
    void click(@Param("id") int id);
}
