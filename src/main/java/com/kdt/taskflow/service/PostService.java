package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.PostRequest;
import com.kdt.taskflow.dto.PostResponse;

import java.util.List;

public interface PostService {

    PostResponse create(PostRequest request, String currentUsername);

    PostResponse getById(Long id);

    List<PostResponse> search(Long projectId, String author, Long reply, String keyword, Boolean topLevelOnly);

    PostResponse update(Long id, PostRequest request);

    void delete(Long id);
}
