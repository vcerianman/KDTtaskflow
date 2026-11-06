package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.domain.Post;
import com.kdt.taskflow.dto.PostRequest;
import com.kdt.taskflow.dto.PostResponse;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.PostMapper;
import com.kdt.taskflow.mapper.ProjectMapper;
import com.kdt.taskflow.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final ProjectMapper projectMapper;

    public PostServiceImpl(PostMapper postMapper, ProjectMapper projectMapper) {
        this.postMapper = postMapper;
        this.projectMapper = projectMapper;
    }

    private void validateRelations(Long projectId, Long replyId) {
        if (projectId != null) {
            projectMapper.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot find project id=" + projectId));
        }
        if (replyId != null) {
            postMapper.findById(replyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot find parent post id=" + replyId));
        }
    }

    @Override
    @Transactional
    public PostResponse create(PostRequest request, String currentUsername) {
        validateRelations(request.projectId(), request.reply());

        String effectiveAuthor = (request.author() != null && !request.author().isBlank())
                ? request.author().trim()
                : currentUsername;

        if (effectiveAuthor == null || effectiveAuthor.isBlank()) {
            effectiveAuthor = "anonymous";
        }

        Post post = request.toDomain(effectiveAuthor);
        postMapper.insert(post);
        return getById(post.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getById(Long id) {
        Post post = postMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find post id=" + id));
        return PostResponse.from(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> search(Long projectId, String author, Long reply, String keyword, Boolean topLevelOnly) {
        return postMapper.search(projectId, author, reply, keyword, topLevelOnly)
                .stream()
                .map(PostResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public PostResponse update(Long id, PostRequest request) {
        Post existing = postMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find post id=" + id));

        if (request.projectId() != null) {
            projectMapper.findById(request.projectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot find project id=" + request.projectId()));
            existing.setProjectId(request.projectId());
        }

        if (request.reply() != null) {
            postMapper.findById(request.reply())
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot find parent post id=" + request.reply()));
            existing.setReply(request.reply());
        }

        if (request.title() != null) {
            existing.setTitle(request.title().trim());
        }

        if (request.description() != null && !request.description().isBlank()) {
            existing.setDescription(request.description().trim());
        }

        if (request.author() != null && !request.author().isBlank()) {
            existing.setAuthor(request.author().trim());
        }

        int affected = postMapper.update(existing);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find post id=" + id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int affected = postMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find post id=" + id);
        }
    }
}
