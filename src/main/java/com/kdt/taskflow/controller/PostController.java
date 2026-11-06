package com.kdt.taskflow.controller;

import com.kdt.taskflow.dto.PostRequest;
import com.kdt.taskflow.dto.PostResponse;
import com.kdt.taskflow.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * POST /api/post or /api/posts — Create a new announcement/post or reply.
     * If request.reply is null -> new post.
     * If request.reply is an ID -> reply to that post ID.
     */
    @PostMapping({"/post", "/posts"})
    public ResponseEntity<PostResponse> create(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal String currentUsername,
            UriComponentsBuilder uriBuilder) {

        PostResponse created = postService.create(request, currentUsername);

        URI location = uriBuilder
                .path("/api/post/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /**
     * GET /api/post or /api/posts — List or search posts/replies.
     * Examples:
     * - /api/post -> all posts and replies
     * - /api/post?topLevelOnly=true -> only top-level posts (reply is null)
     * - /api/post?reply=1 -> only replies to post ID 1
     * - /api/post?projectId=2 -> posts/replies for project 2
     * - /api/post?author=john -> posts/replies by author john
     * - /api/post?keyword=meeting -> search title and description
     */
    @GetMapping({"/post", "/posts"})
    public List<PostResponse> search(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long reply,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean topLevelOnly) {

        return postService.search(projectId, author, reply, keyword, topLevelOnly);
    }

    /**
     * GET /api/post/{id} or /api/posts/{id} — Get single post or reply by ID.
     */
    @GetMapping({"/post/{id}", "/posts/{id}"})
    public PostResponse getById(@PathVariable Long id) {
        return postService.getById(id);
    }

    /**
     * PUT or PATCH /api/post/{id} or /api/posts/{id} — Update post or reply.
     */
    @RequestMapping(value = {"/post/{id}", "/posts/{id}"}, method = {RequestMethod.PUT, RequestMethod.PATCH})
    public PostResponse update(
            @PathVariable Long id,
            @RequestBody PostRequest request) {
        return postService.update(id, request);
    }

    /**
     * DELETE /api/post/{id} or /api/posts/{id} — Delete post or reply by ID.
     * If top-level post is deleted, child replies are cascaded.
     */
    @DeleteMapping({"/post/{id}", "/posts/{id}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }
}
