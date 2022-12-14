package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exeption.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostDto createPost(PostDto postDto){
        //convert DTO to entity

       /* Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());*/
        Post post = mapToEntity(postDto);

        Post newPost = postRepository.save(post);

        //convert entity to DTO
        PostDto postResponse = mapToDTO(newPost);
       /* PostDto postResponse = new PostDto();
        postResponse.setId(newPost.getId());
        postResponse.setTitle(newPost.getTitle());
        postResponse.setDescription(newPost.getDescription());
        postResponse.setContent(newPost.getContent());*/
        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int pageNo,int pageSize, String sortBy) {
        //Create pageable instance
        PageRequest pageable = PageRequest.of(pageNo,pageSize, Sort.by(sortBy));
       Page<Post> posts = postRepository.findAll(pageable);
       //get content from page object
        List<Post> listOfPost =posts.getContent();
       List<PostDto> content = listOfPost.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());
        return postResponse;
    }
    @Override
    public PostDto getPostById(Long id) {
        Post post= postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post","id",id));
        return mapToDTO(post);
    }

    @Override
    @PutMapping("/{id}")
    public PostDto updatePost(PostDto postDto, Long id) {

       //get post by id from database
        Post post= postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post","id",id));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        Post updatePost = postRepository.save(post);
        return mapToDTO(updatePost);
    }

    @Override
    public void deletePostById(Long id) {
        Post post= postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post","id",id));
        postRepository.delete(post);
    }

    //convert Entity into DTO
    private PostDto mapToDTO(Post post){
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setContent(post.getContent());
        return postDto;
    }

    //convert DTO into Entity
    private Post mapToEntity(PostDto postDto){
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        return post;
    }
}
