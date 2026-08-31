package com.kdt.taskflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kdt.taskflow.domain.Appeal;
import com.kdt.taskflow.dto.AppealRequest;
import com.kdt.taskflow.dto.AppealResponse;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.AppealMapper;
import com.kdt.taskflow.service.AppealService;

import java.util.List;

@Service
public class AppealServiceImpl implements AppealService {

    private final AppealMapper appealMapper;

    public AppealServiceImpl(AppealMapper appealMapper) {
        this.appealMapper = appealMapper;
    }

    @Override
    @Transactional
    public AppealResponse create(AppealRequest request) {
        Appeal appeal = request.toDomain();
        appealMapper.insert(appeal);
        return getById(appeal.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public AppealResponse getById(Long id) {
        Appeal appeal = appealMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find appeal id=" + id));
        return AppealResponse.from(appeal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppealResponse> search(String username, String keyword) {
        return appealMapper.search(username, keyword)
                .stream()
                .map(AppealResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int affected = appealMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find appeal id=" + id);
        }
    }
}
