package com.kdt.taskflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.kdt.taskflow.domain.Project;
import com.kdt.taskflow.domain.ProjectStatus;

import java.util.List;
import java.util.Optional;

/**
 * Tầng Data Access dùng MyBatis.
 */
@Mapper
public interface ProjectMapper {
    /** new */
    int insert(Project project);

    /** Tìm theo id. Trả Optional để tầng trên xử lý "không tìm thấy" tường minh. */
    Optional<Project> findById(@Param("id") Long id);

    /**
     * Tìm kiếm động: lọc theo status và/hoặc keyword (khớp tên/mô tả).
     * Cả hai đều có thể null → XML dùng dynamic SQL ({@code <where>}, {@code <if>}).
     */
    List<Project> search(@Param("status") ProjectStatus status,
                         @Param("keyword") String keyword);

    /** Cập nhật. Trả số dòng bị ảnh hưởng (0 nghĩa là id không tồn tại). */
    int update(Project project);

    /** Xóa theo id. Trả số dòng bị ảnh hưởng. */
    int deleteById(@Param("id") Long id);
}