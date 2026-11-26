package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.domain.Post;
import com.kdt.taskflow.domain.PostImage;
import com.kdt.taskflow.dto.PostImageRequest;
import com.kdt.taskflow.dto.PostImageResponse;
import com.kdt.taskflow.dto.PostRequest;
import com.kdt.taskflow.dto.PostResponse;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.PostImageMapper;
import com.kdt.taskflow.mapper.PostMapper;
import com.kdt.taskflow.mapper.ProjectMapper;
import com.kdt.taskflow.service.PostService;
import com.kdt.taskflow.service.PostSseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final int MAX_IMAGES_PER_POST = 4;

    private final PostMapper postMapper;
    private final PostImageMapper postImageMapper;
    private final ProjectMapper projectMapper;
    private final PostSseService postSseService;

    public PostServiceImpl(PostMapper postMapper,
                           PostImageMapper postImageMapper,
                           ProjectMapper projectMapper,
                           PostSseService postSseService) {
        this.postMapper = postMapper;
        this.postImageMapper = postImageMapper;
        this.projectMapper = projectMapper;
        this.postSseService = postSseService;
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

    private void savePostImages(Long postId, List<PostImageRequest> imageRequests) {
        if (imageRequests == null || imageRequests.isEmpty()) {
            return;
        }
        if (imageRequests.size() > MAX_IMAGES_PER_POST) {
            throw new IllegalArgumentException("Maximum " + MAX_IMAGES_PER_POST + " images allowed per post");
        }

        List<PostImage> entities = new ArrayList<>(imageRequests.size());
        Set<Integer> usedOrders = new HashSet<>();

        for (int i = 0; i < imageRequests.size(); i++) {
            PostImageRequest req = imageRequests.get(i);
            if (req == null || req.imageUrl() == null || req.imageUrl().isBlank()) {
                throw new IllegalArgumentException("Image url cannot be blank");
            }

            int order = req.displayOrder() != null ? req.displayOrder() : i;
            if (order < 0 || order >= MAX_IMAGES_PER_POST) {
                throw new IllegalArgumentException("Image displayOrder must be between 0 and 3");
            }
            if (!usedOrders.add(order)) {
                throw new IllegalArgumentException("Duplicate image displayOrder: " + order);
            }

            PostImage img = new PostImage();
            img.setPostId(postId);
            img.setImageUrl(req.imageUrl().trim());
            img.setPublicId(req.publicId() != null && !req.publicId().isBlank() ? req.publicId().trim() : null);
            img.setDisplayOrder(order);
            entities.add(img);
        }

        postImageMapper.insertBatch(entities);
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
        savePostImages(post.getId(), request.images());

        PostResponse created = getById(post.getId());
        postSseService.broadcastCreated(created);
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getById(Long id) {
        Post post = postMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find post id=" + id));

        List<PostImageResponse> images = postImageMapper.findByPostId(id)
                .stream()
                .map(PostImageResponse::from)
                .toList();

        return PostResponse.from(post, images);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> search(Long projectId, String author, Long reply, String keyword, Boolean topLevelOnly) {
        List<Post> posts = postMapper.search(projectId, author, reply, keyword, topLevelOnly);
        if (posts.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = posts.stream().map(Post::getId).toList();
        Map<Long, List<PostImageResponse>> imagesByPostId = postImageMapper.findByPostIds(postIds)
                .stream()
                .collect(Collectors.groupingBy(
                        PostImage::getPostId,
                        Collectors.mapping(PostImageResponse::from, Collectors.toList())
                ));

        return posts.stream()
                .map(post -> PostResponse.from(post, imagesByPostId.getOrDefault(post.getId(), List.of())))
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

        // Replace images only when explicitly provided in the update request (supports partial/half updates)
        if (request.images() != null) {
            postImageMapper.deleteByPostId(id);
            savePostImages(id, request.images());
        }

        PostResponse updated = getById(id);
        postSseService.broadcastUpdated(updated);
        return updated;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Post post = postMapper.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find post id=" + id));

        int affected = postMapper.deleteById(id);
        if (affected == 0) {
            throw new ResourceNotFoundException("Cannot find post id=" + id);
        }
        postSseService.broadcastDeleted(id, post.getProjectId());
    }
}
