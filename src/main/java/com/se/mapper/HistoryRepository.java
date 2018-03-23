package com.se.mapper;

import com.se.model.History;
import com.se.model.UserChannelPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by takahiro on 2017/7/2.
 */
public interface HistoryRepository extends JpaRepository<History, UserChannelPK> {
    List<History> findByPkUserId(int userId);
    List<History>findAll();
    History findByPk(UserChannelPK pk);
}
