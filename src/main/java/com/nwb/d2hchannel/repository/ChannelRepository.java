package com.nwb.d2hchannel.repository;


import com.nwb.d2hchannel.persistence.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChannelRepository
        extends JpaRepository<Channel, Long> {

    @Query("select p from Channel p where p.channelName in (?1)")
    List<Channel> findByChannelName(List<String> channelName);
}
