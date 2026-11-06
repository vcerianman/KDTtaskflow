package com.kdt.taskflow.mapper;

import com.kdt.taskflow.domain.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReportMapper {

    int insert(Report report);

    Optional<Report> findById(@Param("id") Long id);

    List<Report> search(@Param("type") String type,
                        @Param("author") String author,
                        @Param("targetUsername") String targetUsername,
                        @Param("keyword") String keyword);

    int deleteById(@Param("id") Long id);
}
